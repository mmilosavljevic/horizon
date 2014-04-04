/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Mladen
 */
public class SameFileDialog extends JDialog {

    private final JPanel mainPanel;
    private final JPanel filePropPanel;
    private final JPanel filePropPanelData;
    private final JLabel fileNameLabel;
    private final JLabel fileSizeLabel;
    private final JLabel fileDateLabel;
    private final JPanel operationPanel;
    private JButton overwriteButton;
    private JButton cancelButton;
    private JLabel messageLabel;
    private int returnValue;

    private Icons icons;
    
    /*povratna vrednost ako je prepiši izabrano */
    protected static final int OVERWRITE_FILE = 0;
    /*povratna vrednost ako je otkaži izabrano */
    protected static final int CANCEL = 1;

    public SameFileDialog(JFrame parent) {
        super(parent, "File Commander", true);
        setSize(350, 200);

        icons = new Icons();
        
        Dimension dim = new Dimension(0, 2);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        filePropPanel = new JPanel();

        filePropPanelData = new JPanel();
        filePropPanelData.setLayout(new BoxLayout(filePropPanelData, BoxLayout.Y_AXIS));

        fileNameLabel = new JLabel();
        fileSizeLabel = new JLabel();
        fileDateLabel = new JLabel();

        filePropPanelData.add(fileNameLabel);
        filePropPanelData.add(Box.createRigidArea(dim));
        filePropPanelData.add(fileSizeLabel);
        filePropPanelData.add(Box.createRigidArea(dim));
        filePropPanelData.add(fileDateLabel);

        filePropPanel.add(filePropPanelData);

        operationPanel = new JPanel();
        operationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

        messageLabel = new JLabel("Datoteka već postoji na odredištu");
        messageLabel.setIcon(UIManager.getIcon("OptionPane.questionIcon"));

        ActionListener bListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setReturnValue(e);
                setVisible(false);
            }
        };

        overwriteButton = new JButton("Prepiši");
        overwriteButton.addActionListener(bListener);
        cancelButton = new JButton("Otkaži");
        cancelButton.addActionListener(bListener);

        operationPanel.add(overwriteButton);
        operationPanel.add(cancelButton);

        mainPanel.add(messageLabel, BorderLayout.NORTH);
        mainPanel.add(filePropPanel, BorderLayout.CENTER);
        mainPanel.add(operationPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                returnValue = CANCEL;
            }
        });

        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    protected void setFileProperties(File f) {
        fileNameLabel.setText(f.getAbsolutePath());
        fileNameLabel.setIcon(icons.getIcon(f));
        fileDateLabel.setText("Datum: " + FileUtils.getFileDate(f));
        fileSizeLabel.setText("Veličina:  " + FileUtils.getFileSize(f, 3));
    }

    protected int showSameFileDialog() {
        setVisible(true);
        return returnValue;
    }

    private void setReturnValue(ActionEvent e) {
        if (e.getSource() == overwriteButton) {
            returnValue = OVERWRITE_FILE;
        } else if (e.getSource() == cancelButton) {
            returnValue = CANCEL;
        } else {
            returnValue = CANCEL;
        }
    }
}
