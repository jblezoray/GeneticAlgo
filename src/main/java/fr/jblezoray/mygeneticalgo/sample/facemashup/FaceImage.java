package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Image abstraction for the {@code FaceMashupGenerator}.
 * 
 * @author jib
 */
public class FaceImage {

  private final BufferedImage image;

  public FaceImage(File f) throws IOException {
    this(ImageIO.read(f));
  }
  
  public FaceImage(BufferedImage source) {
    BufferedImage sourceARGB = new BufferedImage(
        source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
    sourceARGB.getGraphics().drawImage(source, 0, 0, null);
    sourceARGB.getGraphics().dispose();
    this.image = sourceARGB;
  }
  
  public BufferedImage getImage() {
    return this.image;
  }
  
  public void writeToFile(File dest) throws IOException {
    ImageIO.write(image, "png", dest);
  }

  /**
   * Computes the fitness of {@code toEvaluate} FaceImage relatively to this
   * one. 
   * @param candidateToEvaluate
   * @return an evaluation note in range [0.0f, 1.0f] 
   */
  public double computeFitnessOf(FaceImage candidateToEvaluate) {
    return simpleImageSimilarity(candidateToEvaluate.getImage());
  }
  
  /*
   * Calculates the difference between two images
   * Uses root mean squared analysis
   * If specified, mask is used for the histogram
   */
  private double simpleImageSimilarity(BufferedImage candidateToEvaluate) {
      final int dimension = this.image.getWidth() * this.image.getHeight();
      BufferedImage difference = imageAbsoluteDifference(this.image, candidateToEvaluate);

      // Create histogram with a mask if image is RGBA
      int[] histogram = imageHistogram(difference);
      double sumSquaredValues = 0;
      for(int n = 0; n < histogram.length; n++) {
          double square = n*n;
          sumSquaredValues += (square) * histogram[n];
      }
      double rms = Math.sqrt(sumSquaredValues / dimension);
      return 100 / rms;
  }
  

  // TODO Change setRGB and getRGB, it is now too slow :(
  // @see http://stackoverflow.com/questions/8218072/faster-way-to-extract-histogram-from-an-image
  public static BufferedImage imageAbsoluteDifference(BufferedImage img1, BufferedImage img2) {
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

              int diff =
                      (aDiff << 24) | (rDiff << 16) | (gDiff << 8) | bDiff;
              result.setRGB(x, y, diff);
          }
      }
      return result;
  }

  // Return an ArrayList containing histogram values for separate R, G, B channels
  public static int[] imageHistogram(BufferedImage input) {
      int[] rhistogram = new int[256];
      int[] ghistogram = new int[256];
      int[] bhistogram = new int[256];

      for(int i=0; i<rhistogram.length; i++)
          rhistogram[i] = ghistogram[i] = bhistogram[i] = 0;

      for(int j=0; j<input.getHeight(); j++) {
          for(int i=0; i<input.getWidth(); i++) {
              int rgb = input.getRGB(i, j);

              int r = (rgb >> 16) & 0xFF;
              int g = (rgb >>  8) & 0xFF;
              int b = (rgb      ) & 0xFF;

              // Increase the values of colors
              rhistogram[r]++; ghistogram[g]++; bhistogram[b]++;
          }
      }
      return joinArray(rhistogram, ghistogram, bhistogram);
  }


  /*
   * This method merges any number of arrays of any count.
   */
  private static int[] joinArray(int[]... arrays) {
      int length = 0;
      for (int[] array : arrays) {
          length += array.length;
      }
      final int[] result = new int[length];
      int offset = 0;
      for (int[] array : arrays) {
          System.arraycopy(array, 0, result, offset, array.length);
          offset += array.length;
      }
      return result;
  }
  

}
