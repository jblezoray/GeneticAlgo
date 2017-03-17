package fr.jblezoray.mygeneticalgo.sample.helloworld;

import fr.jblezoray.mygeneticalgo.DNA;
import fr.jblezoray.mygeneticalgo.IPhenotype;

public class HelloWorldGenerator implements IPhenotype {

  private final String expectedResult;
  private final char[] bases;
  
  public HelloWorldGenerator(String expectedResult, char[] bases) {
    this.expectedResult = expectedResult;
    this.bases = bases;
  }

  @Override
  public double computeFitness(DNA dna) {
    int globalDistance = 0;
    for (int i=0; i<dna.size(); i++) {
      char expectedBase = expectedResult.charAt(i);
      char effectiveBase = bases[dna.get(i)];
      int distance = Math.abs(expectedBase - effectiveBase); 
      globalDistance += distance;
    }
    return globalDistance==0 ? 1.0 : 1.0 / (double) globalDistance;
  }
  
  @Override
  public void notificationOfBestMatch(int generation, DNA dna) {
    if (generation%50 == 0) {
      System.out.printf("Generation %6d (%.5f)--> %s\n", 
          generation, dna.getFitness(), buildStringFromDNA(dna));
    }
  }
  
  private String buildStringFromDNA(DNA dna) {
    StringBuilder sb = new StringBuilder();
    for (Integer c : dna) {
      sb.append(this.bases[c]);
    }
    return sb.toString();
  }
}
