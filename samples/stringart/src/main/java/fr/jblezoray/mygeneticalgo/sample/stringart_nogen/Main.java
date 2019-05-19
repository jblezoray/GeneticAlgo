package fr.jblezoray.mygeneticalgo.sample.stringart_nogen;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeImageIO;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.*;
import fr.jblezoray.mygeneticalgo.sample.stringart.edge.ScoredEdge;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;

public class Main {
  
  public static void main(String[] args) throws IOException {
    StringArtAlgo stringArtAlgo = new StringArtAlgo(GOAL_IMAGE_PATH, 
        FEATURES_IMAGE_PATH, EDGE_WAY_ENABLED, DEFAULT_EDGE_WAY, 
        CANVAS_WIDTH_MILLIMETERS, THREAD_THICKNESS_MILLIMETERS, 
        PIN_DIAMETER_MILLIMETERS, NB_NAILS, MIN_NAILS_DIFF);
    stringArtAlgo.addListener(SYSTEM_OUT_PRINTER_LISTENER);
    stringArtAlgo.addListener(IMAGE_SAVER_LISTENER);
    stringArtAlgo.start();
    System.out.println("end");
  }
  
  
  private static final IStringArtAlgoListener IMAGE_SAVER_LISTENER = 
      new IStringArtAlgoListener() {
        @Override
        public void notifyRoundResults(int iteration, Image curImg, 
            Image importanceMappingImg, Image refImg, ScoredEdge scoredEdge) {
          if (iteration%50 != 0) return;
          try {
            EdgeImageIO.writeToFile(curImg, new File("_rendering.png"));
            
            Image diffImg = curImg
                .differenceWith(refImg)
                .multiplyWith(importanceMappingImg);
            EdgeImageIO.writeToFile(diffImg, new File("_diff.png"));
            
          } catch (IOException e) {
            System.out.println("Cannot create results image : " + e.getMessage());
          }
        }
      }; 
  
  private static final IStringArtAlgoListener SYSTEM_OUT_PRINTER_LISTENER = 
      new IStringArtAlgoListener() {
        @Override
        public void notifyRoundResults(int iteration, Image curImg, 
            Image importanceMappingImg, Image refImg, ScoredEdge scoredEdge) {
          String line = String.format(
              "iteration:%d ; norm:%7.0f ; nails:%3d,%3d ; choose:%5dms (mean:%d*%2.3fms)",
              iteration,
              scoredEdge.getNorm(), 
              scoredEdge.getEdge().getNailA(), 
              scoredEdge.getEdge().getNailB(),
              scoredEdge.getTimeTook(), 
              scoredEdge.getNumberOfEdgesEvaluated(),
              scoredEdge.getTimeTook() / (float)scoredEdge.getNumberOfEdgesEvaluated());
          System.out.println(line);
        }
      }; 
}
