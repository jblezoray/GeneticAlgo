package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fr.jblezoray.mygeneticalgo.IPhenotype;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.AbstractFitableImage;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.IFitness;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.UnmodifiableFitableImage;

/**
 * A genetic algo that generates a Face by combinating Face images. 
 * @author jib
 */
public class FaceMashupGenerator implements IPhenotype<FaceImage> {

  private final static String DUMP_IMAGE_PREFIX = "gen";

  private final IFitness faceMatchFitness;
  private final AbstractFitableImage faceMask;
  private final File statusDir;
  private final FaceImageFactory faceImageFactory;
  private final int populationSize;
  private final int nbFacesPerImage;
  
  public FaceMashupGenerator(File faceMatch, File faceMask, File statusDir, 
      IFitness fitness, int populationSize, int nbFacesPerImage) 
      throws IOException{
    this.faceMatchFitness = fitness;
    this.faceMatchFitness.init(new UnmodifiableFitableImage(faceMatch, false));
    this.faceMask = new UnmodifiableFitableImage(faceMask, true);
    this.statusDir = statusDir;
    this.faceImageFactory = new FaceImageFactory(this.faceMask);
    this.populationSize = populationSize;
    this.nbFacesPerImage = nbFacesPerImage;
  }
  
  @Override
  public Collection<FaceImage> createInitialPopulation(Random rand) {
    return IntStream
        .range(0, populationSize)
        .parallel()
        .mapToObj(i -> this.faceImageFactory.createRandomFaceImage(rand, nbFacesPerImage))
        .collect(Collectors.toList());
  }
  
  @Override
  public void notificationOfBestMatch(int generation, FaceImage bestMatch) {
    if (generation == 1 || generation % 100 == 0) {
      System.out.printf("%s %7d : fitness %f : dna : %s\n",
          DUMP_IMAGE_PREFIX, generation, bestMatch.getFitness(), 
          bestMatch.toString());
      String filename = String.format("%s-%07d-%f.png", DUMP_IMAGE_PREFIX, 
          generation, bestMatch.getFitness());
      try {
        bestMatch.writeToFile(new File(statusDir, filename));
      } catch (IOException e) {
        throw new RuntimeException("Cannot write to file.", e); 
      }
    }
  }

  @Override
  public double computeFitness(FaceImage faceImage) {
    return faceMatchFitness.computeFitnessOf(faceImage);
  }

}
