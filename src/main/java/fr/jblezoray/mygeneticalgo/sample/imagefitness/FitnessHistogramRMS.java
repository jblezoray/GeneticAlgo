package fr.jblezoray.mygeneticalgo.sample.imagefitness;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Uses a RMS (Root Mean Square) analysis of the histogram of the image, to 
 * computes its fitness relatively the image it was constructed with.
 * 
 * The histogram contains a value to value comparison.
 * 
 * @author jib
 *
 */
public class FitnessHistogramRMS implements IFitness {
  
  private BufferedImage image;
  
  @Override
  public void init(FitableImage reference) {
    this.image = reference.getImage();
    if (this.image.getType() != BufferedImage.TYPE_3BYTE_BGR)
      throw new RuntimeException("invalid image type : " + this.image.getType());
  }

  @Override
  public double computeFitnessOf(FitableImage candidateToEvaluate) {
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
  
    int[] rgbHistogram = new int[256*3];
    for (int pixel=0; pixel<pixelsThis.length; pixel+=3) {
      
      // the bitmask normalize the value in [0, 255], hence the int.
      int thisB = pixelsThis[pixel]   & 0xFF;
      int thisG = pixelsThis[pixel+1] & 0xFF;
      int thisR = pixelsThis[pixel+2] & 0xFF;
  
      int otherB = pixelsOther[pixel]   & 0xFF;
      int otherG = pixelsOther[pixel+1] & 0xFF;
      int otherR = pixelsOther[pixel+2] & 0xFF;
  
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
