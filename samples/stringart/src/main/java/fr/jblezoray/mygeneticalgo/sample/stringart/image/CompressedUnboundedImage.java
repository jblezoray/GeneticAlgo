package fr.jblezoray.mygeneticalgo.sample.stringart.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

public class CompressedUnboundedImage implements Image {

  private byte[] compressed;
  private ImageSize size;
  private int compressedBytesLength;
  
  
  public CompressedUnboundedImage(UnboundedImage img) {
    this.size = img.getSize();
    compress(img.intStream());
  }

  class Accumulator {
    private ByteArrayOutputStream compressedBytes;
    void write(int i) {
      // TODO
    }
    byte[] toByteArray() {
      return compressedBytes.toByteArray();
    }
    void concat(Accumulator acc2) {
      // TODO flush before.
      try {
        compressedBytes.write(acc2.compressedBytes.toByteArray());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
  // 
  // |size|type|
  // |    |    |         |         |         |
  // |   int   |   int   |   int   |   int   |
  private void compress(IntStream intStream) {
    Accumulator a = intStream.collect(
        () -> new Accumulator(), 
        (acc,i) -> acc.write(i), 
        (acc1,acc2) -> acc1.concat(acc2));
    this.compressed = a.compressedBytes.toByteArray();
  }
  
  private int[] decompressData() {
    
  }
  
//  private void compress(IntStream intStream) {
//    ByteArrayOutputStream out = new ByteArrayOutputStream();
//    
//    Deflater def = new Deflater(Deflater.BEST_SPEED);
//    try (
//        DeflaterOutputStream dos = new DeflaterOutputStream(out, def, 4 * 1024);
//    ) {
//      intStream.forEachOrdered(i -> {
//        try {
//          dos.write((i >> 24) & 0xFF);
//          dos.write((i >> 16) & 0xFF);
//          dos.write((i >> 8) & 0xFF);
//          dos.write(i & 0xFF);
//        } catch (IOException e) {
//          throw new RuntimeException(e);
//        }
//      });
//      this.compressedBytesLength = (int) def.getBytesRead();
//    } catch (IOException e1) {
//      throw new RuntimeException(e1);
//    }
//    
//    this.compressed = out.toByteArray();
//  }
//  
//  private int[] decompressData() {
//    Inflater decompresser = new Inflater();
//    decompresser.setInput(this.compressed, 0, this.compressed.length);
//    
//    byte[] resultBytes = new byte[this.compressedBytesLength];
//    try {
//      decompresser.inflate(resultBytes);
//      
//    } catch (DataFormatException e) {
//      throw new RuntimeException(e);
//    }
//    decompresser.end();
//    
//    int[] resultInts = new int[this.compressedBytesLength/4];
//    for (int i=0, j=0; i<resultBytes.length; i+=4, j++) {
//      resultInts[j] = (resultBytes[i]   << 24)
//                    | (resultBytes[i+1] << 16)
//                    | (resultBytes[i+2] << 8)
//                    | (resultBytes[i+3]);  
//    }
//    return resultInts;
//  }
  
  public UnboundedImage decompress() {
    return new UnboundedImage(size, decompressData());
  }

  @Override
  public ByteImage asByteImage() {
    return decompress().asByteImage();
  }

  @Override
  public ImageSize getSize() {
    return this.size;
  }
  
}
