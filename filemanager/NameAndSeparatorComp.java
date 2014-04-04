/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import com.sun.jndi.toolkit.dir.DirSearch;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;

/**
 *
 * @author Mladen
 */
public class NameAndSeparatorComp extends JComponent {
    

    private FileNameButton nameBtn = null;
    private SeparatorButton separatorBtn = null;
    //private static int index = 0;                  //useful when we need to remove this component from the list
    private File file;
    private ComponentsListener compListenes;
    private JLabel labelIcon;
    private ImageIcon compIcon;
    private JPanel pathPanel;
    private boolean isLeftPath = false;
    private static NameAndSeparatorComp removedComp;
    private static Integer totalWidth;
    private static Integer totalWidthPanelI = new Integer(0);
    private static Integer totalWidthPanel2 = new Integer(0);
    private int compIndex = 0;
    private boolean visible = true;
    private static ComponentsListener prevCompListener = null;
    private static ComponentsListener prevCompListenerHelp = null;
    private static ComponentsListener pressedComp = null; //temporarily keeps NameAndSeparatorComp component which is pressed
    private static DirectorysList dirListLeft = new DirectorysList();
    private static DirectorysList dirListRight = new DirectorysList();
    private static DirectorysList dirListHelper;
    private static boolean activeLeftPathPanel = true;
    private static boolean activeRightPathPanel = false; 
    private static final ArrayList<NameAndSeparatorComp> leftDirPathsComps = new ArrayList<NameAndSeparatorComp>();
    private static final ArrayList<NameAndSeparatorComp> rightDirPathsComps = new ArrayList<NameAndSeparatorComp>();
    private static ArrayList<NameAndSeparatorComp> dirPathsComps;
    private static ArrayList<NameAndSeparatorComp> leftHiddenComps = new ArrayList<NameAndSeparatorComp>();
    private static ArrayList<NameAndSeparatorComp> rightHiddenComps = new ArrayList<NameAndSeparatorComp>();
    private static ArrayList<NameAndSeparatorComp> hiddenComps;
    private static final TableAndFiles[] interactModels = new TableAndFiles[2];
    private boolean isHidden = false;

    public NameAndSeparatorComp(File file, JPanel panel) {
        pathPanel = panel;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        this.file = file;
        //index++;

        //<editor-fold defaultstate="collapsed" desc="samo za test">
        /*FileNameButton name = new FileNameButton(0, file.getName());
         * SeparatorButton sep = new SeparatorButton();
         * 
         * compListenes = new ComponentsListener(name, sep);
         * 
         * add(name);
         * add(sep);*/

        //sumComponentsWidth();
        //</editor-fold>

        addToPathPanel();
        //compIndex++;

    }

    protected static void setInteractModel(TableAndFiles taf1, TableAndFiles taf2) {
        interactModels[0] = taf1;
        interactModels[1] = taf2;
    }

    //when we are going up the directory, the component that represents parent dir
    //of the curently open dir is already in the pathPanel, so we don't want
    //to add it again in the same pathPanel, and we just want to remove the component from the
    //pathPanel in which it represents the last opened directory
    protected static boolean isInThePath(File file, JPanel panel) {

        dirPathsComps = panel == FileCommander.pathPanel ? leftDirPathsComps : rightDirPathsComps;
        hiddenComps = panel == FileCommander.pathPanel ? leftHiddenComps : rightHiddenComps;
        totalWidth = panel == FileCommander.pathPanel ? totalWidthPanelI : totalWidthPanel2;

        for (int i = dirPathsComps.size() - 1; i >= 0; i--) {
            if (dirPathsComps.get(i).getFile().equals(file)) {


                synchronized (panel.getTreeLock()) {
                    for (int j = panel.getComponentCount() - 1; j > i + 1; j--) {
                        panel.remove(j);
                        removedComp = dirPathsComps.remove(j - 1);
                        totalWidth -= removedComp.getWidth();

                        if (hiddenComps.size() > 0) {

                            NameAndSeparatorComp comp = hiddenComps.get(hiddenComps.size() - 1);

                            dirPathsComps.add(0, comp);
                            panel.add(comp, 1);
                            totalWidth += comp.getWidth();
                            hiddenComps.remove(hiddenComps.size() - 1);

                            //if there are no more hidden path components under the
                            //computer component panel change the separator component
                            //in computer component to regular arrow
                            if (hiddenComps.isEmpty()) {
                                NameAndSeparatorComp computerComp = (NameAndSeparatorComp) panel.getComponent(0);
                                computerComp.getSeparatorButton().setHiddenComps(false);
                            }

                        }

                    }
                    panel.revalidate();
                    panel.repaint();
                }
                return true;
            }
        }
        return false;
    }

    private void addToPathPanel() {
        dirPathsComps = pathPanel == FileCommander.pathPanel ? leftDirPathsComps : rightDirPathsComps;
        isLeftPath = pathPanel == FileCommander.pathPanel ? true : false; //pathPanel is above left table
        joinComponentsFor();
    }

