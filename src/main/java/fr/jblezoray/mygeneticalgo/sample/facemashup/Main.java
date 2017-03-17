package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;

public class Main {

  private static final int POP_SIZE = 50;
  private static final int DNA_LENGTH = 5 * 100;
  private static final int NB_OF_BASES = 1000;
  
  
  public static void main(String[] args) throws IOException {
    
    if (args.length!=3) {
      printUsage();
      return;
    }
    
    File fileMatch = new File(args[0]);
    File fileMask = new File(args[1]);
    File dirStatus = new File(args[2]);
    
    FaceMashupGenerator fma = new FaceMashupGenerator(NB_OF_BASES, fileMatch, fileMask, dirStatus);
    GeneticAlgo ga = new GeneticAlgo(POP_SIZE, DNA_LENGTH, NB_OF_BASES, fma);
    ga.evolve(1000);
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
