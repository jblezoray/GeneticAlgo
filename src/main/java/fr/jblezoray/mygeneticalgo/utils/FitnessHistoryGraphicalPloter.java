package fr.jblezoray.mygeneticalgo.utils;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import fr.jblezoray.mygeneticalgo.IGeneticAlgoListener;
import fr.jblezoray.mygeneticalgo.dna.IDNA;

/**
 * A frame that plots a graph of the evolution of the fitness across generations.
 * 
 *  Yeah, it's in swing. It's ugly but it works on any system without a single
 *  dependency. 
 * 
 * @author jbl
 *
 * @param <X>
 */
public class FitnessHistoryGraphicalPloter<X extends IDNA> 
implements IGeneticAlgoListener<X> {
  
  private List<Double> mins = new ArrayList<Double>();  
  private List<Double> maxs = new ArrayList<Double>();  
  
  
  private JFrame frame = null;
  private JPanel graphicsPanel = new JPanel() {
    private static final long serialVersionUID = -6704280332671333413L;

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setPaint(Color.black);
      g2d.setStroke(new BasicStroke());
      g2d.setFont(new Font("Century Schoolbook", Font.PLAIN, 12));
      
      if (maxs.isEmpty() || mins.isEmpty()) return;
      
      // x axis, for the generation counter.
      Float xLower = 0.0f;
      Float xUpper = (float) maxs.size();
      Float dx = xUpper - xLower;
      
      // y axis, for the fitnesses. 
      Float yLower = 0.0f;
      Float yUpper = (float) maxs.stream().mapToDouble(a -> a).max().getAsDouble();
      Float dy = yUpper - yLower;
      
      // draw axes
      g2d.setPaint(Color.gray);
      int padding = 70;
      int width = this.getParent().getWidth() - padding*2;
      int height = this.getParent().getHeight() - padding*2;
      int xNbIntervals = (int)Math.min(10.0f, dx);
      int yNbIntervals = 10;
      for (float xVal=xLower; xVal<xUpper; xVal+=dx/xNbIntervals) {
        int x = (int)Normalizer.normalizeFloat(xVal, xLower, xUpper, padding, padding+width);
        g2d.draw(new Line2D.Float(x, padding + height, x, padding));
        int y = height + (int)(padding * 1.5);
        drawCenteredString(g2d, Integer.toString((int)xVal), x, y, 0.0f);
      }
      for (float yVal=yLower; yVal<yUpper; yVal+=dy/yNbIntervals) {
        int y = height + padding*2 - (int)Normalizer.normalizeFloat(yVal, yLower, yUpper, padding, height+padding);
        g2d.draw(new Line2D.Float(padding, y, padding + width, y));
        int x = (int)(padding * 0.5);
        drawCenteredString(g2d, String.format("%3.3f", yVal), x, y, 0.0f);
      }
      
      // draw points.
      Float diam = 8f;
      for (int i=0; i<maxs.size(); i+=Math.max(1, maxs.size()/(frame.getWidth()/20))) {
        float xVal = i; 
        Float ex = Normalizer.normalizeFloat(xVal, xLower, xUpper, padding - diam/2, padding + width - diam/2);

        // max
        float yVal = maxs.get(i).floatValue();
        Float ey = height + padding - Normalizer.normalizeFloat(yVal, yLower, yUpper, 0, height);
        g2d.fill(new Ellipse2D.Float(ex, ey, diam, diam));
        
        // min
        yVal = mins.get(i).floatValue();
        ey = height + padding - Normalizer.normalizeFloat(yVal, yLower, yUpper, 0, height);
        g2d.fill(new Ellipse2D.Float(ex, ey, diam, diam));
      }
    }
  };
  
  static {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException |
        IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }
  }
  
  public FitnessHistoryGraphicalPloter() {
    frame = new JFrame("Fitness history");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(graphicsPanel, BorderLayout.CENTER);
    frame.setSize(500, 400);
    frame.setVisible(true);
  }
  

  @Override
  public void notificationOfGeneration(int generation, X dnaBestMatch, 
      double[] allFitnessScores) {
    if (allFitnessScores.length==0) throw new RuntimeException();
    mins.add(generation-1, DoubleStream.of(allFitnessScores).min().getAsDouble());
    maxs.add(generation-1, dnaBestMatch.getFitness());

    if (generation >2) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          graphicsPanel.update(graphicsPanel.getGraphics());
        }
      });
    }
  }


  private void drawCenteredString(Graphics2D g2d, String string, int x0, int y0,
      float angle) {
    FontRenderContext frc = g2d.getFontRenderContext();
    Rectangle2D bounds = g2d.getFont().getStringBounds(string, frc);
    LineMetrics metrics = g2d.getFont().getLineMetrics(string, frc);
    if (angle == 0) {
      g2d.drawString(string, x0 - (float) bounds.getWidth() / 2,
          y0 + metrics.getHeight() / 2);
    } else {
      g2d.rotate(angle, x0, y0);
      g2d.drawString(string, x0 - (float) bounds.getWidth() / 2,
          y0 + metrics.getHeight() / 2);
      g2d.rotate(-angle, x0, y0);
    }
  }
  
  
  
}
