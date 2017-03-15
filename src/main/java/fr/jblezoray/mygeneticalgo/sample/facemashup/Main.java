package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;

public class Main {

  private static final int POP_SIZE = 20;
  private static final int DNA_LENGTH = 5 * 50;
  private static final int NB_OF_BASES = 1000;
  
  private static final String WORK_DIR = "/Users/jib/Data/workspace/"
          + "2017 03 04 genetic algo/workspace/results/java";
  private static final File FILE_MASK = new File(WORK_DIR, "mask.png");
  private static final File FILE_MATCH = new File(WORK_DIR, "match.png");
  private static final File DIR_STATUS = new File(WORK_DIR, "status");
  
  public static void main(String[] args) throws IOException {
    FaceMashupGenerator fma = new FaceMashupGenerator(NB_OF_BASES, FILE_MATCH, FILE_MASK, DIR_STATUS);
    GeneticAlgo ga = new GeneticAlgo(POP_SIZE, DNA_LENGTH, NB_OF_BASES, fma);
    ga.setTournamentFraction(0.4f);
    ga.setMutationRate(0.0001f);
    ga.evolve(1000);
    
    ga.setTournamentFraction(0.7f);
    ga.setMutationRate(0.00001f);
    ga.evolve(1000);
    
    ga.setTournamentFraction(1.0f);
    ga.evolve(98000);
  }

}
