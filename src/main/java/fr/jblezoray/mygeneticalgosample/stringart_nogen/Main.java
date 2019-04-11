package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    StringArtAlgo stringArtAlgo = new StringArtAlgo(
        "einstein.png", "einstein_features2.png", false);
    stringArtAlgo.start();
    System.out.println("end");
  }

  
}
