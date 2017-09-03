package fr.jblezoray.mygeneticalgo.sample.helloworld;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import fr.jblezoray.mygeneticalgo.DNAInteger;
import fr.jblezoray.mygeneticalgo.IPhenotype;

public class HelloWorldGenerator implements IPhenotype<DNAInteger> {

  private final String expectedResult;
  private final char[] bases;
  private final int NB_INDIVIDUALS = 100;
  
  public HelloWorldGenerator(String expectedResult, char[] bases) {
    this.expectedResult = expectedResult;
    this.bases = bases;
  }
  
  @Override
  public Collection<DNAInteger> createInitialPopulation(Random rand) {
    Set<DNAInteger> population = new HashSet<>();
    for (int i=0; i<NB_INDIVIDUALS; i++) {
      int nbBases = this.expectedResult.length();
      int maxValue = this.bases.length;
      DNAInteger individual = new DNAInteger(rand, nbBases, 0, maxValue);
      population.add(individual);
    }
    return population;
  }
  
  @Override
  public double computeFitness(DNAInteger dna) {
    int globalDistance = 0;
    for (int i=0; i<dna.size(); i++) {
      char expectedBase = expectedResult.charAt(i);
      char effectiveBase = bases[dna.getElementAt(i)];
      int distance = Math.abs(expectedBase - effectiveBase); 
      globalDistance += distance;
    }
    
    // normalize in 0 --> 1.
    return globalDistance==0 ? 1.0 : 1.0 / (double) globalDistance;
  }
  
  @Override
  public void notificationOfBestMatch(int generation, DNAInteger dna) {
    if (generation%50 == 0) {
      System.out.printf("Generation %6d (%.5f)--> %s\n", 
          generation, dna.getFitness(), buildStringFromDNA(dna));
    }
  }
  
  private String buildStringFromDNA(DNAInteger dna) {
    StringBuilder sb = new StringBuilder();
    for (Integer c : dna.getList())
      sb.append(this.bases[c]);
    return sb.toString();
  }
}
