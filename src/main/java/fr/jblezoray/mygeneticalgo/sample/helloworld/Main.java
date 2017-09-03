package fr.jblezoray.mygeneticalgo.sample.helloworld;

import fr.jblezoray.mygeneticalgo.DNAInteger;
import fr.jblezoray.mygeneticalgo.GeneticAlgo;

public class Main {

  private static final String EXPECTED_RESULT = 
      "Hello world ! Le Lorem Ipsum est simplement du faux texte employé dans"
      + " la composition et la mise en page avant impression."; 
  
  private static final char[] BASES = 
      "abcdefghijklmnopqrstuvwxyz !.éHLI".toCharArray();
  
  public static void main(String[] args) {
    HelloWorldGenerator hwg = new HelloWorldGenerator(EXPECTED_RESULT, BASES);
    GeneticAlgo<DNAInteger> ga = new GeneticAlgo<>(hwg);
    ga.evolve(1000);
  }
}
