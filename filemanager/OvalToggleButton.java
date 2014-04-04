/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;
import javax.swing.JToggleButton;

/**
 *
 * @author Mladen
 */
public class OvalToggleButton extends JToggleButton {

    private LinearGradientPaint gradient = null;

    public OvalToggleButton(String label, Icon icon, boolean bool) {
        super(label, icon, bool);
        setOpaque(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setMargin(new Insets(2, 3, 2, 3));
    }

    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        Paint oldPaint = g2d.getPaint();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        gradient = new LinearGradientPaint(0.0f, 0.0f, 0.0f, getHeight(),
                new float[]{0.0f, 0.60f, 1.0f},
                new Color[]{
                    new Color(0x2200ee),
                    new Color(0x100053),
                    new Color(0x100053)
                });

        Rectangle bounds = g.getClipBounds();

        Shape roundShape = new RoundRectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
        g2d.clip(roundShape);


        if (getModel().isSelected()) {
            g2d.setColor(new Color(0x100053));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        } else {
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }

        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);

        super.paintComponent(g2d);

    }
}