    private void joinComponentsFor() {

        int size = dirPathsComps.size();
        
        //For example if there is only one opened directory and parent of that directory is root dir,
        //remove opened directory from path panel when we go up the directory
        //so we're only left with Computer and root directory component in pathanel.
//        if (size == 2) {
//            
//            if (dirPathsComps.get(size - 1).getFile().getParentFile().equals(file)) {
//                dirPathsComps.remove(size - 1);               
//                pathPanel.remove(size);
//                pathPanel.revalidate();
//                pathPanel.repaint();
//                
//                if (FileUtils.isRoot(file)) {
//                    return;
//                }               
//            }
//        }

        if (file == null) {
            
            //just in case for null-file other then the "Computer" button in path.
            if (size >= 1) {
                return;
            }
            //computer button doesn't go to dirPathsComps
            nameBtn = new FileNameButton(file, separatorBtn, isLeftPath);
            separatorBtn = new SeparatorButton(nameBtn);

            
            compIcon = Icons.compIcon;
            labelIcon = new JLabel();
            labelIcon.setBorder(new EmptyBorder(2, 2, 0, 2));
            labelIcon.setIcon(compIcon);

            add(labelIcon);
            add(nameBtn);
            add(separatorBtn);

            compListenes = new ComponentsListener(nameBtn, separatorBtn);


            pathPanel.add(this);

            return;
        }


        //ako je jednako root disku ne dodaj komponentu
        nameBtn = new FileNameButton(file, separatorBtn, isLeftPath);
        add(nameBtn);

        if (FileUtils.dirHasSubdirs(file)) {

            separatorBtn = new SeparatorButton(nameBtn);
            add(separatorBtn);
        }

        compListenes = new ComponentsListener(nameBtn, separatorBtn);


        dirPathsComps.add(this);
        pathPanel.add(this);       
        sumComponentsWidth(dirPathsComps, pathPanel);
        //pathPanel.revalidate();
    }

    private void sumComponentsWidth(ArrayList<NameAndSeparatorComp> comps, JPanel panel) {
        hiddenComps = panel == FileCommander.pathPanel ? leftHiddenComps : rightHiddenComps;
        totalWidth = panel == FileCommander.pathPanel ? totalWidthPanelI : totalWidthPanel2;
        totalWidth = panel.getComponent(0).getWidth();
        
        synchronized (panel.getTreeLock()) {
            for (int i = 0; i < panel.getComponentCount(); i++) {
                totalWidth += panel.getComponent(i).getWidth();
            }
        }

        if (totalWidth > (panel.getWidth() - panel.getWidth() / 5)) {
            NameAndSeparatorComp comp = (NameAndSeparatorComp) panel.getComponent(1);
            
            totalWidth -= comp.getWidth();
            //don't add root dirs because when we display
            //list with hidden comps roots will be added in that list independently
            if (!FileUtils.isRoot(comp.getFile())) {
                hiddenComps.add(comp);               
            }
            
            panel.remove(1);
            comps.remove(0);

            NameAndSeparatorComp computerComp = (NameAndSeparatorComp) panel.getComponent(0);
            computerComp.getSeparatorButton().setHiddenComps(true);

        }
        
        panel.revalidate();
        panel.repaint();
    }
    
    private void removeCompsStartAfter(FileNameButton fnBtn) {
        boolean computerCompPressed = fnBtn.getParent() == pathPanel.getComponent(0);
        dirPathsComps = fnBtn.isLeftPanel() ? leftDirPathsComps : rightDirPathsComps;
        totalWidth = fnBtn.isLeftPanel() ? totalWidthPanelI : totalWidthPanel2;
        
               
        int index = dirPathsComps.indexOf(this);

        if (index > -1 || computerCompPressed) {

            //computer component pressed
            //we set index to -1 to remove all component after it in path panel            
            if (computerCompPressed) {
                index = -1;
                hiddenComps.clear();
            }

            //this will remove all components except pressed component in path panel
            //it will always leave at least computer component and one after it in path panel
            //even after computer component is pressed if don't modify index
            for (int i = dirPathsComps.size() - 1; i > index; i--) {
                removedComp = dirPathsComps.remove(i);
                pathPanel.remove(i + 1);
                
                totalWidth -= removedComp.getWidth();
            }
        }
    
            showHiddenComps(dirPathsComps, pathPanel);
       
    }

    private void showHiddenComps(ArrayList<NameAndSeparatorComp> comps, JPanel panel) {

        hiddenComps = panel == FileCommander.pathPanel ? leftHiddenComps : rightHiddenComps;
        totalWidth = panel == FileCommander.pathPanel ? totalWidthPanelI : totalWidthPanel2;

        for (int i = hiddenComps.size() - 1; i >= 0; i--) {
            System.out.println(hiddenComps.size());
            NameAndSeparatorComp comp = hiddenComps.get(i);

            comps.add(0, comp);
            panel.add(comp, 1);
            totalWidth += comp.getWidth();
            hiddenComps.remove(i);

        }

        //if there are no more hidden path components under the
        //computer component panel change the separator component
        //in computer component to regular arrow
        if (hiddenComps.isEmpty()) {
            NameAndSeparatorComp computerComp = (NameAndSeparatorComp) panel.getComponent(0);
            computerComp.getSeparatorButton().setHiddenComps(false);
        }
        panel.revalidate();
        panel.repaint();
    }

        
    //After main window becomes active again
    //remove components from path panel
    protected static void clearPaths() {
        leftDirPathsComps.clear();
        rightDirPathsComps.clear();
        leftHiddenComps.clear();
        rightHiddenComps.clear();
        
        totalWidthPanelI = 0;
        totalWidthPanel2 = 0;
        
        FileCommander.pathPanel.removeAll();
        FileCommander.pathPanel2.removeAll();
        
        FileCommander.pathPanel.revalidate();
        FileCommander.pathPanel.repaint();
        FileCommander.pathPanel2.revalidate();
        FileCommander.pathPanel2.repaint();
       
    }
    
