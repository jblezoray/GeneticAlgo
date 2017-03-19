package fr.jblezoray.mygeneticalgo.sample.facemashup;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class FaceImageTest {

  @Test
  public void testComputeFitnessOf() throws IOException {
    // having
    FaceImage matchImage = new FaceImage(ImageIO.read(
        this.getClass().getResourceAsStream("/match.png")));
    FaceImage gen0001 = new FaceImage(ImageIO.read(
        this.getClass().getResourceAsStream("/generation_0000001.png")));
    FaceImage gen0100 = new FaceImage(ImageIO.read(
        this.getClass().getResourceAsStream("/generation_0000100.png")));
    FaceImage gen0500 = new FaceImage(ImageIO.read(
        this.getClass().getResourceAsStream("/generation_0000500.png")));
    FaceImage gen1000 = new FaceImage(ImageIO.read(
        this.getClass().getResourceAsStream("/generation_0001000.png")));
    FaceImage gen2000 = new FaceImage(ImageIO.read(
        this.getClass().getResourceAsStream("/generation_0002000.png")));
    
    // when 
    double fitness0001 = matchImage.computeFitnessOf(gen0001);
    double fitness0100 = matchImage.computeFitnessOf(gen0100);
    double fitness0500 = matchImage.computeFitnessOf(gen0500);
    double fitness1000 = matchImage.computeFitnessOf(gen1000);
    double fitness2000 = matchImage.computeFitnessOf(gen2000);
    
    // then
    assertTrue(fitness0001 < fitness0100);
    assertTrue(fitness0100 < fitness0500);
    assertTrue(fitness0500 < fitness1000);
    assertTrue(fitness1000 < fitness2000);
  }
  
  
  
}
