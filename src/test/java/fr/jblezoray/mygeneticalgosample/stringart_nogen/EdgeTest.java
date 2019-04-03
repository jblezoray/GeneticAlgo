package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import org.junit.Assert;
import org.junit.Test;

public class EdgeTest {
  
  @Test
  public void compressed_image_shall_encode_the_right_nb_of_bytes() {
    ImageSize size = new ImageSize(1000, 1000);
    Edge e = new Edge(1, 3, size, 5, 1.0f);
    
    byte[] comp = Edge.compressDrawnEdgeData(e.getDrawnEdge());

    int cpt = 0;
    for (int i=0; i<comp.length; i+=2) 
      cpt += Byte.toUnsignedInt(comp[i]);
    Assert.assertEquals(size.nbPixels, cpt);
  }
  
  @Test
  public void drawing_a_edge_on_a_white_image_equals_the_edge_itself() {
    ImageSize size = new ImageSize(10, 10);
    Edge e = new Edge(1, 3, size, 5, 1.0f);
    UnboundedImage white = new UnboundedImage(size);
    
    Image whiteWithEdge = e.drawEdgeInImage(white);

    System.out.println(toString(e.getDrawnEdge(), 10));
    System.out.println(toString(Edge.compressDrawnEdgeData(e.getDrawnEdge()), 10));
    System.out.println(toString(whiteWithEdge.getBytes(), 10));
    Assert.assertArrayEquals(e.getDrawnEdge(), whiteWithEdge.getBytes());
  }
  
  @Test
  public void drawing_a_edge_multiple_times_results_in_a_black_and_white_image() {
    ImageSize size = new ImageSize(10, 10);
    Edge e = new Edge(1, 3, size, 5, 1.0f);
    UnboundedImage white = new UnboundedImage(size);
    
    UnboundedImage whiteWithEdge = white;
    for (int i=0; i<100; i++) {
      whiteWithEdge = e.drawEdgeInImage(whiteWithEdge);
    }

    System.out.println(toString(whiteWithEdge.getBytes(), 10));
    for (byte b : whiteWithEdge.getBytes()) {
      int ub = Byte.toUnsignedInt(b);
      Assert.assertTrue(ub==0xFF || ub==0x00);
    }
  }
  

  @Test
  public void a_drawn_edge_can_be_removed() {
    ImageSize size = new ImageSize(10, 10);
    Edge e1 = new Edge(1, 3, size, 5, 1.0f);
    Edge e2 = new Edge(2, 4, size, 5, 1.0f);
    Edge e3 = new Edge(0, 3, size, 5, 1.0f);

    UnboundedImage first = new UnboundedImage(size);
    first = e1.drawEdgeInImage(first);
    first = e3.drawEdgeInImage(first);
    UnboundedImage second = new UnboundedImage(size);
    second = e1.drawEdgeInImage(second);
    second = e2.drawEdgeInImage(second);
    second = e3.drawEdgeInImage(second);
    second = e3.drawEdgeInImage(second);
    second = e3.undrawEdgeInImage(second);
    second = e2.undrawEdgeInImage(second);

    Assert.assertArrayEquals(first.getBytes(), second.getBytes());
    Assert.assertArrayEquals(first.getUnboundedBytes(), second.getUnboundedBytes());
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
