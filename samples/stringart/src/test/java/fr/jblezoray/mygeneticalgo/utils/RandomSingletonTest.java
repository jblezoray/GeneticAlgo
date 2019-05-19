package fr.jblezoray.mygeneticalgo.utils;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class RandomSingletonTest {

  
  @Test
  public void test_if_deterministic_works() {
    RandomSingleton.setDeterministic();
    Random rand = RandomSingleton.instance();
    
    int[] actuals = new int[10];
    for (int i=0; i<actuals.length; i++)
      actuals[i] = rand.nextInt(10);

    int[] expecteds = {0,8,9,7,5,3,1,1,9,4};
    Assert.assertArrayEquals(expecteds, actuals);
  }
  
}
