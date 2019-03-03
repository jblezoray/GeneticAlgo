package fr.jblezoray.mygeneticalgo.utils;

import java.util.Random;

/**
 * Access a singleton initialized "java.util.Random" class. 
 * 
 * @author jbl
 */
public class RandomSingleton {

  private static final Random RANDOM = new Random(System.currentTimeMillis());
  
  public static Random instance() {
    return RANDOM;
  }
  
}
