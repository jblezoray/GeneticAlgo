package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class EdgeMain {

  //debug. 
  public static void main(String[] args) throws IOException {
    int w = 100;
    int h = 100;
    
    // create a white image 
    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics2D = null;
    try {
     graphics2D = image.createGraphics();
     graphics2D.setColor(Color.WHITE);
     graphics2D.fillRect(0, 0, w, h);
    } finally {
     if (graphics2D!=null) graphics2D.dispose();
    }
    byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    Image whiteImage = new Image(w, h, pixels);
    
    // two edges
    Edge e1 = new Edge(0,  7, w, h, 20);
    Edge e2 = new Edge(2, 12, w, h, 20);
    
    // draw the two edges in the image. 
    whiteImage = e1.drawEdgeInImage(whiteImage);
    whiteImage = e2.drawEdgeInImage(whiteImage);
    
    // write to file.
    BufferedImage bi = whiteImage.toBufferedImage();
    ImageIO.write(bi, "png", new File("output.png"));
  }
}
