package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.jblezoray.mygeneticalgo.IDNAFactory;
import fr.jblezoray.mygeneticalgo.dna.image.AbstractImageDNA;
import fr.jblezoray.mygeneticalgo.utils.Normalizer;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

/**
 * Builds an Image from some DNA.
 */
public class FaceImageDNAFactory implements IDNAFactory<FaceImageDNA> {

  private static final int NUMBER_OF_SIZE = 20;
  private static final float MIN_ALPHA = 0.5f;
  private static final float MAX_ALPHA = 0.9f;
  private static final float MIN_SCALE_RATIO = 0.1f;
  private static final float MAX_SCALE_RATIO = 0.4f;
  private static final float MIN_ROTATION = 0.0f; 
  private static final float MAX_ROTATION = (float)(2.0f * Math.PI);

  private final List<BufferedImage> allSizes;
  private final int nbFaces;
  private int w;
  private int h;
  
  public FaceImageDNAFactory(AbstractImageDNA originalFace, int nbFaces) throws IOException {
    this.w = originalFace.getImage().getWidth();
    this.h = originalFace.getImage().getHeight();
    this.allSizes = createAllSizes(originalFace);
    this.nbFaces = nbFaces;
  }

  /**
   * Initializes allSizes by creating 'NUMBER_OF_SIZE' images, whose ratio in 
   * size is between MIN_SCALE and MAX_SCALE, proportionally to the original
   * image.  
   *
   * @param face
   * @throws IOException
   */
  private List<BufferedImage> createAllSizes(AbstractImageDNA face) 
      throws IOException {
    List<BufferedImage> allSizes = new ArrayList<>(NUMBER_OF_SIZE);
    BufferedImage sourceImage = face.getImage();
    for (int n=0; n<NUMBER_OF_SIZE; n++) {
      float scale = Normalizer.normalizeFloat(n, 0, NUMBER_OF_SIZE, 
          MIN_SCALE_RATIO, MAX_SCALE_RATIO);
      int newWidth = Math.round(this.w * scale);
      int newHeight = Math.round(this.h * scale);
      BufferedImage resizedImg = new BufferedImage(newWidth, newHeight, 
          BufferedImage.TYPE_4BYTE_ABGR);
      
      Graphics2D g = resizedImg.createGraphics();
      g.setRenderingHint(
          RenderingHints.KEY_INTERPOLATION, 
          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(sourceImage, 0, 0, newWidth, newHeight, null);
      g.dispose();
      
      allSizes.add(resizedImg);
    }
    return allSizes;
  }
  
  @Override
  public FaceImageDNA createRandomIndividual() {
    List<FaceElement> faceElementsList = new ArrayList<>(this.nbFaces);
    for (int i=0; i<this.nbFaces; i++) {
      FaceElement ramdomFaceElements = new FaceElement(
          randomPosition(this.w),
          randomPosition(this.h),
          randomSize(),
          randomRotation(),
          randomAlpha());
      faceElementsList.add(i, ramdomFaceElements);
    }
    return new FaceImageDNA(faceElementsList, this.w, this.h, this.allSizes);
  }
  
  public static int randomPosition(int maxValue) {
    return RandomSingleton.instance().nextInt(maxValue);
  }
  
  public static int randomSize() {
    return RandomSingleton.instance().nextInt(NUMBER_OF_SIZE);
  }
  
  public static float randomRotation() {
    return Normalizer.normalizeFloat(
        RandomSingleton.instance().nextFloat(), 0.0f, 1.0f, MIN_ROTATION, MAX_ROTATION);
  }
  
  public static float randomAlpha() {
    return Normalizer.normalizeFloat(
        RandomSingleton.instance().nextFloat(), 0.0f, 1.0f, MIN_ALPHA, MAX_ALPHA);
  }
  
}
