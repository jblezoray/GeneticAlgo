package fr.jblezoray.mygeneticalgo.utils;

import java.io.PrintStream;

import fr.jblezoray.mygeneticalgo.IGeneticAlgoListener;
import fr.jblezoray.mygeneticalgo.dna.IDNA;

/**
 * statistics about this generation. 
 * 
 * TODO min, max, std deviation
 * 
 * @author jbl
 *
 * @param <X>
 */
public class StatsListener<X extends IDNA> implements IGeneticAlgoListener<X> {

  private long timestamp;
  private PrintStream printStream;
  private final int generationInterval;
  
  public StatsListener(PrintStream printStream, int generationInterval) {
    this.printStream = printStream;
    this.timestamp = System.currentTimeMillis();
    this.generationInterval = generationInterval;
  }
  
  @Override
  public void notificationOfGeneration(int generation, X dnaBestMatch, double[] allFitnessScores) {
    long curtime = System.currentTimeMillis();
    long timediff = curtime - timestamp;
    this.timestamp = curtime;
    
    if (generation == 1 || generation % generationInterval == 0) {
      // print new status line.
      String statusLine = String.format("generation %7d : fitness %3.5f : time %7dms --> %s",
          generation, dnaBestMatch.getFitness(), timediff, dnaBestMatch.toString());
      this.printStream.println(statusLine);
    }
  }

}
