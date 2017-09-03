package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import fr.jblezoray.mygeneticalgo.DNAAbstract;
import fr.jblezoray.mygeneticalgo.EasyListCrossover;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.AbstractFitableImage;

/**
 * {@code FaceImage} represents an image that is generated from a set 
 * of characteristics modeled as a set of {@code FaceElements}.
 */
public class FaceImage extends AbstractFitableImage {
  
  private List<FaceElements> faceElementsList;
  private int width;
  private int height;
  private final List<BufferedImage> allSizesBuffer;

  
  /**
   * Copy constructor.
   * @param toCopy
   */
  private FaceImage(FaceImage toCopy) {
    this.faceElementsList = toCopy.faceElementsList
        .parallelStream()
        .map(fe -> new FaceElements(fe))
        .collect(Collectors.toList());
    this.width = toCopy.width;
    this.height = toCopy.height;
    this.allSizesBuffer = toCopy.allSizesBuffer;
  }

  public FaceImage(List<FaceElements> faceElementsList, int width, int height,
      List<BufferedImage> allSizesBuffer) {
    this.width = width;
    this.height = height;
    this.faceElementsList = Collections.unmodifiableList(faceElementsList);
    this.allSizesBuffer = allSizesBuffer;
  }
  
  /**
   * Create the image using the {@code faceElementsList} as a generator.
   * 
   * @return the generated image.
   */
  @Override
  protected BufferedImage buildImage() {
    // create a new blank image. 
    BufferedImage image = new BufferedImage(
        this.width, this.height, BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics2D = null;
    try {
      graphics2D = image.createGraphics();
      graphics2D.setBackground(Color.WHITE);
      graphics2D.clearRect(0, 0, this.width, this.height);
      
      // generate image per image.
      for (FaceElements faceElements : faceElementsList)
        drawFace(graphics2D, faceElements);
      
    } finally {
      if (graphics2D!=null)
        graphics2D.dispose();
    }
    return image;
  }

  
  /**
   * Draw a single face in the {@code target} canvas, on the basis of the 
   * {@code faceElements}.
   *  
   * @param graphics2D
   * @param faceElements
   */
  private void drawFace(Graphics2D target, FaceElements fe) {
    BufferedImage imgToDraw = getImageFromSize(fe.getSize());
    int imgW = imgToDraw.getWidth();
    int imgH = imgToDraw.getHeight();
    
    AffineTransform transformation = new AffineTransform();
    transformation.translate(fe.getPositionX()-imgW/2, fe.getPositionY()-imgH/2);
    transformation.rotate(fe.getRotation(), imgW/2, imgH/2);
    target.setComposite(
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fe.getAlpha()));
    target.drawImage(imgToDraw, transformation, null);
  }
  
  private BufferedImage getImageFromSize(int sizeIndex) {
    return this.allSizesBuffer.get(sizeIndex);
  }

  @Override
  public void doMutate(Random rand, float mutationRates) {
    for (FaceElements fe : this.faceElementsList) {
      // do a mutation ? 
      if (rand.nextFloat()>mutationRates) 
        continue;// no.
      this.notifyImageUpdated();
      
      // mutate a random value from this element.
      switch (rand.nextInt(4)) {
      case 0:
        fe.setAlpha(FaceImageFactory.randomAlpha(rand));
        break;
      case 1: 
        // update the whole position.
        fe.setPositionX(FaceImageFactory.randomPosition(rand, this.width));
        fe.setPositionY(FaceImageFactory.randomPosition(rand, this.height));
        break;
      case 2: 
        fe.setRotation(FaceImageFactory.randomRotation(rand));
        break;
      case 3: 
        fe.setSize(FaceImageFactory.randomSize(rand));
        break;
      default:
        break;
      }
    }
  }

  @Override
  public void doDNACrossover(Random rand, DNAAbstract other, int minCrossovers,
      int maxCrossovers) {
    List<FaceElements> out1 = new ArrayList<>(), out2 = new ArrayList<>();
    EasyListCrossover.<FaceElements>doCrossover(rand, minCrossovers, maxCrossovers,
        this.faceElementsList, ((FaceImage)other).faceElementsList, out1, out2);
    this.faceElementsList = out1;
    ((FaceImage)other).faceElementsList = out2;
    
    this.notifyImageUpdated();
    ((FaceImage)other).notifyImageUpdated();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <DNA extends DNAAbstract> DNA copy() {
    return (DNA) new FaceImage(this);
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer("[");
    for (FaceElements fe : faceElementsList) {
      String feStr = String.format("%.3f %4d %4d %.3f %3d", fe.getAlpha(),
          fe.getPositionX(), fe.getPositionY(), fe.getRotation(), fe.getSize());
      sb = sb.append(feStr).append(", ");
    }
    sb = sb.deleteCharAt(sb.length()-1).append("]");
    return sb.toString();
  }
  
}
