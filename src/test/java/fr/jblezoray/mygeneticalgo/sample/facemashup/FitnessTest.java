package fr.jblezoray.mygeneticalgo.sample.facemashup;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Ignore;
import org.junit.Test;

import fr.jblezoray.mygeneticalgo.sample.imagefitness.AbstractFitableImage;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitnessHistogramRMS;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitnessHistogramRMSWithWeight;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitnessHistogramWithPatch;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitnessPatch;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.IFitness;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.UnmodifiableFitableImage;

public class FitnessTest {
  
  
  @Test
  public void testComputeFitnessOf_FitnessHistogramRMSFast() throws IOException {
    AbstractFitableImage matchImage = new UnmodifiableFitableImage(
        ImageIO.read(this.getClass().getResourceAsStream("/match.png")), false);
    IFitness frms = new FitnessHistogramRMS();
    frms.init(matchImage);
    computeFitnessWith(frms);
  }
  
  
  @Test
  public void testComputeFitnessOf_FitnessPatchBased() throws IOException {
    AbstractFitableImage matchImage = new UnmodifiableFitableImage(
        ImageIO.read(this.getClass().getResourceAsStream("/match.png")), false);
    IFitness fpb = new FitnessHistogramWithPatch(10);
    fpb.init(matchImage);
    computeFitnessWith(fpb);
  }
  
  
  @Test
  public void testComputeFitnessOf_FitnessPatchNoHistogram() throws IOException {
    AbstractFitableImage matchImage = new UnmodifiableFitableImage(
        ImageIO.read(this.getClass().getResourceAsStream("/match.png")), false);
    IFitness fpb = new FitnessPatch(10);
    fpb.init(matchImage);
    computeFitnessWith(fpb);
  }
  
  
  @Test
  public void testComputeFitnessOf_FitnessHistogramRMSWithWeight() throws IOException {
    AbstractFitableImage matchImage = new UnmodifiableFitableImage(
        ImageIO.read(this.getClass().getResourceAsStream("/match.png")), false);
    IFitness fpb = new FitnessHistogramRMSWithWeight();
    fpb.init(matchImage);
    computeFitnessWith(fpb);
  }
  

  @Test
  @Ignore("Errr this is not a unit test")
  public void FitnessHistogramRMSWithWeight_dumpWeight() throws IOException {
    AbstractFitableImage matchImage = new UnmodifiableFitableImage(
        ImageIO.read(this.getClass().getResourceAsStream("/match.png")), false);
    
    FitnessHistogramRMSWithWeight fitness = new FitnessHistogramRMSWithWeight();
    fitness.init(matchImage);
    float[] weight = fitness.initWeight();
    byte[] visualisable = new byte[weight.length]; 
    for (int i=0; i<weight.length; i++) {
      float w =  weight[i];
      visualisable[i] = (byte)((int)(w * 0xFF) + Byte.MIN_VALUE);
    }

    int w = matchImage.getImage().getWidth();
    int h = matchImage.getImage().getHeight(); 
    new UnmodifiableFitableImage(visualisable, w, h).writeToFile(new File("output.png"));
  }
  
  
  
  private static void computeFitnessWith(IFitness frms) throws IOException {
    // having
    AbstractFitableImage gen0001 = new UnmodifiableFitableImage(ImageIO.read(
        FitnessTest.class.getResourceAsStream("/generation_0000001.png")), false);
    AbstractFitableImage gen0100 = new UnmodifiableFitableImage(ImageIO.read(
        FitnessTest.class.getResourceAsStream("/generation_0000100.png")), false);
    AbstractFitableImage gen0500 = new UnmodifiableFitableImage(ImageIO.read(
        FitnessTest.class.getResourceAsStream("/generation_0000500.png")), false);
    AbstractFitableImage gen1000 = new UnmodifiableFitableImage(ImageIO.read(
        FitnessTest.class.getResourceAsStream("/generation_0001000.png")), false);
    AbstractFitableImage gen2000 = new UnmodifiableFitableImage(ImageIO.read(
        FitnessTest.class.getResourceAsStream("/generation_0002000.png")), false);
    
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
