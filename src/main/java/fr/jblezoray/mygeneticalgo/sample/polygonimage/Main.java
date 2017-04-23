package fr.jblezoray.mygeneticalgo.sample.polygonimage;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitnessHistogramRMS;

/**
 * inspired by : 
 * https://rogerjohansson.blog/2008/12/07/genetic-programming-evolution-of-mona-lisa/
 * @author jib
 *
 */
public class Main {
  

  private static final int POP_SIZE = 100;
  private static final int DNA_LENGTH = 500 * CircleImageFactory.GENES_PER_IMAGE;
  private static final int NB_OF_BASES = 1000;

  public static void main(String[] args) throws IOException {

//    if (args.length!=2) {
//      printUsage();
//      return;
//    }
    File imageFile = new File("samples", "monalisa_croped.png");
    File dirStatus = new File("statusDir");
    
    FitnessHistogramRMS fitness = new FitnessHistogramRMS();
    GeneticImageGenerator gig = new GeneticImageGenerator(imageFile, fitness, 
        dirStatus, NB_OF_BASES);
    GeneticAlgo ga = new GeneticAlgo(POP_SIZE, DNA_LENGTH, NB_OF_BASES, gig);
    ga.setMutationRate(0.005f);
    ga.evolve();
    while (true) {
      ga.evolve();
    }
  }

//  private static void printUsage() {
//    System.out.println("Generates an approximation of image by combinating polygons.");
//    System.out.println("");
//    System.out.println("usage : java -cp myGeneticAlgo.jar fr.jblezoray.mygeneticalgo.sample.polygonimage.Main image statusDir");
//    System.out.println("");
//    System.out.println("   image     : the goal image.");
//    System.out.println("   statusDir : directory where to store some intermediate results.");
//    System.out.println("");
//  }
  
}
