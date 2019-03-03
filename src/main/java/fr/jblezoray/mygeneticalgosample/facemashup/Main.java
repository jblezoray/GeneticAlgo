package fr.jblezoray.mygeneticalgosample.facemashup;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.IDNAFactory;
import fr.jblezoray.mygeneticalgo.IFitness;
import fr.jblezoray.mygeneticalgo.ISelection;
import fr.jblezoray.mygeneticalgo.dna.image.AbstractImageDNA;
import fr.jblezoray.mygeneticalgo.dna.image.BasicAbstractImagePrinterListener;
import fr.jblezoray.mygeneticalgo.dna.image.FitnessHistogramWithPatch;
import fr.jblezoray.mygeneticalgo.dna.image.UnmodifiableImageDNA;
import fr.jblezoray.mygeneticalgo.selection.BinaryTournamentSelection;
import fr.jblezoray.mygeneticalgo.utils.FitnessPloterListener;
import fr.jblezoray.mygeneticalgo.utils.StatsListener;

public class Main {

  private final static int POP_SIZE = 200;
  private final static int NB_FACES_PER_IMAGE = 100;
  private final static int LOG_INTERVAL = 10;
  
  public static void main(String[] args) throws IOException {
    
    if (args.length!=3) {
      printUsage();
      return;
    }
    
    File fileMatch = new File(args[0]);
    File fileMask = new File(args[1]);
    File dirStatus = new File(args[2]);

    ISelection<FaceImageDNA> sel = new BinaryTournamentSelection<>();
    
    AbstractImageDNA referenceImage = new UnmodifiableImageDNA(fileMatch, false);
    IFitness<FaceImageDNA> fitness = new FitnessHistogramWithPatch<>(3, referenceImage);
        
    AbstractImageDNA faceMaskImage = new UnmodifiableImageDNA(fileMask, true);
    IDNAFactory<FaceImageDNA> factory = new FaceImageDNAFactory(faceMaskImage, NB_FACES_PER_IMAGE);
    
    GeneticAlgo<FaceImageDNA> ga = new GeneticAlgo<>(fitness, factory, sel, POP_SIZE);
    ga.addListener(new BasicAbstractImagePrinterListener<>(dirStatus, LOG_INTERVAL));
    ga.addListener(new FitnessPloterListener<>(System.out, 30, 80, LOG_INTERVAL));
    ga.addListener(new StatsListener<>(System.out, LOG_INTERVAL));
    ga.evolve(2_000);
  }

  private static void printUsage() {
    System.out.println("Generates a face by combinating instances of itself.");
    System.out.println("");
    System.out.println("usage : java -cp myGeneticAlgo.jar fr.jblezoray.mygeneticalgosample.facemashup.Main matchImage maskImage statusDir ");
    System.out.println("");
    System.out.println("   matchImage : the goal image.");
    System.out.println("   maskImage  : same as the goal image, but with some transmarency to create a mask.");
    System.out.println("   statusDir  : directory where to store some intermediate results.");
    System.out.println("");
  }

}
