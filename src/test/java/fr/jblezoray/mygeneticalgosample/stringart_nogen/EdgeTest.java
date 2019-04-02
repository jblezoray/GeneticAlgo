package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import org.junit.Assert;
import org.junit.Test;

public class EdgeTest {
  
  @Test
  public void compressed_image_shall_encode_the_right_nb_of_bytes() {
    Edge e = new Edge(1, 3, 1000, 1000, 5);
    
    byte[] comp = Edge.compressDrawnEdgeData(e.getDrawnEdge());

    int cpt = 0;
    for (int i=0; i<comp.length; i+=2) 
      cpt += Byte.toUnsignedInt(comp[i]);
    Assert.assertEquals(1000*1000, cpt);
  }
  
  @Test
  public void drawing_a_edge_on_a_white_image_equals_the_edge_itself() {
    Edge e = new Edge(1, 3, 10, 10, 5);
    Image white = new Image(10, 10);
    
    Image whiteWithEdge = e.drawEdgeInImage(white);

    System.out.println(toString(e.getDrawnEdge(), 10));
    System.out.println(toString(Edge.compressDrawnEdgeData(e.getDrawnEdge()), 10));
    System.out.println(toString(whiteWithEdge.getBytes(), 10));
    Assert.assertArrayEquals(e.getDrawnEdge(), whiteWithEdge.getBytes());
  }
  
  @Test
  public void drawing_a_edge_multiple_times_results_in_a_black_and_white_image() {
    Edge e = new Edge(1, 3, 10, 10, 5);
    Image white = new Image(10, 10);
    
    Image whiteWithEdge = white;
    for (int i=0; i<100; i++) {
      whiteWithEdge = e.drawEdgeInImage(whiteWithEdge);
    }

    System.out.println(toString(whiteWithEdge.getBytes(), 10));
    for (byte b : whiteWithEdge.getBytes()) {
      int ub = Byte.toUnsignedInt(b);
      Assert.assertTrue(ub==0xFF || ub==0x00);
    }
  }
  
  

  private String toString(byte[] bytes, int lineW) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<bytes.length; i++) {
      byte b = bytes[i];
      sb.append(String.format("%02x ", Byte.toUnsignedInt(b)));
      if (i%lineW==lineW-1)
        sb.append('\n');
    }
    return sb.toString();
  }

}
