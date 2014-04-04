/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Mladen
 */
public class OptionsDialog extends JDialog {

    /*povratna vrednost ako je izbor prepiši */
    protected static final int OVERWRITE_FILE = 0;
    /*povratna vrednost ako je izbor prepiši sve */
    protected static final int OVERWRITE_ALL_FILES = 1;
    /*povratna vrednost ako je izbor preskoči */
    protected static final int SKIP = 2;
    /*povratna vrednost ako je izbor otkaži */
    protected static final int CANCEL = 3;
    /*povratna vrednost ako je izbor prepiši statije */
    protected static final int OVERWRITE_OLDER_FILES = 4;
    /*povratna vrednost ako je izbor preskoči sve */
    protected static final int SKIP_ALL_FILES = 5;
    /*povratna vrednost ako je izbor preimenuj */
    protected static final int RENAME_FILE = 6;

    private int returnValue;
    
    private Icons icons;

    private  JPanel filePropPanel;
    private  JPanel filePropPanelUp;
    private  JPanel filePropPanelDown;
    private  JPanel operationPanel;
    private  JLabel fileLLabel;
    private  JLabel fileLSizeLabel;
    private  JLabel fileLDateLabel;
    private  JLabel fileRLabel;
    private  JLabel fileRSizeLabel;
    private  JLabel fileRDateLabel;
    private  JButton overWrite;
    private  JButton overWriteAll;
    private  JButton skip;
    private  JButton cancel;
    private  JButton overWriteOlder;
    private  JButton skipAll;
    private  JButton rename;

    private JLabel messageLabel;

    public OptionsDialog(JFrame parent) {
        super(parent, "File Manager", true);
        setSize(350, 400);

        icons = new Icons();
        
        filePropPanel = new JPanel();
        filePropPanel.setLayout(new GridLayout(3, 1, 0, 5));
        filePropPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        filePropPanel.setBackground(Color.WHITE);

        filePropPanelUp = new JPanel();
        filePropPanelDown = new JPanel();

        messageLabel = new JLabel("Prepiši odredišnu datoteku sa izvornom?");
        messageLabel.setIcon(UIManager.getIcon("OptionPane.questionIcon"));

        fileLLabel = new JLabel();
        fileLSizeLabel = new JLabel();
        fileLDateLabel = new JLabel();

        filePropPanelUp.setLayout(new BoxLayout(filePropPanelUp, BoxLayout.Y_AXIS));

        filePropPanelUp.add(fileLLabel);
        filePropPanelUp.add(fileLSizeLabel);
        filePropPanelUp.add(fileLDateLabel);

        filePropPanelUp.setBorder(new TitledBorder(
                new LineBorder(Color.BLACK, 1), "Izvorna datoteka"));

        fileRLabel = new JLabel();
        fileRSizeLabel = new JLabel();
        fileRDateLabel = new JLabel();

        filePropPanelDown.setLayout(new BoxLayout(filePropPanelDown, BoxLayout.Y_AXIS));

        filePropPanelDown.add(fileRLabel);
        filePropPanelDown.add(fileRSizeLabel);
        filePropPanelDown.add(fileRDateLabel);

        filePropPanelDown.setBorder(new TitledBorder(
                new LineBorder(Color.BLACK, 1), "Odredišna datoteka"));

        filePropPanel.add(messageLabel);
        filePropPanel.add(filePropPanelUp);
        filePropPanel.add(filePropPanelDown);

        operationPanel = new JPanel();
        operationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        operationPanel.setBackground(Color.WHITE);

        addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            returnValue = CANCEL;
        }});

        ActionListener bListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setReturnValue(e);
                setVisible(false);

            }
        };


        overWriteAll = new JButton("Prepiši sve");
        //Dimension bestSize = overWriteAll.getPreferredSize();
        overWriteAll.addActionListener(bListener);

        overWrite = new JButton("Prepiši");
        //overWrite.setPreferredSize(bestSize);
        overWrite.addActionListener(bListener);

        skip = new JButton("Preskoči");
        //skip.setPreferredSize(bestSize);
        skip.addActionListener(bListener);

        cancel = new JButton("Otkaži");
        //cancel.setPreferredSize(bestSize);
        cancel.addActionListener(bListener);

        overWriteOlder = new JButton("Prepiši starije");
        //overWriteOlder.setPreferredSize(bestSize);
        //overWriteOlder.setMargin(new Insets(2, 2, 2, 2));
        overWriteOlder.addActionListener(bListener);

        skipAll = new JButton("Preskoči sve");
        //skipAll.setPreferredSize(bestSize);
        skipAll.addActionListener(bListener);

        rename = new JButton("Preimenuj");
        //rename.setPreferredSize(bestSize);
        rename.addActionListener(bListener);

        operationPanel.add(overWrite);
        operationPanel.add(overWriteAll);
        operationPanel.add(skip);
        operationPanel.add(cancel);
        operationPanel.add(overWriteOlder);
        operationPanel.add(skipAll);
        operationPanel.add(rename);

        getContentPane().add(filePropPanel, BorderLayout.NORTH);
        getContentPane().add(operationPanel, BorderLayout.CENTER);
        
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    protected  void setSourceAndDestPanel(ZipEntry zipInfo, File dest) {

        String entryName = zipInfo.getName();
        fileLLabel.setText(entryName);
        try {
            fileLLabel.setIcon(icons.getIcon(File.createTempFile("icon", entryName.substring(entryName.lastIndexOf(".")))));
        } catch (IOException ex) {
            System.err.println(ex);
        }
        fileLSizeLabel.setText("Veličina: " + String.valueOf(FileUtils.sizeFormat.format(zipInfo.getSize())));
        fileLDateLabel.setText("Datum: " + FileUtils.simple.format(zipInfo.getTime()));

        fileRLabel.setText(dest.getAbsolutePath());
        fileRLabel.setIcon(icons.getIcon(dest));
        fileRSizeLabel.setText("Veličina: " + FileUtils.getFileSize(dest, 3));
        fileRDateLabel.setText("Datum: " + FileUtils.getFileDate(dest));

    }

    protected  void setSourceAndDestPanel(File source, File dest) {

        String entryName = source.getName();
        fileLLabel.setText(entryName);
       
        fileLLabel.setIcon(icons.getIcon(dest));
        fileLSizeLabel.setText("Veličina: " + FileUtils.getFileSize(dest, 3));
        fileLDateLabel.setText("Datum: " + FileUtils.getFileDate(dest));

        fileRLabel.setText(dest.getAbsolutePath());
        fileRLabel.setIcon(icons.getIcon(dest));
        fileRSizeLabel.setText("Veličina: " + FileUtils.getFileSize(dest, 3));
        fileRDateLabel.setText("Datum: " + FileUtils.getFileDate(dest));

    }
    
    private void setReturnValue(ActionEvent e) {
        if(e.getSource() == overWrite) {
            returnValue = OVERWRITE_FILE;
        } else if(e.getSource() == overWriteAll) {
            returnValue = OVERWRITE_ALL_FILES;
        } else if(e.getSource() == skip) {
            returnValue = SKIP;
        } else if(e.getSource() == cancel) {
            returnValue = CANCEL;
        } else if(e.getSource() == overWriteOlder) {
            returnValue = OVERWRITE_OLDER_FILES;
        } else if(e.getSource() == skipAll) {
            returnValue = SKIP_ALL_FILES;
        } else if(e.getSource() == rename) {
            returnValue = RENAME_FILE;
        } else {
            returnValue = CANCEL;
        }
    }

    public int showOptionsDialog() {
        setVisible(true);
        return returnValue;
    }

}

