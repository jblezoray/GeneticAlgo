package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.DEFAULT_EDGE_WAY;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.EDGE_WAY_ENABLED;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.FEATURES_IMAGE_PATH;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.GOAL_IMAGE_PATH;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.MIN_NAILS_DIFF;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.NB_NAILS;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.PIN_DIAMETER_MILLIMETERS;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.THREAD_THICKNESS_MILLIMETERS;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.IGeneticAlgoListener;
import fr.jblezoray.mygeneticalgo.ISelection;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeDrawer;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeImageIO;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ByteImage;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ImageSize;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;
import fr.jblezoray.mygeneticalgo.selection.BinaryTournamentSelection;
import fr.jblezoray.mygeneticalgo.utils.FitnessRepartitionTextPloterListener;
import fr.jblezoray.mygeneticalgo.utils.StatsListener;

public class Main {
  
  public static void main(String[] args) throws IOException {
    
    int nbIndividuals = 50;
    
    ByteImage refImg = EdgeImageIO.readFile(GOAL_IMAGE_PATH);
    ImageSize refImgSize = refImg.getSize();
    UnboundedImage refImgUnbounded = new UnboundedImage(refImgSize).add(refImg);
    ByteImage impImg = EdgeImageIO.readFile(FEATURES_IMAGE_PATH);
    EdgeDrawer edgeDrawer = new EdgeDrawer(refImgSize, NB_NAILS, 
        THREAD_THICKNESS_MILLIMETERS, PIN_DIAMETER_MILLIMETERS);
    EdgeFactory edgeFactory = new EdgeFactory(MIN_NAILS_DIFF, NB_NAILS, 
        EDGE_WAY_ENABLED, DEFAULT_EDGE_WAY, edgeDrawer); 
    Fitness fitness = 
          new FitnessOptimized(edgeFactory, refImgUnbounded, impImg, 100);
    
    StringPathDNAFactory dnaFactory = new StringPathDNAFactory(5_000, 
        MIN_NAILS_DIFF, NB_NAILS, EDGE_WAY_ENABLED, DEFAULT_EDGE_WAY);
    
    ISelection<StringPathDNA> selection = new BinaryTournamentSelection<>();

    GeneticAlgo<StringPathDNA> ga = new GeneticAlgo<>(fitness, dnaFactory, 
        selection, nbIndividuals);
    ga.addListener(new StatsListener<StringPathDNA>(System.out, 1));
    ga.addListener(new FitnessRepartitionTextPloterListener<StringPathDNA>(System.out, 10, 80, 5));
    ga.addListener(new ImagePrintListener(fitness, edgeDrawer, 1));
//    ga.addListener(new FitnessHistoryGraphicalPloter<>());
    while (true)
      ga.evolve();
    
    // TODO 
    // 1/ start evolving with an image reduced by a factor 4:1, and 2000 random 
    // edges, until there is no significant improvement in the fitness.
    // 2/ move to an image reduced only by a factor 2:1, then 1:1 
  }
  
  
  private static class ImagePrintListener implements IGeneticAlgoListener<StringPathDNA> {
    
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
                                         StringPathDNA dnaBestMatch, 
                                         double[] allFitnessScores) {
      if (generation%nbIntervals==0) {
        
        Set<Integer> dis = new HashSet<>();
        for (int i=1; i<dnaBestMatch.getSize(); i++) {
          dis.add(dnaBestMatch.getBase(i).getNail());
        }
        
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
  
}
