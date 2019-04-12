package fr.jblezoray.mygeneticalgo.sample.stringart;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.GeneticAlgo;
import fr.jblezoray.mygeneticalgo.IDNAFactory;
import fr.jblezoray.mygeneticalgo.ISelection;
import fr.jblezoray.mygeneticalgo.dna.image.AbstractImageDNA;
import fr.jblezoray.mygeneticalgo.dna.image.FitnessRMSWithWeight;
import fr.jblezoray.mygeneticalgo.dna.image.ImageGraphicalPresentationListener;
import fr.jblezoray.mygeneticalgo.dna.image.UnmodifiableImageDNA;
import fr.jblezoray.mygeneticalgo.selection.BinaryTournamentSelection;
import fr.jblezoray.mygeneticalgo.utils.FitnessHistoryGraphicalPloter;
import fr.jblezoray.mygeneticalgo.utils.FitnessRepartitionTextPloterListener;
import fr.jblezoray.mygeneticalgo.utils.StatsListener;

public class Main {

  private final static int POP_SIZE = 200;
//  private final static int NB_STRINGS_PER_IMAGE = 600;
  private final static int NB_NAILS_PER_IMAGE = 100;
  private final static int LOG_INTERVAL = 10;
  private final static Color STRING_COLOR = new Color(0xFF,0xFF,0xFF, 0x3f);
  private final static Color BACKGROUND_COLOR = new Color(0, 0, 0);
  
  public static void main(String[] args) throws IOException {
    
//    if (args.length!=2) {
//      printUsage();
//      return;
//    }
//    
//    File fileImage = new File(args[0]);
//    File dirStatus = new File(args[1]);
    
    File fileImage = new File("samples/stringart/match.png");
    File fileImagePois = new File("samples/stringart/match_poi.png");

    ISelection<StringArtImageDNA> sel = new BinaryTournamentSelection<>();

    AbstractImageDNA image = new UnmodifiableImageDNA(fileImage, false);
    AbstractImageDNA weights = new UnmodifiableImageDNA(fileImagePois, false);
    
//    IFitness<StringArtImageDNA> fitness = 
//        new FitnessHistogramRMS<>(image);
//        new FitnessHistogramRMSWithWeight<>(image, 30);
//        new FitnessPatch<>(20, image);
    FitnessRMSWithWeight<StringArtImageDNA> fitness =
        new FitnessRMSWithWeight<>(image, weights);
    
    IDNAFactory<StringArtImageDNA> factory = new StringArtImageFactory(image,
        NB_NAILS_PER_IMAGE, BACKGROUND_COLOR, STRING_COLOR); 
    
    GeneticAlgo<StringArtImageDNA> ga = new GeneticAlgo<>(fitness, factory, sel, POP_SIZE);
//    ga.addListener(new ImageSaverListener<>(dirStatus, LOG_INTERVAL));
    ga.addListener(new FitnessRepartitionTextPloterListener<>(System.out, 10, 80, LOG_INTERVAL));
    ga.addListener(new StatsListener<>(System.out, LOG_INTERVAL));
    ga.addListener(new ImageGraphicalPresentationListener<>(image, 
        fitness.getWeightAsImage(), fitness.getWeightedOriginalImage()));
    ga.addListener(new FitnessHistoryGraphicalPloter<>());
    while (true) ga.evolve(2000);
  }

  private static void printUsage() {
    System.out.println("Generates a face by combinating strings like a string art canvas.");
    System.out.println("");
    System.out.println("usage : java -cp myGeneticAlgo.jar fr.jblezoray.mygeneticalgo.sample.stringart.Main matchImage statusDir ");
    System.out.println("");
    System.out.println("   matchImage : the goal image.");
    System.out.println("   statusDir  : directory where to store some intermediate results.");
    System.out.println("");
  }

}
