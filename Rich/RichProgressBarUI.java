/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rich;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 *
 * @author Mladen
 */
public class RichProgressBarUI extends BasicProgressBarUI {
   
    private Border border = null;
    
    private LinearGradientPaint foregroundPaint;
    private LinearGradientPaint backgroundPaint;
    private Dimension horizontalDim;
    
    public RichProgressBarUI() {
        
    }
    
    // Create our own progressBar UI!
  public static ComponentUI createUI( JComponent c ) {
    return new RichProgressBarUI();
  }
  
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);    
        
        c.setOpaque(false);
      
        horizontalDim = UIManager.getDimension("ProgressBar.horizontalSize");
        border = UIManager.getBorder("ProgressBar.Border");
        c.setBorder(border);
        
    }
    
   public void update(Graphics g, JComponent c) {
       Graphics2D g2 = (Graphics2D)g; 
       Insets b = progressBar.getInsets(); // area for border
       
       
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
                                      
             backgroundPaint = new LinearGradientPaint(0.0f, 0.0f, 0.0f, progressBar.getHeight()/2,
                     new float[]{0.0f, 1.0f},
                     new Color[]{new Color(0xf1f1f1),
                         new Color(0xffffff)},
                     MultipleGradientPaint.CycleMethod.REFLECT);
             g2.setPaint(backgroundPaint);
             g2.fillRoundRect(1, 1, progressBar.getWidth()-2, progressBar.getHeight()-2, 5, 5);
             
         } else {
             backgroundPaint = new LinearGradientPaint(0.0f, 0.0f, progressBar.getWidth()/2, 0f,
                     new float[]{0.0f, 1.0f},
                     new Color[]{new Color(0xf1f1f1),
                         new Color(0xffffff)},
                     MultipleGradientPaint.CycleMethod.REFLECT);
             g2.setPaint(backgroundPaint);
             g2.fillRoundRect(1, 1, progressBar.getWidth()-2, progressBar.getHeight()-2, 5, 5);
                         
         }
        paint(g, c);
   }
  
    @Override
    public void paintDeterminate(Graphics g, JComponent c) {


        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }

        /*int cellLength = getCellLength();
        int cellSpacing = getCellSpacing();*/
        // amount of progress to draw
        int amountFull = getAmountFull(b, progressBar.getWidth(), progressBar.getHeight());

        Graphics2D g2 = (Graphics2D) g;

        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {

            Shape shapeClip =
                    new RoundRectangle2D.Double(1, 1, progressBar.getWidth()-2, progressBar.getHeight()-2, 5, 5);
            g2.clip(shapeClip);

            foregroundPaint = new LinearGradientPaint(0.0f, 0.0f, 0.0f, progressBar.getHeight()/2,
                    new float[]{0.0f, 1.0f},
                    new Color[]{new Color(0xffffff),
                        new Color(0xffda6c)},
                    MultipleGradientPaint.CycleMethod.REFLECT);
            g2.setPaint(foregroundPaint);
            g2.fillRect(1, 1, amountFull-2, progressBar.getHeight()-2);

        } else {

            Shape shapeClip =
                    new RoundRectangle2D.Double(1, 1, progressBar.getWidth()-2, progressBar.getHeight()-2, 5, 5);
            g2.clip(shapeClip);

            foregroundPaint = new LinearGradientPaint(0.0f, 0.0f, progressBar.getWidth()/2, 0f,
                    new float[]{0.0f, 1.0f},
                    new Color[]{new Color(0xffffff),
                        new Color(0xffda6c)},
                    MultipleGradientPaint.CycleMethod.REFLECT);
            g2.setPaint(foregroundPaint);
            g2.fillRect(1, 1, amountFull-2, progressBar.getHeight()-2);
        }
    }
    
    // Many of the Basic*UI components have the following methods.
    // This component does not have these methods because *ProgressBarUI
    //  is not a compound component and does not accept input.
    //
    // protected void installComponents()
    // protected void uninstallComponents()
    // protected void installKeyboardActions()
    // protected void uninstallKeyboardActions()

    @Override
    protected Dimension getPreferredInnerHorizontal() {
        
        return horizontalDim;
    }
    
   
}
