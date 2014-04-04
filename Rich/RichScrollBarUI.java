/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Rich;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.*;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.Animator.Direction;
import org.jdesktop.animation.timing.Animator.RepeatBehavior;
import org.jdesktop.animation.timing.TimingTarget;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 *
 * @author Mladen
 */
public class RichScrollBarUI extends MetalScrollBarUI implements TimingTarget {

    protected RichArrowButton increaseButton;
    protected RichArrowButton decreaseButton;

    protected Color r_borderNormal = null;  
    protected Color r_borderOver = null;

    protected Color rectColor;              
    protected Rectangle rectThumb;          
    protected LinearGradientPaint gradientT;

    private Component ce;                   //promenljiva za komponentu koju hoćemo da iscrtamo izvan 
                                            //paint**** metoda                                           

    private boolean mouseActive = false;    //da znamo kada da idemo sa postepenom promenom boje
                                            //(fade-in, fade-out) nakon događaja miša
    
    private Animator in;                    //dva animatora za (fade-in, fade-out)
    private Animator out;
    
    private float alpha = 1.0f;
    
    
    public RichScrollBarUI() {
       in = new Animator(400, 1.0f, RepeatBehavior.REVERSE, this);
       out = new Animator(400, 1.0f, RepeatBehavior.REVERSE, this);
       out.setStartFraction(1.0f);
       out.setStartDirection(Direction.BACKWARD);
    }


    @Override
    public void installUI(JComponent c) {
        super.installUI(c);

        r_borderNormal = UIManager.getColor("ScrollBar.BorderNormal");
        r_borderOver = UIManager.getColor("ScrollBar.BorderOver");
        c.addMouseListener(new thumb());
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        c.removeMouseListener(new thumb());
    }

    // Create our own scrollbar UI!
  public static ComponentUI createUI( JComponent c ) {
    return new RichScrollBarUI( );
  }


    //Return custom IncreaseButton
    @Override
    protected JButton createIncreaseButton(int orientation) {
    return increaseButton = new RichArrowButton(orientation, scrollBarWidth, isFreeStanding);
    }

    //Return custom DecreaseButton
    @Override
    protected JButton createDecreaseButton(int orientation)  {
    return decreaseButton = new RichArrowButton(orientation, scrollBarWidth, isFreeStanding);
    }


    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {

        g.translate(trackBounds.x, trackBounds.y);
        Graphics2D g2d = (Graphics2D) g.create();
        
        LinearGradientPaint gradient = null;

        if (!c.isEnabled()) {
            return;
        }

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            if (!isFreeStanding) {
                trackBounds.width += 2;
            }

            gradient = new LinearGradientPaint(0.0f, 0.0f, trackBounds.width, 0.0f,
                    new float[]{0.0f, 0.5f, 1.0f},
                    new Color[]{new Color(0xc2c0d0),
                        new Color(0xffffff),
                        new Color(0xffffff)});

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, trackBounds.width, trackBounds.height);
            g2d.dispose();

