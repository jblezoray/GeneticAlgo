package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import fr.jblezoray.mygeneticalgosample.stringart_nogen.Image.ImageSize;

public class Main {

  private static final int NUMBER_NAILS = 200;
  private static final float LINE_THICKNESS = 1.0f;
  private static final int MIN_NAILS_DIFF = NUMBER_NAILS / 10;
  
  private static final List<Edge> ALL_POSSIBLE_EDGES = new ArrayList<>();
  
  
  public static void main(String[] args) throws IOException {
    // load the reference image
    BufferedImage refImgBI = ImageIO.read(new File("samples/stringart/einstein.png"));
    Image refImg = new Image(refImgBI);
    refImg.writeToFile(new File("_refImg.png"));
    
    // load the features image 
    BufferedImage featImgBI = ImageIO.read(new File("samples/stringart/einstein_features.png"));
    Image importanceMappingImg =  new Image(featImgBI);
    importanceMappingImg.writeToFile(new File("_features.png"));
    
    // initialize an array with all the possible edges.
    ImageSize size = refImg.getSize();
    for (int i=0; i<NUMBER_NAILS; i++) {
      for (int j=i; j<NUMBER_NAILS; j++) {
        if (Math.abs(i - j) > NUMBER_NAILS / MIN_NAILS_DIFF) {
          Edge edge = new Edge(i, j, size, NUMBER_NAILS, LINE_THICKNESS);
          ALL_POSSIBLE_EDGES.add(edge);
        }
      }
    }
    
    // edges of the image to build.
    TargetEdges edges = new TargetEdges();

    // optimization algo
    double prevNorm = Float.MAX_VALUE;
    int prevPin = 0; 
    Image curImg = new Image(size);
    int iteration = 0;
    while (true) {
      long before = System.currentTimeMillis();

      // search for the edge that contributes the most to the reduction of the norm.
      AtomicInteger counter = new AtomicInteger();
      final int prevPinFinalCopy = prevPin;
      final Image curImgCpy = curImg;//.deepCopy();
      ScoredEdge scoredEdge = ALL_POSSIBLE_EDGES.stream().parallel()
          // only edges that start where the previous one finishes ; skip edges 
          // that are already in the graph
          .filter(edge -> 
              (edge.getPinA()==prevPinFinalCopy || edge.getPinB()==prevPinFinalCopy) 
              && !edges.getEdges().contains(edge))
          .peek(edge -> counter.incrementAndGet())
          // compute a resulting image, and score it. 
          .map(edge -> {
            Image targetImg = renderImage(size, curImgCpy.getBytes(), edge);
            Image diffImage = imageDiff(targetImg, refImg);
            diffImage = multiplyImportanceMapping(diffImage, importanceMappingImg);
            double norm = l2norm(diffImage);
            return new ScoredEdge(edge, norm);
          })
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
      curImg = renderImage(size, curImg.getBytes(), scoredEdge.getEdge());
      edges.getEdges().add(scoredEdge.getEdge());
      prevNorm = scoredEdge.getNorm();
      prevPin = scoredEdge.getEdge().getPinA() == prevPin ? 
          scoredEdge.getEdge().getPinB() : scoredEdge.getEdge().getPinA();
      
      // debug stuffs...
      if (iteration++%50 == 0) {
        curImg.writeToFile(new File("_rendering.png"));
        multiplyImportanceMapping(imageDiff(curImg, refImg), importanceMappingImg)
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


//  // TODO : upscaling (Ãx) + clamping C(Ãx) + downsampling to target size DC(Ãx)
//  private static Image renderImage(ImageSize size, TargetEdges edges, Edge... additionalEdges) {
//    Image img = new Image(size);
//    for (Edge e : edges.getEdges()) img = e.drawEdgeInImage(img);
//    for (Edge e : additionalEdges)  img = e.drawEdgeInImage(img);
//    return img;
//  }
  
  
  private static Image renderImage(ImageSize size, byte[] canevas, Edge... additionalEdges) {
    Image img = new Image(size, canevas);
    for (Edge e : additionalEdges)  img = e.drawEdgeInImage(img);
    return img;
  }

  
  /**
   * Each pixel the importance image describes the influence of the 
   * corresponding pixel in the reference image.  
   * 
   * A value of 0x00 implies that the pixel does not contributes to the 
   * fitness. A value of OxFF corresponds to the maximum influence possible.
   * Therefore, the brighter the zone are in the importanceMappingImage, the
   * more they represent important features of the reference image.
   *  
   * @param referenceImage
   * @param importanceMappingImage
   * @return
   */
  private static Image multiplyImportanceMapping(
      Image diffImage, 
      Image importanceMappingImage) {
    ImageSize size = diffImage.getSize();
    
    byte[] diffBytes = diffImage.getBytes();
    byte[] importanceMappingBytes = importanceMappingImage.getBytes();
    byte[] output = new byte[size.nbPixels];
    
    for (int i=0; i<output.length; i++) {
      int diffPixel = Byte.toUnsignedInt(diffBytes[i]);
      int importanceMappingPixel = Byte.toUnsignedInt(importanceMappingBytes[i]);
      output[i] = (byte)(diffPixel * importanceMappingPixel / (float)0xFF);
    }
    return new Image(size, output);
  }

  
  /** 
   * Pixel to pixel difference.
   *   
   * @param targetImage
   * @param refImg
   * @return
   */
  private static Image imageDiff(Image target, Image ref) {
    ImageSize size = target.getSize();
    if (!size.equals(ref.getSize()))
      throw new RuntimeException("Images do not have the same size.");

    byte[] refBytes = ref.getBytes();
    byte[] targetBytes = target.getBytes();
    byte[] diff = new byte[size.nbPixels];
    
    for (int i=0; i<diff.length; i++) {
      int refPixel = Byte.toUnsignedInt(refBytes[i]);
      int targetPixel = Byte.toUnsignedInt(targetBytes[i]);
      // the darker, the better. 
      diff[i] = (byte) (Math.abs(refPixel - targetPixel)); 
    }
    return new Image(size, diff);
  }
  

  /**
   * compute a L2-norm of the image.
   * 
   * A L2 norm is the square root of the sum of the squared elements.   
   * 
   * TODO compute a diff on a 3*3 patch around the considered pixel.  
   * 
   * For more information about Ln-norms, see this very good introduction : 
   * https://rorasa.wordpress.com/2012/05/13/l0-norm-l1-norm-l2-norm-l-infinity-norm/
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
