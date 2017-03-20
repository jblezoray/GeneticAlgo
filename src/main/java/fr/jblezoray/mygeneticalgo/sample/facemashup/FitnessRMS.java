package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Uses RMS (Root Mean Square) analysis to computes the fitness of an Image 
 * relatively the image it was constructed with.
 * 
 * @author jib
 *
 */
public class FitnessRMS implements IFitness {
  
  private BufferedImage image;
  
  private FitnessRMS(FaceImage original){
    this.image = original.getImage();
    if (this.image.getType() != BufferedImage.TYPE_INT_ARGB)
      throw new RuntimeException("invalid image type : " + this.image.getType());
  }
  
  public static IFitness build(FaceImage reference) {
    return new FitnessRMS(reference);
  }

  @Override
  public double computeFitnessOf(FaceImage candidateToEvaluate) {
    return simpleImageSimilarity(candidateToEvaluate.getImage());
  }
  
  /**
   * Calculates the difference between two images.
   * 
   * Uses root mean squared analysis.
   * See https://en.wikipedia.org/wiki/Root_mean_square
   * 
   * @param candidateToEvaluate
   * @return
   */
  private double simpleImageSimilarity(BufferedImage candidateToEvaluate) {
    if (this.image.getType() != BufferedImage.TYPE_INT_ARGB)
      throw new RuntimeException("invalid image type : " + this.image.getType());
    final int dimension = this.image.getWidth() * this.image.getHeight();
    BufferedImage difference = imageAbsoluteDifference(this.image, candidateToEvaluate);
    int[] histogram = imageHistogram(difference);
    double sumSquaredValues = 0;
    for(int n = 0; n < histogram.length; n++) 
      sumSquaredValues += n * n * histogram[n];
    double rms = Math.sqrt(sumSquaredValues / dimension);
    
    return 100 / rms;
  }
  

  /**
   * @see http://stackoverflow.com/questions/8218072/faster-way-to-extract-histogram-from-an-image
   * TODO Change setRGB and getRGB, it is now too slow :(
   * 
   * @param img1
   * @param img2
   * @return
   */
  private static BufferedImage imageAbsoluteDifference(BufferedImage img1, BufferedImage img2) {
    BufferedImage result = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);
    int width1 = img1.getWidth();
    int width2 = img2.getWidth();
    int height1 = img1.getHeight();
    int height2 = img2.getHeight();
    if ((width1 != width2) || (height1 != height2)) {
      System.err.println("Error: Images dimensions are not same");
    }
    for (int y = 0; y < height1; y++) {
      for (int x = 0; x < width1; x++) {
        int argb0 = img1.getRGB(x, y);
        int argb1 = img2.getRGB(x, y);

        int a0 = (argb0 >> 24) & 0xFF;
        int r0 = (argb0 >> 16) & 0xFF;
        int g0 = (argb0 >>  8) & 0xFF;
        int b0 = (argb0      ) & 0xFF;

        int a1 = (argb1 >> 24) & 0xFF;
        int r1 = (argb1 >> 16) & 0xFF;
        int g1 = (argb1 >>  8) & 0xFF;
        int b1 = (argb1      ) & 0xFF;

        int aDiff = Math.abs(a1 - a0);
        int rDiff = Math.abs(r1 - r0);
        int gDiff = Math.abs(g1 - g0);
        int bDiff = Math.abs(b1 - b0);

        int diff = (aDiff << 24) | (rDiff << 16) | (gDiff << 8) | bDiff;
        result.setRGB(x, y, diff);
      }
    }
    return result;
  }

  
  /**
   * Return an ArrayList containing histogram values for separate R, G, B channels
   * 
   * @param input
   * @return
   */
  private static int[] imageHistogram(BufferedImage input) {
    int[] rhistogram = new int[256];
    int[] ghistogram = new int[256];
    int[] bhistogram = new int[256];
    Arrays.fill(rhistogram, 0);
    Arrays.fill(ghistogram, 0);
    Arrays.fill(bhistogram, 0);

    for(int j=0; j<input.getHeight(); j++) {
      for(int i=0; i<input.getWidth(); i++) {
        int rgb = input.getRGB(i, j);

        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >>  8) & 0xFF;
        int b = (rgb      ) & 0xFF;

        // Increase the values of colors
        rhistogram[r]++; 
        ghistogram[g]++; 
        bhistogram[b]++;
      }
    }
    return joinArray(rhistogram, ghistogram, bhistogram);
  }


  /**
   * This method merges any number of arrays of any count.
   * 
   * See http://stackoverflow.com/a/784842/2082935
   */
  private static int[] joinArray(int[]... arrays) {
    int length = 0;
    for (int[] array : arrays)
      length += array.length;
    
    final int[] result = new int[length];
    int offset = 0;
    for (int[] array : arrays) {
      System.arraycopy(array, 0, result, offset, array.length);
      offset += array.length;
    }
    return result;
  }
  
  
}
