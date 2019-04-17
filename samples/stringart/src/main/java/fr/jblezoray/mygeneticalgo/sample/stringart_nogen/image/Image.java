package fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image;

import java.util.stream.IntStream;

public interface Image {

  ByteImage asByteImage();
  
  ImageSize getSize();

  /**
   * Pixel to pixel difference.
   * 
   * @param other
   * @return
   */
  default Image differenceWith(Image other) {
    ImageSize size = getSize();
    if (!size.equals(other.getSize()))
      throw new RuntimeException("Images do not have the same size.");

    byte[] thisBytes = asByteImage().getRawBytes();
    byte[] otherBytes = other.asByteImage().getRawBytes();
    byte[] diff = new byte[size.nbPixels];
    
    IntStream.range(0, size.nbPixels).parallel().forEach(i -> {
      int thisPixel = ((int) thisBytes[i]) & 0xff;
      int otherPixel = ((int) otherBytes[i]) & 0xff;
      // the darker, the better. 
      int diffPixel = thisPixel - otherPixel;
      diff[i] = (byte) (diffPixel<0 ? -diffPixel : diffPixel);
    });
    return new ByteImage(size, diff);
  }
  
  
  /**
   * Multiply each pixel of this image with the ones from 'other'.
   *    
   * @param other
   * @return a new image. 
   */
  default Image multiplyWith(Image other) {
    ImageSize size = getSize();
    if (!size.equals(other.getSize()))
      throw new RuntimeException("Images do not have the same size.");
    
    byte[] thisBytes = asByteImage().getRawBytes();
    byte[] otherBytes = other.asByteImage().getRawBytes();
    byte[] output = new byte[size.nbPixels];

    IntStream.range(0, size.nbPixels).parallel().forEach(i -> {
      int thisPixel = ((int) thisBytes[i]) & 0xff;
      int otherPixel = ((int) otherBytes[i]) & 0xff;
      output[i] = (byte)(thisPixel * otherPixel / (float)0xFF);
    });
    return new ByteImage(size, output);
  }

  
  /**
   * Compute a L2-norm of the image.
   * 
   * A L2 norm is the square root of the sum of the squared elements.   
   * 
   * TODO compute a diff on a 3*3 patch around the considered pixel.  
   * 
   * For more information about Ln-norms, see this very good introduction : 
   * https://rorasa.wordpress.com/2012/05/13/l0-norm-l1-norm-l2-norm-l-infinity-norm/
   * 
   * @param diffImage
   * @return
   */
  default double l2norm() {
    long sum = 0;
    for (byte b : this.asByteImage().getRawBytes()) {
      int unsigned = Byte.toUnsignedInt(b);
      sum += unsigned * unsigned;
    }
    return Math.sqrt(sum);
  }
  
  

}