package fr.jblezoray.mygeneticalgo.sample.imagefitness;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Uses a RMS (Root Mean Square) analysis of the histogram of the image to 
 * compute its fitness relatively the image it was constructed with.
 * 
 * @author jib
 *
 */
public class FitnessHistogramRMSWithWeight implements IFitness {
  
  private static float MIN_WEIGHT = 0.4f;
  
  private int patchSize;
  private BufferedImage image;
  private float[] weigth = null;

  
  @Override
  public void init(FitableImage original){
    this.image = original.getImage();
    if (this.image.getType() != BufferedImage.TYPE_3BYTE_BGR)
      throw new RuntimeException("invalid image type : " + this.image.getType());
    this.patchSize = this.image.getWidth() / 20; 
  }

  @Override
  public double computeFitnessOf(FitableImage candidateToEvaluate) {
    if (this.image.getType() != BufferedImage.TYPE_3BYTE_BGR)
      throw new RuntimeException("invalid image type : " + this.image.getType());
    
    int[] histogram = diffHistogram(candidateToEvaluate.getImage());
    
    double sumSquaredValues = 0;
    for (int n=0; n<histogram.length; n++)
      sumSquaredValues += n * n * histogram[n];
    double rms = Math.sqrt(sumSquaredValues / 
        (this.image.getWidth() * this.image.getHeight()));
    return 100 / rms;
  }
  
  
  public FitableImage getWeightAsImage() {
    byte[] bgr = new byte[this.weigth.length];
    for (int i=0; i<this.weigth.length; i++) {
      int byteAsInt = (int)(this.weigth[i] * 256.0f);
      bgr[i] = (byte)byteAsInt;
    }
    return new FitableImage(bgr, this.image.getWidth(), this.image.getHeight());
  }
  
  
  /**
   * Computes an histogram of the differences between this image and an other 
   * one. 
   * @param other
   * @return
   *    An histogram for separate R, G, B channels.
   */
  private int[] diffHistogram(BufferedImage other) {
  
    synchronized (this) {
      if (this.weigth==null) initWeight(); 
    }
    byte[] pixelsThis = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    byte[] pixelsOther = ((DataBufferByte) other.getRaster().getDataBuffer()).getData();
    
    int[] rgbHistogram = new int[256*3];
    for (int pixel=0; pixel<pixelsThis.length; pixel+=3) {
      
      float weightB = this.weigth[pixel];
      float weightG = this.weigth[pixel+1];
      float weightR = this.weigth[pixel+2];
      
      int thisB = pixelsThis[pixel]   & 0xFF;
      int thisG = pixelsThis[pixel+1] & 0xFF;
      int thisR = pixelsThis[pixel+2] & 0xFF;
  
      int otherB = pixelsOther[pixel]   & 0xFF;
      int otherG = pixelsOther[pixel+1] & 0xFF;
      int otherR = pixelsOther[pixel+2] & 0xFF;
  
      int diffB = (int) (weightB * Math.abs(thisB - otherB));
      int diffG = (int) (weightG * Math.abs(thisG - otherG));
      int diffR = (int) (weightR * Math.abs(thisR - otherR));
      
      rgbHistogram[diffB*3]++; 
      rgbHistogram[1+diffG*3]++; 
      rgbHistogram[2+diffR*3]++;
    }
    
    return rgbHistogram;
  }

  
  /**
   * Creates a weight array from this pic.
   * 
   * A weight is computed from each pixel, where a value nearer of 1 is more
   * important, and a value nearer MIN_WEIGHT is less important.  
   * 
   * It is based on an an evaluation of the variations in a n*n patch around
   * each pixel : 
   * Weight(i) = (1/n^2) * Sum( j in Patch(i) ) | image(i) - image(j) |
   * 
   * @return
   *    A weight array with values in in [MIN_WEIGHT, 1.0f].
   */
  public float[] initWeight() {
    byte[] pixelsThis = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

    int pixelLen = 3;
    int halfPatchSize = patchSize / 2 - (patchSize-1) % 2;
    int w = image.getWidth();
    int h = image.getHeight();

    this.weigth = new float[pixelsThis.length];
    Arrays.fill(this.weigth, MIN_WEIGHT);
    // don't start at 0 nor end at h or w, other wise the patch do overlap the 
    // border of the image.
    for (int y=halfPatchSize; y<h-halfPatchSize; y++) {
      for (int x=halfPatchSize; x<w-halfPatchSize; x++) {
        int index = pixelLen*(y*w + x);
        int thisB = pixelsThis[index]   & 0xFF;
        int thisG = pixelsThis[index+1] & 0xFF;
        int thisR = pixelsThis[index+2] & 0xFF;
        
        // compare with the patchSize*patchSize square around the point at [x,y]
        float weightB = 0.0f;
        float weightG = 0.0f;
        float weightR = 0.0f; 
        for (int py=y-halfPatchSize; py<=y+halfPatchSize; py++) {
          for (int px=x-halfPatchSize; px<=x+halfPatchSize; px++) {
            int pIndex = py*w*pixelLen + px*pixelLen;
            int patchB = pixelsThis[pIndex] & 0xFF;
            int patchG = pixelsThis[pIndex+1] & 0xFF;
            int patchR = pixelsThis[pIndex+2] & 0xFF;

            weightB += Math.abs(thisB - patchB);
            weightG += Math.abs(thisG - patchG);
            weightR += Math.abs(thisR - patchR);
          }
        }
        
        // fill result array.
        this.weigth[index] = weightB;
        this.weigth[index+1] = weightG;
        this.weigth[index+2] = weightR;
      }
    }

    // normalize values in [MIN_WEIGHT, 1.0f]
    float max = (float) IntStream.range(0, this.weigth.length)
        .mapToDouble(i -> this.weigth[i]).max().getAsDouble();
    for (int i=0; i<this.weigth.length; i++) {
      this.weigth[i] = MIN_WEIGHT + this.weigth[i] * (1.0f-MIN_WEIGHT) / max;
      if (this.weigth[i]>1.0f ||this.weigth[i]<0.0f) {
        System.out.println("houston");
      }
    }
    
    return this.weigth;
  }
  
}
