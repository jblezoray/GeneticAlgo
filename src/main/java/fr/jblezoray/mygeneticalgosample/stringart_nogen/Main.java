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

public class Main {

  private static final int NB_NAILS = 200;
  private static final float LINE_THICKNESS = 1.0f;
  
  public static void main(String[] args) throws IOException {
    // load the reference image
    Image refImg = new ByteImage("samples/stringart/einstein.png");
    refImg.writeToFile(new File("_refImg.png"));
    
    // load the importance image 
    // Each pixel the importance image describes the influence of the 
    // corresponding pixel in the reference image.  
    // A value of 0x00 implies that the pixel does not contributes to the 
    // fitness. A value of OxFF corresponds to the maximum influence possible.
    // Therefore, the brighter the zone are in the importanceMappingImage, the
    // more they represent important features of the reference image.
    Image importanceMappingImg =  new ByteImage("samples/stringart/einstein_features.png");
    importanceMappingImg.writeToFile(new File("_features.png"));
    
    // edges of the image to build.
    List<Edge> edges = new ArrayList<>();

    // optimization algo
    ImageSize size = refImg.getSize();
    EdgeFactory edgeFactory = new EdgeFactory(size, NB_NAILS, LINE_THICKNESS);
    double prevNorm = Float.MAX_VALUE;
    int prevPin = 0; 
    UnboundedImage curImg = new UnboundedImage(size);
    int iteration = 0;
    while (true) {
      long before = System.currentTimeMillis();

      // stream edges that start where the previous one finishes ; skip edges 
      // that are already in the graph
      final int prevPinFinalCopy = prevPin;
      Stream<Edge> edgeStream = edgeFactory.getAllPossibleEdges()
          .stream()
          .filter(edge -> edge.contains(prevPinFinalCopy) && !edges.contains(edge));
      
      // for perf. 
      edgeStream = edgeStream.parallel();
      
      // count the number of edges in the stream.
      AtomicInteger counter = new AtomicInteger();
      edgeStream = edgeStream.peek(edge -> counter.incrementAndGet());

      // compute a resulting image for each edge and score it to get the edge  
      // that contributes the most to the reduction of the norm.
      final UnboundedImage curImgFinal = curImg;
      ScoredEdge scoredEdge = edgeStream
          .map(edge -> new ScoredEdge(edge, 
              edgeFactory.drawEdgeInImage(curImgFinal.deepCopy(), edge)
                  .differenceWith(refImg)
                  .multiplyWith(importanceMappingImg)
                  .l2norm()))
          .min((a, b) -> a.getNorm() < b.getNorm()? -1 : 1)
          .orElseThrow(() -> new RuntimeException());
      long after = System.currentTimeMillis();

      // find the edge that, if removed, contributes the most to the reduction 
      // of the norm. 
      // TODO 

      // stop if we reached an optimal form (the best result)
      if (scoredEdge.getNorm()>prevNorm) {
        break;
      }

      // save image !
      edgeFactory.drawEdgeInImage(curImg, scoredEdge.getEdge());
      edges.add(scoredEdge.getEdge());
      prevNorm = scoredEdge.getNorm();
      prevPin = scoredEdge.getEdge().getPinA() == prevPin ? 
          scoredEdge.getEdge().getPinB() : scoredEdge.getEdge().getPinA();
      
      // debug stuffs...
      if (iteration++%50 == 0) {
        curImg.writeToFile(new File("_rendering.png"));
        curImg.differenceWith(refImg)
            .multiplyWith(importanceMappingImg)
            .writeToFile(new File("_diff.png"));
      }
      long rendered = System.currentTimeMillis();
      System.out.println(String.format(
          "iteration:%d ; norm:%7.0f ; pins:%3d,%3d ; choose:%5dms (mean:%d*%.3fms) ; drawing:%5dms)",
          iteration,
          scoredEdge.getNorm(), 
          scoredEdge.getEdge().getPinA(), 
          scoredEdge.getEdge().getPinB(),
          after - before, 
          counter.get(),
          (after - before) / (float)counter.get(), 
          rendered - after));
    }
    System.out.println("end");
  }

}
