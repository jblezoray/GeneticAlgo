package fr.jblezoray.mygeneticalgo.dna.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;
import java.util.stream.IntStream;

import fr.jblezoray.mygeneticalgo.IFitness;

/**
 * Uses a RMS (Root Mean Square) analysis of the histogram of the image to 
 * compute its fitness relatively the image it was constructed with.
 * 
 * @author jib
 *
 */
public class FitnessRMSWithWeight<X extends AbstractImageDNA> 
implements IFitness<X> {
  
  private static int MIN_WEIGHT = 0x66;
  
  private final BufferedImage image;
  private final int[] weigth;

  
  public FitnessRMSWithWeight(AbstractImageDNA original){
    this(original, original.getImage().getWidth() / 10);
  }
  
  
  public FitnessRMSWithWeight(AbstractImageDNA original, int patchSize){
    this.image = original.getImage();
    if (this.image.getType() != BufferedImage.TYPE_3BYTE_BGR)
      throw new RuntimeException("invalid image type : " + this.image.getType());
    
    this.weigth = initPatchBasedWeight(this.image, patchSize);
  }
  
  
  public FitnessRMSWithWeight(AbstractImageDNA original, AbstractImageDNA weights){
    this.image = original.getImage();
    if (this.image.getType() != BufferedImage.TYPE_3BYTE_BGR)
      throw new RuntimeException("invalid image type : " + this.image.getType());
    
    BufferedImage weightsImage = weights.getImage();
    if (weightsImage.getType() != BufferedImage.TYPE_3BYTE_BGR)
      throw new RuntimeException("invalid weights image type : " + weightsImage.getType());
    
    this.weigth = initImageBasedWeight(weightsImage);
  }


  
  private int[] initImageBasedWeight(BufferedImage weightsImage) {
    byte[] pixelsThis = ((DataBufferByte) weightsImage.getRaster().getDataBuffer()).getData();
    int[] weigths = new int[pixelsThis.length];
    for (int i=0; i<pixelsThis.length; i++)
      weigths[i] = pixelsThis[i] & 0xFF;
    return weigths;
  }


  /**
   * Creates a weight array from this pic.
   * 
   * A weight is computed from each pixel, where a value nearer of 1 is more
   * important, and a value nearer MIN_WEIGHT is less important.  
   * 
   * It is based on an evaluation of the variations in a n*n patch around
   * each pixel : 
   * Weight(i) = (1/n^2) * Sum( j in Patch(i) ) | image(i) - image(j) |
   * 
   * @return
   *    A weight array with values in in [MIN_WEIGHT, 0xFF].
   */
  private static int[] initPatchBasedWeight(BufferedImage image, int patchSize) {
    
    byte[] pixelsThis = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

    int pixelLen = 3;
    int halfPatchSize = patchSize / 2 - (patchSize-1) % 2;
    int w = image.getWidth();
    int h = image.getHeight();

    int[] weigth = new int[pixelsThis.length];
    Arrays.fill(weigth, MIN_WEIGHT);
    // don't start at 0 nor end at h or w, otherwise the patch do overlap the 
    // border of the image.
    for (int y=halfPatchSize; y<h-halfPatchSize; y++) {
      for (int x=halfPatchSize; x<w-halfPatchSize; x++) {
        int index = pixelLen*(y*w + x);
        int thisB = pixelsThis[index]   & 0xFF;
        int thisG = pixelsThis[index+1] & 0xFF;
        int thisR = pixelsThis[index+2] & 0xFF;
        
        // compare with the patchSize*patchSize square around the point at [x,y]
        int weightB = 0;
        int weightG = 0;
        int weightR = 0; 
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
        weigth[index] = weightB / (pixelLen*pixelLen);
        weigth[index+1] = weightG / (pixelLen*pixelLen);
        weigth[index+2] = weightR / (pixelLen*pixelLen);
      }
    }

    // normalize values in [MIN_WEIGHT, 0xFF]
    int max = IntStream.range(0, weigth.length).map(i -> weigth[i]).max().getAsInt();
    for (int i=0; i<weigth.length; i++) {
      weigth[i] = MIN_WEIGHT + weigth[i] * (0xFF-MIN_WEIGHT) / max;
      if (weigth[i]>0xFF || weigth[i]<0) {
        throw new RuntimeException("invalid weigth : " + weigth[i]);
      }
    }
    
    return weigth;
  }
  
  @Override
  public double computeFitnessOf(X candidateToEvaluate) {
    if (this.image.getType() != BufferedImage.TYPE_3BYTE_BGR)
      throw new RuntimeException("invalid candidate to evaluate image type : " + this.image.getType());
    
    int[] diff = diff(candidateToEvaluate.getImage());
    
    long sumSquaredValues = 0;
    for (int n=0; n<diff.length; n++)
      sumSquaredValues += (long)n * (long)n * (long)diff[n];
    double rms = Math.sqrt((double)sumSquaredValues / 
        (this.image.getWidth() * this.image.getHeight()));
    return 10000.0 / rms;
  }
  
  
  public AbstractImageDNA getWeightAsImage() {
    byte[] bgr = new byte[this.weigth.length];
    for (int i=0; i<this.weigth.length; i++) {
      bgr[i] = (byte)this.weigth[i];
    }
    return new UnmodifiableImageDNA(bgr, this.image.getWidth(), 
        this.image.getHeight());
  }

  public AbstractImageDNA getWeightedOriginalImage() {
    byte[] originalImage = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    byte[] bgr = new byte[this.weigth.length];
    for (int i=0; i<this.weigth.length; i++) {
      int pixWeight = this.weigth[i]& 0xFF;
      int pix = originalImage[i] & 0xFF;
      bgr[i] = (byte) ((pixWeight * pix) / 0xFF);
    }
    return new UnmodifiableImageDNA(bgr, this.image.getWidth(), 
        this.image.getHeight());
  }


  
  /**
   * Computes the differences between this image and an other one. 
   * @param other
   * @return
   *    An histogram for separate R, G, B channels.
   */
  private int[] diff(BufferedImage other) {
    byte[] pixelsThis = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    byte[] pixelsOther = ((DataBufferByte) other.getRaster().getDataBuffer()).getData();
    int[] rgbdiff = new int[pixelsThis.length];
    for (int i=0; i<pixelsThis.length; i++) {
      int weight = this.weigth[i];
      int thisPixel = pixelsThis[i]   & 0xFF;
      int otherPixel = pixelsOther[i]   & 0xFF;
      rgbdiff[i] = weight * Math.abs(thisPixel - otherPixel) / 0xFF; 
    }
    
    return rgbdiff;
  }

  
}
