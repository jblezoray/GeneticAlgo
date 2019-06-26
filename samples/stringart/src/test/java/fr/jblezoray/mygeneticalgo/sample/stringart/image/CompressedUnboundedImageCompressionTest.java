package fr.jblezoray.mygeneticalgo.sample.stringart.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import fr.jblezoray.mygeneticalgo.sample.stringart.image.CompressedUnboundedImage.AccumulatorMode;

@RunWith(Parameterized.class)
public class CompressedUnboundedImageCompressionTest {

  private final static int[] TEST_1_UNCOMPRESSED = {
      0x01
  };
  private final static byte[] TEST_1_COMPRESSED = {
      AccumulatorMode.SEQUENCE.getHeaderByte(), 
      0x00, 1*2, // size 
      0x00, 0x01, // value 
  };

  private final static int[] TEST_2_UNCOMPRESSED = {
      0x01, 0x01, 0x01
  };
  private final static byte[] TEST_2_COMPRESSED = {
      AccumulatorMode.MULT.getHeaderByte(), 
      0x00, 3*2, // size 
      0x00, 0x01  // value
  };

  private final static int[] TEST_3_UNCOMPRESSED = {
      0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
      0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
      0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
      0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
      0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  };
  private final static byte[] TEST_3_COMPRESSED = {
      AccumulatorMode.MULT.getHeaderByte(), 
      (12*2*5) >> 8, (byte) ((12*2*5) & 0xFF), // size 
      0x00, 0x00  // value
  };

  private final static int[] TEST_4_UNCOMPRESSED = {
      0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
      0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B,
  };
  private final static byte[] TEST_4_COMPRESSED = {
      AccumulatorMode.MULT.getHeaderByte(), 
      0x00, 10*2, // size 
      0x00, 0x01, // value
      AccumulatorMode.SEQUENCE.getHeaderByte(), 
      0x00, 10*2, // size 
      0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00, 0x05, 0x00, 0x06, 
      0x00, 0x07, 0x00, 0x08, 0x00, 0x09, 0x00, 0x0A, 0x00, 0x0B,// value
  };

  @Parameters
  public static Collection<Object[]> data() {
    ArrayList<Object[]> data = new ArrayList<>();
    data.add(new Object[] {TEST_1_UNCOMPRESSED, TEST_1_COMPRESSED});
    data.add(new Object[] {TEST_2_UNCOMPRESSED, TEST_2_COMPRESSED});
    data.add(new Object[] {TEST_3_UNCOMPRESSED, TEST_3_COMPRESSED});
    data.add(new Object[] {TEST_4_UNCOMPRESSED, TEST_4_COMPRESSED});
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
