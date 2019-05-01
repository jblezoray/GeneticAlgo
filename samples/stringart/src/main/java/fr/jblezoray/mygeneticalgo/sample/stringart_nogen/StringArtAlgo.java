package fr.jblezoray.mygeneticalgo.sample.stringart_nogen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeDrawer;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeImageIO;
import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;
import fr.jblezoray.mygeneticalgo.sample.stringart.edge.ScoredEdge;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ImageSize;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;

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
    this.edgeFactory = new EdgeFactory(minNailsDiff, nbNails, edgeWayEnabled, 
        this.edgeDrawer);
    this.listeners = new HashSet<>();
  }
  
  
  public void start() {
    // edges of the image to build.
    List<Edge> edges = new ArrayList<>();

    // create an image that represents the result.
    UnboundedImage curImg = new UnboundedImage(this.size);
    curImg.add(this.edgeDrawer.drawAllNails());
    
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
      scoredEdge = getBestEdge(prevNail, isPrevNailClockwise, curImg, edges);

      // store the new prev nail for the next round.
      curImg.add(scoredEdge.getEdge().getDrawnEdgeData());
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
            scoredEdge);
      }
      
    } while (scoredEdge.getNorm()<=prevNorm); // stop if it does not reduces the norm.
  }
  
  
  /**
   * Tries to add all the possible edges, and keep only the one that results in 
   * a minimum score. 
   *  
   * @param prevNail
   * @param isPrevNailClockwise
   * @param curImg
   * @param edges
   * @return
   */
  private ScoredEdge getBestEdge(int prevNail, boolean isPrevNailClockwise, 
      UnboundedImage curImg, List<Edge> edges) {
    AtomicInteger numberOfEdges = new AtomicInteger();
    long before = System.currentTimeMillis();
    ScoredEdge scoredEdge = this.edgeFactory
        .streamEdges(prevNail, !isPrevNailClockwise)
        .filter(edge -> !edges.contains(edge)) // not already in the image
        .map(edge -> getScoreIfAddedInImage(edge, curImg))
        .peek(edge -> numberOfEdges.incrementAndGet())
        .min((a, b) -> a.getNorm()<b.getNorm() ? -1 : 1)
        .orElseThrow(() -> new RuntimeException("Invalid state: no edge."));
    long after = System.currentTimeMillis();
    scoredEdge.setNumberOfEdgesEvaluated(numberOfEdges.get());
    scoredEdge.setTimeTook(after-before);
    return scoredEdge;
  }


  /**
   * Builds a score for this edge when added in an image.
   * 
   * @param edge the edge to add.
   * @param curImg the current image (will be left untouched)
   * @return The score of the resulting image if the edge is added.
   */
  private ScoredEdge getScoreIfAddedInImage(Edge edge, UnboundedImage curImg) {
    double score = curImg
        .deepCopy()
        .add(edge.getDrawnEdgeData())
        .differenceWith(refImg)
        .multiplyWith(importanceMappingImg)
        .l2norm();
    return new ScoredEdge(edge, score);
  }
  

  public void addListener(IStringArtAlgoListener listener) {
    this.listeners.add(listener);
  }

}
