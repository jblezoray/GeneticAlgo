package fr.jblezoray.mygeneticalgo.sample.polygonimage;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.DNA;
import fr.jblezoray.mygeneticalgo.IPhenotype;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitableImage;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.IFitness;

public class PolygonImageGenerator implements IPhenotype {

  private final IFitness fitness;
  private final PolygonImageFactory polygonImageFactory;
  private final File statusDir;
  
  private int curLineLength = 0; 
  
  public PolygonImageGenerator(File imageFile, IFitness fitness, File statusDir, 
      int numberOfBases) throws IOException {
    FitableImage img = new FitableImage(imageFile, false);
    
    this.fitness = fitness;
    this.fitness.init(img);
    this.polygonImageFactory = new PolygonImageFactory(
        img.getImage().getWidth(), img.getImage().getHeight(), numberOfBases);
    this.statusDir = statusDir;
  }
  
  @Override
  public double computeFitness(DNA dna) {
    FitableImage constructed = this.polygonImageFactory.fromDNA(dna);
    return fitness.computeFitnessOf(constructed);
  }

  @Override
  public void notificationOfBestMatch(int generation, DNA dna) {
    
    // clear current line. 
    StringBuilder builder = new StringBuilder();
    for (int i=0; i<curLineLength; i++) builder.append('\b');
    System.out.print(builder.toString());
    
    // print new status line. 
    String statusLine = String.format("generation %7d : fitness %f",
        generation, dna.getFitness());
    curLineLength = statusLine.length();
    System.out.printf(statusLine);
    System.out.flush();
    
    if (generation == 1 || generation % 1000 == 0) {
      // keeps current status line, and moves to the next line. 
      System.out.println();
      System.out.flush();  
      curLineLength = 0;
      
      // save image as a file.
      FitableImage bestMatch = this.polygonImageFactory.fromDNA(dna);
      String filename = String.format("gen-%07d-%f.png", generation, dna.getFitness());
      try {
        bestMatch.writeToFile(new File(statusDir, filename));
      } catch (IOException e) {
        e.printStackTrace(); // TODO better error handling
      }
    }
  }

}
