package fr.jblezoray.mygeneticalgo.dna.image;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.IGeneticAlgoListener;

public class BasicAbstractImagePrinterListener<X extends AbstractImageDNA> 
implements IGeneticAlgoListener<X> {

  private final File statusDir;
  private final int generationInterval;
  private final static String DUMP_IMAGE_PREFIX = "gen";
  
  public BasicAbstractImagePrinterListener(File statusDir, int generationInterval) {
    this.statusDir = statusDir;
    this.generationInterval = generationInterval;
  }
  
  @Override
  public void notificationOfGeneration(int generation, X dnaBestMatch, 
      double[] allFitnessScores) {
    if (generation == 1 || generation % generationInterval == 0) {
      
      String filename = String.format("%s-%07d-%f.png", DUMP_IMAGE_PREFIX, 
          generation, dnaBestMatch.getFitness());
      
      try {
        dnaBestMatch.writeToFile(new File(statusDir, filename));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    
  }
}
