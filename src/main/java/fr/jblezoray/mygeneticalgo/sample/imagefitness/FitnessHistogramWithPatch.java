package fr.jblezoray.mygeneticalgo.sample.imagefitness;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Uses a RMS (Root Mean Square) analysis of the histogram of the image, to 
 * computes its fitness relatively the image it was constructed with.
 * 
 * The histogram contains a patch to value comparison.
 * 
 * @author jib
 *
 */
public class FitnessHistogramWithPatch implements IFitness {

  private BufferedImage image;
  private int patchSize;
  
  public FitnessHistogramWithPatch(int patchSize){
    this.patchSize = patchSize;
  }

  @Override
  public void init(AbstractFitableImage reference) {
    this.image = reference.getImage();
    if (this.image.getType() != BufferedImage.TYPE_3BYTE_BGR)
      throw new RuntimeException("invalid image type : " + this.image.getType());
  }
  
  @Override
  public double computeFitnessOf(AbstractFitableImage candidateToEvaluate) {
    int[] histogram = patchDiffHistogram(candidateToEvaluate.getImage(), this.patchSize);
    
    double sumSquaredValues = 0;
    for (int n=0; n<histogram.length; n++)
      sumSquaredValues += n * n * histogram[n];
    double rms = Math.sqrt(sumSquaredValues / (this.image.getWidth() * this.image.getHeight()));
    return 100 / rms;
  }
  
  
  private int[] patchDiffHistogram(BufferedImage other, int patchSize) {
    
    byte[] pixelsThis = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    byte[] pixelsOther = ((DataBufferByte) other.getRaster().getDataBuffer()).getData();

    int pixelLen = 3;
    int halfPatchSize = patchSize / 2 - (patchSize-1) % 2;
    int w = image.getWidth();
    int h = image.getHeight();
    
    int[] rgbHistogram = new int[256*3];

    for (int y=halfPatchSize; y<h-halfPatchSize; y++) {
      for (int x=halfPatchSize; x<w-halfPatchSize; x++) {
        
        int index = y*w*pixelLen + x*pixelLen;
        int otherB = pixelsOther[index]   & 0xFF;
        int otherG = pixelsOther[index+1] & 0xFF;
        int otherR = pixelsOther[index+2] & 0xFF;
        
        // compare with the patchSize*patchSize square around the point at [x,y]
        int sumB = 0;
        int sumG = 0;
        int sumR = 0;
        for (int py=y-halfPatchSize; py<=y+halfPatchSize; py++) {
          for (int px=x-halfPatchSize; px<=x+halfPatchSize; px++) {
            int pIndex = py*w*pixelLen + px*pixelLen;
            
            int thisB = pixelsThis[pIndex]   & 0xFF;
            int thisG = pixelsThis[pIndex+1] & 0xFF;
            int thisR = pixelsThis[pIndex+2] & 0xFF;

            int diffB = Math.abs(thisB - otherB);
            int diffG = Math.abs(thisG - otherG);
            int diffR = Math.abs(thisR - otherR);
              
            sumB += diffB;
            sumG += diffG;
            sumR += diffR;
          }
        }
        // normalize to [0,255]
        sumB = sumB / (patchSize*patchSize);
        sumG = sumG / (patchSize*patchSize);
        sumR = sumR / (patchSize*patchSize);

        // fill histogram.
        rgbHistogram[sumB*3]++; 
        rgbHistogram[1+sumG*3]++; 
        rgbHistogram[2+sumR*3]++;
      }
    }
    
    return rgbHistogram;
  }

}
