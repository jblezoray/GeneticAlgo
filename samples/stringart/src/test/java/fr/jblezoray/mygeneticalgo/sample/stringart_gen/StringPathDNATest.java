package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.MIN_NAILS_DIFF;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.NB_NAILS;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

public class StringPathDNATest {
  
  private static StringPathDNAFactory FACTORY;

  @BeforeClass
  public static void beforeClass() {
    RandomSingleton.setDeterministic();
    FACTORY = new StringPathDNAFactory(1000, MIN_NAILS_DIFF, NB_NAILS, false, false);
  }
  
  @Test
  public void test() {
    StringPathDNA dna = FACTORY.createRandomIndividual();
    Assert.assertTrue(FACTORY.isValid(dna));

    dna.addNail(0);
    dna.addNail(dna.getSize()/2);
    dna.addNail(dna.getSize());
    dna.doMutate(1.0f);
    
    Assert.assertTrue(FACTORY.isValid(dna));
  }
}

