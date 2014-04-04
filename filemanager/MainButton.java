/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 *
 * @author Mladen
 */
public class MainButton extends JButton {

    private LinearGradientPaint gradient = null;

    public MainButton(String label) {
        super(label);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
    }

    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        Paint oldPaint = g2d.getPaint();

        gradient = new LinearGradientPaint(0.0f, 0.0f, 0.0f, getHeight(),
                new float[]{0.0f, 0.60f, 1.0f},
                new Color[]{
                    new Color(0x100053),
                    new Color(0x100053),
                    new Color(0x2200ee)
                });

        if (getModel().isPressed()) {

            g2d.setColor(new Color(0x100053));
            g2d.fillRect(0, 0, getWidth(), getHeight());

        } else if (getModel().isRollover()) {

            g2d.setColor(new Color(0x100067));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setPaint(new GradientPaint(0, 0, new Color(1.0f, 1.0f, 1.0f, 0.4f),
                    0, getHeight() / 2, new Color(1.0f, 1.0f, 1.0f, 0.1f)));
            g2d.fillRect(0, 0, getWidth(), getHeight() / 2);

            g2d.setPaint(new GradientPaint(0, getHeight() / 2, new Color(1.0f, 1.0f, 1.0f, 0.0f),
                    0, getHeight(), new Color(1.0f, 1.0f, 1.0f, 0.1f)));
            g2d.fillRect(0, getHeight() / 2, getWidth(), getHeight());

            g2d.setPaint(new GradientPaint(0, 0, new Color(1.0f, 1.0f, 1.0f, 0.4f),
                    0, getHeight() / 2, new Color(1.0f, 1.0f, 1.0f, 0.1f)));
            g2d.fillRect(0, 0, getWidth(), getHeight() / 2);

        } else {
            g2d.setColor(new Color(0x100053));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setPaint(new GradientPaint(0, 0, new Color(1.0f, 1.0f, 1.0f, 0.4f),
                    0, getHeight() / 2, new Color(1.0f, 1.0f, 1.0f, 0.1f)));
            g2d.fillRect(0, 0, getWidth(), getHeight() / 2);

            g2d.setPaint(new GradientPaint(0, getHeight() / 2, new Color(1.0f, 1.0f, 1.0f, 0.0f),
                    0, getHeight(), new Color(1.0f, 1.0f, 1.0f, 0.1f)));
            g2d.fillRect(0, getHeight() / 2, getWidth(), getHeight());
        }

        super.paintComponent(g2d);
    }
}
