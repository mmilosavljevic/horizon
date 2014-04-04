/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rich.borders;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

/**
 *
 * @author Mladen
 */
public class RichProgressBarBorder extends AbstractBorder implements UIResource {

    protected int strokeWidth;
    protected int radius = 5;
    protected Color borderColor = Color.ORANGE;

    public RichProgressBarBorder() {
        strokeWidth = 1;       
    }

    public RichProgressBarBorder(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
    
     public RichProgressBarBorder(int strokeWidth, int radius) {
        this.strokeWidth = strokeWidth;
        this.radius = radius;
    }

    public RichProgressBarBorder(int strokeWidth, Color color) {
        this.strokeWidth = strokeWidth;
        borderColor = color;
    }
    
    public RichProgressBarBorder(int strokeWidth, int radius, Color color) {
        this.strokeWidth = strokeWidth;
        this.radius = radius;
        borderColor = color;     
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(strokeWidth, strokeWidth, strokeWidth, strokeWidth);
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g,
            int x, int y, int w, int h) {
        
        Graphics2D g2d = (Graphics2D) g;   
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        w--;
        h--;
        
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(strokeWidth));
        
        g2d.drawLine(x, y + h - radius, x, y + radius);
        g2d.drawArc(x, y, 2*radius, 2*radius, 180, -90);
        g2d.drawLine(x + radius, y, x + w - radius, y);
        g2d.drawArc(x + w - 2*radius, y, 2*radius, 2*radius, 90, -90);
      
        g2d.drawLine(x + w, y + radius, x + w, y + h - radius);
        g2d.drawArc(x + w - 2*radius, y + h - 2*radius, 2*radius, 2*radius, 0, -90);
        g2d.drawLine(x + radius, y + h, x + w - radius, y + h);
        g2d.drawArc(x, y + h - 2*radius, 2*radius, 2*radius, -90, -90);
    }
}