    //after we clean path panel when main window gets focus
    //we update path panels with fresh state of the file path
    protected static void updatePath(File file, JPanel panel) {
        dirPathsComps = panel == FileCommander.pathPanel ? leftDirPathsComps : rightDirPathsComps;
        
        ArrayList<File> files = FileUtils.getFilesInPath(file);
        
        //computer component
        new NameAndSeparatorComp(null, panel);
        
        for (int i = files.size() - 1; i >= 0; i--) {
            
            new NameAndSeparatorComp(files.get(i), panel); 
        }      
    }
       
    
    private void setHidden(boolean hidden) {
        isHidden = hidden;
    }
    
    private boolean isHidden() {
        return isHidden;
    }

    private File getFile() {
        return file;
    }

    private int getIndex() {
        return compIndex;
    }

    private JPanel getParentPanel() {
        return pathPanel;
    }

    protected FileNameButton getNameButton() {
        return nameBtn;
    }

    protected SeparatorButton getSeparatorButton() {
        return separatorBtn;
    }

    private void setVisibility(boolean visible) {
        this.visible = visible;
    }

    private boolean isVisibleComponent() {
        return visible;
    }

    private static boolean compsHidden() {
        return hiddenComps.size() > 0;
    }

    protected static void cancelPressedBtn() {
        if (pressedComp != null) {
            pressedComp.setPressedState(false);
        }
        if (dirListLeft.isVisible()) {
            dirListLeft.setVisible(false);
        } else {
            dirListRight.setVisible(false);
        }
    }

    class FileNameButton extends JComponent {

        private String text;
        private float alpha = 0.0f;
        private float helperAlpha = 0.0f;
        private GradientPaint gradientPaint;
        private boolean mouseOverLabel = true;
        private boolean mouseOverSeparator = false;
        private boolean isPressed = false;
        private SeparatorButton separatorButton;
        private File file;
        private boolean leftPanel = false;

        FileNameButton(File file, SeparatorButton separatorButton, boolean leftPanel) {
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(new Insets(0, 5, 0, 5)));
            setFont(new Font("Arial", Font.BOLD, 11));

            this.file = file;
            prepareButtonName(file);
            this.leftPanel = leftPanel;
            this.separatorButton = separatorButton;

        }

        private File getFile() {
            return file;
        }

        private boolean isLeftPanel() {
            return leftPanel;
        }

        private void prepareButtonName(File f) {
            if (f != null) {
                text = f.getName().equals("") ? f.toString() : f.getName();
            } else {
                text = "Computer";
            }
        }

        public void setPressed(boolean pressed) {
            isPressed = pressed;
            helperAlpha = 1.0f;
            repaint();
        }

        public float getAlpha() {
            return alpha;
        }

        private void setAlpha(float newAlpha) {
            alpha = newAlpha;
        }

        public String getButtonName() {
            return text;
        }


        /* public void removeFromIndex() {
         //getIndex();
         if (compIndex == leftDirPathsComps.size() - 1) {
         return;
         }

         for (int i = compIndex + 1; i < leftDirPathsComps.size(); i++) {
         leftDirPathsComps.remove(i);
         }
         }*/
        private void changeColor(boolean keepColor) {
            mouseOverSeparator = keepColor;
            mouseOverLabel = !keepColor;
        }

        private void repaintBtn(float alpha) {
            this.alpha = alpha;
            repaint();
        }

        public void setMousePressedAlpha(float alpha) {
            helperAlpha = alpha;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);



            /*
             * int[] xPoints = {getWidth(), 0, 0, getWidth()}; int[] yPoints =
             * {0, 0, getHeight() - 1, getHeight() - 1};
             */

            Graphics2D g2 = (Graphics2D) g.create();

            /*
             * g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
             * RenderingHints.VALUE_ANTIALIAS_ON);
             */

            Dimension dim = getSize();

            if (mouseOverLabel || isPressed) {

                g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
                gradientPaint = new GradientPaint(new Point(1, 1), Color.WHITE, new Point(1, dim.height / 2), new Color(0xffda6c), true);
                g2.setPaint(gradientPaint);
                g2.fillRect(1, 1, dim.width, dim.height);
                g2.setColor(Color.WHITE);
                g2.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
                g2.setColor(new Color(0xffda6c));
                g2.drawLine(0, 0, 0, getHeight() - 1);

                //This is purely cosmeticaly-
                //if NameAndSeparatorComp component doesnt't contain separatorBtn which means
                //that curently opened directory doesn't have directories inside it, draw
                //right vertical line in nameBtn (rim it).
                synchronized (NameAndSeparatorComp.this.getTreeLock()) {
                    if (NameAndSeparatorComp.this.getComponentCount() < 2) {
                        g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);
                    }
                }

