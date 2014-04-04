
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Mladen M
 */
public class Icons {
    
    //private static ImageIcon icon = null;
    
     //koristimo da bi smo dobili sistemske ikonice
    public final static JFileChooser chooser = new JFileChooser();
    public final static FileSystemView view = chooser.getFileSystemView();
    public final static String defDir = view.getDefaultDirectory().toString();
    
    public static ImageIcon compIcon;
    public static ImageIcon dir_up;
    public static ImageIcon column_up;
    public static ImageIcon column_down;
    
    public static ImageIcon fontIcon;
    public static ImageIcon iniIcon;
    public static ImageIcon hourglassIcon;
    
    public Icons() {       
        compIcon = new ImageIcon(getClass().getResource("/filemanager/resources/computer.png"));
        dir_up = new ImageIcon(getClass().getResource("/filemanager/resources/buttonUp.png"));
        column_up = new ImageIcon(getClass().getResource("/filemanager/resources/sortUp.png"));
        column_down = new ImageIcon(getClass().getResource("/filemanager/resources/sortDown.png"));
        
        fontIcon = new ImageIcon(getClass().getResource("/filemanager/resources/fonIcon.png"));
        iniIcon = new ImageIcon(getClass().getResource("/filemanager/resources/iniIcon.png"));
        hourglassIcon = new ImageIcon(getClass().getResource("/filemanager/resources/hourglass.png"));    
    }
    
  
    public static ImageIcon getIcon(final File f) {
        //icon = null;

        File parent = f.getParentFile();

        String ext = FileUtils.getFileExt(f);

        if (parent != null) {
            if (parent.getName().equalsIgnoreCase("fonts")) {
                if (ext.equalsIgnoreCase("fon")) {
                    return fontIcon;
                } else if (ext.equalsIgnoreCase("ttf")) {
                    return fontIcon;
                } else if (ext.equalsIgnoreCase("ttc")) {
                    return fontIcon;
                } else if (ext.equalsIgnoreCase("otf")) {
                    return fontIcon;
                } else if (ext.equalsIgnoreCase("ini")) {

                    return iniIcon;

                } else if (ext.equalsIgnoreCase("compositefont")) {
                    return fontIcon;
                }
            }
        }


        /*long start = System.nanoTime() / 1000000;
        icon = (ImageIcon) view.getSystemIcon(f);
        long end = System.nanoTime() / 1000000;
        long total = end - start;
        System.out.println("Ucitavanje ikonica za fajl " + f.toString() + " iznosi " + total+" ms");*/

        return (ImageIcon) view.getSystemIcon(f);

    } 
    
    /*public ImageIcon getIcon(ArrayList<File> list, int row) {

        if (row <= list.size() - 1) {
            return getIcon(list.get(row));        
        } 
    }*/
}
