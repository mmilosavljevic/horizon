/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

/**
 *
 * @author Mladen
 */
public class DiscSpace extends JLabel {

    private LinearGradientPaint gradientEmpty;
    private LinearGradientPaint gradientFull;
    private File disc;
    private float totalSpace;
    private float spaceUsed;
    private float usableSpace;

    public DiscSpace(File disc) {
        super.setPreferredSize(new Dimension(200, 15));
        setBorder(new LineBorder(Color.WHITE, 2));
        setDisc(disc);
        setHorizontalAlignment(CENTER);
    }

    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        Paint oldPaint = g2d.getPaint();

        totalSpace = disc.getTotalSpace();
        usableSpace = disc.getUsableSpace();
        spaceUsed = (totalSpace - usableSpace) / totalSpace;

        gradientFull = new LinearGradientPaint(0.0f, 0.0f, 0.0f, getHeight(),
                new float[]{0.0f, 0.5f, 1.0f},
                new Color[]{new Color(0xffffff),
                    new Color(0xffda6c),
                    new Color(0xffffff)},
                MultipleGradientPaint.CycleMethod.REFLECT);

        gradientEmpty = new LinearGradientPaint(0.0f, 0.0f, 0.0f, getHeight(),
                new float[]{0.0f, 0.5f, 1.0f},
                new Color[]{new Color(0xffffff),
                    new Color(0xcfccdd),
                    new Color(0xffffff)},
                MultipleGradientPaint.CycleMethod.REFLECT);

        //prikazuje zauzet prostor na disku
        g2d.setPaint(gradientFull);
        g2d.fillRect(0, 0, (int) (spaceUsed * getWidth()), getHeight());

        //prikazuje slobodan prostor na disku
        g2d.setPaint(gradientEmpty);
        g2d.fillRect((int) (spaceUsed * getWidth()), 0, getWidth(), getHeight());

        setText(FileUtils.formatDiscSize((long) usableSpace, 2)
                + " slobodno od " + FileUtils.formatDiscSize((long) totalSpace, 2));

        super.paintComponent(g);
    }

    public void setDisc(File file) {
        
        if (file == null) {
            return;
        }
        
        File rootFile = file;
        while (rootFile.getParentFile() != null) {
            rootFile = rootFile.getParentFile();
        }  
        disc = rootFile;
//        if (file.getParentFile() == null) {
//            disc = file;
//        }
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }
}
