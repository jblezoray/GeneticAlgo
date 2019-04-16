package fr.jblezoray.mygeneticalgo.sample.stringart_nogen;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.core.EdgeImageIO;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.edge.ScoredEdge;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image.Image;

public class Main {

  private static final String GOAL_IMAGE_PATH = "test2/einstein.png";
  private static final String FEATURES_IMAGE_PATH = "test2/einstein_features2.png";
  private static final boolean EDGE_WAY_ENABLED = false;
  private static final float CANVAS_WIDTH_MILLIMETERS = 630.0f;
  private static final float THREAD_THICKNESS_MILLIMETERS = 0.15f; 
  private static final float PIN_DIAMETER_MILLIMETERS = 2.0f;
  private static final int NB_NAILS = 200;
  private static final int MIN_NAILS_DIFF = Math.max(1, (int)NB_NAILS/20);

  
  public static void main(String[] args) throws IOException {
    StringArtAlgo stringArtAlgo = new StringArtAlgo(GOAL_IMAGE_PATH, 
        FEATURES_IMAGE_PATH, EDGE_WAY_ENABLED, CANVAS_WIDTH_MILLIMETERS,
        THREAD_THICKNESS_MILLIMETERS, PIN_DIAMETER_MILLIMETERS, NB_NAILS,
        MIN_NAILS_DIFF);
    stringArtAlgo.addListener(SYSTEM_OUT_PRINTER_LISTENER);
    stringArtAlgo.addListener(IMAGE_SAVER_LISTENER);
    stringArtAlgo.start();
    System.out.println("end");
  }
  
  
  private static final IStringArtAlgoListener IMAGE_SAVER_LISTENER = 
      new IStringArtAlgoListener() {
        @Override
        public void notifyRoundResults(int iteration, Image curImg, 
            Image importanceMappingImg, Image refImg, ScoredEdge scoredEdge, 
            AtomicInteger counterOfEvaluatedEdges, long timeTookForThisRound) {
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
            Image importanceMappingImg, Image refImg, ScoredEdge scoredEdge, 
            AtomicInteger counterOfEvaluatedEdges, long timeTookForThisRound) {
          String line = String.format(
              "iteration:%d ; norm:%7.0f ; nails:%3d,%3d ; choose:%5dms (mean:%d*%2.3fms)",
              iteration,
              scoredEdge.getNorm(), 
              scoredEdge.getEdge().getNailA(), 
              scoredEdge.getEdge().getNailB(),
              timeTookForThisRound, 
              counterOfEvaluatedEdges.get(),
              timeTookForThisRound / (float)counterOfEvaluatedEdges.get());
          System.out.println(line);
        }
      }; 

  
}
