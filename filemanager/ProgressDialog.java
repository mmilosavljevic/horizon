/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Mladen
 */
public class ProgressDialog extends JDialog {

    public JTextArea text;
    public JProgressBar progressBar;
    private JPanel panel;

    public ProgressDialog(JFrame parent) {
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
        progressBar.setBackground(Color.WHITE);
        
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);

        panel.add(progressBar);
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
}