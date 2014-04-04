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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Mladen
 */
public class Archives {

    private CreateArchiveDialog archDiag;
    private ExtractArchiveDialog exctDiag;
    private OptionsDialog optDiag;
    private SameFileDialog sameFileDialog;
    private File currentDir;
    private File[] selectedFiles;
    private File archiveFile;
    private File extractDestination;
    //sadrži zbir veličine datoteka koje se pakuju
    private long archiveFilesSize = 0;
    protected JFileChooser fileChooser;
    protected SimpleFilter m_zipFilter;
    public static int BUFFER_SIZE = 10240;
    private static ProgressDialog operationProgressDialog;
    //koristimo ovo u metodi determinePercentage
    //da se naš progress bar ne bi dva puta iscrtao za istu vrednost
    private int samePart = -1;
    //promenljiva koja prati za progress bar koliko je bajtova pročitano iz datoteka
    private static double readSum = 0;
    private String path = "";
    private long sizeCounter = 0;

    public Archives(JFrame parent) {


        operationProgressDialog = new ProgressDialog(parent);

        archDiag = new CreateArchiveDialog(parent);
        exctDiag = new ExtractArchiveDialog(parent);
        optDiag = new OptionsDialog(parent);
        sameFileDialog = new SameFileDialog(parent);

        optDiag.addWindowListener(new WindowAdapter() {

            public void windowClosed(WindowEvent e) {
                if (operationProgressDialog.isVisible()) {
                    operationProgressDialog.setVisible(false);
                }
            }
        });

        sameFileDialog.addWindowListener(new WindowAdapter() {

            public void windowClosed(WindowEvent e) {
                if (operationProgressDialog.isVisible()) {
                    operationProgressDialog.setVisible(false);
                }
            }
        });

        try {
            currentDir = (new File(".")).getCanonicalFile();
        } catch (IOException ex) {
            Logger.getLogger(Archives.class.getName()).log(Level.SEVERE, null, ex);
        }

        m_zipFilter = new SimpleFilter("zip", "ZIP Files");
    }

    public void prepareSourceAndDestination(File[] fileSource, File fileDest, boolean pack) {
        String fileDestPath = fileDest.getAbsolutePath();
        String archiveFileName = null;
        selectedFiles = fileSource;

        //ako je izabrana datoteka zip datoteka onda raspakujemo
        if (!pack) {
            archiveFile = fileSource[0];
            extractDestination = fileDest;

            exctDiag.setPath(extractDestination.getAbsolutePath());
            exctDiag.setVisible(true);
            return;
        }

        if (fileSource.length > 1) {

            if (fileDest.getParentFile() != null) {

                if (!FileUtils.isRoot(fileSource[0].getParentFile())) {

                    archiveFileName = fileDestPath + File.separatorChar
                            + fileSource[0].getParentFile().getName() + ".zip";
                    archDiag.setPath(archiveFileName);
                } else {
                    archiveFileName = fileDestPath + File.separatorChar
                            + "pack" + ".zip";
                    archDiag.setPath(archiveFileName);
                }

            } else {

                if (!FileUtils.isRoot(fileSource[0].getParentFile())) {

                    archiveFileName = fileDestPath
                            + fileSource[0].getParentFile().getName() + ".zip";
                    archDiag.setPath(archiveFileName);
                } else {

                    archiveFileName = fileDestPath
                            + "pack" + ".zip";
                    archDiag.setPath(archiveFileName);
                }

            }
        } else if (fileSource.length == 1) {

            if (fileDest.getParentFile() != null) {
                archiveFileName = fileDestPath + File.separatorChar
                        + FileUtils.getFileName(fileSource[0]) + ".zip";
                archDiag.setPath(archiveFileName);
            } else {
                archiveFileName = fileDestPath
                        + FileUtils.getFileName(fileSource[0]) + ".zip";
                archDiag.setPath(archiveFileName);
            }
        } else {
            archDiag.setPath("Nije izabrana nijedna datoteka");
        }

        archiveFile = new File(archiveFileName);
        archDiag.setVisible(true);
    }

