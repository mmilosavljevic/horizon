/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rich;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import javax.sound.sampled.Clip;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/**
 *
 * @author Mladen
 */
public class RichButtonUI extends BasicButtonUI {

    // Create our own Button UI!
    public static ComponentUI createUI(JComponent c) {
        return new RichButtonUI();
    }
    private AbstractButton b = null;
    private ButtonModel model;
    private Border border;
    private Border borderOver;
    private LinearGradientPaint elipsePaint;
    //private LinearGradientPaint backgroundPaint;
    private GradientPaint backgroundPaint;
    private Timer fadeInTimer = null;
    private Timer fadeOutTimer = null;
    private Timer defaultButtonTimer = null;
    private long animationStartTime;
    private long animationDuration = 300;
    private long animationDurationFocus = 2000;
    private float alpha = 0.0f;
    private float focusAlpha = 0.0f;
    private boolean rollover = false;
    private MouseAdapter mouseAdapter;
    private FocusListener focusListener;

    @Override
    public void installUI(final JComponent c) {
        super.installUI(c);

        c.setOpaque(false);       

        b = (AbstractButton) c;
        model = b.getModel();

        border = UIManager.getBorder("Button.Border");
        borderOver = UIManager.getBorder("Button.BorderOver");

        //b.setBorder(border);

        b.setBorder(new EmptyBorder(6, 17, 6, 17));

        fadeInTimer = new Timer(30, new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                // calculate the elapsed fraction
                long currentTime = System.nanoTime() / 1000000;
                long totalTime = currentTime - animationStartTime;

                float fraction = (float) totalTime / animationDuration;
                fraction = Math.min(1.0f, fraction);

                if (focusAlpha > 0.0f) {
                    fraction = Math.min(1.0f, fraction + focusAlpha);
                }

                alpha = Math.abs(0 - fraction);

                if (alpha == 1.0f) {
                    //focusAlpha = 0.0f;
                    fadeInTimer.stop();
                }
                c.repaint();
            }
        });

        fadeOutTimer = new Timer(30, new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                // calculate the elapsed fraction
                long currentTime = System.nanoTime() / 1000000;
                long totalTime = currentTime - animationStartTime;

                float fraction = (float) totalTime / animationDuration;
                fraction = Math.min(1.0f, fraction);

                alpha = Math.abs(1 - fraction);

                if (b.isFocusOwner() && alpha <= 0.5f) {
                    fadeOutTimer.stop();

                    defaultButtonTimer.start();
                    alpha = 0.5f;

                }
                
                if (alpha == 0.0f) {
                    fadeOutTimer.stop();
                }
                c.repaint();
            }
        });


        defaultButtonTimer = new Timer(30, new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                long currentTime = System.nanoTime() / 1000000;
                long totalTime = currentTime - animationStartTime;
                if (totalTime > animationDurationFocus) {
                    animationStartTime = currentTime;
                }
                float fraction = (float) totalTime / animationDurationFocus;
                fraction = Math.min(1.0f, fraction);

                alpha = Math.abs(1.0f - fraction);

                if (alpha < 0.5f) {
                    alpha = 1 - alpha;//0.5f + Math.abs(0.5f - (2*fraction));                   
                }

                c.repaint();
            }
        });

        mouseAdapter = new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                if (b.isFocusOwner() && defaultButtonTimer.isRunning()) {
                    animationDuration = 300;
                    //System.out.println("owner");
                    defaultButtonTimer.stop();
                    focusAlpha = alpha;
                }

                //b.setBorder(borderOver);

                if (fadeOutTimer.isRunning()) {
                    fadeOutTimer.stop();
                }
                animationStartTime = System.nanoTime() / 1000000;

                fadeInTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {

                if (fadeInTimer.isRunning()) {
                    fadeInTimer.stop();
                }

                //just in case
                if (defaultButtonTimer.isRunning()) {
                    return;
                }

                //b.setBorder(border);

                if (b.isFocusOwner()) {
                    animationDuration = 2000;
                }

                animationStartTime = System.nanoTime() / 1000000;

                fadeOutTimer.start();
                //focusAlpha = alpha;
            }
        };

        focusListener = new FocusListener() {

            public void focusGained(FocusEvent e) {
                animationStartTime = System.nanoTime() / 1000000;
                defaultButtonTimer.start();
            }

            public void focusLost(FocusEvent e) {
                defaultButtonTimer.stop();
                animationStartTime = System.nanoTime() / 1000000;
                fadeOutTimer.start();
                //alpha = 0.0f;
            }
        };

        c.addMouseListener(mouseAdapter);
        c.addFocusListener(focusListener);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();

        Dimension d = b.getSize();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Rectangle rect = new Rectangle(-d.width/3, -d.width*2, d.width + d.width/3, d.height/2 + d.width*2);

        //Ellipse2D elipse = new Ellipse2D.Double(-d.width/3,-d.height/2 , d.width*1.64, d.height+2);
        //Ellipse2D elipse = new Ellipse2D.Double(1, 1, d.width-2, d.height-2);
        //RoundRectangle2D roundClip = new RoundRectangle2D.Double(1, 1, d.width - 2, d.height - 2, 5, 5);

        if (b.isContentAreaFilled() && c.isEnabled() && !b.hasFocus()) {
            
            backgroundPaint = new GradientPaint(new Point(0, 0), Color.WHITE, new Point(0, d.height/2), new Color(0xf1f1f1), true);
        

            g2.setPaint(backgroundPaint);
            g2.fillRoundRect(0, 0, d.width, d.height, 10, 10);


            if (!b.hasFocus()) {
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, d.width - 1, d.height - 1, 10, 10);
            }

            g2.setComposite(AlphaComposite.SrcOver.derive(alpha));

            //b.setBorder(borderOver);
            
            backgroundPaint = new GradientPaint(new Point(0, 0), Color.WHITE, new Point(0, d.height/2), new Color(0xffda6c), true);

            
            g2.setPaint(backgroundPaint);
            g2.fillRoundRect(0, 0, d.width, d.height, 10, 10);      
        }

        if (model.isArmed() || model.isPressed()) {
            
            backgroundPaint = new GradientPaint(new Point(0, 0), Color.WHITE, new Point(0, d.height/2), new Color(0xffbf00), true);
  
            g2.setPaint(backgroundPaint);
            g2.fillRoundRect(0, 0, d.width, d.height, 10, 10);
            g2.setColor(new Color(0xffbf00));
            g2.drawRoundRect(0, 0, d.width - 1, d.height - 1, 10, 10);
        }


        if (!b.hasFocus()) {
            g2.setColor(new Color(255, 218, 108));
            g2.drawRoundRect(0, 0, d.width - 1, d.height - 1, 10, 10);
        }

        if (b.isFocusPainted() && b.hasFocus() && !(model.isArmed() || model.isPressed())) {
 
            g2.setColor(new Color(255, 218, 108));
            g2.drawRoundRect(0, 0, d.width - 1, d.height - 1, 10, 10);
            
            backgroundPaint = new GradientPaint(new Point(0, 0), Color.WHITE, new Point(0, d.height/2), new Color(0xf1f1f1), true);
            
            
            //System.out.println("alpha " + focusAlpha);
            g2.setPaint(backgroundPaint);
            g2.fillRoundRect(1, 1, d.width - 2, d.height - 2, 10, 10);


            g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
            
            backgroundPaint = new GradientPaint(new Point(0, 0), Color.WHITE, new Point(0, d.height/2), new Color(0xffda6c), true);

           
            g2.setPaint(backgroundPaint);
            g2.fillRoundRect(1, 1, d.width - 2, d.height - 2, 10, 10);

        } 
        /*if (model.isArmed() && model.isPressed()) {
        
        backgroundPaint = new LinearGradientPaint(0.0f, 0.0f, 0.0f, d.height / 2,
        new float[]{0.0f, 1.0f},
        new Color[]{new Color(0xffffff),
        new Color(0xffbf00)},
        MultipleGradientPaint.CycleMethod.REFLECT);
        
        
        //g2.setPaint(new Color(0xfcdb7a));
        g2.setPaint(backgroundPaint);
        g2.fillRoundRect(0, 0, d.width, d.height, 10, 10);
        g2.setColor(new Color(255, 218, 108));
        g2.drawRoundRect(0, 0, d.width-1, d.height-1, 10, 10);
        }*/

        /*elipsePaint = new LinearGradientPaint(0.0f, roundClip.getBounds().y, 0.0f, roundClip.getBounds().height / 2 + 5,
        new float[]{0.0f, 1.0f},
        new Color[]{new Color(0xffffff),
        new Color(255, 255, 255, 0)});*/

        //g2.setComposite(AlphaComposite.SrcOver.derive(0.7f));
        //g2.setPaint(elipsePaint);
        //g2.clip(roundClip);
        //g2.fill(elipse);
        //g2.fillOval(0, -d.width/2, d.width+d.width/2, d.height);
        g2.dispose();
        paint(g, c);
    }

    /*public Dimension getMinimumSize(JComponent c) {
    Dimension d = getPreferredSize(c);
    
    return d;
    }
    
    public Dimension getMaximumSize(JComponent c) {
    Dimension d = getPreferredSize(c);
    
    return d;
    }
    
    public Dimension getPreferredSize(JComponent c) {
    Dimension d = super.getPreferredSize(c);
    
    if (border != null) {
    Insets ins = border.getBorderInsets(c);
    System.out.println(ins);
    d.setSize(d.width + ins.left + ins.right,
    d.height + ins.top + ins.bottom);          
    }
    
    return d;
    }*/
    /*
    private String layout(AbstractButton b, FontMetrics fm,
    int width, int height) {
    Insets i = b.getInsets();
    viewRect.x = i.left;
    viewRect.y = i.top;
    viewRect.width = width - (i.right + viewRect.x);
    viewRect.height = height - (i.bottom + viewRect.y);
    
    textRect.x = textRect.y = textRect.width = textRect.height = 0;
    iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
    
    // layout the text and icon
    return SwingUtilities.layoutCompoundLabel(
    b, fm, b.getText(), b.getIcon(),
    b.getVerticalAlignment(), b.getHorizontalAlignment(),
    b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
    viewRect, iconRect, textRect,
    b.getText() == null ? 0 : b.getIconTextGap());
    }*/
}
