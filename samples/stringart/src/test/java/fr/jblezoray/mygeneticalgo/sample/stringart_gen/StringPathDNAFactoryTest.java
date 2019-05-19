package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.MIN_NAILS_DIFF;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.NB_NAILS;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

public class StringPathDNAFactoryTest {

  @BeforeClass
  public static void before() {
    RandomSingleton.setDeterministic();
  }
  
  @Test
  public void test() {
    StringPathDNAFactory factory = new StringPathDNAFactory(100, MIN_NAILS_DIFF, NB_NAILS, false, false);
    
    StringPathDNA dna = factory.createRandomIndividual();
    
    Assert.assertTrue(factory.isValid(dna));
  }
  
}
