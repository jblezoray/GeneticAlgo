package fr.jblezoray.mygeneticalgo;

public interface IResultListener {

  public void notificationOfBestMatch(int generation, double score, DNA dna);
  
}
