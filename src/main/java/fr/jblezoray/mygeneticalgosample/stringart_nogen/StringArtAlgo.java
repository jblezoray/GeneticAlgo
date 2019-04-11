package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import fr.jblezoray.mygeneticalgosample.stringart_nogen.edge.Edge;
import fr.jblezoray.mygeneticalgosample.stringart_nogen.edge.EdgeFactory;
import fr.jblezoray.mygeneticalgosample.stringart_nogen.image.ByteImage;
import fr.jblezoray.mygeneticalgosample.stringart_nogen.image.Image;
import fr.jblezoray.mygeneticalgosample.stringart_nogen.image.ImageSize;
import fr.jblezoray.mygeneticalgosample.stringart_nogen.image.UnboundedImage;

public class StringArtAlgo {

  private static final float CANVAS_WIDTH_MILLIMETERS = 630.0f;
  private static final float THREAD_THICKNESS_MILLIMETERS = 0.15f; 
  private static final float PIN_DIAMETER_MILLIMETERS = 2.0f;
  private static final int NB_NAILS = 200;
  private static final int MIN_NAILS_DIFF = Math.max(1, (int)NB_NAILS/20);

  /**
   * the reference image
   */
  private final Image refImg;
  
  /**
   * The importance image 
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
   * size of the reference image.
   */
  private final ImageSize size;

  /** 
   * Resolution of the reference image. 
   */
  private final float resolutionMmPerPx;
  
  /**
   * Width of the lines (threads) when represented in the image.  
   */
  private final float lineThicknessInPx;
  
  /**
   * Diameter of the nails (pins).
   */
  private final float pinDiameterInPx;
  
  /**
   * A factory for building the edges, and their representation in images.
   */
  private final EdgeFactory edgeFactory;

  
  public StringArtAlgo(String refImage, String importanceMappingImg) throws IOException {
    this.refImg = new ByteImage(refImage);
    this.importanceMappingImg = new ByteImage(importanceMappingImg);
    this.size = this.refImg.getSize();
    this.resolutionMmPerPx = CANVAS_WIDTH_MILLIMETERS / this.size.w;
    this.lineThicknessInPx = THREAD_THICKNESS_MILLIMETERS / this.resolutionMmPerPx;
    this.pinDiameterInPx = PIN_DIAMETER_MILLIMETERS / resolutionMmPerPx;
    this.edgeFactory = new EdgeFactory(
        size, NB_NAILS, lineThicknessInPx, pinDiameterInPx, MIN_NAILS_DIFF);
  }


  public void start() {
    // edges of the image to build.
    List<Edge> edges = new ArrayList<>();

    // create an image that represents the result.
    UnboundedImage curImg = new UnboundedImage(this.size);
    this.edgeFactory.drawAllPins(curImg);
    
    // values from kept from the previous round. 
    double prevNorm = Float.MAX_VALUE;
    int prevPin = 0; 
    boolean isPrevPinClockwise = false;
    
    // optimization algo.  
    int iteration = 0;
    ScoredEdge scoredEdge = null;
    do {
      // search for the edge that, when added to 'curImg', results in the 
      // maximum reduction of the norm. 
      long before = System.currentTimeMillis();
      AtomicInteger counterOfEvaluatedEdges = new AtomicInteger();
      scoredEdge = findBestEdgeToAdd(curImg, edges, prevPin, isPrevPinClockwise,
          counterOfEvaluatedEdges);
      long after = System.currentTimeMillis();

      // store the new prev pin.
      this.edgeFactory.drawEdgeInImage(curImg, scoredEdge.getEdge());
      edges.add(scoredEdge.getEdge());
      prevNorm = scoredEdge.getNorm();
      if (scoredEdge.getEdge().getPinA() == prevPin) {
        prevPin = scoredEdge.getEdge().getPinB();
        isPrevPinClockwise = scoredEdge.getEdge().isPinBClockwise();
      } else {
        prevPin = scoredEdge.getEdge().getPinA();
        isPrevPinClockwise = scoredEdge.getEdge().isPinAClockwise();
      }
      
      printDebugInfo(iteration, curImg, scoredEdge, counterOfEvaluatedEdges,
          after-before);
    } while (scoredEdge.getNorm()>prevNorm); // stop if it does not reduces the norm.
  }


  /**
   * Print debug stuffs...
   * @param iteration
   * @param curImg
   * @param scoredEdge
   * @param counterOfEvaluatedEdges
   * @param timeTookForThisRound
   */
  private void printDebugInfo(int iteration, UnboundedImage curImg, 
      ScoredEdge scoredEdge, AtomicInteger counterOfEvaluatedEdges,
      long timeTookForThisRound) {
    
    boolean drawn = iteration++%50 == 0;
    if (drawn) {
      try {
        curImg.writeToFile(new File("_rendering.png"));
        curImg.differenceWith(this.refImg)
            .multiplyWith(this.importanceMappingImg)
            .writeToFile(new File("_diff.png"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    System.out.println(String.format(
        "iteration:%d ; norm:%7.0f ; pins:%3d,%3d ; choose:%5dms (mean:%d*%2.3fms) %s",
        iteration,
        scoredEdge.getNorm(), 
        scoredEdge.getEdge().getPinA(), 
        scoredEdge.getEdge().getPinB(),
        timeTookForThisRound, 
        counterOfEvaluatedEdges.get(),
        timeTookForThisRound / (float)counterOfEvaluatedEdges.get(),
        drawn ? " [drawn]" : ""));
    
  }


  private ScoredEdge findBestEdgeToAdd(UnboundedImage curImg, List<Edge> edges,
      int prevPin, boolean isPrevPinClockwise, AtomicInteger edgesCounter) {

    // stream edges that start where the previous one finishes ; skip edges 
    // that are already in the graph
    Stream<Edge> edgeStream = this.edgeFactory.getAllPossibleEdges()
        .stream()
        .filter(edge -> 
            edge.contains(prevPin, !isPrevPinClockwise) 
            && !edges.contains(edge));
    
    // for perf. 
    edgeStream = edgeStream.parallel();
    
    // count the number of edges in the stream.
    edgeStream = edgeStream.peek(edge -> edgesCounter.incrementAndGet());

    // compute a resulting image for each edge and score it to get the edge  
    // that contributes the most to the reduction of the norm.
    ScoredEdge scoredEdge = edgeStream
        .map(edge -> new ScoredEdge(edge, 
            this.edgeFactory.drawEdgeInImage(curImg.deepCopy(), edge)
                .differenceWith(refImg)
                .multiplyWith(importanceMappingImg)
                .l2norm()))
        .min((a, b) -> a.getNorm() < b.getNorm()? -1 : 1)
        .orElseThrow(() -> new RuntimeException());
    
    return scoredEdge;
  }
}
