package fr.jblezoray.mygeneticalgo.sample.facemashup;

import fr.jblezoray.mygeneticalgo.DNA;
import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.IPhenotype;

/**
 *
 */
public class FaceMashupGenerator implements IPhenotype {
  private static final int POP_SIZE = 30;
  private static final int DNA_LENGTH = 250;
  private static final int NB_OF_BASES = 100;

  public static void main(String[] args){
    FaceMashupGenerator fma = new FaceMashupGenerator();
    GeneticAlgo ga = new GeneticAlgo(POP_SIZE, DNA_LENGTH, NB_OF_BASES, fma);
    ga.evolve(10000);
  }
  
  @Override
  public void notificationOfBestMatch(int generation, DNA dna) {
    // TODO
  }

  @Override
  public double computeFitness(DNA dna) {
//    BufferedImage phenotype = phenotypeObject.createPhenotype(DNA);
//    double similarity = calculateImageSimilarity(phenotype, source);
//    return ((similarity - similarityMin) / (similarityMax - similarityMin))*100;
    return 0.0;
  }

}
