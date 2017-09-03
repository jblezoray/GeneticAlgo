package fr.jblezoray.mygeneticalgo.sample.disksimage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import fr.jblezoray.mygeneticalgo.IPhenotype;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.AbstractFitableImage;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.IFitness;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.UnmodifiableFitableImage;

public class DisksImageGenerator implements IPhenotype<DisksImage> {

  private final AbstractFitableImage targetImage;
  private final IFitness fitness;
  private final File statusDir;
  
  private long timestamp;
  
  private static final int POPULATION_SIZE = 100;
  
  public DisksImageGenerator(File targetImage, IFitness fitness, File statusDir)
      throws IOException {
    this.targetImage = new UnmodifiableFitableImage(targetImage, false);
    this.fitness = fitness;
    this.fitness.init(this.targetImage);
    this.statusDir = statusDir;
    this.timestamp = System.currentTimeMillis();
  }

  @Override
  public Collection<DisksImage> createInitialPopulation(Random rand) {
    Collection<DisksImage> population = new ArrayList<>(POPULATION_SIZE);
    int width = this.targetImage.getImage().getWidth();
    int height = this.targetImage.getImage().getHeight();
    for (int i=0; i<POPULATION_SIZE; i++) {
      population.add(new DisksImage(width, height));
    }
    return population;
  }

  @Override
  public double computeFitness(DisksImage disksImage) {
    return fitness.computeFitnessOf(disksImage);
  }

  @Override
  public void notificationOfBestMatch(int generation, DisksImage disksImage) {
    // print new status line.
    long curtime = System.currentTimeMillis();
    long timediff = curtime - timestamp;
    this.timestamp = curtime;
    String statusLine = String.format("generation %7d : fitness %3.5f : time %7dms",
        generation, disksImage.getFitness(), timediff);
    System.out.println(statusLine);
    if (generation == 1 || generation % 200 == 0) {
      String filename = String.format("gen-%07d-%f.png", generation, 
          disksImage.getFitness());
      try {
        disksImage.writeToFile(new File(statusDir, filename));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
  
}
