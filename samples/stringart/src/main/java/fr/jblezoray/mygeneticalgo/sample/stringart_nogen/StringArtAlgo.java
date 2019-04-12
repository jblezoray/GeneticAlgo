package fr.jblezoray.mygeneticalgo.sample.stringart_nogen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.edge.Edge;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.edge.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.edge.ScoredEdge;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image.ByteImage;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image.ImageSize;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image.UnboundedImage;

public class StringArtAlgo {

  private static final float CANVAS_WIDTH_MILLIMETERS = 630.0f;
  private static final float THREAD_THICKNESS_MILLIMETERS = 0.15f; 
  private static final float PIN_DIAMETER_MILLIMETERS = 2.0f;
  private static final int NB_NAILS = 200;
  private static final int MIN_NAILS_DIFF = Math.max(1, (int)NB_NAILS/20);

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

  /**
   * If enabled, then the way a thread turns around a pin is considered. 
   */
  private boolean edgeWayEnabled;
  
  /**
   * 
   * @param refImage
   * @param importanceMappingImg
   * @param edgeWayEnabled disable to get faster processing, enable to lessen 
   *        the moiré effect. 
   * @throws IOException
   */
  public StringArtAlgo(
      String refImage, 
      String importanceMappingImg, 
      boolean edgeWayEnabled) throws IOException {
    this.refImg = new ByteImage(refImage);
    this.importanceMappingImg = new ByteImage(importanceMappingImg);
    this.size = this.refImg.getSize();
    this.resolutionMmPerPx = CANVAS_WIDTH_MILLIMETERS / this.size.w;
    this.lineThicknessInPx = THREAD_THICKNESS_MILLIMETERS / this.resolutionMmPerPx;
    this.pinDiameterInPx = PIN_DIAMETER_MILLIMETERS / resolutionMmPerPx;
    this.edgeFactory = new EdgeFactory(
        size, NB_NAILS, lineThicknessInPx, pinDiameterInPx, MIN_NAILS_DIFF);
    this.edgeWayEnabled = false;
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
      AtomicInteger numberOfEdges = new AtomicInteger();
      Stream<Edge> edgeStream = 
          streamPossibleEdgesFromPin(prevPin, isPrevPinClockwise, edges)
          .peek(edge -> numberOfEdges.incrementAndGet());
      scoredEdge = findBestEdgeToAdd(curImg, edgeStream);
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
      
      printDebugInfo(++iteration, curImg, scoredEdge, numberOfEdges, after-before);
      
    } while (scoredEdge.getNorm()<=prevNorm); // stop if it does not reduces the norm.
  }
  
  
  /**
   * Stream edges that start where the previous one finishes, and skip edges 
   * that are already in the graph.
   * 
   * @param pin
   * @param isPinClockwise
   * @param edgesInImage  the edges that are already in the image.
   * @return a parallel stream of the available pins.
   */
  private Stream<Edge> streamPossibleEdgesFromPin(
      int pin, boolean isPinClockwise, List<Edge> edgesInImage) {
    boolean clockwise = edgeWayEnabled ? !isPinClockwise : false;
    return this.edgeFactory
        .getAllPossibleEdges()
        .stream()
        .filter(edge -> 
            edge.contains(pin, clockwise)
            && (edgeWayEnabled||edge.isPinAClockwise()==edge.isPinBClockwise())
            && !edgesInImage.contains(edge))
        .parallel(); // for perf. 
  }
  

  /**
   * Compute a resulting image for each edge and score it to get the edge that 
   * contributes the most to the reduction of the norm.
   * 
   * @param curImg the current image (will be left untouched)
   * @param edgeStream the possible edges to add.
   * @return The best Edge to add, and the resulting score.
   */
  private ScoredEdge findBestEdgeToAdd(
      UnboundedImage curImg, Stream<Edge> edgeStream) {
    return edgeStream
        .map(edge -> new ScoredEdge(edge, 
            this.edgeFactory.drawEdgeInImage(curImg.deepCopy(), edge)
                .differenceWith(refImg)
                .multiplyWith(importanceMappingImg)
                .l2norm()))
        .min((a, b) -> a.getNorm() < b.getNorm()? -1 : 1)
        .orElseThrow(() -> new RuntimeException());
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
    
    boolean drawn = iteration%50 == 0;
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


}
