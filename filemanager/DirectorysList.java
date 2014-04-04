/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Mladen
 */
public final class DirectorysList  {
    
    private JPanel listPanel;
    
    private final JScrollPane scrollPane;
    private EtchedSeparator separator;
    
    GridBagLayout grid;
    GridBagConstraints constraints;
    
    private boolean initialized = false;
    
    private static final int VIEW_HEIGHT = 400;
    private boolean visible;
    private boolean shoudValidate = false;
        
    private static JLayeredPane layeredPane = FileCommander.getTopJFrame().getLayeredPane();
    
    private ArrayList<File> list;
    
    public DirectorysList() {
                      
        listPanel = new JPanel();
      
        
        constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        grid = new GridBagLayout();
        
        listPanel.setLayout(grid);
        
      //setLayout(grid);
        listPanel.setBackground(Color.WHITE);
        
                     
        //initDirList(directory, comp);
        
        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getViewport().add(listPanel);
        //scrollPane.setSize(200, 400);
           
        //calculatePosition(comp);
        
        //initialized = true;
    }
    
    public void initDirList(ArrayList<File> files, JComponent comp) {
       
        list = files;      
        fillPanel();   
        
        //if (initialized) {           
            calculatePosition(comp);
            
            if (shoudValidate) {
            scrollPane.getParent().validate();
            
        }
            
            //FileCommander.getTopJFrame().getLayeredPane().repaint();
        //}
        
       
    }
    
    private void calculatePosition(JComponent comp) {
        Dimension dim = comp.getSize();
        Point point = comp.getLocationOnScreen();
        Point layersPoint = FileCommander.getTopJFrame().getLayeredPane().getLocationOnScreen();      
        
        calculateSize();
        scrollPane.setLocation(Math.abs(layersPoint.x - point.x),
                Math.abs(layersPoint.y - point.y) + dim.height);
        
    }
    
    private void calculateSize() {
        int length = list.size();
//        int labelHeight = 0;
//        int sepHeight = 0;
//          int sepCount = 0;
//        synchronized (listPanel.getTreeLock()) {
//            if (listPanel.getComponentCount() > 2) {
//                labelHeight = listPanel.getComponent(0).getHeight();
//                sepHeight = listPanel.getComponent(1).getHeight();
//            }         
//        }
        
        
        //30 is heigh of JLabel
        //15 is max number of components visible without scrolling
        if (length * 30 > VIEW_HEIGHT) {
            //sepCount = VIEW_HEIGHT / labelHeight;
            scrollPane.setSize(200, VIEW_HEIGHT-6);//if eatch separator height is 1px
        } else {
            scrollPane.setSize(200, length * 30+length+2);
        }
    }
    
    private void fillPanel() {
        
        synchronized (listPanel.getTreeLock()) {
            if (listPanel.getComponentCount() > 0) {
                listPanel.removeAll();
                shoudValidate = true;
                
            }
        }
//        if (initialized) {
//            listPanel.removeAll();
//        }
        
        //listPanel.add(Box.createVerticalStrut(5));
        for (int i = 0; i < list.size(); i++) {  
            constraints.gridy = i;
            listPanel.add(new ListItem(list.get(i).getName(), Icons.getIcon(list.get(i)), SwingConstants.CENTER), constraints);
            //listPanel.add(Box.createVerticalStrut(8));
            constraints.gridy = i+1;
            listPanel.add(new EtchedSeparator(new Color(0xEBEBEB), new Color(0xF7F7F7)), constraints);
        }
        //listPanel.add(Box.createVerticalStrut(5));
    }
    
    
    
    protected void hideDialog() {
        //setVisible(false);
    }
    
    protected void setVisible(boolean visible) {
        this.visible = visible;
       
        if (!visible) {
            if (listPanel.getComponentCount() > 0) {               
                listPanel.removeAll();               
                scrollPane.validate();
                layeredPane.remove(scrollPane);
                layeredPane.repaint(scrollPane.getBounds());
                shoudValidate = false;               
            }
        } else {
            layeredPane.add(scrollPane, 210, 0);
        }
        
        
    }
    
    protected boolean isVisible() {
        return visible;
    }
    
    protected JScrollPane getScrollPane() {
        return scrollPane;
    }
    
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//
//            public void run() {
//                
//                JFrame fm = new JFrame();
//                fm.setUndecorated(true);
//                fm.setSize(100, 250);
//                fm.getContentPane().setBackground(Color.WHITE);
//                DirectorysList dirPanel = new DirectorysList(null, new File(System.getProperty("user.home")), null);
//                
//                fm.getContentPane().add(dirPanel.getScrollPane());
//                //fm.pack();
//                fm.setVisible(true);
//                fm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                
//                //dirPanel.setLocationRelativeTo(null);
//                
//                //dirPanel.setVisible(true);
//               
//            }
//        });
//    }
    
    class ListItem extends JLabel {
        
        Insets insets = new Insets(7, 5, 7, 3);
        
        private boolean mouseEntered = false;
        private GradientPaint gradientPaint;

        public ListItem(String text, Icon icon, int horizontalAlignment) {
            super(text, icon, horizontalAlignment);                  
            setIconTextGap(8);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(insets));           
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    mouseEntered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    mouseEntered = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    
                }
            });
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            
            if (mouseEntered) {
                Graphics2D g2 = (Graphics2D) g.create();
                
                //Dimension dim = getParent().getSize();              
            
                //System.out.println(getHeight());
               
                //g2.setColor(new Color(0xffda6c));
                //g2.drawRect(0, 0, getWidth()-1, getHeight()-2);
                                               
                gradientPaint = new GradientPaint(new Point(0, 0), Color.WHITE,
                    new Point(0, getHeight() / 2), new Color(0xffda6c), true);
                g2.setPaint(gradientPaint);
                
                //g2.fillRect(1, 1, getWidth()-2, getHeight()-2);
                g2.fillRect(1, 2, getWidth()-2, getHeight()-3);
                
                g2.setColor(new Color(0xffe187));
                g2.drawRect(0, 1, getWidth()-1, getHeight()-2);
                super.paintComponent(g);
            }
            
        }       
    }
}
    

