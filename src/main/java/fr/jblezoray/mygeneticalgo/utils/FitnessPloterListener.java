package fr.jblezoray.mygeneticalgo.utils;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.OptionalDouble;

import fr.jblezoray.mygeneticalgo.IGeneticAlgoListener;
import fr.jblezoray.mygeneticalgo.dna.IDNA;


/**
 * A listener that plots the fitnesses repartition of the population. 
 * 
 * @author jbl
 *
 * @param <X>
 */
public class FitnessPloterListener<X extends IDNA> implements IGeneticAlgoListener<X> {
  
  private final PrintStream stream;
  private final int nbIntervals;
  private final int printedWidth;
  private final int generationInterval;

  private Double min = null;
  private Double max = null;
  
  private double[] intervals;
  private int[] valuesPerInterval;
  private int[] widthPerInterval;
  
  public FitnessPloterListener(PrintStream stream, int nbIntervals, int printedWidth, int generationInterval) {
    this.stream = stream;
    this.nbIntervals = nbIntervals;
    this.printedWidth = printedWidth;
    this.generationInterval = generationInterval;
  }
  
  @Override
  public void notificationOfGeneration(int generation, X dnaBestMatch, 
      double[] allFitnessScores) {
    if (generation == 1 || generation % generationInterval == 0) {
      updateMinAndMax(allFitnessScores);
      establishIntervals();
      countValuesPerInterval(allFitnessScores);
      computeWidthPerInterval();
      plot();
    }
  }

  private void updateMinAndMax(double[] values) {
    OptionalDouble maxValue = Arrays.stream(values).max();
    OptionalDouble minValue = Arrays.stream(values).min(); 
    if (!minValue.isPresent() || !maxValue.isPresent()) 
      throw new RuntimeException("invalid values");
    if (this.max==null || maxValue.getAsDouble() > this.max ) 
      this.max = maxValue.getAsDouble();
    this.min = minValue.getAsDouble();
  }

  private void establishIntervals() {
    intervals = new double[nbIntervals+1];
    double intervalWidth = (double)(max - min) / (double)nbIntervals;
    intervals[0] = min;
    intervals[intervals.length-1] = max;
    for (int i=1; i<intervals.length-1; i++) 
      intervals[i] = intervals[i-1] + intervalWidth;
  }

  private void countValuesPerInterval(double[] values) {
    valuesPerInterval = new int[nbIntervals];
    for (int i=0; i<valuesPerInterval.length; i++) {
      double intervalBegining = intervals[i];
      double intervalEnding = intervals[i+1];
      valuesPerInterval[i] = 0;
      for (double v : values) {
        if (intervalBegining==min && v == min ||
            intervalBegining<v && v<=intervalEnding) {
          valuesPerInterval[i]++;
        }
      }
    }
  }

  private void computeWidthPerInterval() {
    int max = Arrays.stream(valuesPerInterval).max().getAsInt();
    this.widthPerInterval = new int[nbIntervals];
    for (int i=0; i<valuesPerInterval.length; i++)
      this.widthPerInterval[i] = Normalizer.normalizeInteger(
          valuesPerInterval[i], 0, max, 0, printedWidth);
  }

  private void plot() {
    for (int i=0; i<this.widthPerInterval.length; i++) {
      String bar = new String(new char[this.widthPerInterval[i]]).replace('\0', '*');
      double start = this.intervals[i];
      double end = this.intervals[i+1];
      int nbValues = this.valuesPerInterval[i];
      this.stream.println(String.format("[%5f..%5f] %3d %s", start, end, nbValues, bar));
    }
  }
  

}
