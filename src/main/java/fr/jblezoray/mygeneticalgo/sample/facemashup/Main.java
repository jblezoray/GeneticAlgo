package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitnessHistogramWithPatch;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.IFitness;

public class Main {

  private static final int POP_SIZE = 200;
  private final static int NB_FACES_PER_IMAGE = 100;
  
  public static void main(String[] args) throws IOException {
    
    if (args.length!=3) {
      printUsage();
      return;
    }
    
    File fileMatch = new File(args[0]);
    File fileMask = new File(args[1]);
    File dirStatus = new File(args[2]);
    
    IFitness fitness = new FitnessHistogramWithPatch(5);
    FaceMashupGenerator fma = new FaceMashupGenerator(fileMatch, fileMask, 
        dirStatus, fitness, POP_SIZE, NB_FACES_PER_IMAGE);
    GeneticAlgo<FaceImage> ga = new GeneticAlgo<>(fma);
    ga.evolve(2000);
  }

  private static void printUsage() {
    System.out.println("Generates a face by combinating instances of itself.");
    System.out.println("");
    System.out.println("usage : java -cp myGeneticAlgo.jar fr.jblezoray.mygeneticalgo.sample.facemashup.Main matchImage maskImage statusDir ");
    System.out.println("");
    System.out.println("   matchImage : the goal image.");
    System.out.println("   maskImage  : same as the goal image, but with some transmarency to create a mask.");
    System.out.println("   statusDir  : directory where to store some intermediate results.");
    System.out.println("");
  }

}
