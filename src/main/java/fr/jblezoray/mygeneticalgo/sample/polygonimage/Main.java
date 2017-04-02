package fr.jblezoray.mygeneticalgo.sample.polygonimage;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitableImage;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitnessHistogramRMSWithWeight;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.IFitness;

/**
 * inspired by : 
 * https://rogerjohansson.blog/2008/12/07/genetic-programming-evolution-of-mona-lisa/
 * @author jib
 *
 */
public class Main {
  

  private static final int POP_SIZE = 100;
  private static final int DNA_LENGTH = 5000 * PolygonImageFactory.GENES_PER_IMAGE;
  private static final int NB_OF_BASES = 1000;

  public static void main(String[] args) throws IOException {
//    
//    if (args.length!=2) {
//      printUsage();
//      return;
//    }
//    
//    File imageFile = new File(args[0]);
//    File dirStatus = new File(args[1]);
//    File imageFile = new File("samples", "The_Scream.jpg");
//  File imageFile = new File("samples", "St_John_the_Baptist.jpg");
    File imageFile = new File("samples", "monalisa.jpg");
//    File imageFile = new File("samples", "monalisa_big.jpg");
    
    File dirStatus = new File("statusDir");
    
    FitnessHistogramRMSWithWeight fitness = new FitnessHistogramRMSWithWeight();
    PolygonImageGenerator pig = new PolygonImageGenerator(imageFile, fitness, 
        dirStatus, NB_OF_BASES);
    GeneticAlgo ga = new GeneticAlgo(POP_SIZE, DNA_LENGTH, NB_OF_BASES, pig);
    ga.setMutationRate(0.003f);
    ga.evolve();
    fitness.getWeightAsImage().writeToFile(new File(dirStatus, "weight.png"));
    while(true) ga.evolve();
  }

  private static void printUsage() {
    System.out.println("Generates an approximation of image by combinating polygons.");
    System.out.println("");
    System.out.println("usage : java -cp myGeneticAlgo.jar fr.jblezoray.mygeneticalgo.sample.polygonimage.Main image statusDir");
    System.out.println("");
    System.out.println("   image     : the goal image.");
    System.out.println("   statusDir : directory where to store some intermediate results.");
    System.out.println("");
  }
  
}
