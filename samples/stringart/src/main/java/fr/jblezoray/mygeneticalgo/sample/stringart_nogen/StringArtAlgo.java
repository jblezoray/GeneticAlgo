package fr.jblezoray.mygeneticalgo.sample.stringart_nogen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.core.EdgeDrawer;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.core.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.core.EdgeImageIO;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.edge.Edge;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.edge.ScoredEdge;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image.ImageSize;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image.UnboundedImage;

public class StringArtAlgo {

  /**
   * The reference image.
   */
  private final Image refImg;
  
  /**
   * The importance image.
   * 
   * Each pixel the importance image describes the influence of the
   * corresponding pixel in the reference image.  
   * A value of 0x00 implies that the pixel does not contributes to the 
   * fitness. A value of OxFF corresponds to the maximum influence possible.
   * Therefore, the brighter the zone are in the importanceMappingImage, the
   * more they represent important features of the reference image.
   */
  private final Image importanceMappingImg;
  
  /**
   * Size of the reference image.
   */
  private final ImageSize size;

  /**
   * A class to build the representation of Edges in images.
   */
  private final EdgeDrawer edgeDrawer;
  
  /**
   * A factory for building the edges.
   */
  private final EdgeFactory edgeFactory;

  /**
   * Listeners of the results of the processing. 
   */
  private Set<IStringArtAlgoListener> listeners;
  
  /**
   * If enabled, then the way a thread turns around a nail is considered. 
   */
  private boolean edgeWayEnabled;
  
  /**
   * 
   * @param refImage
   * @param importanceMappingImg
   * @param edgeWayEnabled disable to get faster processing, enable to lessen 
   *        the moiré effect.
   * @param canvasWidthMilimeters
   * @param threadThicknessMilimeters
   * @param nailDiameterMilimeters
   * @param nbNails
   * @param minNailsDiff
   * @throws IOException
   */
  public StringArtAlgo(
      String refImage, 
      String importanceMappingImg, 
      boolean edgeWayEnabled, 
      float canvasWidthMilimeters,
      float threadThicknessMilimeters,
      float nailDiameterMilimeters,
      int nbNails,
      int minNailsDiff
      ) throws IOException {
    this.refImg = EdgeImageIO.readFile(refImage);
    this.importanceMappingImg = EdgeImageIO.readFile(importanceMappingImg);
    this.size = this.refImg.getSize();
    float resolutionMmPerPx = canvasWidthMilimeters / this.size.w;
    float lineThicknessInPx = threadThicknessMilimeters / resolutionMmPerPx;
    float nailDiameterInPx = nailDiameterMilimeters / resolutionMmPerPx;
    this.edgeDrawer = 
        new EdgeDrawer(size, nbNails, lineThicknessInPx, nailDiameterInPx);
    this.edgeFactory = new EdgeFactory(minNailsDiff, nbNails, this.edgeDrawer);
    this.edgeWayEnabled = false;
  }
  
  
  public void start() {
    // edges of the image to build.
    List<Edge> edges = new ArrayList<>();

    // create an image that represents the result.
    UnboundedImage curImg = new UnboundedImage(this.size);
    this.edgeDrawer.drawAllNails(curImg);
    
    // values from kept from the previous round. 
    double prevNorm = Float.MAX_VALUE;
    int prevNail = 0; 
    boolean isPrevNailClockwise = false;
    
    // optimization algo.  
    AtomicInteger iteration = new AtomicInteger(0);
    ScoredEdge scoredEdge = null;
    do {
      // search for the edge that, when added to 'curImg', results in the 
      // maximum reduction of the norm. 
      long before = System.currentTimeMillis();
      AtomicInteger numberOfEdges = new AtomicInteger();
      scoredEdge = this.edgeFactory.getAllPossibleEdges().stream().parallel()
          .filter(predicateForEdgesFromNail(prevNail, isPrevNailClockwise, edges))
          .peek(edge -> numberOfEdges.incrementAndGet())
          .map(edge -> getScoreIfAddedInImage(edge, curImg))
          .min((a, b) -> a.getNorm()<b.getNorm() ? -1 : 1)
          .orElseThrow(() -> new RuntimeException("Invalid state: no edge."));
      long after = System.currentTimeMillis();

      // store the new prev nail for the next round. 
      this.edgeDrawer.drawEdgeInImage(curImg, scoredEdge.getEdge());
      edges.add(scoredEdge.getEdge());
      prevNorm = scoredEdge.getNorm();
      if (scoredEdge.getEdge().getNailA() == prevNail) {
        prevNail = scoredEdge.getEdge().getNailB();
        isPrevNailClockwise = scoredEdge.getEdge().isNailBClockwise();
      } else {
        prevNail = scoredEdge.getEdge().getNailA();
        isPrevNailClockwise = scoredEdge.getEdge().isNailAClockwise();
      }
      
      // notify results to listeners. 
      int it = iteration.incrementAndGet();
      for (IStringArtAlgoListener listener : this.listeners) {
        listener.notifyRoundResults(it, curImg, importanceMappingImg, refImg, 
            scoredEdge, numberOfEdges, after-before);
      }
      
    } while (scoredEdge.getNorm()<=prevNorm); // stop if it does not reduces the norm.
  }
  
  
  
  /**
   * Builds a predicate that filters to keep only the edges that start from a 
   * specific nail.
   *  
   *  If an edge is already in the image, it does not pass this filter.  
   *  
   *  If the 'edgeWayEnabled' boolean option is not setted to true, then only 
   *  the edges that goes clockwise are kept.  Otherwise, the predicates 
   *  considers that the string has make a 'turn' around the nail, and therefore
   *  arises at the other side.
   *  
   * @param nail  number of the start nail.
   * @param isNailClockwise if the previous edge was on the 'clockwise' side of 
   *        the nail.  
   * @param edgesInImage  the list of all the edges currently in the image.
   * @return
   */
  private Predicate<Edge> predicateForEdgesFromNail(
      int nail, boolean isNailClockwise, List<Edge> edgesInImage) {
    boolean clockwise = edgeWayEnabled ? !isNailClockwise : false;
    return edge -> edge.contains(nail, clockwise)
        && (edgeWayEnabled||edge.isNailAClockwise()==edge.isNailBClockwise())
        && !edgesInImage.contains(edge);
  }
  

  /**
   * 
   * @param edge the edge to add.
   * @param curImg the current image (will be left untouched)
   * @return The score of the resulting image if the edge is added.
   */
  private ScoredEdge getScoreIfAddedInImage(Edge edge, UnboundedImage curImg) {
    double score = this.edgeDrawer.drawEdgeInImage(curImg.deepCopy(), edge)
        .differenceWith(refImg)
        .multiplyWith(importanceMappingImg)
        .l2norm();
    return new ScoredEdge(edge, score);
  }
  

  public void addListener(IStringArtAlgoListener listener) {
    this.listeners.add(listener);
  }

}
