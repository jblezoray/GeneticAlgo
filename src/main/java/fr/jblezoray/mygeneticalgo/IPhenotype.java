package fr.jblezoray.mygeneticalgo;

public interface IPhenotype {

  double computeFitness(DNA dna);

  public void notificationOfBestMatch(int generation, DNA dna);
  
}
