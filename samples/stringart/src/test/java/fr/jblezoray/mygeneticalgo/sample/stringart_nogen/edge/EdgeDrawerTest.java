package fr.jblezoray.mygeneticalgo.sample.stringart_nogen.edge;

import org.junit.Assert;
import org.junit.Test;

import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.core.EdgeDrawer;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image.ImageSize;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image.UnboundedImage;

public class EdgeDrawerTest {
  
  @Test
  public void compressed_image_shall_encode_the_right_nb_of_bytes() {
    ImageSize size = new ImageSize(1000, 1000);
    EdgeDrawer d = new EdgeDrawer(size, 5, 1.0f, 2.0f);
    byte[] drawnEdge = d.getDrawnEdge(1, false, 3, false);
    byte[] comp = d.compressDrawnEdgeData(drawnEdge);

    int cpt = 0;
    for (int i=0; i<comp.length; i+=2) 
      cpt += Byte.toUnsignedInt(comp[i]);
    Assert.assertEquals(size.nbPixels, cpt);
  }
  
  @Test
  public void drawing_a_edge_on_a_white_image_equals_the_edge_itself() {
    ImageSize size = new ImageSize(10, 10);
    EdgeDrawer d = new EdgeDrawer(size, 5, 1.0f, 2.0f);
    Edge e = new Edge(1, false, 3, false, d);
    UnboundedImage image = new UnboundedImage(size);

    byte[] drawnEdge = d.getDrawnEdge(1, false, 3, false);
    byte[] comp = d.compressDrawnEdgeData(drawnEdge);
    d.drawEdgeInImage(image, e);

    System.out.println(toString(drawnEdge, 10));
    System.out.println(toString(comp, 10));
    System.out.println(toString(image.asByteImage().getBytes(), 10));
    Assert.assertArrayEquals(drawnEdge, image.asByteImage().getBytes());
  }
  
  @Test
  public void drawing_a_edge_multiple_times_results_in_a_black_and_white_image() {
    ImageSize size = new ImageSize(10, 10);
    EdgeDrawer d = new EdgeDrawer(size, 5, 1.0f, 2.0f);
    Edge e = new Edge(1, false, 3, false, d);
    UnboundedImage image = new UnboundedImage(size);
    
    for (int i=0; i<100; i++) {
      d.drawEdgeInImage(image, e);
    }

    System.out.println(toString(image.asByteImage().getBytes(), 10));
    for (byte b : image.asByteImage().getBytes()) {
      int ub = Byte.toUnsignedInt(b);
      Assert.assertTrue(ub==0xFF || ub==0x00);
    }
  }
  

  @Test
  public void a_drawn_edge_can_be_removed() {
    ImageSize size = new ImageSize(10, 10);
    EdgeDrawer d = new EdgeDrawer(size, 5, 1.0f, 2.0f);
    Edge e1 = new Edge(1, false, 3, false, d);
    Edge e2 = new Edge(2, false, 4, false, d);
    Edge e3 = new Edge(0, false, 3, false, d);

    UnboundedImage first = new UnboundedImage(size);
    d.drawEdgeInImage(first, e1);
    d.drawEdgeInImage(first, e3);
    UnboundedImage second = new UnboundedImage(size);
    d.drawEdgeInImage(second, e1);
    d.drawEdgeInImage(second, e2);
    d.drawEdgeInImage(second, e3);
    d.drawEdgeInImage(second, e3);
    d.undrawEdgeInImage(second, e3);
    d.undrawEdgeInImage(second, e2);

    Assert.assertArrayEquals(first.asByteImage().getBytes(), second.asByteImage().getBytes());
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
