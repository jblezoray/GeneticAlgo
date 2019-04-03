package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class EdgeMain {

  //debug. 
  public static void main(String[] args) throws IOException {
    ImageSize size = new ImageSize(100, 100);
    
    // create a white image 
    UnboundedImage whiteImage = new UnboundedImage(size);
    
    // two edges
    Edge e1 = new Edge(0,  7, size, 20, 0.5f);
    Edge e2 = new Edge(2, 12, size, 20, 0.5f);
    
    // draw the two edges in the image. 
    whiteImage = e1.drawEdgeInImage(whiteImage);
    whiteImage = e2.drawEdgeInImage(whiteImage);
    
    // write to file.
    BufferedImage bi = whiteImage.toBufferedImage();
    ImageIO.write(bi, "png", new File("output.png"));
  }
}
