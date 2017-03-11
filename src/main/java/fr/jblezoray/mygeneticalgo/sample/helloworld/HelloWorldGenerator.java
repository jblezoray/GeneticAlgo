package fr.jblezoray.mygeneticalgo.sample.helloworld;

import fr.jblezoray.mygeneticalgo.DNA;
import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.IPhenotype;

public class HelloWorldGenerator implements IPhenotype {

  private static final String EXPECTED_RESULT = 
      "Hello world ! Le Lorem Ipsum est simplement du faux texte employé dans"
      + " la composition et la mise en page avant impression."; 
  
  private static final char[] BASES = 
      "abcdefghijklmnopqrstuvwxyz !.éHLI".toCharArray();
  
  public static void main(String[] args) {
    HelloWorldGenerator hwg = new HelloWorldGenerator();
    GeneticAlgo ga = new GeneticAlgo(100, EXPECTED_RESULT.length(), 
        BASES.length, hwg);
    
    // Reducing the mutation rate after some time enables a faster convergence. 
    ga.evolve(300);
    ga.setMutationRate(0.000001f);
    ga.evolve(300);
  }

  @Override
  public double computeFitness(DNA dna) {
    int globalDistance = 0;
    for (int i=0; i<dna.size(); i++) {
      char expectedBase = EXPECTED_RESULT.charAt(i);
      char effectiveBase = BASES[dna.get(i)];
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
  
  private static String buildStringFromDNA(DNA dna) {
    StringBuilder sb = new StringBuilder();
    for (Integer c : dna) {
      sb.append(BASES[c]);
    }
    return sb.toString();
  }
}
