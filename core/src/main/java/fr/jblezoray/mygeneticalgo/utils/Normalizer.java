package fr.jblezoray.mygeneticalgo.utils;

public class Normalizer {

  /**
   * This method normalizes a float from range [currentMin, currentMax] to 
   * range [newMin, newMax].
   * 
   * @param valueToNormalize
   * @param currentMin
   * @param currentMax
   * @param newMin
   * @param newMax
   * @return
   */
  public static float normalizeFloat(float valueToNormalize, 
      float currentMin, float currentMax, float newMin, float newMax) {
    float scale = (valueToNormalize - currentMin) / currentMax; 
    return (scale * (newMax - newMin)) + newMin;
  }
  
  
  /**
   * This method normalizes a, integer from range [currentMin, currentMax] to 
   * range [newMin, newMax].
   * 
   * @param valueToNormalize
   * @param currentMin 
   * @param currentMax
   * @param newMin
   * @param newMax
   * @return
   */
  public static int normalizeInteger(int valueToNormalize, int currentMin, 
      int currentMax, int newMin, int newMax) {
    float scale = (valueToNormalize - currentMin) / (float) currentMax;
    return (int)((scale * (newMax - newMin)) + newMin);
  }
  
}
