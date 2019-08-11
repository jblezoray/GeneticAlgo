package fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed;

import java.util.stream.IntStream;

import fr.jblezoray.mygeneticalgo.sample.stringart.image.ByteImage;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ImageSize;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;


public class CompressedUnboundedImage implements Image {

  private byte[] compressed;
  private ImageSize size;
  private int uncompressedBytesLength;
  
  public CompressedUnboundedImage(UnboundedImage img) {
    this.size = img.getSize();
    compress(img.intStream());
  }
  
  /**
   * for tests only.
   * @param size
   * @param compressed
   * @param uncompressedBytesLength
   */
  CompressedUnboundedImage(
      ImageSize size, byte[] compressed, int uncompressedBytesLength) {
    this.size = size;
    this.compressed = compressed;
    this.uncompressedBytesLength = uncompressedBytesLength;
  }

  public UnboundedImage decompress() {
    int[] decompressed = new int[this.uncompressedBytesLength];
    new Decompressor().decompress(this.compressed, decompressed);
    return new UnboundedImage(size, decompressed);
  }

  @Override
  public ByteImage asByteImage() {
    return decompress().asByteImage();
  }

  @Override
  public ImageSize getSize() {
    return this.size;
  }

  private void compress(IntStream intStream) {
    this.uncompressedBytesLength = 0;
    this.compressed = intStream
        .peek((i) -> this.uncompressedBytesLength++)
        .collect(
            () -> new AccumulatorFacade(), 
            (acc,i) -> acc.write(i), 
            (acc1,acc2) -> acc1.concat(acc2))
        .toByteArray();
  }
  

  /**
   * Getter of the raw compressed bytes array.  
   * 
   * Only usefull for tests.
   *  
   * @return raw compressed bytes array.
   */
  byte[] getCompressedBytes() {
    return this.compressed;
  }
  
}
