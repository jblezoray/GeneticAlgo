package fr.jblezoray.mygeneticalgo.sample.stringart;

import static fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Constants.CANVAS_WIDTH_MILLIMETERS;
import static fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Constants.DEFAULT_EDGE_WAY;
import static fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Constants.EDGE_WAY_ENABLED;
import static fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Constants.FEATURES_IMAGE_PATH;
import static fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Constants.GOAL_IMAGE_PATH;
import static fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Constants.MIN_NAILS_DIFF;
import static fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Constants.NB_NAILS;
import static fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Constants.PIN_DIAMETER_MILLIMETERS;
import static fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Constants.THREAD_THICKNESS_MILLIMETERS;

import java.io.IOException;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.ISelection;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeDrawer;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeImageIO;
import fr.jblezoray.mygeneticalgo.sample.stringart.genetic.EdgeListDNA;
import fr.jblezoray.mygeneticalgo.sample.stringart.genetic.EdgeListDNAFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Fitness;
import fr.jblezoray.mygeneticalgo.sample.stringart.genetic.FitnessFast;
import fr.jblezoray.mygeneticalgo.sample.stringart.genetic.ImagePrintListener;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ByteImage;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ImageSize;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;
import fr.jblezoray.mygeneticalgo.selection.BinaryTournamentSelection;
import fr.jblezoray.mygeneticalgo.utils.FitnessRepartitionTextPloterListener;
import fr.jblezoray.mygeneticalgo.utils.StatsListener;

public class GeneticMain {
  
  public static void main(String[] args) throws IOException {
    
    int nbIndividuals = 100;
    
    System.out.println("loading files ...");
    ByteImage refImg = EdgeImageIO.readFile(GOAL_IMAGE_PATH);
    ByteImage impImg = EdgeImageIO.readFile(FEATURES_IMAGE_PATH);
    ImageSize refImgSize = refImg.getSize();
    UnboundedImage refImgUnbounded = new UnboundedImage(refImgSize).add(refImg);

    System.out.println("loading edge drawer ...");
    float resolutionMmPerPx = CANVAS_WIDTH_MILLIMETERS / refImgSize.w;
    float lineThicknessInPx = THREAD_THICKNESS_MILLIMETERS / resolutionMmPerPx;
    float nailDiameterInPx = PIN_DIAMETER_MILLIMETERS / resolutionMmPerPx;
    EdgeDrawer edgeDrawer = new EdgeDrawer(refImgSize, NB_NAILS, 
        lineThicknessInPx, nailDiameterInPx);
    
    System.out.println("initalizing edge factory (may took a few seconds) ...");
    EdgeFactory edgeFactory = new EdgeFactory(MIN_NAILS_DIFF, NB_NAILS, 
        EDGE_WAY_ENABLED, DEFAULT_EDGE_WAY, edgeDrawer); 
    Fitness fitness = 
          new FitnessFast(refImgUnbounded, impImg, 100);

    System.out.println("initalizing edge factory ...");
    EdgeListDNAFactory dnaFactory = new EdgeListDNAFactory(edgeFactory, 5_000, 
        MIN_NAILS_DIFF, NB_NAILS, EDGE_WAY_ENABLED, DEFAULT_EDGE_WAY);
    
    System.out.println("initalizing selection function ...");
    ISelection<EdgeListDNA> selection = new BinaryTournamentSelection<>();

    System.out.println("initalizing genetic algo ...");
    GeneticAlgo<EdgeListDNA> ga = new GeneticAlgo<>(fitness, dnaFactory, 
        selection, nbIndividuals);
    
    System.out.println("initalizing listeners ...");
    ga.addListener(new StatsListener<EdgeListDNA>(System.out, 1));
    ga.addListener(new FitnessRepartitionTextPloterListener<EdgeListDNA>(System.out, 10, 80, 5));
    ga.addListener(new ImagePrintListener(fitness, edgeDrawer, 10));
//    ga.addListener(new FitnessHistoryGraphicalPloter<>());
    ga.setCrossoversRange(1, 1);
    ga.setMutationRate(0.02f);
    
    System.out.println("starting to evolve.");
    while (true)
      ga.evolve();
    
    // TODO 
    // 1/ start evolving with an image reduced by a factor 4:1, and 2000 random 
    // edges, until there is no significant improvement in the fitness.
    // 2/ move to an image reduced only by a factor 2:1, then 1:1 
  }
  
}
