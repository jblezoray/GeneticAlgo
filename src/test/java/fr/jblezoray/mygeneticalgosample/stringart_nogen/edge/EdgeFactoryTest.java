package fr.jblezoray.mygeneticalgosample.stringart_nogen.edge;

import org.junit.Assert;
import org.junit.Test;

import fr.jblezoray.mygeneticalgosample.stringart_nogen.image.ImageSize;
import fr.jblezoray.mygeneticalgosample.stringart_nogen.image.UnboundedImage;

public class EdgeFactoryTest {
  
  @Test
  public void compressed_image_shall_encode_the_right_nb_of_bytes() {
    ImageSize size = new ImageSize(1000, 1000);
    EdgeFactory f = new EdgeFactory(size, 5, 1.0f);
    
    byte[] drawnEdge = f.getDrawnEdge(1, 3);
    byte[] comp = f.compressDrawnEdgeData(drawnEdge);

    int cpt = 0;
    for (int i=0; i<comp.length; i+=2) 
      cpt += Byte.toUnsignedInt(comp[i]);
    Assert.assertEquals(size.nbPixels, cpt);
  }
  
  @Test
  public void drawing_a_edge_on_a_white_image_equals_the_edge_itself() {
    ImageSize size = new ImageSize(10, 10);
    EdgeFactory f = new EdgeFactory(size, 5, 1.0f);
    Edge e = new Edge(1, 3, f);
    UnboundedImage image = new UnboundedImage(size);

    byte[] drawnEdge = f.getDrawnEdge(1, 3);
    byte[] comp = f.compressDrawnEdgeData(drawnEdge);
    f.drawEdgeInImage(image, e);

    System.out.println(toString(drawnEdge, 10));
    System.out.println(toString(comp, 10));
    System.out.println(toString(image.getBytes(), 10));
    Assert.assertArrayEquals(drawnEdge, image.getBytes());
  }
  
  @Test
  public void drawing_a_edge_multiple_times_results_in_a_black_and_white_image() {
    ImageSize size = new ImageSize(10, 10);
    EdgeFactory f = new EdgeFactory(size, 5, 1.0f);
    Edge e = new Edge(1, 3, f);
    UnboundedImage image = new UnboundedImage(size);
    
    for (int i=0; i<100; i++) {
      f.drawEdgeInImage(image, e);
    }

    System.out.println(toString(image.getBytes(), 10));
    for (byte b : image.getBytes()) {
      int ub = Byte.toUnsignedInt(b);
      Assert.assertTrue(ub==0xFF || ub==0x00);
    }
  }
  

  @Test
  public void a_drawn_edge_can_be_removed() {
    ImageSize size = new ImageSize(10, 10);
    EdgeFactory f = new EdgeFactory(size, 5, 1.0f);
    Edge e1 = new Edge(1, 3, f);
    Edge e2 = new Edge(2, 4, f);
    Edge e3 = new Edge(0, 3, f);

    UnboundedImage first = new UnboundedImage(size);
    f.drawEdgeInImage(first, e1);
    f.drawEdgeInImage(first, e3);
    UnboundedImage second = new UnboundedImage(size);
    f.drawEdgeInImage(second, e1);
    f.drawEdgeInImage(second, e2);
    f.drawEdgeInImage(second, e3);
    f.drawEdgeInImage(second, e3);
    f.undrawEdgeInImage(second, e3);
    f.undrawEdgeInImage(second, e2);

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
