package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.MIN_NAILS_DIFF;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.NB_NAILS;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeDrawer;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeImageIO;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ByteImage;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

public class FitnessTest {

  private static final String REF_IMG_RESOURCE_NAME = "test_image.png";
  private static final String POI_IMG_RESOURCE_NAME = "test_image.poi.png";
  private static UnboundedImage REF_IMG; 
  private static ByteImage POI_IMG; 
  private static EdgeFactory EDGE_FACTORY;
  private static StringPathDNAFactory DNA_FACTORY;
  private static int DNA_SIZE = 1_000;
  
  @BeforeClass
  public static void before_class() throws IOException {
    RandomSingleton.setDeterministic();
    ByteImage refImgByte = EdgeImageIO.readResource(REF_IMG_RESOURCE_NAME);
    REF_IMG = new UnboundedImage(refImgByte.getSize()).add(refImgByte);
    POI_IMG = EdgeImageIO.readResource(POI_IMG_RESOURCE_NAME);
    EdgeDrawer edgeDrawer = new EdgeDrawer(REF_IMG.getSize(), NB_NAILS, 0.15f, 2f);
    EDGE_FACTORY = new EdgeFactory(MIN_NAILS_DIFF, NB_NAILS, false, false, edgeDrawer); 
    DNA_FACTORY = new StringPathDNAFactory(DNA_SIZE, MIN_NAILS_DIFF, NB_NAILS, false, false);
  }
  
  /**
   * Creates a population composed of random albeit similar individuals. 
   * @param size
   * @return
   */
  private StringPathDNA[] createRelatedPopulation(int size) {
    StringPathDNA[] dnas = new StringPathDNA[size];
    dnas[0] = DNA_FACTORY.createRandomIndividual();
    Assert.assertTrue(DNA_FACTORY.isValid(dnas[0])); 
    for (int i=1; i<dnas.length; i++) {
      dnas[i] = dnas[i-1].copy();
      dnas[i].doMutate(0.015f);
    }
    for (StringPathDNA dna : dnas) 
      Assert.assertTrue(DNA_FACTORY.isValid(dna)); 
    return dnas;
  }
  
  @Test
  public void test() throws IOException {
    Fitness f = new Fitness(EDGE_FACTORY, REF_IMG, POI_IMG);
    FitnessOptimized fo = new FitnessOptimized(EDGE_FACTORY, REF_IMG, POI_IMG, 100);
    StringPathDNA[] dnas = createRelatedPopulation(3);

    for (StringPathDNA dna : dnas) {
      UnboundedImage uif = f.drawImage(dna);
      UnboundedImage uifo = fo.drawImage(dna);
      Image diff = uif.differenceWith(uifo);
      Assert.assertEquals(0.0, diff.l2norm(), 0.00001);
    }
  }
  
}
