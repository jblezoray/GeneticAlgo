package fr.jblezoray.mygeneticalgo.dna.image;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import fr.jblezoray.mygeneticalgo.IFitness;
import fr.jblezoray.mygeneticalgo.dna.image.AbstractImageDNA;
import fr.jblezoray.mygeneticalgo.dna.image.FitnessHistogramRMS;
import fr.jblezoray.mygeneticalgo.dna.image.FitnessHistogramRMSWithWeight;
import fr.jblezoray.mygeneticalgo.dna.image.FitnessHistogramWithPatch;
import fr.jblezoray.mygeneticalgo.dna.image.FitnessPatch;
import fr.jblezoray.mygeneticalgo.dna.image.UnmodifiableImageDNA;

public class FitnessTest {
  
  
  @Test
  public void testComputeFitnessOf_FitnessHistogramRMSFast() throws IOException {
    AbstractImageDNA matchImage = new UnmodifiableImageDNA(
        ImageIO.read(this.getClass().getResourceAsStream("/match.png")), false);
    IFitness<AbstractImageDNA> frms = new FitnessHistogramRMS<>(matchImage);
    computeFitnessWith(frms);
  }
  
  
  @Test
  public void testComputeFitnessOf_FitnessPatchBased() throws IOException {
    AbstractImageDNA matchImage = new UnmodifiableImageDNA(
        ImageIO.read(this.getClass().getResourceAsStream("/match.png")), false);
    IFitness<AbstractImageDNA> fpb = new FitnessHistogramWithPatch<>(10, matchImage);
    computeFitnessWith(fpb);
  }
  
  
  @Test
  public void testComputeFitnessOf_FitnessPatchNoHistogram() throws IOException {
    AbstractImageDNA matchImage = new UnmodifiableImageDNA(
        ImageIO.read(this.getClass().getResourceAsStream("/match.png")), false);
    IFitness<AbstractImageDNA> fpb = new FitnessPatch<>(10, matchImage);
    computeFitnessWith(fpb);
  }
  
  
  @Test
  public void testComputeFitnessOf_FitnessHistogramRMSWithWeight() throws IOException {
    AbstractImageDNA matchImage = new UnmodifiableImageDNA(
        ImageIO.read(this.getClass().getResourceAsStream("/match.png")), false);
    IFitness<AbstractImageDNA> fpb = new FitnessHistogramRMSWithWeight<>(matchImage);
    computeFitnessWith(fpb);
  }
  
  
  
  
  private static void computeFitnessWith(IFitness<AbstractImageDNA> frms) throws IOException {
    // having
    AbstractImageDNA gen0001 = new UnmodifiableImageDNA(ImageIO.read(
        FitnessTest.class.getResourceAsStream("/generation_0000001.png")), false);
    AbstractImageDNA gen0100 = new UnmodifiableImageDNA(ImageIO.read(
        FitnessTest.class.getResourceAsStream("/generation_0000100.png")), false);
    AbstractImageDNA gen0500 = new UnmodifiableImageDNA(ImageIO.read(
        FitnessTest.class.getResourceAsStream("/generation_0000500.png")), false);
    AbstractImageDNA gen1000 = new UnmodifiableImageDNA(ImageIO.read(
        FitnessTest.class.getResourceAsStream("/generation_0001000.png")), false);
    AbstractImageDNA gen2000 = new UnmodifiableImageDNA(ImageIO.read(
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
