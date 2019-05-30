package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.CANVAS_WIDTH_MILLIMETERS;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.DEFAULT_EDGE_WAY;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.EDGE_WAY_ENABLED;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.FEATURES_IMAGE_PATH;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.GOAL_IMAGE_PATH;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.MIN_NAILS_DIFF;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.NB_NAILS;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.PIN_DIAMETER_MILLIMETERS;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.THREAD_THICKNESS_MILLIMETERS;

import java.io.IOException;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;
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
    
    int nbIndividuals = 100;
    
    ByteImage refImg = EdgeImageIO.readFile(GOAL_IMAGE_PATH);
    ImageSize refImgSize = refImg.getSize();
    UnboundedImage refImgUnbounded = new UnboundedImage(refImgSize).add(refImg);
    
    ByteImage impImg = EdgeImageIO.readFile(FEATURES_IMAGE_PATH);
    
    float resolutionMmPerPx = CANVAS_WIDTH_MILLIMETERS / refImgSize.w;
    float lineThicknessInPx = THREAD_THICKNESS_MILLIMETERS / resolutionMmPerPx;
    float nailDiameterInPx = PIN_DIAMETER_MILLIMETERS / resolutionMmPerPx;
    EdgeDrawer edgeDrawer = new EdgeDrawer(refImgSize, NB_NAILS, 
        lineThicknessInPx, nailDiameterInPx);
    EdgeFactory edgeFactory = new EdgeFactory(MIN_NAILS_DIFF, NB_NAILS, 
        EDGE_WAY_ENABLED, DEFAULT_EDGE_WAY, edgeDrawer); 
    Fitness fitness = 
          new FitnessFast(refImgUnbounded, impImg, 100);
    
    EdgeListDNAFactory dnaFactory = new EdgeListDNAFactory(edgeFactory, 5_000, 
        MIN_NAILS_DIFF, NB_NAILS, EDGE_WAY_ENABLED, DEFAULT_EDGE_WAY);
    
    ISelection<EdgeListDNA> selection = new BinaryTournamentSelection<>();

    GeneticAlgo<EdgeListDNA> ga = new GeneticAlgo<>(fitness, dnaFactory, 
        selection, nbIndividuals);
    ga.addListener(new StatsListener<EdgeListDNA>(System.out, 1));
    ga.addListener(new FitnessRepartitionTextPloterListener<EdgeListDNA>(System.out, 10, 80, 5));
    ga.addListener(new ImagePrintListener(fitness, edgeDrawer, 10));
//    ga.addListener(new FitnessHistoryGraphicalPloter<>());
    ga.setCrossoversRange(1, 1);
    ga.setMutationRate(0.02f);
    while (true)
      ga.evolve();
    
    // TODO 
    // 1/ start evolving with an image reduced by a factor 4:1, and 2000 random 
    // edges, until there is no significant improvement in the fitness.
    // 2/ move to an image reduced only by a factor 2:1, then 1:1 
  }
  
}
