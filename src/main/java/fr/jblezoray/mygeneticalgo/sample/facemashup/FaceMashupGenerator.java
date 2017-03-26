package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.DNA;
import fr.jblezoray.mygeneticalgo.IPhenotype;

/**
 * A genetic algo that generates a Face by combinating Face images. 
 * @author jib
 */
public class FaceMashupGenerator implements IPhenotype {

  private final IFitness faceMatchFitness;
  private final FaceImage faceMask;
  private final File statusDir;
  private final FaceImageFactory faceImageFactory;
  
  public FaceMashupGenerator(int numberOfBases, File faceMatch, File faceMask, File statusDir) 
      throws IOException{
    this.faceMatchFitness = FitnessHistogramRMSWithWeight.build(new FaceImage(faceMatch, false));
    this.faceMask = new FaceImage(faceMask, true);
    this.statusDir = statusDir;
    this.faceImageFactory = new FaceImageFactory(this.faceMask, numberOfBases);
  }
  
  @Override
  public void notificationOfBestMatch(int generation, DNA dna) {
    if (generation == 1 || generation % 100 == 0) {
      System.out.printf("generation %7d : fitness %f : dna : %s\n", 
          generation, dna.getFitness(), dna.toString());
      FaceImage bestMatch = this.faceImageFactory.fromDNA(dna);
      String filename = String.format("generation_%07d-%f.png", generation, dna.getFitness());
      try {
        bestMatch.writeToFile(new File(statusDir, filename));
      } catch (IOException e) {
        e.printStackTrace(); // TODO better error handling
      }
    }
  }

  @Override
  public double computeFitness(DNA dna) {
    FaceImage constructed = this.faceImageFactory.fromDNA(dna);
    return faceMatchFitness.computeFitnessOf(constructed);
  }

}