                if (isPressed) {

                    int colorAlpha;
                    float startAlpha = 0.5f;

                    g2.setComposite(AlphaComposite.SrcOver.derive(helperAlpha));
                    gradientPaint = new GradientPaint(new Point(1, 1), Color.WHITE, new Point(1, dim.height / 2), new Color(0xffbf00), true);
                    g2.setPaint(gradientPaint);
                    g2.fillRect(0, 0, dim.width, dim.height);
                    //g2.setStroke(new BasicStroke(2f));
                    g2.setColor(new Color(0xffbf00));
                    g2.drawLine(0, 0, 0, getHeight() - 1);
                    g2.drawLine(0, 0, getWidth() - 1, 0);

                    //draw shadow
                    for (int i = 1; i <= 2; i++) {
                        //new Color(255, 191, 0) is RGB value of new Color(0xffbf00)
                        //we're reducing alpha here by 50% from startAlpha
                        //we need alpha value in range 0-255
                        //which we get by multiplying 255 with startAlpha, and cast the result to int                                          
                        startAlpha /= i;
                        colorAlpha = (int) (255 * startAlpha);
                        g2.setColor(new Color(255, 191, 0, colorAlpha));
                        g2.drawLine(i, i, getWidth() - 1, i);
                        g2.drawLine(i, i, i, getHeight() - 1);
                    }

                    /*
                     * g2.drawLine(1, 1, getWidth(), 1); g2.drawLine(1, 1, 1,
                     * getHeight()); g2.drawLine(getWidth(), 1, getWidth(),
                     * getHeight());
                     */

                }

            } else if (mouseOverSeparator) {

                g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
                gradientPaint = new GradientPaint(new Point(1, 1), Color.WHITE, new Point(1, dim.height / 2), new Color(0xe1e1e1), true);
                g2.setPaint(gradientPaint);
                g2.fillRect(1, 1, dim.width, dim.height);
                g2.setColor(Color.WHITE);
                g2.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
                g2.setColor(new Color(0xbebcbc));
                g2.drawLine(0, 0, 0, getHeight() - 1);

                //g2.drawPolyline(xPoints, yPoints, 4);
                //g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
            }


            g2.setComposite(AlphaComposite.SrcOver.derive(1.0f));

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            FontMetrics fm = g2.getFontMetrics();

            // Center text horizontally and vertically
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getMaxAscent();


            g2.setColor(Color.BLACK);
            g2.drawString(text, x, y);  // Draw the string.

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {

            return getPreferredSize(this);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public Dimension getPreferredSize(JComponent c) {

            Graphics g = c.getGraphics();
            FontMetrics fm = g.getFontMetrics();

            int textWidth = fm.stringWidth(text);

            if (text.length() > 48) {
                text = text.substring(0, 45).concat("...");
                textWidth = fm.stringWidth(text) - fm.stringWidth(text.substring(32));
            }



            Insets ins = getBorder().getBorderInsets(c);
            return new Dimension(textWidth + ins.left + ins.right, 25);
        }
    }

    class SeparatorButton extends JComponent {

        private float alpha = 0.0f;
        private float helperAlpha = 0.0f;
        private GradientPaint gradientPaint;
        private boolean mouseOverSeparator = false;
        private boolean isPressed = false;
        private boolean activeSelection = false; //true if this button is still down;
        //using only for the fast refresh of the triangle on this button
        private boolean hiddenComps = false;
        private FileNameButton nameButton;
        private boolean hasFocus = false;

        SeparatorButton(FileNameButton nameButton) {
            //setSize(20, getHeight());
            this.nameButton = nameButton;

        }

        public void setFocus(boolean inFocus) {
            hasFocus = inFocus;
        }

        public boolean isInFocus() {
            return hasFocus;
        }

        public void mouseOver(float alpha) {
            this.alpha = alpha;
            repaint();
        }

        public void setMousePressedAlpha(float alpha) {
            helperAlpha = alpha;
            repaint();
        }

        public void setPressed(boolean pressed) {
            isPressed = pressed;
            activeSelection = isPressed;
            helperAlpha = 1.0f;
            repaint();
        }

        public float getAlpha() {
            return alpha;
        }

        private void setAlpha(float newAlpha) {
            alpha = newAlpha;
        }

        public boolean isMousePresent() {
            return mouseOverSeparator;
        }

        private void repaintBtn(float alpha) {
            this.alpha = alpha;
            repaint();
        }

        private boolean isPressed() {
            return isPressed;
        }

        protected void setHiddenComps(boolean hidden) {
            hiddenComps = hidden;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2D = (Graphics2D) g.create();

            int w, h, size;

            Dimension dim = getSize();


            //<editor-fold defaultstate="collapsed" desc="paint triangle as filled polygon">
            /*int[] xPoints = {getWidth(), 0, 0, getWidth()};
             * int[] yPoints = {0, 0, getHeight() - 1, getHeight() - 1};*/

            /*
             * Set triangle points manualy instead of using method paintTriangle
             * int trSize = Math.min(getWidth()/2, getHeight()/2)+1; int centarX
             * = getWidth()/2; int centarY = getHeight()/2;
             *
             * int[] xPointsTriangle = {centarX-trSize/2, centarX-trSize/2,
             * centarX+trSize/2-2};
             * int[] yPointsTriangle = {centarY-trSize/2,
             * centarY+trSize/2, centarY};
             */
            //</editor-fold>

            g2D.setComposite(AlphaComposite.SrcOver.derive(alpha));
            gradientPaint = new GradientPaint(new Point(1, 1), Color.WHITE, new Point(1, dim.height / 2), new Color(0xffda6c), true);
            g2D.setPaint(gradientPaint);
            g2D.fillRect(1, 1, dim.width, dim.height);
            g2D.setColor(Color.WHITE);
            g2D.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
            g2D.setColor(new Color(0xffda6c));
            g2D.drawLine(0, 0, 0, getHeight() - 1);
            g2D.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);

            if (activeSelection) {

                int colorAlpha = 0;
                float startAlpha = 0.5f;

                g2D.setComposite(AlphaComposite.SrcOver.derive(helperAlpha));
                gradientPaint = new GradientPaint(new Point(1, 1), Color.WHITE, new Point(1, dim.height / 2), new Color(0xffbf00), true);
                g2D.setPaint(gradientPaint);
                g2D.fillRect(0, 0, dim.width, dim.height);
                g2D.setColor(new Color(0xffbf00));
                g2D.drawLine(0, 0, getWidth() - 1, 0);
                g2D.drawLine(0, 0, 0, getHeight() - 1);
                g2D.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);

                //draw inner shadow
                for (int i = 1; i <= 2; i++) {
                    startAlpha /= i;
                    colorAlpha = (int) (255 * startAlpha);
                    g2D.setColor(new Color(255, 191, 0, colorAlpha));
                    g2D.drawLine(i, i, getWidth() - 1, i);
                    g2D.drawLine(i, i, i, getHeight() - 1);
                }

            }

            g2D.setStroke(new BasicStroke());

            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2D.setComposite(AlphaComposite.SrcOver.derive(1.0f));

            w = getWidth();
            h = getHeight();

            size = Math.min((h - 4) / 2, (w - 4) / 2);
            size = Math.max(size, 2);

            g2D.setColor(Color.BLACK);

            paintTriangle(g2D, (w - size) / 2, (h - size) / 2, size, isPressed);




        }

        public void paintTriangle(Graphics2D g2D, int x, int y, int size,
                boolean pressed) {
            int mid, i, j;

            j = 0;
            //size = Math.max(size, 2);

            mid = (size / 2) - 1;

            g2D.translate(x, y + 1);

            if (hiddenComps) { // if some components were removed from path in order to make
                // place for new ones paint two open arrows in "Computer" separator
                g2D.translate(-x, -(y + 1));



                int trSize = Math.min(getWidth() / 2, getHeight() / 2) - 4;

                int centarX = getWidth() / 2;
                int centarY = getHeight() / 2;

                g2D.translate(centarX + 2, centarY);


                int[] xPointsTriangle = {-trSize / 2, trSize / 2,
                    -trSize / 2};
                int[] yPointsTriangle = {-trSize / 2 - 1,
                    0, trSize / 2 + 1};

                for (int k = 0; k < 2; k++) {
                    g2D.drawPolyline(xPointsTriangle, yPointsTriangle, 3);
                    g2D.translate(-4, 0);
                }



            } else {
                if (pressed) {
                    for (i = size - 1; i >= 0; i--) {
                        g2D.drawLine(mid - i, j, mid + i, j);

                        j++;
                    }
                } else {

                    //j = 0;
                    for (i = size - 1; i >= 0; i--) {
                        g2D.drawLine(j, mid - i, j, mid + i);

                        j++;
                    }
                }
            }



        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(15, 25);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
    }

    class ComponentsListener implements MouseListener {

        private FileNameButton nameBtnL;
        private SeparatorButton separatorBtnL;
        private Timer fadeInTimer = null;
        private Timer fadeOutTimer = null;
        private Timer pressedTimer = null;
        private Timer pressedFadeOutTimer = null;
        private long animationStartTime = 0;
        private static final long animationDuration = 300;
        private float alpha = 0.0f;
        private float helperAlpha = 0.0f;
        //we use it to mark color change on mouseover event between buttons
        private boolean changeColor = false;
        private boolean mouseExited = false;
        private float addAlpha = 0.0f;
        private TableAndFiles activeModel;
        private ComponentsListener activeListener = null;
        private ArrayList<File> dirArrayList;

        ComponentsListener(final FileNameButton nameBtn, final SeparatorButton separatorBtn) {
            this.nameBtnL = nameBtn;
            this.separatorBtnL = separatorBtn;
            
            dirArrayList = new ArrayList<File>();

            fadeInTimer = new Timer(30, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (fadeOutTimer.isRunning()) {

                        fadeInTimer.stop();
                    }

                    // calculate the elapsed fraction
                    long currentTime = System.nanoTime() / 1000000;
                    long totalTime = currentTime - animationStartTime;


                    if (totalTime > animationDuration) {
                        animationStartTime = currentTime;
                    }

                    float fraction = (float) totalTime / animationDuration;
                    fraction = Math.min(1.0f, fraction);

                    alpha = Math.abs(0 - fraction);

                    if (addAlpha > 0) {
                        alpha += addAlpha;
                    }

                    if (alpha >= 1.0f) {
                        alpha = 1.0f;
                        fadeInTimer.stop();
                        addAlpha = 0;
                    }

                    nameBtn.repaintBtn(alpha);

                    if (separatorBtn != null) {
                        separatorBtn.repaintBtn(alpha);
                    }

                }
            });

            fadeOutTimer = new Timer(30, new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    if (fadeInTimer.isRunning()) {

                        fadeOutTimer.stop();
                    }

                    // calculate the elapsed fraction
                    long currentTime = System.nanoTime() / 1000000;
                    long totalTime = currentTime - animationStartTime;

                    if (totalTime > animationDuration) {
                        animationStartTime = currentTime;
                    }
                    float fraction = (float) totalTime / animationDuration;
                    fraction = Math.min(1.0f, fraction);

                    alpha = Math.abs(1 - fraction);
//System.out.println("alpha " + alpha);
                    if (alpha == 0.0f) {

                        fadeOutTimer.stop();
                    }

                    nameBtn.repaintBtn(alpha);
                    if (separatorBtn != null) {
                        separatorBtn.repaintBtn(alpha);
                    }

                }
            });

            pressedTimer = new Timer(30, new ActionListener() {
                public void actionPerformed(ActionEvent e) {


                    // calculate the elapsed fraction
                    long currentTime = System.nanoTime() / 1000000;
                    long totalTime = currentTime - animationStartTime;

                    if (totalTime > animationDuration) {
                        animationStartTime = currentTime;
                    }
                    float fraction = (float) totalTime / animationDuration;
                    fraction = Math.min(1.0f, fraction);

                    helperAlpha = Math.abs(1 - fraction);

                    nameBtn.setMousePressedAlpha(helperAlpha);

                    if (separatorBtn != null) {
                        separatorBtn.setMousePressedAlpha(helperAlpha);
                    }
                    //System.out.println("helper " + helperAlpha);
                    if (helperAlpha == 0.0f) {

                        pressedTimer.stop();
                        nameBtn.setPressed(false);

                        if (separatorBtn != null) {
                            separatorBtn.setPressed(false);
                        }

                        if (mouseExited) {

                            animationStartTime = System.nanoTime() / 1000000;
                            fadeOutTimer.start();
                        }
                    }
                }
            });

            pressedFadeOutTimer = new Timer(30, new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    // calculate the elapsed fraction
                    long currentTime = System.nanoTime() / 1000000;
                    long totalTime = currentTime - animationStartTime;

                    if (totalTime > animationDuration) {
                        animationStartTime = currentTime;
                    }
                    float fraction = (float) totalTime / animationDuration;
                    fraction = Math.min(1.0f, fraction);

                    helperAlpha = Math.abs(1 - fraction);


                    nameBtn.setMousePressedAlpha(helperAlpha);

                    if (separatorBtn != null) {
                        separatorBtn.setMousePressedAlpha(helperAlpha);
                    }
                    
                    if (helperAlpha == 0.0f) {
                        pressedFadeOutTimer.stop();
                        
                        nameBtn.setPressed(false);

                        if (separatorBtn != null) {
                            separatorBtn.setPressed(false);
                        }
                    }

                }
            });



            nameBtn.addMouseListener(this);

            if (separatorBtn != null) {
                separatorBtn.addMouseListener(this);
            }
        }

        private void startPressedTimer() {
            nameBtnL.setAlpha(0.0f);
            
            if (separatorBtnL != null) {
                separatorBtnL.setAlpha(0.0f);
            }
            
            nameBtnL.changeColor(false);
            animationStartTime = System.nanoTime() / 1000000;
            pressedFadeOutTimer.start();
        }

        public void mouseClicked(MouseEvent e) {

            if (nameBtnL.isLeftPanel()) {

                activeModel = interactModels[0];
            } else {

                activeModel = interactModels[1];
            }

            if (e.getSource() == nameBtnL) {
                
                if (dirListLeft.isVisible() && nameBtnL.isLeftPanel()) {
                    dirListLeft.setVisible(false);
                                                       //it's button in the right panel then hide directory list
                } else if (dirListRight.isVisible() && !nameBtnL.isLeftPanel()) {
                    dirListRight.setVisible(false);
                    
                } 
                    Thread runner = new Thread() {
                    @Override
                    public void run() {

                        activeModel.getTableData().setData(nameBtnL.getFile());

                        {
                            Runnable runnable = new Runnable() {
                                public void run() {

                                    activeModel.getTable().tableChanged(new TableModelEvent(activeModel.getTableData()));
                                    activeModel.getTableData().makeSelection();
                                    if (FileCommander.activePanel == FileCommander.pathPanel) {
                                        FileCommander.ds1.setDisc(nameBtnL.getFile());
                                    } else {
                                        FileCommander.ds2.setDisc(nameBtnL.getFile());
                                    }
                                }
                            };
                            SwingUtilities.invokeLater(runnable);
                        }
                    }
                };
                runner.start();
                

                removeCompsStartAfter(nameBtnL);

                //cancel previous selections on separator button
                prevCompListener = null;
                prevCompListenerHelp = null;

            } else if (e.getSource() == separatorBtnL) {

                //if directory list is already visible and we have pressed
                //again the same button in the same path panel then hide the list
                if (dirListLeft.isVisible() && nameBtnL.isLeftPanel()) {
                    dirListLeft.setVisible(false);
                                                       //it's button in the right panel then hide directory list
                } else if (dirListRight.isVisible() && !nameBtnL.isLeftPanel()) {
                    dirListRight.setVisible(false);
                    
                } else {
                    Thread runner = new Thread() {
                        @Override
                        public void run() {
                            
                            dirArrayList.clear();
                            File file = nameBtnL.getFile();

                            if (file != null) {
                                dirArrayList.addAll(Arrays.asList(FileUtils.filterDirs(file)));
                                //dirs = FileUtils.filterDirs(file);
                            } else {
                                
                                hiddenComps = nameBtnL.isLeftPanel() ? leftHiddenComps : rightHiddenComps;

                                for (int i = hiddenComps.size() - 1; i >= 0; i--) { //reverse order display
                                    dirArrayList.add(hiddenComps.get(i).getFile());
                                }
                                dirArrayList.addAll(Arrays.asList(File.listRoots()));
                            }
                            
                            

                            dirListHelper = nameBtnL.isLeftPanel() ? dirListLeft : dirListRight;
                            dirListHelper.initDirList(dirArrayList, nameBtnL);
                       
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    dirListHelper.setVisible(true);
                                }
                            };
                            SwingUtilities.invokeLater(runnable);
                        }
                    };
                    runner.start();
                }

                if (nameBtnL.isLeftPanel()) {
                    if (activeLeftPathPanel) {
                        prevCompListener = null;
                        activeLeftPathPanel = false;
                    }
                } else {
                    if (activeRightPathPanel) {
                        prevCompListenerHelp = null;
                        activeRightPathPanel = false;

                    }
                }
            }
        }

        public void mousePressed(MouseEvent e) {

            if (e.getSource() == nameBtnL) {
                
                if (separatorBtnL != null && separatorBtnL.isPressed) {
                   
                    nameBtnL.setPressed(false);
                    separatorBtnL.setPressed(false);
                    
                    animationStartTime = System.nanoTime() / 1000000;
                    pressedFadeOutTimer.start();
                    return;
                }

                //nameBtn.removeFromIndex();
                fadeInTimer.stop();
                nameBtnL.setPressed(true);
                if (separatorBtnL != null) {
                    separatorBtnL.setPressed(true);
                }

            } else if (separatorBtnL != null) {
                pressedComp = this; 
                if (separatorBtnL.isPressed) {
                   
                    nameBtnL.setPressed(false);
                    separatorBtnL.setPressed(false);
                    return;
                }
                fadeInTimer.stop();
                separatorBtnL.setPressed(true);
                nameBtnL.setPressed(true);
            }

        }
        
        //if directory list is visible and we click on the table row
        //after directory list is hidden, this method will cancel buttons
        //pressed state
        private void setPressedState(boolean state) {

            pressedComp = null;
            
            nameBtnL.setPressed(state);
            separatorBtnL.setPressed(state);

            if (nameBtnL.isLeftPanel()) {
                if (activeLeftPathPanel) {
                    prevCompListener = null;
                    activeLeftPathPanel = false;
                }
            } else {
                if (activeRightPathPanel) {
                    prevCompListenerHelp = null;
                    activeRightPathPanel = false;

                }
            }

            animationStartTime = System.nanoTime() / 1000000;
            fadeOutTimer.start();
        }

        public void mouseReleased(MouseEvent e) {

            if (!(e.getSource() == separatorBtnL)) {
                mouseExited = false; //if nameBtn was pressed after separatorBtn was pressed too
                //we don't want to start fadeOutTimer because we want to keep color
                animationStartTime = System.nanoTime() / 1000000;
                pressedTimer.start();
            }
        }

        public void mouseEntered(MouseEvent e) {

            if (nameBtnL.isLeftPanel()) {
                activeListener = prevCompListener;
            } else {
                activeListener = prevCompListenerHelp;
            }

            if (!(separatorBtnL != null && separatorBtnL.isPressed)) {
                mouseExited = false;
                nameBtnL.changeColor(false);
            } else if (e.getSource() == nameBtnL) { //if nameBtn was pressed after separatorBtn was pressed too
                nameBtnL.changeColor(false);       //this will change color to orange, otherwise the nameBtn would
                //be gray after mousePress  
                
            } else { //separatorBtn  

                nameBtnL.changeColor(true);
            }

            if ((activeListener != null && this != activeListener)) {

                File file = nameBtnL.getFile();
                
                if (file != null && FileUtils.filterDirs(nameBtnL.getFile()).length <= 0) {
                    dirListHelper.setVisible(false);
                }
                
                activeListener.startPressedTimer();
                nameBtnL.setPressed(true);
                nameBtnL.setAlpha(1.0f);
                if (separatorBtnL != null) {
                    pressedComp = this; 
                    separatorBtnL.setPressed(true);
                    separatorBtnL.setAlpha(1.0f);
                    
                   
                    Thread runner = new Thread() {
                        @Override
                        public void run() {
                             
                            dirArrayList.clear();
                            File file = nameBtnL.getFile();
                           
                            if (file != null) {
                                dirArrayList.addAll(Arrays.asList(FileUtils.filterDirs(file)));
                                //dirs = FileUtils.filterDirs(file);
                            } else {
                                

                                hiddenComps = nameBtnL.isLeftPanel() ? leftHiddenComps : rightHiddenComps;

                                
                                for (int i = hiddenComps.size() - 1; i >= 0; i--) { //reverse order display
                                    dirArrayList.add(hiddenComps.get(i).getFile());
                                }
                                dirArrayList.addAll(Arrays.asList(File.listRoots()));
                            }

                            dirListHelper = nameBtnL.isLeftPanel() ? dirListLeft : dirListRight;
                            dirListHelper.initDirList(dirArrayList, nameBtnL);
                            
                            if (!dirListHelper.isVisible()) {
                                Runnable runnable = new Runnable() {
                                    public void run() {
                                        dirListHelper.setVisible(true);
                                    }
                                };
                                SwingUtilities.invokeLater(runnable);
                            }
                        }
                    };
                    runner.start();

                    //FileCommander.getTopJFrame().getLayeredPane().add(dirList.getScrollPane(), 210, 0);
                    //dirList = new DirectorysList(null, nameBtnL.getFile(), nameBtnL);

                }
                return;
            }

            if (!(separatorBtnL != null && separatorBtnL.isPressed)) {
                fadeOutTimer.stop();

                if (e.getSource() == nameBtnL) {

                    if (separatorBtnL != null && separatorBtnL.getAlpha() > 0) {
                        changeColor = false;
                        //when we go from separator to name button inside common component and fade animation
                        //didn't finished for either of those two, we need to keep alpha value in helper variable
                        //so we can add it to original alpha value inside timer to avoid gap in time 
                        //which happens during this transition and thus is reducing alpha, which appears as animation has been reseted.
                        //This occurs very rarerly and maybe won't be noticed, but it occurs.              
                        if (alpha < 1.0) {
                            nameBtnL.changeColor(changeColor);
                            addAlpha = alpha;
                            return; //don't need to repaint button if animation hasn't finished
                        }

                        nameBtnL.changeColor(changeColor);
                        nameBtnL.repaintBtn(alpha);

                    } else {

                        changeColor = false;
                        nameBtnL.changeColor(changeColor);
                        animationStartTime = System.nanoTime() / 1000000;
                        fadeInTimer.start();

                    }
                } else if (e.getSource() == separatorBtnL) {
                    changeColor = true;
                    if (nameBtnL.getAlpha() > 0) {


                        if (alpha < 1.0) {
                            nameBtnL.changeColor(changeColor);

                            addAlpha = alpha;
                            return;
                        }

                        nameBtnL.changeColor(changeColor);
                        nameBtnL.repaintBtn(alpha);
                    } else {

                        changeColor = true;
                        nameBtnL.changeColor(changeColor);
                        animationStartTime = System.nanoTime() / 1000000;
                        fadeInTimer.start();
                    }
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            mouseExited = true;


            /*if (nameBtn.isPressed) {
             nameBtn.repaintBtn(0.0f);
             if (separatorBtn != null) {
             separatorBtn.repaintBtn(0.0f);
             }
             }*/

            if (!(separatorBtnL != null && separatorBtnL.isPressed)) {
                //prevCompListener = null;

                if (!pressedTimer.isRunning()) {

                    if (nameBtnL.isPressed) {
                        if (nameBtnL.isLeftPanel()) {
                            activeLeftPathPanel = true;
                            prevCompListener = this;
                        } else {
                            activeRightPathPanel = true;
                            prevCompListenerHelp = this;
                        }
                    } else {
                        animationStartTime = System.nanoTime() / 1000000;
                        fadeOutTimer.start();
                    }

                }

                //if we move mouse pointer fast out of the button after we press it
                //before pressedTimer has finished and set
                //buttons as not pressed, prevCompListener variable
                //will point to this button which will lead to the next button we enter
                //with mouse as being selected, just as we have selected
                //separatorBtn on previous button
            } else if (!pressedTimer.isRunning()) {


                //if we have pressed separator button on one path panel we only want to keep pressed state
                //of buttons when we move from button to button on the same path panel, and not when
                //we enter one of the buttons on the other pathPanel
                if (nameBtnL.isLeftPanel()) {
                    activeLeftPathPanel = true;
                    prevCompListener = this;
                } else {
                    activeRightPathPanel = true;
                    prevCompListenerHelp = this;
                }
            }
        }
    }
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//
//            public void run() {
//                JFrame fm = new JFrame();
//                fm.getContentPane().setBackground(Color.WHITE);
//                fm.getContentPane().add(new NameAndSeparatorComp(new File(System.getProperty("user.home")), null));
//                fm.pack();
//                fm.setVisible(true);
//                fm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            }
//        });
//    }
}
