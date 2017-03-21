package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Uses RMS (Root Mean Square) analysis to computes the fitness of an Image 
 * relatively the image it was constructed with.
 * 
 * @author jib
 *
 */
public class FitnessHistogramRMSFast implements IFitness {
  
  private BufferedImage image;
  
  private FitnessHistogramRMSFast(FaceImage original){
    this.image = original.getImage();
    if (this.image.getType() != BufferedImage.TYPE_3BYTE_BGR)
      throw new RuntimeException("invalid image type : " + this.image.getType());
  }
  
  public static IFitness build(FaceImage reference) {
    return new FitnessHistogramRMSFast(reference);
  }

  @Override
  public double computeFitnessOf(FaceImage candidateToEvaluate) {
    if (this.image.getType() != BufferedImage.TYPE_3BYTE_BGR)
      throw new RuntimeException("invalid image type : " + this.image.getType());
    
    int[] histogram = diffHistogram(candidateToEvaluate.getImage());
    
    double sumSquaredValues = 0;
    for (int n=0; n<histogram.length; n++)
      sumSquaredValues += n * n * histogram[n];
    double rms = Math.sqrt(sumSquaredValues / (this.image.getWidth() * this.image.getHeight()));
    return 100 / rms;
  }
  
  
  /**
   * Computes an histogram of the differences between this image and an other 
   * one. 
   * @param other
   * @return
   *    An histogram for separate R, G, B channels.
   */
  private int[] diffHistogram(BufferedImage other) {
  
    // Grab raw data. Don't use getRGB(), it's performance is crappy (See 
    // http://stackoverflow.com/a/9470843/2082935)
    byte[] pixelsThis = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    byte[] pixelsOther = ((DataBufferByte) other.getRaster().getDataBuffer()).getData();
  
    // TODO assert that pixelsThis and pixelsOther are the same size.
    
    int[] rgbHistogram = new int[256*3];
    for (int pixel=0; pixel<pixelsThis.length; pixel+=3) {
      
      byte thisB = pixelsThis[pixel];
      byte thisG = pixelsThis[pixel+1];
      byte thisR = pixelsThis[pixel+2];
  
      byte otherB = pixelsOther[pixel];
      byte otherG = pixelsOther[pixel+1];
      byte otherR = pixelsOther[pixel+2];
  
      int diffB = Math.abs(thisB - otherB);
      int diffG = Math.abs(thisG - otherG);
      int diffR = Math.abs(thisR - otherR);
      
      rgbHistogram[diffB*3]++; 
      rgbHistogram[1+diffG*3]++; 
      rgbHistogram[2+diffR*3]++;
    }
    
    return rgbHistogram;
  }
  
}
