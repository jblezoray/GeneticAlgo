package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.awt.image.BufferedImage;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.IEvolver;
import fr.jblezoray.mygeneticalgo.Individual;

/**
 * Hello world!
 *
 */
public class FaceMashupGenerator implements IEvolver {
  private static final int POPULATION_SIZE = 30;
  private static final int NUMBER_OF_BASES = 100;
  private static final int DNA_LENGTH = 250;

  public static void main(String[] args){
    FaceMashupGenerator fma = new FaceMashupGenerator();
    GeneticAlgo ga = new GeneticAlgo(POPULATION_SIZE, DNA_LENGTH,  NUMBER_OF_BASES, 
        fma);
    ga.evolve(5); 
  }

  @Override
  public double computeFitness(Individual individual) {
    BufferedImage phenotype = phenotypeObject.createPhenotype(DNA);
    double similarity = calculateImageSimilarity(phenotype, source);
    return ((similarity - similarityMin) / (similarityMax - similarityMin))*100;
  }
}
