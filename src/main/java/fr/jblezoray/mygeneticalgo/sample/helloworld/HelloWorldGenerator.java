package fr.jblezoray.mygeneticalgo.sample.helloworld;

import fr.jblezoray.mygeneticalgo.DNA;
import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.IEvolver;
import fr.jblezoray.mygeneticalgo.IResultListener;
import fr.jblezoray.mygeneticalgo.Individual;

public class HelloWorldGenerator implements IEvolver, IResultListener {

  private static final String EXPECTED_RESULT = 
      "Hello world ! Le Lorem Ipsum est simplement du faux texte employé dans"
      + " la composition et la mise en page avant impression."; 
  
  private static final char[] BASES = 
      "abcdefghijklmnopqrstuvwxyz !.éHLI".toCharArray();
  
  public static void main(String[] args) {
    HelloWorldGenerator hwg = new HelloWorldGenerator();
    GeneticAlgo ga = new GeneticAlgo(EXPECTED_RESULT.length(), BASES.length, hwg);
    ga.setListener(hwg);
    while(true) {
      ga.evolve();
    }
  }

  @Override
  public double computeFitness(Individual individual) {
    DNA dna = individual.getDNA();
    int fitnessSum = 0;
    for (int i=0; i<dna.size(); i++) {
      char expectedBase = EXPECTED_RESULT.charAt(i);
      char effectiveBase = BASES[dna.get(i)];
      int distance = Math.abs(expectedBase - effectiveBase); 
      fitnessSum += distance;
    }
    return 1.0 / (double) fitnessSum;
  }
  
  @Override
  public void notificationOfBestMatch(int generation, double score, DNA dna) {
    if (generation%100 == 0)
      System.out.printf("Generation %6d (%.5f)--> %s\n", generation, score, buildStringFromDNA(dna)); 
  }
  
  private static String buildStringFromDNA(DNA dna) {
    StringBuilder sb = new StringBuilder();
    for (Integer c : dna) {
      sb.append(BASES[c]);
    }
    return sb.toString();
  }
}
