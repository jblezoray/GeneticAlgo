package fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image;

public class ByteImage implements Image {

  private final byte[] bytes;
  private final ImageSize size;
  
  public ByteImage(ImageSize size, byte[] bytes) {
    this.size = size;
    this.bytes = bytes;
  }

  public ByteImage deepCopy() {
    return new ByteImage(this.size, this.bytes.clone());
  }
  
  @Override
  public ByteImage asByteImage() {
    return this;
  }
  
  public byte[] getBytes() {
    return bytes;
  }
  
  @Override
  public ImageSize getSize() {
    return this.size;
  }


}
