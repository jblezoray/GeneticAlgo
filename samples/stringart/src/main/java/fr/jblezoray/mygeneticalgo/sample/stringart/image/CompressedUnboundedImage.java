package fr.jblezoray.mygeneticalgo.sample.stringart.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;


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

  public static enum AccumulatorMode {
    NONE(0x00), SEQUENCE(0x01), MULT(0x02), SEQUENCE_BIG_INT(0x03);
    private byte headerByte;
    AccumulatorMode(int headerByte) {
      this.headerByte = (byte)headerByte;
    }
    public byte getHeaderByte() {
      return this.headerByte;
    }
    public static AccumulatorMode fromHeaderByte(byte curByte) {
      for (AccumulatorMode m : AccumulatorMode.values())
        if (m.headerByte == curByte)
          return m;
      return null;
    }
  }

  private static class Accumulator {
    private ByteArrayOutputStream compressedBytes;
    private byte[] workBuffer;
    private AccumulatorMode curMode;
    private int curWorkBufferIndex;
    private static final int BUF_SIZE = 0x1024;
    private static final int HALF_INT = Integer.MAX_VALUE >> 16;
    
    public Accumulator() {
      this.compressedBytes = new ByteArrayOutputStream();
      this.workBuffer = new byte[BUF_SIZE];
      this.curMode = AccumulatorMode.NONE;
      this.curWorkBufferIndex = -1;
    }
    
    void write(int i) {
      if (this.curWorkBufferIndex>=BUF_SIZE-4) {
        this.flush();
      }
      
      if (i>HALF_INT) {
        if (this.curMode != AccumulatorMode.SEQUENCE_BIG_INT) {
          this.flush();
          this.curMode = AccumulatorMode.SEQUENCE_BIG_INT;
          
        } else {
          this.workBuffer[++this.curWorkBufferIndex] = (byte) ((i >> 24) & 0xFF);
          this.workBuffer[++this.curWorkBufferIndex] = (byte) ((i >> 16) & 0xFF);
          this.workBuffer[++this.curWorkBufferIndex] = (byte) ((i >> 8)  & 0xFF);
          this.workBuffer[++this.curWorkBufferIndex] = (byte) ((i)       & 0xFF);
        }
      }
      else if (curMode==AccumulatorMode.MULT) {
        if (this.workBuffer[0] == (byte) ((i >> 8)  & 0xFF) && 
            this.workBuffer[1] == (byte) ((i)       & 0xFF)) {
          this.curWorkBufferIndex += 2;
          
        } else {
          this.flush();
        }
      }
      else if (curMode==AccumulatorMode.SEQUENCE) {
        byte b0 = (byte) ((i >> 8)  & 0xFF);
        byte b1 = (byte) ((i)       & 0xFF);
        if (b0 == this.workBuffer[this.curWorkBufferIndex-1] &&
            b1 == this.workBuffer[this.curWorkBufferIndex]) {
          // switch to mult mode.
          this.curWorkBufferIndex = this.curWorkBufferIndex-2;
          this.flush();
          this.workBuffer[0] = b0;
          this.workBuffer[1] = b1;
          this.curWorkBufferIndex = 3; // two half bytes.  
          this.curMode = AccumulatorMode.MULT;
        } else {
          this.workBuffer[++this.curWorkBufferIndex] = (byte) ((i >> 8)  & 0xFF);
          this.workBuffer[++this.curWorkBufferIndex] = (byte) ((i)       & 0xFF);
        }
      }
      
      // no mode, add to buffer. 
      if (curMode==AccumulatorMode.NONE) {
        this.workBuffer[++this.curWorkBufferIndex] = (byte) ((i >> 8)  & 0xFF);
        this.workBuffer[++this.curWorkBufferIndex] = (byte) ((i)       & 0xFF);
        if (this.curWorkBufferIndex==3) {
          if (this.workBuffer[0] == this.workBuffer[2] && 
              this.workBuffer[1] == this.workBuffer[3]) {
            this.curMode = AccumulatorMode.MULT;
          } else {
            this.curMode = AccumulatorMode.SEQUENCE;
          }
        }
      }
    }
    
    void flush() {
      if (this.curMode == AccumulatorMode.SEQUENCE_BIG_INT) {
        this.compressedBytes.write(AccumulatorMode.SEQUENCE_BIG_INT.getHeaderByte());
        this.compressedBytes.write(((curWorkBufferIndex+1) >> 8) & 0xFF);
        this.compressedBytes.write( (curWorkBufferIndex+1)       & 0xFF);
        this.compressedBytes.write(this.workBuffer, 0, curWorkBufferIndex+1);
        
      } else if (this.curMode == AccumulatorMode.SEQUENCE
              || this.curMode == AccumulatorMode.NONE) {
        this.compressedBytes.write(AccumulatorMode.SEQUENCE.getHeaderByte());
        this.compressedBytes.write(((curWorkBufferIndex+1) >> 8) & 0xFF);
        this.compressedBytes.write( (curWorkBufferIndex+1)       & 0xFF);
        this.compressedBytes.write(this.workBuffer, 0, curWorkBufferIndex+1);
        
      } else if (this.curMode == AccumulatorMode.MULT) {
        this.compressedBytes.write(AccumulatorMode.MULT.getHeaderByte());
        this.compressedBytes.write(((curWorkBufferIndex+1) >> 8) & 0xFF);
        this.compressedBytes.write( (curWorkBufferIndex+1)       & 0xFF);
        this.compressedBytes.write(this.workBuffer[0]);
        this.compressedBytes.write(this.workBuffer[1]);
        
      } else {
        throw new RuntimeException("Unknown mode " + this.curMode);
      }

      // reset.
      this.curMode = AccumulatorMode.NONE;
      this.curWorkBufferIndex = -1;
    }
    
    byte[] toByteArray() {
      flush();
      return compressedBytes.toByteArray();
    }
    
    void concat(Accumulator acc2) {
      flush();
      try {
        compressedBytes.write(acc2.compressedBytes.toByteArray());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void compress(IntStream intStream) {
    this.uncompressedBytesLength = 0;
    this.compressed = intStream
        .peek((i) -> this.uncompressedBytesLength++)
        .collect(
            () -> new Accumulator(), 
            (acc,i) -> acc.write(i), 
            (acc1,acc2) -> acc1.concat(acc2))
        .toByteArray();
  }
  
  private int[] decompressData() {
    int curCompressedIndex = 0;
    int[] decompressed = new int[this.uncompressedBytesLength];
    int curDecompressedIndex = 0;
    AccumulatorMode curMode;
    byte headerByte;
    while (curCompressedIndex<this.compressed.length){
      // read mode. 
      headerByte = this.compressed[curCompressedIndex++];
      curMode = AccumulatorMode.fromHeaderByte(headerByte);
      // read size. 
      int size = 
          this.compressed[curCompressedIndex++] >> 8 |
          this.compressed[curCompressedIndex++];
    
      if (curMode == AccumulatorMode.SEQUENCE_BIG_INT) {
        int toIndex = curCompressedIndex + size/4;
        while (curCompressedIndex < toIndex) {
          decompressed[curDecompressedIndex++] = 
              this.compressed[curCompressedIndex++] >> 24 | 
              this.compressed[curCompressedIndex++] >> 16 | 
              this.compressed[curCompressedIndex++] >>  8 | 
              this.compressed[curCompressedIndex++]; 
        }
        
      } else if (curMode == AccumulatorMode.SEQUENCE) {
        int toIndex = curCompressedIndex + size;
        while (curCompressedIndex < toIndex) {
          decompressed[curDecompressedIndex++] = 
              this.compressed[curCompressedIndex++] >>  8 | 
              this.compressed[curCompressedIndex++]; 
        }
        
      } else if (curMode == AccumulatorMode.MULT) {
        int value = 
            this.compressed[curCompressedIndex++] >>  8 | 
            this.compressed[curCompressedIndex++]; 
        int toIndex = curDecompressedIndex + size/2;
        while (curDecompressedIndex < toIndex) {
          decompressed[curDecompressedIndex++] = value;
        }
      }
    }
    return decompressed;
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
//      this.uncompressedBytesLength = (int) def.getBytesRead();
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
//    byte[] resultBytes = new byte[this.uncompressedBytesLength];
//    try {
//      decompresser.inflate(resultBytes);
//      
//    } catch (DataFormatException e) {
//      throw new RuntimeException(e);
//    }
//    decompresser.end();
//    
//    int[] resultInts = new int[this.uncompressedBytesLength/4];
//    for (int i=0, j=0; i<resultBytes.length; i+=4, j++) {
//      resultInts[j] = (resultBytes[i]   << 24)
//                    | (resultBytes[i+1] << 16)
//                    | (resultBytes[i+2] << 8)
//                    | (resultBytes[i+3]);  
//    }
//    return resultInts;
//  }
  
  
}