    public void createExtractDestination(File dest) {
        exctDiag.setPath(dest.getAbsolutePath());
    }

    public void selectArchiveName(JFrame dialogParent) {
        fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(m_zipFilter);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(m_zipFilter);
        javax.swing.filechooser.FileFilter ft =
                fileChooser.getAcceptAllFileFilter();
        fileChooser.removeChoosableFileFilter(ft);
        fileChooser.setCurrentDirectory(currentDir);
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setDialogTitle("Nova arhiva");

        if (fileChooser.showSaveDialog(dialogParent) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        currentDir = fileChooser.getCurrentDirectory();


        final File aFile = fileChooser.getSelectedFile();
        if (!FileUtils.isArchiveFile(archiveFile)) {
            return;
        }
        archDiag.setPath(aFile.getAbsolutePath());
        archiveFile = aFile;
    }

    public void selectDestinationDir(JFrame dialogParent) {
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setCurrentDirectory(currentDir);
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setDialogTitle("Novo odredište");

        if (fileChooser.showSaveDialog(dialogParent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        final File file = fileChooser.getSelectedFile();
        if (!file.isDirectory()) {
            return;
        }

        extractDestination = file;

        exctDiag.setPath(file.getAbsolutePath());
    }

    public File getArchiveFile() {
        return archiveFile;
    }

    public File[] getSelectedFiles() {
        return selectedFiles;
    }

    public void createZipArchive(File archiveFile, File[] selected) {
        if (archiveFile.exists()) {

            sameFileDialog.setFileProperties(archiveFile);

            switch (sameFileDialog.showSameFileDialog()) {
                case SameFileDialog.OVERWRITE_FILE:
                    break;
                case SameFileDialog.CANCEL:
                    operationProgressDialog.setVisible(false);
                    return;
                default:
                    operationProgressDialog.setVisible(false);
                    return;
            }
        }

        archiveFilesSize = calculatePreArchiveSize(selected);

        try {

            FileOutputStream stream = new FileOutputStream(archiveFile);
            ZipOutputStream out = new ZipOutputStream(stream);

            zipIt(selected, out, path);

            out.close();
            stream.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ne mogu da pišem na zahtevanom odredištu:"
                    + System.getProperty("line.separator") + archiveFile.getAbsolutePath());
            operationProgressDialog.setVisible(false);
        }

    }

    private void zipIt(File[] selected, ZipOutputStream zos, String path) {


        byte buffer[] = new byte[10240];

        for (int k = 0; k < selected.length; k++) {
            if (selected[k] == null || !selected[k].exists()) {
                continue;	// za svaki slučaj...
            }



            operationProgressDialog.updateLabel("\t\t      Pakujem:" + System.getProperty("line.separator")
                    + System.getProperty("line.separator")
                    + "Iz: " + selected[k].getAbsolutePath()
                    + System.getProperty("line.separator")
                    + "U: " + archiveFile.getAbsolutePath());
            try {

                if (selected[k].isDirectory()) {
                    ZipEntry zipAdd = new ZipEntry(path + selected[k].getName() + "/");
                    zipAdd.setTime(selected[k].lastModified());
                    zos.putNextEntry(zipAdd);
                    if (selected[k].listFiles().length > 0) {
                        zipIt(selected[k].listFiles(), zos, path + selected[k].getName() + File.separatorChar);
                        continue;
                    } else {
                        continue;
                    }
                }


                FileInputStream in = new FileInputStream(selected[k]);

                // Dodaj stavku arhive
                ZipEntry zipAdd = new ZipEntry(path + selected[k].getName());
                zipAdd.setTime(selected[k].lastModified());


                zos.putNextEntry(zipAdd);


                while (true) {

                    int nRead = in.read(buffer, 0, buffer.length);

                    if (nRead < 0) {
                        break;
                    }

                    zos.write(buffer, 0, nRead);

                    readSum = readSum + nRead;
                    calculateOperationProgress(readSum);
                }
                in.close();

                if (readSum == archiveFilesSize) {
                    operationProgressDialog.setVisible(false);
                    readSum = 0;
                    operationProgressDialog.setProgressValue(0);
                    break;
                }

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    protected void extractArchive() {

        if (!archiveFile.exists()) {
            return;
        }

        byte buffer[] = new byte[BUFFER_SIZE];



        boolean overwriteAll = false; // Ok da prepišemo sve datoteke u direktorijumu
        boolean overwriteAllOlder = false; // Ok da prepišemo samo starije datoteke u direktorijumu
        boolean allSkip = false; // preskoči sve iste datoteke

        /*ove promenljive su potrebne zbog unutrašnje "do while" petlje
        da bi ponovo prikazali dialog sa opcijama ili da nastavimo sa spoljašnjom
        "while" petljom da bi preskočili raspakivanje trenutne zip stavke*/

        boolean renameOk = true;    // ako nakon preimenovanja još uvek postoji datoteka
        //sa istim imenom u destinacionom direktorijumu ponov prikaži
        //dijalog sa opcijama

        boolean skipSingle = false; // preskoči jendu datoteku

        boolean isYounger = false;  // ako je kompresovana datoteka mlađa od iste
        // od iste datoteke udestinacionom direktorijumu

        File outFile;



        try {

            ZipFile zf = new ZipFile(archiveFile);
            archiveFilesSize = calculatePоsтArchiveSize(zf);

            Enumeration e = zf.entries();


            // Pronađi stavku u arhivi
            while (e.hasMoreElements()) {
                ZipEntry zipItemEntry = (ZipEntry) e.nextElement();

                sizeCounter += zipItemEntry.getSize();                         

                System.out.println("zip " + zipItemEntry);

                if (zipItemEntry == null) {
                    break;
                }


                outFile = new File(extractDestination,
                        zipItemEntry.getName());

                System.out.println(outFile);

                operationProgressDialog.updateLabel("\t\t      Raspakujem:" + System.getProperty("line.separator")
                        + System.getProperty("line.separator")
                        + "Iz: " + archiveFile.getCanonicalPath() + File.separatorChar + zipItemEntry
                        + System.getProperty("line.separator")
                        + "U: " + extractDestination.getAbsolutePath());

                /*if (outFile.isDirectory()) {
                System.out.println("direktorijum " + outFile);
                
                continue;
                }*/

                if (outFile.exists()) {
                    
                    if(zipItemEntry.isDirectory()) {
                        continue;
                    }
                    
                    if (!exctDiag.getOverWriteValue() && !overwriteAll) {                      
                        
                        if (overwriteAllOlder) {
                            if (zipItemEntry.getTime() >= outFile.lastModified()) {
                                continue;
                            }
                        } else if (allSkip) {
                            continue;
                        } else {
                            do {
                                System.out.println("prošlo");
                                optDiag.setSourceAndDestPanel(zipItemEntry, outFile);
                                System.out.println("prošlo 1");

                                switch (optDiag.showOptionsDialog()) {
                                    case OptionsDialog.OVERWRITE_FILE:
                                        break;
                                    case OptionsDialog.OVERWRITE_ALL_FILES:
                                        overwriteAll = true;
                                        break;
                                    case OptionsDialog.SKIP:
                                        skipSingle = true;
                                        break;
                                    case OptionsDialog.CANCEL:
                                        zf.close();
                                        operationProgressDialog.setVisible(false);
                                        readSum = 0;
                                        operationProgressDialog.setProgressValue(0);
                                        sizeCounter = 0;
                                        archiveFilesSize = 0;
                                        return;
                                    case OptionsDialog.OVERWRITE_OLDER_FILES:
                                        overwriteAllOlder = true;
                                        if (zipItemEntry.getTime() < outFile.lastModified()) {
                                            break;
                                        } else {
                                            isYounger = true;
                                            break;
                                        }
                                    case OptionsDialog.SKIP_ALL_FILES:
                                        allSkip = true;
                                        break;
                                    case OptionsDialog.RENAME_FILE:

                                        String input = (String) JOptionPane.showInputDialog(operationProgressDialog,
                                                "Novo ime:", "Preimenuj", JOptionPane.INFORMATION_MESSAGE,
                                                null, null, outFile.getName());
                                        if (input != null && !input.isEmpty()) {
                                            renameOk = true;
                                            outFile = new File(outFile.getParentFile(), input);

                                            if (outFile.exists()) {
                                                renameOk = false;
                                            }
                                        } else {
                                            renameOk = false;
                                        }

                                        break;
                                    default:
                                        return;
                                }
                            } while (!renameOk);
                        }
                        if (allSkip || skipSingle || isYounger) {
                            isYounger = false;
                            skipSingle = false;
                            continue;
                        }
                    }
                    
                } else {
                    if (zipItemEntry.isDirectory()) {
                        
                        if (!outFile.mkdir()) {
                            JOptionPane.showMessageDialog(null, "Ne mogu da napravim neophodni direktorijum!"
                                    + System.getProperty("line.separator") + outFile.getAbsolutePath()
                                    + System.getProperty("line.separator") + "operacija se prekida.");
                            operationProgressDialog.setVisible(true);
                            readSum = 0;
                            operationProgressDialog.setProgressValue(0);
                            sizeCounter = 0;
                            archiveFilesSize = 0;
                            break;
                        }
                        continue;
                    } else { /*ovaj uslov koristimo u slučaju da kada čitamo zip datoteku, direktorijum sa
                        svakom datotekom tretira se kao jedan zipEntry. Primer: Stripes/Stripes 001.jpg
                        u okviru zip datoteke Stripes.zip se ne tretira odvojeno (posebno direktorijum a posebno datoteka)
                        već kao jedan zipEntry ako mi ne napravimo direktorijum Stripes, neće uspeti raspakivanje, jer nedostaje putanja.
                        Moguće da je ovo slučaj samo sa 100% zip kompresijom */
                        
                        File newFile = new File(extractDestination, zipItemEntry.getName());

                        if (!newFile.getParentFile().exists()) {
                            System.out.println("parent " + newFile.getParentFile());
                            newFile.getParentFile().mkdir();
                        }
                    }
                }


                FileOutputStream out =
                        new FileOutputStream(outFile);

                BufferedInputStream in = new BufferedInputStream(zf.getInputStream(zipItemEntry));


                while (true) {

                    int nRead = in.read(buffer,
                            0, buffer.length);
                    if (nRead < 0) {
                        break;
                    }
                    out.write(buffer, 0, nRead);

                    readSum = readSum + nRead;
                    calculateOperationProgress(readSum);
                    
                }

                in.close();
                out.close();
                outFile.setLastModified(zipItemEntry.getTime());
            }
            zf.close();

            
            
            if (sizeCounter == archiveFilesSize) {
                operationProgressDialog.setVisible(false);
                readSum = 0;
                operationProgressDialog.setProgressValue(0);
                sizeCounter = 0;
                archiveFilesSize = 0;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Greška u Zip datoteci"
                    + System.getProperty("line.separator") + archiveFile.getName());
            System.out.println(ex);
            operationProgressDialog.setVisible(false);
            readSum = 0;
            operationProgressDialog.setProgressValue(0);
            sizeCounter = 0;
            archiveFilesSize = 0;
        }

    }

    public File getExtractDestination() {
        return extractDestination;
    }

    public long calculatePreArchiveSize(File[] files) {
        long sum = 0;
        for (File file : files) {
            if (file.isFile()) {
                sum += file.length();
            } else {
                sum += calculatePreArchiveSize(file.listFiles());
            }
        }
        return sum;
    }

    public long calculatePоsтArchiveSize(ZipFile zf) {
        long sum = 0;
        Enumeration e = zf.entries();
        ZipEntry zipExtract;

        while (e.hasMoreElements()) {
            zipExtract = (ZipEntry) e.nextElement();
            sum += zipExtract.getSize();
        }

        return sum;
    }

    public void calculateOperationProgress(double bytesSum) {
        int parts = (int) ((bytesSum / archiveFilesSize) * 100);
        if (parts >= 1 && parts != samePart) {
            operationProgressDialog.updateProgressBar(parts);
            samePart = parts;
        }
    }

    private class CreateArchiveDialog extends JDialog {

        private JLabel archivePathLabel;
        private JTextField pathTextField;
        private JButton browseCreateArchive;
        private JPanel createArchivePanel;
        private JPanel archiveButtonsPanel;
        private JButton createArchiveButton;
        private JButton cancelButton;
        private boolean nameChanged = false;

        private CreateArchiveDialog(final JFrame parent) {
            super(parent, "Spakuj datoteku(e)", true);
            setSize(500, 150);

            archivePathLabel = new JLabel("Spakuj datoteku(e) u: ");
            pathTextField = new JTextField();

            pathTextField.addKeyListener(new KeyAdapter() {

                public void keyTyped(KeyEvent ke) {
                    nameChanged = true;
                }
            });

            createArchiveButton = new JButton("Spakuj");
            browseCreateArchive = new JButton("Pronađi");
            cancelButton = new JButton("Otkaži");

            //izbor arhive u jFileChooserDialog
            ActionListener browseArchiveAction = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    selectArchiveName(parent);
                }
            };
            browseCreateArchive.addActionListener(browseArchiveAction);

            ActionListener createArchive = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (nameChanged) {
                        if (!FileUtils.isArchiveFile(new File(pathTextField.getText()))) {
                            JOptionPane.showMessageDialog(parent, "Nema arhivske datoteke! Arhivska datoteka mora imati 'zip' ekstenziju."
                                    + System.getProperty("line.separator") + "Primer: vasadatoteka.zip");
                            return;
                        } else {
                            archiveFile = new File(pathTextField.getText());
                            if (!archiveFile.isAbsolute()) {
                                makeItApsolute(archiveFile.toString());
                            }
                        }
                    }
                    CreateArchiveDialog.this.setVisible(false);
                    Thread runner = new Thread() {

                        public void run() {
                            createZipArchive(archiveFile, selectedFiles);
                        }
                    };
                    runner.start();
                    operationProgressDialog.setVisible(true);
                    nameChanged = false;

                }

                //ako je samo ime datoteke dato bez korenog direktorijuma
                private void makeItApsolute(String relFilePath) {
                    int index = selectedFiles[0].toString().indexOf(File.separatorChar);
                    String root = selectedFiles[0].toString().substring(0, index + 1);
                    if (relFilePath.charAt(0) != File.separatorChar) {
                        File file = new File(root + File.separatorChar + relFilePath);
                        archiveFile = file;
                    } else {
                        File file = new File(root + relFilePath);
                        archiveFile = file;
                    }
                }
            };

            createArchiveButton.addActionListener(createArchive);


            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    CreateArchiveDialog.this.setVisible(false);
                }
            });

