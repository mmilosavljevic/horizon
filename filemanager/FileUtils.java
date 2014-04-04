/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.Dimension;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Mladen M
 */
public final class FileUtils {

    private static JFrame dialogParent;
    private JOptionPane deletePane;
    // dijalog sa opcijama prilikom brisanja datoteka
    private JDialog diag;
    //niz sa opcijama prilikom brisanja datoteka
    private Object[] options;
    // sadrži vrednost veličine svih datoteka
    private long size = 0;
    // sadrži vrednost o ukupnom broju datoteka
    private long totalNumberOfFiles = 0;
    // sadrži vrednost o broju obrisanih datoteka
    private long numberOfFilesDeleted = 0;
    // sadrži vrednost pročitanih bajtova
    private double readSum = 0;
    //premesti-obriši sve datoteke
    private boolean deleteAll = false;
    protected static ProgressDialog operationProgressDialog;
    /**
     * Allows cross-platform compatibility.
     */
    
    public static final File users = new File(System.getProperty("user.home"));
    
    public static final String fileSeparator =
            System.getProperty("file.separator");
    public static final SimpleDateFormat simple =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    public static DecimalFormat sizeFormat;
    //koristimo ovo u metodi determinePercentage()
    //da se naš progressBar ne bi dva puta iscrtao za istu vrednost
    private int samePart = -1;
    // dijalog koji se pojavljuje kada postoji ista datoteka na odredištu
    // prilikom opeacije kopiranja ili premeštanja
    private OptionsDialog optDiag;
    private static boolean allSkip = false; // preskoči sve iste datoteke
    private static boolean overwriteAll = false; // prepiši sve datoteke
    private static boolean overwriteAllOlder = false; // prepiši sve starije datoteke
    private static long lengthCounter = 0;
    private TableAndFiles table;

    public FileUtils(JFrame parent) {

        sizeFormat = new DecimalFormat();

        DecimalFormatSymbols dfs = sizeFormat.getDecimalFormatSymbols();

        dfs.setDecimalSeparator('.');
        sizeFormat.setDecimalFormatSymbols(dfs);

        operationProgressDialog = new ProgressDialog(parent);
        deletePane = new JOptionPane();

        options = new String[]{"Obriši", "Sve", "Preskoči", "Otkaži"};
        deletePane.setOptions(options);

        diag = deletePane.createDialog(
                dialogParent, "FILE COMMANDER");
        Dimension dSize = diag.getPreferredSize();
        diag.setSize(dSize.width, dSize.height + 10);

        optDiag = new OptionsDialog(parent);

    }

    public static boolean isRoot(File f) {
        File[] roots = File.listRoots();

        for (int i = 0; i < roots.length; i++) {

            if (roots[i].equals(f)) {
                return true;
            }
        }
        return false;
    }

    public static String getFileName(File f) {               
        String name = f.getName();
        
        if (name.equals("")) {
            return f.toString();
        }
        int index;               

        if (!f.isDirectory()) {
            
            index = name.lastIndexOf('.');           

            if (index > 0) {
                name = name.substring(0, index);
            }
        }
        return name;
    }

    public static String getFileExt(File f) {
        String name = f.getName();
        String ext = "";
        int index;


        if (f.isDirectory()) {
            return "";
        }

        index = name.lastIndexOf('.');

        if (index > 0) {
            ext = name.substring(index + 1);
        }
        return ext;
    }

    public static String getFileSize(File f, int decimalPos) {

        if (f.isDirectory()) {
            return " Omotnica";
        }

        long longSize = f.length();

        if (decimalPos >= 0) {
            sizeFormat.setMaximumFractionDigits(decimalPos);
        }
        final double size = longSize;

        double val = size / Math.pow(1024, 4);

        if (val >= 1) {
            return sizeFormat.format(val).concat(" TB");
        }
        val = size / Math.pow(1024, 3);
        if (val >= 1) {

            return sizeFormat.format(val).concat(" GB");
        }
        val = size / Math.pow(1024, 2);
        if (val >= 1) {

            return sizeFormat.format(val).concat(" MB");
        }
        val = size / 1024;
        return sizeFormat.format(val).concat(" KB");

    }

