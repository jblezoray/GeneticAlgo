package fr.jblezoray.mygeneticalgo.sample.disksimage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.jblezoray.mygeneticalgo.crossover.EasyListCrossover;
import fr.jblezoray.mygeneticalgo.dna.IDNA;
import fr.jblezoray.mygeneticalgo.dna.image.AbstractImageDNA;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

public class DiskImageDNA extends AbstractImageDNA {

  private static final float MIN_RADIUS_RATIO = 0.01f; 
  private static final float MAX_RADIUS_RATIO = 0.20f;
  private static final int MAX_NUMBER_OF_DISKS = 500;
  
  private int width;
  private int height;
  
  private int bgColorRed;
  private int bgColorGreen;
  private int bgColorBlue;
  
  private List<DiskShape> disks; 
  
  /**
   * Initializes a blank image. 
   * 
   * @param width
   * @param height
   */
  public DiskImageDNA(int width, int height) {
    this.width = width;
    this.height = height;
    this.bgColorRed = 0;
    this.bgColorGreen = 0;
    this.bgColorBlue = 0;
    this.disks = new ArrayList<>();
  }

  /**
   * Copy constructor. 
   * @param copyMe
   */
  private DiskImageDNA(DiskImageDNA copyMe) {
    this.width = copyMe.width;
    this.height = copyMe.height;
    this.bgColorRed = copyMe.bgColorRed;
    this.bgColorGreen = copyMe.bgColorGreen;
    this.bgColorBlue = copyMe.bgColorBlue;
    this.disks = new ArrayList<>(); 
    for (DiskShape toCopy : copyMe.disks) {
      this.disks.add(new DiskShape(toCopy));
    }
  }
  
  @Override
  public void doMutate(float mutationRate) {
    Random rand = RandomSingleton.instance();
    
    // mutate background color ?  
    if (rand.nextFloat()<mutationRate) {
      this.bgColorRed = rand.nextInt(256);
      this.bgColorGreen = rand.nextInt(256);
      this.bgColorBlue = rand.nextInt(256);
      this.notifyImageUpdated();
    }
    
    // mutate by adding a new disk ? 
    if (rand.nextFloat()<mutationRate && this.disks.size() <= MAX_NUMBER_OF_DISKS) {
      DiskShape randomDiskShape = new DiskShape(
          rand.nextInt(this.width), rand.nextInt(this.height), 
          buildRandomRadius(rand),
          rand.nextInt(256), rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
      this.disks.add(randomDiskShape);
      this.notifyImageUpdated();
    }
    
    // mutate existing disks.
    for (DiskShape disk : disks) {
      // mutate this disk  ? 
      if (rand.nextFloat()>mutationRate)
        continue; // no.
      this.notifyImageUpdated();
      
      // mutate a random value from this element.
      switch (rand.nextInt(5)) {
      case 1: 
        disk.setOriginX(rand.nextInt(this.width));
        disk.setOriginY(rand.nextInt(this.height));
        break;
      case 2:
        disk.setAlpha(rand.nextInt(256));
        break;
      case 3: 
        disk.setRed(rand.nextInt(256));
        disk.setGreen(rand.nextInt(256));
        disk.setBlue(rand.nextInt(256));
        break;
      case 4: 
        disk.setRadius(buildRandomRadius(rand));
        break; 
      default: 
        break;
      }
    }
  }
  
  private int buildRandomRadius(Random rand) {
    int minRadius = (int)(width * MIN_RADIUS_RATIO) * 2;
    int maxRadius = (int)(width * MAX_RADIUS_RATIO) * 2;
    return minRadius + rand.nextInt(maxRadius - minRadius);
  }

  @Override
  public <X extends IDNA> void doDNACrossover(X other, int minCrossovers, int maxCrossovers) {
    List<DiskShape> out1 = new ArrayList<>(), out2 = new ArrayList<>();
    EasyListCrossover.<DiskShape>doCrossover(minCrossovers, maxCrossovers,
        this.disks, ((DiskImageDNA)other).disks, out1, out2);
    this.disks = out1;
    ((DiskImageDNA)other).disks = out2;
    
    this.notifyImageUpdated();
    ((DiskImageDNA)other).notifyImageUpdated();
  }

  @SuppressWarnings("unchecked")
  @Override
  public DiskImageDNA copy() {
    return new DiskImageDNA(this);
  }

  @Override
  protected BufferedImage buildImage() {
    BufferedImage image = new BufferedImage(this.width, this.height, 
        BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics2D = null;
    try {
      graphics2D = image.createGraphics();
      
      // set background color 
      graphics2D.setBackground(new Color(this.bgColorRed%0xFF, 
          this.bgColorGreen%0xFF, this.bgColorBlue%0xFF));
      graphics2D.clearRect(0, 0, this.width, this.height);
      
      // draw each shape: color, radius and coordinates are needed.
      for (DiskShape disk : this.disks) {
        graphics2D.setColor(new Color(disk.getRed()%0xFF, disk.getGreen()%0xFF,
            disk.getBlue()%0xFF, disk.getAlpha()%0xFF));
        graphics2D.fillOval(
            disk.getOriginX()-disk.getRadius()/2, 
            disk.getOriginY()-disk.getRadius()/2, 
            disk.getRadius(), 
            disk.getRadius());
      }
    } finally {
      if (graphics2D!=null)
        graphics2D.dispose();
    }
    return image;
  }
}
