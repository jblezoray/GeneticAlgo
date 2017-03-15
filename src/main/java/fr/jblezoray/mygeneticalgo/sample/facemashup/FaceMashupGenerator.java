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

  private final FaceImage faceMatch;
  private final FaceImage faceMask;
  private final File statusDir;
  private final FaceImageFactory faceImageFactory;
  
  public FaceMashupGenerator(int numberOfBases, File faceMatch, File faceMask, File statusDir) 
      throws IOException{
    this.faceMatch = new FaceImage(faceMatch);
    this.faceMask = new FaceImage(faceMask);
    this.statusDir = statusDir;
    this.faceImageFactory = new FaceImageFactory(this.faceMask, numberOfBases);
  }
  
  @Override
  public void notificationOfBestMatch(int generation, DNA dna) {
    if (generation == 1 || generation % 100 == 0) {
      System.out.printf("generation %7d : fitness %f\n", generation, dna.getFitness());
      FaceImage bestMatch = this.faceImageFactory.fromDNA(dna);
      String filename = String.format("generation_%07d.png", generation);
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
    return faceMatch.computeFitnessOf(constructed);
  }

}
