package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

public class Main {

  private static final int NUMBER_NAILS = 200;
  private static final float LINE_THICKNESS = 0.5f;
  private static final int MIN_NAILS_DIFF = NUMBER_NAILS / 10;
  
  private static List<Edge> ALL_POSSIBLE_EDGES = new ArrayList<>();
  
  
  public static void main(String[] args) throws IOException {
    // load images
//    BufferedImage refImgBI = ImageIO.read(new File("samples/stringart/match.png"));
//    BufferedImage refImgBI = ImageIO.read(new File("samples/stringart/match_bck2.png"));
    BufferedImage refImgBI = ImageIO.read(new File("samples/stringart/einstein.png"));
    Image refImg = new Image(refImgBI);
//    Image importanceMappingImg = null;
//    multiplyImportanceMapping(refImg, importanceMappingImg);
    refImg.writeToFile(new File("_refImg.png"));
    
    // initialize ALL_POSSIBLE_EDGES
    int w = refImg.getW();
    int h = refImg.getH();
    for (int i=0; i<NUMBER_NAILS; i++) {
      for (int j=0; j<NUMBER_NAILS; j++) {
        if (Math.abs(i - j) > NUMBER_NAILS / MIN_NAILS_DIFF) {
          Edge edge = new Edge(i, j, w, h, NUMBER_NAILS, LINE_THICKNESS);
          ALL_POSSIBLE_EDGES.add(edge);
        }
      }
    }
    
    // edges of the image to build.
    TargetEdges edges = TargetEdges.factory();

    // optimization algo
    double prevNorm = Float.MAX_VALUE;
    int prevPin = 0; 
    Image curImg = new Image(refImg.getW(), refImg.getH());
    while (true) {
      long before = System.currentTimeMillis();

      // search for the edge that contributes the most to the reduction of the norm.
      AtomicInteger counter = new AtomicInteger();
      final int prevPinFinalCopy = prevPin;
      final Image curImgCpy = curImg;//.deepCopy();
      ScoredEdge scoredEdge = ALL_POSSIBLE_EDGES.stream().parallel()
          // only edges that start where the previous one finishes ; skip edges 
          // that are already in the graph
          .filter(edge -> edge.getPinA()==prevPinFinalCopy 
              && !edges.getEdges().contains(edge))
          .peek(edge -> counter.incrementAndGet())
          // compute a resulting image, and score it. 
          .map(edge -> {
            Image targetImg = renderImage(refImg.getW(), refImg.getH(), 
                curImgCpy.getBytes(), edge);
            Image diffImage = imageDiff(targetImg, refImg);
            double norm = l2norm(diffImage);
            return new ScoredEdge(edge, norm);
          })
          .min((a, b) -> a.getNorm() < b.getNorm()? -1 : 1)
          .orElseThrow(() -> new RuntimeException());
      long after = System.currentTimeMillis();

      // find the edge that, if removed, contributes the most to the reduction 
      // of the norm. 
      // TODO 

      // stop if we reached an optimal form the best result
      if (scoredEdge.getNorm()>prevNorm) {
        break;
      }

      // save image !
      curImg = renderImage(refImg.getW(), refImg.getH(), curImg.getBytes(), 
          scoredEdge.getEdge());
      edges.getEdges().add(scoredEdge.getEdge());
      prevNorm = scoredEdge.getNorm();
      prevPin = scoredEdge.getEdge().getPinB();
      
      // debug stuffs...
      curImg.writeToFile(new File("_rendering.png"));
      imageDiff(curImg, refImg).writeToFile(new File("_diff.png"));;
      long rendered = System.currentTimeMillis();
      System.out.println(String.format(
          "iteration:%d ; norm:%7.0f ; pins:%3d,%3d ; choose:%5dms (mean:%d*%.3fms) ; drawing:%5dms)",
          edges.getEdges().size(),
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




  // TODO : upscaling (Ãx) + clamping C(Ãx) + downsampling to target size DC(Ãx)
  private static Image renderImage(int w, int h, TargetEdges edges, Edge... additionalEdges) {
    Image img = new Image(w, h);
    for (Edge e : edges.getEdges()) img = e.drawEdgeInImage(img);
    for (Edge e : additionalEdges)  img = e.drawEdgeInImage(img);
    return img;
  }
  private static Image renderImage(int w, int h, byte[] canevas, Edge... additionalEdges) {
    Image img = new Image(w, h, canevas);
    for (Edge e : additionalEdges)  img = e.drawEdgeInImage(img);
    return img;
  }

  
  private static Image multiplyImportanceMapping(
      Image referenceImage, 
      Image importanceMappingImage) {
    // TODO 
    return null;
  }

  
  /** 
   * Pixel to pixel difference.
   *   
   * @param targetImage
   * @param refImg
   * @return
   */
  private static Image imageDiff(Image target, Image ref) {
    if (target.getH()!=ref.getH() || target.getW()!=ref.getW())
      throw new RuntimeException("Images do not have the same size.");

    byte[] refBytes = ref.getBytes();
    byte[] targetBytes = target.getBytes();
    byte[] diff = new byte[ref.getH()*ref.getW()];
    
    for (int i=0; i<diff.length; i++) {
      int refPixel = Byte.toUnsignedInt(refBytes[i]);
      int targetPixel = Byte.toUnsignedInt(targetBytes[i]);
      // the darker, the better. 
      diff[i] = (byte) (Math.abs(refPixel - targetPixel)); 
    }
    return new Image(target.getW(), target.getH(), diff);
  }
  

  /**
   * compute a L2-norm of the image.
   * 
   * A L2 norm is the square root of the sum of the squared elements.   
   * 
   * @param diffImage
   * @return
   */
  private static double l2norm(Image diffImage) {
    long sum = 0;
    for (byte b : diffImage.getBytes()) {
      int unsigned = Byte.toUnsignedInt(b);
      sum += unsigned * unsigned;
    }
    return Math.sqrt(sum);
  }

}
