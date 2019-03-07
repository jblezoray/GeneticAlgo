package fr.jblezoray.mygeneticalgosample.helloworld;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.IDNAFactory;
import fr.jblezoray.mygeneticalgo.IFitness;
import fr.jblezoray.mygeneticalgo.IGeneticAlgoListener;
import fr.jblezoray.mygeneticalgo.ISelection;
import fr.jblezoray.mygeneticalgo.dna.integer.DNAInteger;
import fr.jblezoray.mygeneticalgo.selection.BinaryTournamentSelection;

public class HelloWorldMain implements 
    IFitness<DNAInteger>, IDNAFactory<DNAInteger>, IGeneticAlgoListener<DNAInteger> {

  private static final int LOG_INTERVAL = 50;
  private static final String EXPECTED_RESULT =
      "Hello world ! Le Lorem Ipsum est simplement du faux texte employé dans"
      + " la composition et la mise en page avant impression."; 
  private static final char[] BASES = 
      "abcdefghijklmnopqrstuvwxyz !.éHLI".toCharArray();

  public static void main(String[] args) { 
    HelloWorldMain hwg = new HelloWorldMain();
    ISelection<DNAInteger> sel = new BinaryTournamentSelection<>();
    
    GeneticAlgo<DNAInteger> ga = new GeneticAlgo<>(hwg, hwg, sel, 100);
    ga.setCrossoversRange(1, 5);
    ga.setMutationRate(0.005f);
    ga.addListener(hwg);
    ga.evolve(1_000);
  }
  
  
  @Override
  public double computeFitnessOf(DNAInteger dna) {
    int globalDistance = 0;
    for (int i=0; i<dna.size(); i++) {
      char expectedBase = EXPECTED_RESULT.charAt(i);
      char effectiveBase = BASES[dna.getElementAt(i)];
      int distance = expectedBase==effectiveBase ? 0 : 1; 
      globalDistance += distance;
    }
    
    // normalize in 0 --> 1.
    return 1.0f - (double)globalDistance / dna.size();
  }
  
  
  @Override
  public DNAInteger createRandomIndividual() {
      int nbBases = EXPECTED_RESULT.length();
      int maxValue = BASES.length;
      DNAInteger individual = new DNAInteger(nbBases, 0, maxValue);
      return individual;
  }


  @Override
  public void notificationOfGeneration(int generation, DNAInteger dnaBestMatch, double[] allFitnessScores) {
    if (generation % LOG_INTERVAL == 0) {
      System.out.printf("Generation %6d, best one (%.5f) --> %s\n", 
          generation, dnaBestMatch.getFitness(), toString(dnaBestMatch));
    }
  }

  
  private String toString(DNAInteger dna) {
    StringBuilder sb = new StringBuilder();
    for (Integer c : dna.getList())
      sb.append(BASES[c]);
    return sb.toString();
  }


  
}
