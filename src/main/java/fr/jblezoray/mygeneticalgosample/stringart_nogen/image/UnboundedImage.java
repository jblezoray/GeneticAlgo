package fr.jblezoray.mygeneticalgosample.stringart_nogen.image;

import java.util.Arrays;

/**
 * We here use integer to represent bytes, in order to be able to reverse 
 * anything that was printed simply by reversing the addition of the element.  
 */
public class UnboundedImage implements Image {

  private final int[] unboundedBytes;
  private final ImageSize size;
  private ByteImage clampedCopy;

  /** 
   * Blank image constructor (filed with 0xFF).  
   * @param size
   */
  public UnboundedImage(ImageSize size) {
    this.size = size;
    this.unboundedBytes = new int[this.size.nbPixels];
    Arrays.fill(this.unboundedBytes, 0xFF);
  }
  
  private UnboundedImage(ImageSize size, int[] bytes) {
    this.size = size;
    this.unboundedBytes = bytes;
  }
  
  /**
   * Constructs a deep copy.
   * @return
   */
  public UnboundedImage deepCopy() {
    return new UnboundedImage(
        this.size, 
        Arrays.copyOf(this.unboundedBytes, this.unboundedBytes.length));
  }
  

  public int[] getUnboundedBytes() {
    this.clampedCopy = null;
    return this.unboundedBytes;
  }
  
  /** 
   * Clamps all unbounded ints in [0x00, 0xFF], and build a copy from it. 
   */
  @Override
  public ByteImage asByteImage() {
    if (clampedCopy==null) {
      synchronized (this) {
        if (clampedCopy==null) {
          byte[] clampedBytes = new byte[this.size.nbPixels];
          for(int i=0; i<this.unboundedBytes.length; i++) {
            clampedBytes[i] = (byte)(0x00>this.unboundedBytes[i] ? 0x00 : 
                this.unboundedBytes[i]>0xFF ? 0xFF : this.unboundedBytes[i]);
          }
          this.clampedCopy = new ByteImage(size, clampedBytes);
        }
      }
    }
    return clampedCopy;
  }

  @Override
  public ImageSize getSize() {
    return this.size;
  }

}
