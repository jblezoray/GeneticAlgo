package fr.jblezoray.mygeneticalgo.sample.stringart.genetic;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.IGeneticAlgoListener;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeDrawer;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeImageIO;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;

public class ImagePrintListener implements IGeneticAlgoListener<EdgeListDNA> {
  
  private Fitness fitness;
  private EdgeDrawer edgeDrawer;
  private int nbIntervals;
  
  public ImagePrintListener(Fitness fitness, 
                            EdgeDrawer edgeDrawer, 
                            int nbIntervals) {
    this.fitness = fitness;
    this.edgeDrawer = edgeDrawer;
    this.nbIntervals = nbIntervals;
  }

  @Override
  public void notificationOfGeneration(int generation, 
                                       EdgeListDNA dnaBestMatch, 
                                       double[] allFitnessScores) {
    if (generation%nbIntervals==0) {
      
      UnboundedImage image = this.fitness.drawImage(dnaBestMatch);
      image.add(this.edgeDrawer.drawAllNails());
      try {
        EdgeImageIO.writeToFile(image, new File("_rendering.png"));
      } catch (IOException e) {
        throw new RuntimeException("Cannot write image.", e);
      }
    }
  }
  
}