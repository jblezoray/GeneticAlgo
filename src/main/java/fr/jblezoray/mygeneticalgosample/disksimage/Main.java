package fr.jblezoray.mygeneticalgosample.disksimage;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.IFitness;
import fr.jblezoray.mygeneticalgo.ISelection;
import fr.jblezoray.mygeneticalgo.dna.image.AbstractImageDNA;
import fr.jblezoray.mygeneticalgo.dna.image.BasicAbstractImagePrinterListener;
import fr.jblezoray.mygeneticalgo.dna.image.FitnessHistogramRMS;
import fr.jblezoray.mygeneticalgo.dna.image.UnmodifiableImageDNA;
import fr.jblezoray.mygeneticalgo.selection.BinaryTournamentSelection;
import fr.jblezoray.mygeneticalgo.utils.StatsListener;

/**
 * inspired by : 
 * https://rogerjohansson.blog/2008/12/07/genetic-programming-evolution-of-mona-lisa/
 * @author jib
 *
 */
public class Main {
  
  private static final int POPULATION_SIZE = 100;
  private final static int LOG_INTERVAL = 10;
  
  public static void main(String[] args) throws IOException {

    if (args.length!=2) {
      printUsage();
      return;
    }
    File imageFile = new File(args[0]);
    File dirStatus = new File(args[1]);
    
    AbstractImageDNA referenceImage = new UnmodifiableImageDNA(imageFile, false);
    IFitness<DiskImageDNA> fit = new FitnessHistogramRMS<>(referenceImage);
    
    DisksImageDNAFactory fac = new DisksImageDNAFactory(referenceImage);
    
    ISelection<DiskImageDNA> sel = new BinaryTournamentSelection<>();
    
    GeneticAlgo<DiskImageDNA> ga = new GeneticAlgo<>(fit, fac, sel, POPULATION_SIZE);
    ga.addListener(new BasicAbstractImagePrinterListener<>(dirStatus, LOG_INTERVAL));
    ga.addListener(new StatsListener<>(System.out, LOG_INTERVAL));
    ga.evolve(2_000);
  }

  private static void printUsage() {
    System.out.println("Generates an approximation of an image by composing disks.");
    System.out.println("");
    System.out.println("usage : java -cp myGeneticAlgo.jar fr.jblezoray.mygeneticalgo.sample.polygonimage.Main image statusDir");
    System.out.println("");
    System.out.println("   image     : the goal image.");
    System.out.println("   statusDir : directory where to store some intermediate results.");
    System.out.println("");
  }
  
}