            if (!isFreeStanding) {
                trackBounds.width -= 2;
            }

        } else { // HORIZONTAL
            if (!isFreeStanding) {
                trackBounds.height += 2;
            }
            gradient = new LinearGradientPaint(0.0f, 0.0f, 0.0f, trackBounds.height,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{new Color(0xc2c0d0),
                        new Color(0xffffff),
                        new Color(0xffffff)});

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, trackBounds.width, trackBounds.height);
            g2d.dispose();

            if (!isFreeStanding) {
                trackBounds.height -= 2;
            }
        }
        g.translate(-trackBounds.x, -trackBounds.y);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {

        g.translate(thumbBounds.x, thumbBounds.y);      
        Graphics2D g2d = (Graphics2D) g.create();
        
        ce = c;

        rectThumb = thumbBounds;
        

        if (!c.isEnabled()) {
            return;
        }      
        
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            if (!isFreeStanding) {
                thumbBounds.width += 2;
            }

            gradientT = new LinearGradientPaint(3.0f, 0.0f, rectThumb.width - 2, 0.0f,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{new Color(0xffffff),
                        new Color(0xcfccdd),
                        new Color(0xffffff)},
                    MultipleGradientPaint.CycleMethod.REFLECT);
         

            g2d.setPaint(gradientT);
            g2d.fillRect(0, 0, thumbBounds.width-1, thumbBounds.height-1);

            g2d.setColor(r_borderNormal);
            g2d.drawRect(0, 0, thumbBounds.width-1, thumbBounds.height-1);

            if (mouseActive == true) {

                gradientT = new LinearGradientPaint(3.0f, 0.0f, thumbBounds.width - 2, 0.0f,
                        new float[]{0.0f, 0.6f, 1.0f},
                        new Color[]{new Color(0xffffff),
                            new Color(0xffca2e),
                            new Color(0xffffff)},
                        MultipleGradientPaint.CycleMethod.REFLECT);

            g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
            g2d.setPaint(gradientT);
            g2d.fillRect(0, 0, thumbBounds.width-1, thumbBounds.height-1);

            g2d.setColor(r_borderOver);
            g2d.drawRect(0, 0, thumbBounds.width-1, thumbBounds.height-1);

            }

            g2d.dispose();

            if (!isFreeStanding) {
                thumbBounds.width -= 2;
            }

        } else { // HORIZONTAL
            if (!isFreeStanding) {
                thumbBounds.height += 2;
            } 
            
            gradientT = new LinearGradientPaint(0.0f, 3.0f, 0.0f, rectThumb.height - 2,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{new Color(0xffffff),
                        new Color(0xcfccdd),
                        new Color(0xffffff)},
                    MultipleGradientPaint.CycleMethod.REFLECT);


            g2d.setPaint(gradientT);
            g2d.fillRect(0, 0, thumbBounds.width-1, thumbBounds.height-1);

            g2d.setColor(r_borderNormal);
            g2d.drawRect(0, 0, thumbBounds.width-1, thumbBounds.height-1);

            if (mouseActive == true) {

                gradientT = new LinearGradientPaint(0.0f, 3.0f, 0.0f, rectThumb.height - 2,
                        new float[]{0.0f, 0.6f, 1.0f},
                        new Color[]{new Color(0xffffff),
                            new Color(0xffca2e),
                            new Color(0xffffff)},
                        MultipleGradientPaint.CycleMethod.REFLECT);

            g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
            g2d.setPaint(gradientT);
            g2d.fillRect(0, 0, thumbBounds.width-1, thumbBounds.height-1);

            g2d.setColor(r_borderOver);
            g2d.drawRect(0, 0, thumbBounds.width-1, thumbBounds.height-1);

            }

            g2d.dispose();

            if (!isFreeStanding) {
                thumbBounds.height -= 2;
            }
        }
        g.translate(-thumbBounds.x, -thumbBounds.y);
    }

    public Color getRectColor() {
        return rectColor;
    }

    public void timingEvent(float alpha) {
        this.alpha = alpha;
        ce.repaint();      
    }

    public void begin() {}

    public void end() {}

    public void repeat() {}

      
 
    public class thumb extends MouseAdapter {

        private boolean on;      //kada pritisnemo mišem jezičak i dok je taster pritisnut
                                 //izađem iz granica scrolBara ne želimo da ponovo iscrtamo (repaint)

        public thumb() {
            on = true;
        }

        @Override
        public void mousePressed(MouseEvent e) {            
            on = false;           
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            on = true;
            if (in.isRunning())
                in.stop();
            if (!e.getComponent().contains(e.getPoint()))   //ne idemo u fade out
                out.start();                                //ako je kursor iznad jezička
            
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        
            if (on == true) {
                mouseActive = true;
                if (out.isRunning())
                    out.stop();              
                in.start();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {

            if (on == true) {
                if (in.isRunning())
                    in.stop();               
                out.start();
            }

        }
    }
}


  


