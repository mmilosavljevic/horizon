/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

/**
 *
 * @author Mladen
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Mladen
 */
public class ProgressDemo extends JDialog {

    public JTextArea text;
    public JProgressBar progressBar;
    private JPanel panel;

    public ProgressDemo(JFrame parent) {
        super(parent, "File Commander", true);
        
        setSize(500, 150);

        panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));       
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        text = new JTextArea();
        makeMultilineLabel(text);

        panel.add(text);
        panel.setBorder(new EmptyBorder(0, 10, 0, 10));


        progressBar = new JProgressBar();
        //progressBar.setBackground(Color.WHITE);
        
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);

        panel.add(progressBar);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JButton("Pause"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        getContentPane().add(panel, BorderLayout.CENTER);

        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }   

    public void setProgressValue(int progress) {
        updateProgressBar(progress);
    }

    public static void makeMultilineLabel(JTextComponent area) {
        area.setFont(UIManager.getFont("Label.font"));
        area.setEditable(false);
        area.setOpaque(false);
        if (area instanceof JTextArea) {
            ((JTextArea) area).setWrapStyleWord(true);
            ((JTextArea) area).setLineWrap(true);
            ((JTextArea) area).setRows(3);
        }
    }

    public final void updateProgressBar(final int progress) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                progressBar.setValue(progress);
            }
        });
    }

    public final void updateLabel(final String label) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                text.setText(label);
            }
        });
    }
    
    public static void main(String args[]) {
        
      
      LookAndFeel rich = new Rich.RichLF();
            UIManager.LookAndFeelInfo info =
                    new UIManager.LookAndFeelInfo(rich.getName(),
                    rich.getClass().getName());
            UIManager.installLookAndFeel(info);
        try {
            UIManager.setLookAndFeel(rich);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ProgressDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    ProgressDemo diag = new ProgressDemo(null);
    
    diag.updateProgressBar(50);
    
    
    diag.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    diag.setVisible(true);
  }
    
}
