package fr.jblezoray.mygeneticalgo.sample.facemashup;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class FaceImageTest {
  
  
  
  @Test
  public void testComputeFitnessOf_FitnessHistogramRMSFast() throws IOException {
    FaceImage matchImage = new FaceImage(ImageIO.read(
        this.getClass().getResourceAsStream("/match.png")), false);
    IFitness frms = FitnessHistogramRMSFast.build(matchImage);
    computeFitnessWith(frms);
  }
  
  
  @Test
  public void testComputeFitnessOf_FitnessPatchBased() throws IOException {
    FaceImage matchImage = new FaceImage(ImageIO.read(
        this.getClass().getResourceAsStream("/match.png")), false);
    IFitness fpb = FitnessPatchBased.build(matchImage, 10);
    computeFitnessWith(fpb);
  }
  
  private static void computeFitnessWith(IFitness frms) throws IOException {
    // having
    FaceImage gen0001 = new FaceImage(ImageIO.read(
        FaceImageTest.class.getResourceAsStream("/generation_0000001.png")), false);
    FaceImage gen0100 = new FaceImage(ImageIO.read(
        FaceImageTest.class.getResourceAsStream("/generation_0000100.png")), false);
    FaceImage gen0500 = new FaceImage(ImageIO.read(
        FaceImageTest.class.getResourceAsStream("/generation_0000500.png")), false);
    FaceImage gen1000 = new FaceImage(ImageIO.read(
        FaceImageTest.class.getResourceAsStream("/generation_0001000.png")), false);
    FaceImage gen2000 = new FaceImage(ImageIO.read(
        FaceImageTest.class.getResourceAsStream("/generation_0002000.png")), false);
    
    // when 
    double fitness0001 = frms.computeFitnessOf(gen0001);
    double fitness0100 = frms.computeFitnessOf(gen0100);
    double fitness0500 = frms.computeFitnessOf(gen0500);
    double fitness1000 = frms.computeFitnessOf(gen1000);
    double fitness2000 = frms.computeFitnessOf(gen2000);
    
    // then
    assertTrue(fitness0001 < fitness0100);
    assertTrue(fitness0100 < fitness0500);
    assertTrue(fitness0500 < fitness1000);
    assertTrue(fitness1000 < fitness2000);
  }
  
}
