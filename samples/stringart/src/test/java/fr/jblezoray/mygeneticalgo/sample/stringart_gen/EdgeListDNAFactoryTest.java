package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.MIN_NAILS_DIFF;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.NB_NAILS;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeDrawer;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ImageSize;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

public class EdgeListDNAFactoryTest {

  private static EdgeFactory EDGE_FACTORY;
  
  @Before
  public void before() {
    // reset PRNG to get deterministic values regardless of tests order. 
    RandomSingleton.setDeterministic();
    // reinit everything with this new PRNG.
    ImageSize imageSize = new ImageSize(100, 100);
    EdgeDrawer edgeDrawer = new EdgeDrawer(imageSize, NB_NAILS, 0.15f, 2f);
    EDGE_FACTORY = new EdgeFactory(MIN_NAILS_DIFF, NB_NAILS, false, false, edgeDrawer);;
  }
  
  @Test
  public void a_big_generation_is_valid() {
    EdgeListDNAFactory factory = new EdgeListDNAFactory(EDGE_FACTORY, 
        10_000, MIN_NAILS_DIFF, NB_NAILS, false, false);
    
    EdgeListDNA dna = factory.createRandomIndividual();
    
    Assert.assertTrue(dna.isValid());
  }
  
}