    public static String formatDiscSize(long length, int decimalPos) {



        if (decimalPos >= 0) {
            sizeFormat.setMaximumFractionDigits(decimalPos);
        }


        double val = length / Math.pow(1024, 4);

        if (val >= 1) {
            return sizeFormat.format(val).concat(" TB");
        }
        val = length / Math.pow(1024, 3);
        if (val >= 1) {

            return sizeFormat.format(val).concat(" GB");
        }
        val = length / Math.pow(1024, 2);
        if (val >= 1) {

            return sizeFormat.format(val).concat(" MB");
        }
        val = length / 1024;
        return sizeFormat.format(val).concat(" KB");

    }

    public static File getRootFile(File file) {
        File rootFile = file;
        while (rootFile.getParentFile() != null) {
            rootFile = rootFile.getParentFile();
        }              
        return rootFile;
    }
    
    public static ArrayList<File> getFilesInPath(File file) {
        ArrayList<File> files = new ArrayList<File>();
        
        
        files.add(file);
        while ((file = file.getParentFile()) != null) {            
            files.add(file);
            
        }
       
        return files;
        
    }
       
    public static String getFileDate(File f) {

        return " " + simple.format(f.lastModified());
    }

    public static String getFilePermission(File f) {      
        return  f.canRead() ? (f.canWrite() ? (f.canExecute()
                ? " rwe" : " rw -") : " r - -") : " - - -";
    }
    
    public static File[] filterFiles(File file) {
        return file.listFiles(new FilterFiles());        
    }
    
    public static File[] filterDirs(File file) {
        return file.listFiles(new FilterDirectories());        
    }

    public static boolean dirHasSubdirs(File file) {
        if (file.isDirectory()) {
            File[] dirs = file.listFiles(new FilterDirectories());
            return dirs.length > 0 ? true : false;
        }
        
        return false;
    }

    public static boolean isArchiveFile(File f) {
        String name = f.getName().toLowerCase();
        return (name.endsWith(".zip"));
    }

    public static File renameFile(File file, String name) {
        String filePath;
        String newFilePath;
        int index;
        boolean done = false;


        filePath = file.getPath();
        index = filePath.lastIndexOf(File.separatorChar);
        newFilePath = filePath.substring(0, index + 1) + name;

        try {
            done = file.renameTo(new File(newFilePath));

        } catch (SecurityException se) {
            JOptionPane.showMessageDialog(null,
                    "Greška u promeni imena datoteke " + file.getName(),
                    "File Commander", JOptionPane.WARNING_MESSAGE);
        } catch (NullPointerException ne) {
            JOptionPane.showMessageDialog(null,
                    "Zadajte novo ime za datoteku " + file.getName(),
                    "File Commander", JOptionPane.INFORMATION_MESSAGE);
        }

        return done == true ? new File(newFilePath) : file;
    }

