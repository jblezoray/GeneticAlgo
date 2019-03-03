package fr.jblezoray.mygeneticalgo.dna.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import fr.jblezoray.mygeneticalgo.IFitness;

/**
 * Compares two images on the basis of a patch to value comparison.
 * 
 * @author jib
 *
 */
public class FitnessPatch<X extends AbstractImageDNA> 
implements IFitness<X> {

  private final BufferedImage image;
  private final int patchSize;
  
  
  public FitnessPatch(int patchSize, AbstractImageDNA reference) {
    this.patchSize = patchSize;
    this.image = reference.getImage();
    if (this.image.getType() != BufferedImage.TYPE_3BYTE_BGR)
      throw new RuntimeException("invalid image type : " + this.image.getType());
  }
  
  @Override
  public double computeFitnessOf(X candidateToEvaluate) {
    int[] patchDiff = patchDiff(candidateToEvaluate.getImage(), this.patchSize);
    
    double sum = 0;
    for (int n=0; n<patchDiff.length; n++)
      sum += patchDiff[n];
    double result = sum / patchDiff.length;
    return 100 / result;
  }
  
  
  private int[] patchDiff(BufferedImage other, int patchSize) {
    
    byte[] pixelsThis = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    byte[] pixelsOther = ((DataBufferByte) other.getRaster().getDataBuffer()).getData();

    int pixelLen = 3;
    int halfPatchSize = patchSize / 2 - (patchSize-1) % 2;
    int w = image.getWidth();
    int h = image.getHeight();
    
    int[] patchDiff = new int[pixelsThis.length];

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

        // fill result array.
        patchDiff[index] = sumB;
        patchDiff[index+1] = sumG;
        patchDiff[index+2] = sumR;
      }
    }
    
    return patchDiff;
  }

}
