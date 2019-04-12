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

import fr.jblezoray.mygeneticalgo.crossover.EasyListCrossover;
import fr.jblezoray.mygeneticalgo.dna.IDNA;
import fr.jblezoray.mygeneticalgo.dna.image.AbstractImageDNA;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

/**
 * {@code FaceImage} represents an image that is generated from a set 
 * of characteristics modeled as a set of {@code FaceElements}.
 */
public class FaceImageDNA extends AbstractImageDNA {
  
  private List<FaceElement> faceElements;
  private int width;
  private int height;
  private final List<BufferedImage> allSizesBuffer;
  
  /**
   * Copy constructor.
   * @param toCopy
   */
  private FaceImageDNA(FaceImageDNA toCopy) {
    this.faceElements = toCopy.faceElements
        .parallelStream()
        .map(fe -> new FaceElement(fe))
        .collect(Collectors.toList());
    this.width = toCopy.width;
    this.height = toCopy.height;
    this.allSizesBuffer = toCopy.allSizesBuffer;
  }

  public FaceImageDNA(List<FaceElement> faceElements, int width, int height,
      List<BufferedImage> allSizesBuffer) {
    this.width = width;
    this.height = height;
    this.faceElements = Collections.unmodifiableList(faceElements);
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
      for (FaceElement faceElement : faceElements)
        drawFace(graphics2D, faceElement);
      
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
  private void drawFace(Graphics2D target, FaceElement fe) {
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
  public void doMutate(float mutationRates) {
    Random rand = RandomSingleton.instance();
    for (FaceElement fe : this.faceElements) {
      // do a mutation ? 
      if (rand.nextFloat()>mutationRates) 
        continue;// no.
      this.notifyImageUpdated();
      
      // mutate a random value from this element.
      switch (rand.nextInt(4)) {
      case 0:
        fe.setAlpha(FaceImageDNAFactory.randomAlpha());
        break;
      case 1: 
        // update the whole position.
        fe.setPositionX(FaceImageDNAFactory.randomPosition(this.width));
        fe.setPositionY(FaceImageDNAFactory.randomPosition(this.height));
        break;
      case 2: 
        fe.setRotation(FaceImageDNAFactory.randomRotation());
        break;
      case 3: 
        fe.setSize(FaceImageDNAFactory.randomSize());
        break;
      default:
        break;
      }
    }
  }

  @Override
  public <X extends IDNA> void doDNACrossover(X other, int minCrossovers, int maxCrossovers) {
    if (!(other instanceof FaceImageDNA)) throw new RuntimeException();
    List<FaceElement> out1 = new ArrayList<>(), out2 = new ArrayList<>();
    EasyListCrossover.<FaceElement>doCrossover(minCrossovers, maxCrossovers,
        this.faceElements, ((FaceImageDNA)other).faceElements, out1, out2);
    this.faceElements = out1;
    ((FaceImageDNA)other).faceElements = out2;
    
    this.notifyImageUpdated();
    ((FaceImageDNA)other).notifyImageUpdated();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <X extends IDNA> X copy() {
    return (X) new FaceImageDNA(this);
  }

  public List<FaceElement> getFaceElements() {
    return faceElements;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("[");
    for (FaceElement fe : this.faceElements) {
      String feStr = String.format("%.3f %4d %4d %.3f %3d", fe.getAlpha(),
          fe.getPositionX(), fe.getPositionY(), fe.getRotation(), fe.getSize());
      sb = sb.append(feStr).append(", ");
    }
    sb = sb.deleteCharAt(sb.length()-1).append("]");
    return sb.toString();
  }
  
}
