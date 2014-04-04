/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Mladen
 */
public class EtchedSeparator extends JComponent {
    
    Color shadowColor;
    Color highlighColor;
    
    public EtchedSeparator(Color shadowColor, Color highlightColor) {
        this.shadowColor = shadowColor;
        this.highlighColor = highlightColor;

    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(shadowColor);
        g.drawLine(0, 0, getWidth(), 0);
        
        g.setColor(highlighColor);
        g.drawLine(0, 1, getWidth(), 1);
    }
    
     public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame fm = new JFrame();
                
                fm.setSize(100, 250);
                fm.getContentPane().setBackground(Color.WHITE);
                fm.getContentPane().add(new EtchedSeparator(new Color(0xEBEBEB), new Color(0xF7F7F7)));
                
                
                //fm.pack();
                fm.setVisible(true);
                fm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
               
            }
        });
    }
}