    public final void copyFilePath(File[] files, File destinationFilePath)
            throws IOException {

        FileInputStream sourceInputStream = null;
        FileOutputStream destinationInputStream = null;
        FileChannel inChannel;
        FileChannel outChannel;
        FileLock outLock;
        FileLock inLock;
        ByteBuffer buffer;

        boolean skipSingle = false; // preskoči jednu datoteku
        boolean renameOk = true; // ako destinaciona datoteka sa novim imenom ne postoji
        boolean isYounger = false; //


        for (int i = 0; i < files.length; i++) {

            if (!files[i].exists()) {
                throw new IOException("Datoteka nije pronađena: " + files[i]);
            }

            if (!files[i].canRead()) {
                throw new IOException("Datoteka se ne može čitati: " + files[i]);
            }

            if (files[i].isDirectory()) {

                if (destinationFilePath.isFile()) {
                    throw new IOException("Ne mogu da kopiram direktorijum " + files[i] + " u datoteku " + destinationFilePath);
                }

                String targetDestination = destinationFilePath + File.separator + files[i].getName();

                File dir = new File(targetDestination);

                if (!dir.exists()) {
                    dir.mkdir();
                }

                copyFilePath(files[i].listFiles(), new File(targetDestination));

            } else if (files[i].isFile()) {

                lengthCounter += files[i].length();

                File destination = new File(destinationFilePath.toString() + File.separatorChar + files[i].getName());

                operationProgressDialog.updateLabel("\t\t      Kopiram: " + System.getProperty("line.separator")
                        + System.getProperty("line.separator") + "Iz: "
                        + files[i].getAbsoluteFile() + System.getProperty("line.separator")
                        + "U: " + destination);



                if (destination.exists() && !overwriteAll) {

                    if (overwriteAllOlder) {
                        if (files[i].lastModified() >= destination.lastModified()) {
                            continue;
                        }
                    } else if (allSkip) {
                        continue;
                    } else {

                        do {

                            optDiag.setSourceAndDestPanel(files[i], destination);

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
                                    lengthCounter = 0;
                                    operationProgressDialog.setVisible(false);
                                    return;
                                case OptionsDialog.OVERWRITE_OLDER_FILES:
                                    overwriteAllOlder = true;

                                    if (files[i].lastModified() < destination.lastModified()) {
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
                                            null, null, files[i].getName());
                                    if (input != null && !input.isEmpty()) {
                                        renameOk = true;
                                        destination = new File(destinationFilePath.toString() + File.separatorChar + input);

                                        if (destination.exists()) {
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
                        skipSingle = false;
                        isYounger = false;
                        continue;
                    }

                }

                try {

                    sourceInputStream = new FileInputStream(files[i]);
                    destinationInputStream = new FileOutputStream(destination);
                    inChannel = sourceInputStream.getChannel();
                    outChannel = destinationInputStream.getChannel();
                    outLock = outChannel.tryLock();
                    inLock = inChannel.tryLock(0, inChannel.size(), true);
                    buffer = ByteBuffer.allocate(1024 * 1024);

                    while (true) {
                        int readCount = inChannel.read(buffer);
                        if (readCount == -1) {
                            break;
                        }
                        buffer.flip();
                        outChannel.write(buffer);
                        buffer.compact();

                        readSum += readCount;
                        updateCopyMoveProgress(readSum);
                    }

                    outLock.release();
                    inLock.release();
                    inChannel.close();
                    outChannel.close();

                    destination.setLastModified(files[i].lastModified());

                } finally {
                    if (sourceInputStream != null) {
                        try {
                            sourceInputStream.close();
                        } catch (IOException ignoreException) {
                        }
                    }
                    if (destinationInputStream != null) {
                        try {
                            destinationInputStream.close();
                        } catch (IOException ignoreException) {
                        }
                    }
                }
            }
        }
        if (lengthCounter == size) {
            operationProgressDialog.setVisible(false);
            readSum = 0;
            operationProgressDialog.setProgressValue(0);
            size = 0;
            lengthCounter = 0;
        }
    }

    public final void moveFilePath(File[] files, File destinationFilePath)
            throws IOException {

        FileInputStream sourceInputStream = null;
        FileOutputStream destinationInputStream = null;
        FileChannel inChannel;
        FileChannel outChannel;
        FileLock outLock;
        FileLock inLock;
        ByteBuffer buffer;

        boolean skipSingle = false; // preskoču jednu datoteku
        boolean renameOk = true; // ako destinaciona datoteka sa novim imenom ne postoji
        boolean isYounger = false; //

        for (int i = 0; i < files.length; i++) {

            if (!files[i].exists()) {
                throw new IOException("Datoteka nije pronađena: " + files);
            }

            if (!files[i].canRead()) {
                throw new IOException("Datoteka se ne može pročitati: " + files);
            }

            if (files[i].isDirectory()) {

                if (destinationFilePath.isFile()) {
                    throw new IOException("Ne mogu da kopiram direktorijum " + files[i] + " u datoteku " + destinationFilePath);
                }

                String targetDestination = destinationFilePath + File.separator + files[i].getName();

                File dir = new File(targetDestination);

                if (!dir.exists()) {
                    dir.mkdir();
                }

                moveFilePath(files[i].listFiles(), new File(targetDestination));

                if (!(files[i].listFiles().length > 0)) {
                    deleteFile(files[i]);
                }

            } else if (files[i].isFile()) {

                lengthCounter += files[i].length();

                if (files[i].isHidden()) {
                    deletePane.setMessage("Datoteka " + files[i] + System.getProperty("line.separator")
                            + "ima atribut SAKRIVEN ili SISTEM!" + System.getProperty("line.separator")
                            + "Ipak obriši?");


                    diag.setVisible(true);
                    Object obj = deletePane.getValue();

                    if (!deleteAll) {
                        if (obj == options[1]) { //premesti sve
                            deleteAll = true;
                        } else if (obj == options[2]) { //preskoči datoteku-ne premeštaj                          
                            continue;
                        } else if (obj == options[3]) { // otkaži operaciju
                            operationProgressDialog.setVisible(false);
                            break;
                        } else { // obriši-premesti trenutnu datoteku
                            deleteAll = false;
                        }
                    }
                }

                File destination = new File(destinationFilePath.toString() + File.separatorChar + files[i].getName());

                operationProgressDialog.updateLabel("\t\t      Premeštam: " + System.getProperty("line.separator")
                        + System.getProperty("line.separator") + "Iz: "
                        + files[i].getAbsoluteFile() + System.getProperty("line.separator")
                        + "U: " + destination);


                if (destination.exists() && !overwriteAll) {

                    if (overwriteAllOlder) {
                        if (files[i].lastModified() >= destination.lastModified()) {
                            continue;
                        }
                    } else if (allSkip) {
                        continue;
                    } else {

                        do {

                            optDiag.setSourceAndDestPanel(files[i], destination);

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
                                    lengthCounter = 0;
                                    operationProgressDialog.setVisible(false);
                                    return;
                                case OptionsDialog.OVERWRITE_OLDER_FILES:
                                    overwriteAllOlder = true;

                                    if (files[i].lastModified() < destination.lastModified()) {
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
                                            null, null, files[i].getName());
                                    if (input != null && !input.isEmpty()) {
                                        renameOk = true;
                                        destination = new File(destinationFilePath.toString() + File.separatorChar + input);

                                        if (destination.exists()) {
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
                        skipSingle = false;
                        isYounger = false;
                        continue;
                    }

                }



                try {

                    sourceInputStream = new FileInputStream(files[i]);
                    destinationInputStream = new FileOutputStream(destination);
                    inChannel = sourceInputStream.getChannel();
                    outChannel = destinationInputStream.getChannel();
                    outLock = outChannel.tryLock();
                    inLock = inChannel.tryLock(0, inChannel.size(), true);
                    buffer = ByteBuffer.allocate(1024 * 1024);

                    while (true) {
                        int readCount = inChannel.read(buffer);
                        if (readCount == -1) {
                            break;
                        }
                        buffer.flip();
                        outChannel.write(buffer);
                        buffer.compact();

                        readSum = readSum + readCount;
                        updateCopyMoveProgress(readSum);
                    }

                    outLock.release();
                    inLock.release();
                    inChannel.close();
                    outChannel.close();

                    destination.setLastModified(files[i].lastModified());

                } finally {
                    if (sourceInputStream != null) {
                        try {
                            sourceInputStream.close();
                        } catch (IOException ignoreException) {
                        }
                    }
                    if (destinationInputStream != null) {
                        try {
                            destinationInputStream.close();
                        } catch (IOException ignoreException) {
                        }
                    }
                }

                //obriši datoteku naon završtka kopiranja
                deleteFile(files[i]);

            }

        }

        if (lengthCounter == size) {
            operationProgressDialog.setVisible(false);
            readSum = 0;
            operationProgressDialog.setProgressValue(0);
            size = 0;
            lengthCounter = 0;
        }

    }

    public boolean deleteFiles(File[] files) {

        for (int i = 0; i < files.length; i++) {

            if (files[i].isDirectory()) {

                deleteFiles(files[i].listFiles());

                if (!(files[i].listFiles().length > 0)) {

                    deleteFile(files[i]);
                    updateDeleteProgress(++numberOfFilesDeleted);
                }

            } else if (files[i].isFile()) {

                if (files[i].isHidden()) {
                    deletePane.setMessage("Datoteka " + files[i] + System.getProperty("line.separator")
                            + "ima atribut SAKRIVEN ili SISTEM!" + System.getProperty("line.separator")
                            + "Delete anyway?");


                    diag.setVisible(true);
                    Object obj = deletePane.getValue();

                    if (!deleteAll) {
                        if (obj == options[1]) { //obriši sve
                            deleteAll = true;
                        } else if (obj == options[2]) { //premesti datoteku-ne briši
                            updateDeleteProgress(++numberOfFilesDeleted);
                            if (totalNumberOfFiles == numberOfFilesDeleted) {
                                operationProgressDialog.setVisible(false);
                                readSum = 0;
                                operationProgressDialog.setProgressValue(0);
                                break;
                            }
                            continue;
                        } else if (obj == options[3]) { // otkaži operaciju
                            operationProgressDialog.setVisible(false);
                            break;
                        } else { // dobriši trenutnu datoteku
                            deleteAll = false;
                        }
                    }
                }


                operationProgressDialog.updateLabel("\t\t      Brišem: " + System.getProperty("line.separator")
                        + System.getProperty("line.separator") + "Iz: "
                        + files[i].getAbsoluteFile());

                //obriši datoteku nakon završtka kopiranja
                deleteFile(files[i]);
                updateDeleteProgress(++numberOfFilesDeleted);

            }

            if (totalNumberOfFiles == numberOfFilesDeleted) {
                numberOfFilesDeleted = 0;
                operationProgressDialog.setVisible(false);
                readSum = 0;
                operationProgressDialog.setProgressValue(0);
                break;
            }

        }
        return true;
    }

    public final boolean deleteFile(File file) {

        if (file == null) {
            return false;
        }

        table.getTableData().removeFromList(file);

        try {


            if (!file.delete()) {
                JOptionPane.showMessageDialog(dialogParent, "Ne mogu da obrišem: "
                        + file.getAbsolutePath());
                return false;
            }



        } catch (SecurityException se) {
            JOptionPane.showMessageDialog(dialogParent, "Ne mogu da obrišem: "
                    + file.getAbsolutePath() + "/n možda je koristi druga aplikacija");
            return false;
        }

        return true;
    }

    protected void setTableToUpdate(TableAndFiles table) {
        this.table = table;
    }

    public void updateDeleteProgress(double part) {

        int parts = (int) ((part / totalNumberOfFiles) * 100);

        if (parts >= 1 && parts != samePart) {
            operationProgressDialog.updateProgressBar(parts);
            samePart = parts;
        } else {
            return;
        }
    }

    public void updateCopyMoveProgress(double bytesSum) {
        int parts = (int) ((bytesSum / size) * 100);
        if (parts >= 1 && parts != samePart) {
            operationProgressDialog.updateProgressBar(parts);
            samePart = parts;
        } else {
            return; //0;
        }
    }

    public long calculateSize(File[] files) {
        long totalSize = 0;

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                totalSize += files[i].length();
            } else {
                totalSize += calculateSize(files[i].listFiles());
            }
        }

        size = totalSize;
        return totalSize;

    }

    public long getTotalNumberOfFiles(File[] dir) {
        long totalNumber = 0;

        for (int i = 0; i < dir.length; i++) {

            if (dir[i].isFile()) {
                totalNumber++;
            } else {
                ++totalNumber; // uključujući i sam direktorijum
                totalNumber += getTotalNumberOfFiles(dir[i].listFiles());
            }
        }

        return totalNumberOfFiles = totalNumber;

    }

    static File createNewDir(TableAndFiles table, String input) {
        File currentDir;
        File newDir = null;

        currentDir = table.getTableData().getCurrentDir();
        newDir = new File(currentDir + File.separator + input);

        try {
            if (!newDir.mkdir()) {
                JOptionPane.showMessageDialog(dialogParent, "Greška: direktorijum [" + input + "]"
                        + System.getProperty("line.separator") + "već postoji!"
                        + System.getProperty("line.separator") + "Unesite drugo ime");
                return null;
            }
        } catch (SecurityException se) {
            JOptionPane.showMessageDialog(dialogParent, "Greška: kreiranje direktorijuma [" + input + "]"
                    + System.getProperty("line.separator") + "Nemate dozvolu.");
            return null;
        }

        return newDir;
    }

    static final class FilterDirectories implements FileFilter {

        public boolean accept(File pathname) {
            return pathname.isDirectory() && !pathname.isHidden();
        }
    }

    static final class FilterFiles implements FileFilter {

        public boolean accept(File pathname) {
            return pathname.isFile() && !pathname.isHidden();
        }
    }
}
