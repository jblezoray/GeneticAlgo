package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.util.Arrays;

/**
 * We here use integer to represent bytes, in order to be able to reverse 
 * anything that was printed simply by reversing the addition of the element.  
 */
public class UnboundedImage implements Image {

  private final int[] bytes;
  
  private final ImageSize size;

  /** 
   * Blank image constructor (filed with 0xFF).  
   * @param size
   */
  public UnboundedImage(ImageSize size) {
    this.size = size;
    this.bytes = new int[this.size.nbPixels];
    Arrays.fill(this.bytes, 0xFF);
  }
  
  /**
   * Deep copy constructor.
   * @param image
   */
  public UnboundedImage(UnboundedImage image) {
    this.size = image.size;
    this.bytes = Arrays.copyOf(image.bytes, image.bytes.length);
  }
  

  public int[] getUnboundedBytes() {
    return this.bytes;
  }
  
  /** 
   * Clamps all unbounded ints in [0x00, 0xFF]. 
   */
  @Override
  public byte[] getBytes() {
    byte[] clampedBytes = new byte[this.size.nbPixels];
    for(int i=0; i<this.bytes.length; i++)
      clampedBytes[i] = (byte)(0x00>this.bytes[i] ? 0x00 : 
          this.bytes[i]>0xFF ? 0xFF : this.bytes[i]);;
    return clampedBytes;
  }

  @Override
  public ImageSize getSize() {
    return this.size;
  }

}