            createArchivePanel = new JPanel();
            createArchivePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            createArchivePanel.setLayout(new BoxLayout(createArchivePanel, BoxLayout.X_AXIS));
            createArchivePanel.add(archivePathLabel);
            createArchivePanel.add(Box.createRigidArea(new Dimension(5, 0)));
            createArchivePanel.add(pathTextField);
            createArchivePanel.add(Box.createRigidArea(new Dimension(5, 0)));
            createArchivePanel.add(browseCreateArchive);

            archiveButtonsPanel = new JPanel();
            archiveButtonsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            archiveButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            archiveButtonsPanel.add(createArchiveButton);
            archiveButtonsPanel.add(cancelButton);

            getContentPane().add(createArchivePanel, BorderLayout.NORTH);
            getContentPane().add(archiveButtonsPanel, BorderLayout.SOUTH);

            setResizable(false);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }

        private void setPath(String path) {
            pathTextField.setText(path);
        }
    }

    private class ExtractArchiveDialog extends JDialog {

        private JLabel extractPathLabel;
        private JTextField pathTextField;
        private JButton browseExtractArchive;
        private JPanel extractArchivePanel;
        private JPanel checkBoxesPanel;
        private JPanel extractButtonsPanel;
        private JButton cancelButton;
        private JButton extractArchiveButton;
        private JCheckBox overWrite;
        private boolean nameChanged;

        private ExtractArchiveDialog(final JFrame parent) {
            super(parent, "Raspakuj datoteku(e)", true);
            setSize(500, 150);

            extractPathLabel = new JLabel("Raspakuj datoteku(e) u: ");
            pathTextField = new JTextField();

            pathTextField.addKeyListener(new KeyAdapter() {

                public void keyTyped(KeyEvent ke) {
                    nameChanged = true;
                }
            });

            extractArchiveButton = new JButton("Raspakuj");
            browseExtractArchive = new JButton("Pronađi");
            cancelButton = new JButton("Otkaži");

            //izbor direktorijuma pomoću JFileChooserDialog-a u koji će se raspakovati datoteke
            ActionListener browseExtractArchiveAction = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    selectDestinationDir(parent);
                }
            };
            browseExtractArchive.addActionListener(browseExtractArchiveAction);

            ActionListener extractArchive = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ExtractArchiveDialog.this.setVisible(false);

                    if (nameChanged) {
                        File newDestination = new File(pathTextField.getText());
                        if (!newDestination.exists() && !newDestination.mkdir()) {
                            JOptionPane.showMessageDialog(parent, "Ne mogu da napravim direktorijum"
                                    + System.getProperty("line.separator") + pathTextField.getText());
                            return;
                        }
                        extractDestination = newDestination;
                        nameChanged = false;
                    }

                    Thread runner = new Thread() {

                        public void run() {
                            extractArchive();
                        }
                    };
                    runner.start();
                    operationProgressDialog.setVisible(true);
                }
            };

            extractArchiveButton.addActionListener(extractArchive);

            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ExtractArchiveDialog.this.setVisible(false);
                }
            });

            extractArchivePanel = new JPanel();
            extractArchivePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            extractArchivePanel.setLayout(new BoxLayout(extractArchivePanel, BoxLayout.X_AXIS));
            extractArchivePanel.add(extractPathLabel);
            extractArchivePanel.add(Box.createRigidArea(new Dimension(5, 0)));
            extractArchivePanel.add(pathTextField);
            extractArchivePanel.add(Box.createRigidArea(new Dimension(5, 0)));
            extractArchivePanel.add(browseExtractArchive);

            checkBoxesPanel = new JPanel();
            checkBoxesPanel.setLayout(new BoxLayout(checkBoxesPanel, BoxLayout.Y_AXIS));
            overWrite = new JCheckBox("Prepiši postojeće datoteke", false);
            checkBoxesPanel.add(overWrite);

            extractButtonsPanel = new JPanel();
            extractButtonsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            extractButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            extractButtonsPanel.add(extractArchiveButton);
            extractButtonsPanel.add(cancelButton);

            getContentPane().add(extractArchivePanel, BorderLayout.NORTH);
            getContentPane().add(checkBoxesPanel, BorderLayout.CENTER);
            getContentPane().add(extractButtonsPanel, BorderLayout.SOUTH);

            setResizable(false);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }

        private void setPath(String path) {
            pathTextField.setText(path);
        }

        private boolean getOverWriteValue() {
            return overWrite.isSelected();
        }
    }
}

class SimpleFilter
        extends javax.swing.filechooser.FileFilter {

    private String m_description = null;
    private String m_extension = null;

    public SimpleFilter(String extension, String description) {
        m_description = description;
        m_extension = "." + extension.toLowerCase();
    }

    public String getDescription() {
        return m_description;
    }

    public boolean accept(File f) {
        if (f == null) {
            return false;
        }
        if (f.isDirectory()) {
            return true;
        }
        return f.getName().toLowerCase().endsWith(m_extension);
    }
}
