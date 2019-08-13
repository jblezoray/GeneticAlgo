package fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import fr.jblezoray.mygeneticalgo.sample.stringart.image.ImageSize;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed.IAccumulator.AccumulatorType;

@RunWith(Parameterized.class)
public class CompressedUnboundedImageCompressionTest {

  private final static int[] TEST_0_UNCOMPRESSED = {
      0x01
  };
  private final static byte[] TEST_0_COMPRESSED = {
      new MultAccumulator(AccumulatorType.BYTE_1).getHeaderByte(),
      0x01, // type
      1, // size 
      0x01, // value 
  };

  private final static int[] TEST_1_UNCOMPRESSED = {
      0x01, 0x01, 0x01
  };
  private final static byte[] TEST_1_COMPRESSED = {
      new MultAccumulator(AccumulatorType.BYTE_1).getHeaderByte(), 
      0x01, // type
      3, // size 
      0x01  // value
  };

  private final static int[] TEST_2_UNCOMPRESSED = new int[300];
  static {Arrays.fill(TEST_2_UNCOMPRESSED, 0x00);}
  private final static byte[] TEST_2_COMPRESSED = {
      new MultAccumulator(AccumulatorType.BYTE_1).getHeaderByte(), 
      0x01, // type
      (byte)0xFF, // size 
      0x00,  // value
      new MultAccumulator(AccumulatorType.BYTE_1).getHeaderByte(), 
      0x01, // type
      (byte)(300-0xFF), // size
      0x00  // value
  };

  private final static int[] TEST_3_UNCOMPRESSED = {
      0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
      0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B,
  };
  private final static byte[] TEST_3_COMPRESSED = {
      new MultAccumulator(AccumulatorType.BYTE_1).getHeaderByte(), 
      0x01, // type BYTE_1
      10, // size 
      0x01, // value
      new MultAccumulator(AccumulatorType.BYTE_1).getHeaderByte(), 
      0x01, // type BYTE_1
      1, // size 
      0x02, // value
      new SequenceAccumulator(AccumulatorType.BYTE_1).getHeaderByte(),
      0x01, // type BYTE_1
      9, // size 
      0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, // value
  };

  private final static int[] TEST_4_UNCOMPRESSED = {
      1_000, 1_000,
      1, 
  };
  private final static byte[] TEST_4_COMPRESSED = {
      new MultAccumulator(AccumulatorType.BYTE_2).getHeaderByte(),
      0x02, // type BYTE_2
      2, // size
      (byte)0xE8, 0x03, // value 1000
      
      new MultAccumulator(AccumulatorType.BYTE_1).getHeaderByte(), 
      0x01, // type BYTE_1
      1, // size 
      0x01, // value
  };

  private final static int[] TEST_5_UNCOMPRESSED = {
      1_000_000, 1_000_000, 1_000_000,
      1, 2, 3 
  };
  private final static byte[] TEST_5_COMPRESSED = {
      new MultAccumulator(AccumulatorType.BYTE_4).getHeaderByte(),
      0x04, // type BYTE_4
      3, // size 
      0x40, 0x42, 0x0F, 0x00, // value 1_000_000
      
      new MultAccumulator(AccumulatorType.BYTE_1).getHeaderByte(), 
      0x01, // type BYTE_1
      1, // size 
      0x01, // value
      
      new SequenceAccumulator(AccumulatorType.BYTE_1).getHeaderByte(), 
      0x01, // type BYTE_1
      2, // size 
      0x02, 0x03, // value
  };


  @Parameters
  public static Collection<Object[]> data() {
    ArrayList<Object[]> data = new ArrayList<>();
    data.add(new Object[] {TEST_0_UNCOMPRESSED, TEST_0_COMPRESSED});
    data.add(new Object[] {TEST_1_UNCOMPRESSED, TEST_1_COMPRESSED});
    data.add(new Object[] {TEST_2_UNCOMPRESSED, TEST_2_COMPRESSED});
    data.add(new Object[] {TEST_3_UNCOMPRESSED, TEST_3_COMPRESSED});
    data.add(new Object[] {TEST_4_UNCOMPRESSED, TEST_4_COMPRESSED});
    data.add(new Object[] {TEST_5_UNCOMPRESSED, TEST_5_COMPRESSED});
    return data;
  }

  @Parameter(0)
  public int[] uncompressed;

  @Parameter(1)
  public byte[] compressed;
  
  @Test
  public void test_compress() throws IOException {
    // having a (Parameterized) uncompressed int array   
    
    // when 
    ImageSize fakeSize = new ImageSize(1, uncompressed.length);
    UnboundedImage img = new UnboundedImage(fakeSize, uncompressed);
    CompressedUnboundedImage compressedImg = new CompressedUnboundedImage(img);
    byte[] compressedResult = compressedImg.getCompressedBytes();
    
    // then
    Assert.assertArrayEquals(debug(compressed, compressedResult),
        compressed, compressedResult);
  }

  @Test
  public void test_decompress() throws IOException {
    // having a (Parameterized) compressed byte array   
    
    // when 
    ImageSize fakeSize = new ImageSize(1, uncompressed.length);
    CompressedUnboundedImage compressedImg = new CompressedUnboundedImage(
        fakeSize, compressed, uncompressed.length);
    int[] decompressedResult = compressedImg.decompress().getRawIntegers();
    
    // then
    Assert.assertArrayEquals(debug(uncompressed, decompressedResult), 
        uncompressed, decompressedResult);
  }
  
  private String debug(byte[] expected, byte[] effective) {
    return debug(asString(expected), asString(effective));
  }

  private String debug(int[] expected, int[] effective) {
    return debug(asString(expected), asString(effective));
  }

  private String asString(byte[] array) {
    StringBuilder sb = new StringBuilder("[ ");
    for (byte e : array)
      sb.append(String.format("%02X", e)).append(" ");
    return sb.append("]").toString();
  }
  
  private String asString(int[] array) {
    StringBuilder sb = new StringBuilder("[ ");
    for (int i : array)
      sb.append(String.format("%02X", i)).append(" ");
    return sb.append("]").toString();
  }

  private String debug(String expected, String effective) {
    return "\n"+
        "expected :"+expected+"\n"+
        "effective:"+effective+"\n";
  }
  
}
