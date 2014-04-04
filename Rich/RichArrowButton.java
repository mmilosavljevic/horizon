/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Rich;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicArrowButton;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.Animator.Direction;
import org.jdesktop.animation.timing.Animator.RepeatBehavior;
import org.jdesktop.animation.timing.TimingTarget;
/**
 *
 * @author Mladen
 */
public class RichArrowButton extends BasicArrowButton implements TimingTarget {

    private boolean isFreeStanding = false;
    private int buttonWidth;

    private Color r_borderNormal = null;
    private Color r_borderOver = null;

    private boolean mouseActive = false;

    private float alpha = 0.0f;
    private Animator in;

    public RichArrowButton(int direction, int width, boolean freeStanding) {

        super(direction);       
        r_borderNormal = UIManager.getColor("ScrollBar.BorderNormal");
        r_borderOver = UIManager.getColor("ScrollBar.BorderOver");

        buttonWidth = width;
        isFreeStanding = freeStanding;

        in = new Animator(400, 1.0f, RepeatBehavior.REVERSE, this);
        addMouseListener(new RichArrowButtonListener());
    }

    public void setFreeStanding(boolean freeStanding) {
        isFreeStanding = freeStanding;
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g.create();
        
        LinearGradientPaint gradient = null;

        int width = getWidth();
        int height = getHeight();

        int arrowHeight = height / 2;
        int arrowWidth = height / 2;


        if (getDirection() == NORTH) {
            if (!isFreeStanding) {
                height += 1;
                g.translate(0, -1);
                width += 2;
            }

            gradient = new LinearGradientPaint(3.0f, 0.0f, width - 2, 0.0f,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{new Color(0xffffff),
                        new Color(0xcfccdd),
                        new Color(0xffffff)},
                    MultipleGradientPaint.CycleMethod.REFLECT);

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width-1, height-1);

            //draw border
            g2d.setColor(r_borderNormal);
            g2d.drawRect(0, 0, width-1, height-1);

            if (mouseActive == true)
                paintVertical(g2d);

            // Draw the arrow
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.BLACK);

            int[] xPoints = {width / 2+1, (width / 2 - width / 4)+1, width / 2+1, (width / 2 + width / 4)+1, width / 2+1};
            int[] yPoints = {(height / 2 - height / 4), (height / 2 - height / 4) + arrowHeight-1,
                (height / 2 + arrowHeight / 2) - 3, (height / 2 - height / 4) + arrowHeight-1, (height / 2 - height / 4)};

            g2d.fillPolygon(xPoints, yPoints, 5);

            g2d.dispose();

            if (!isFreeStanding) {
                height -= 1;
                g.translate(0, 1);
                width -= 2;

            }
        } else if (getDirection() == SOUTH) {
            if (!isFreeStanding) {
                height += 1;
                g.translate(0, -1);
                width += 2;
            }

            gradient = new LinearGradientPaint(3.0f, 0.0f, width - 2, 0.0f,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{new Color(0xffffff),
                        new Color(0xcfccdd),
                        new Color(0xffffff)},
                    MultipleGradientPaint.CycleMethod.REFLECT);

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width-1, height-1);

            //draw border
            g2d.setColor(r_borderNormal);
            g2d.drawRect(0, 0, width - 1, height-1);

            if (mouseActive == true)
                paintVertical(g2d);
            

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.BLACK);

            int[] xPoints = {width / 2+1, (width / 2 - width / 4)+1, width / 2+1, (width / 2 + width / 4)+1, width / 2+1};
            int[] yPoints = {(height / 2 + height / 4)+1, (height / 2 + height / 4) - arrowHeight+2,
                (height / 2 - arrowHeight / 2) + 4, (height / 2 + height / 4) - arrowHeight+2, (height / 2 + height / 4)+1};


            g2d.fillPolygon(xPoints, yPoints, 5);
            g2d.dispose();

            if (!isFreeStanding) {
                height -= 1;
                g.translate(0, 1);
                width -= 2;

            }
        } else if (getDirection() == EAST) {
            if (!isFreeStanding) {
                height += 2;
                width += 1;
            }
           
            gradient = new LinearGradientPaint(0.0f, 3.0f, 0.0f, height-2,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{new Color(0xffffff),
                        new Color(0xcfccdd),
                        new Color(0xffffff)},
                    MultipleGradientPaint.CycleMethod.REFLECT);

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width-1, height-1);

            //draw border
            g2d.setColor(r_borderNormal);
            g2d.drawRect(0, 0, width - 1, height - 1);

             if (mouseActive == true)
                paintHorizontal(g2d);
           

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.BLACK);

            int[] xPoints = {(width / 2 + width / 4)+1, (width / 2 + width / 4) - arrowHeight+1,
                (width / 2 - arrowHeight / 2) + 4, (width / 2 + width / 4) - arrowHeight+1, (width / 2 + width / 4)+1};
            int[] yPoints = {height / 2+1, (height / 2 - height / 4)+1, height / 2+1, (height / 2 + height / 4)+1, height / 2+1};


            g2d.fillPolygon(xPoints, yPoints, 5);
            g2d.dispose();

             if (!isFreeStanding) {
                height -= 2;
                width -= 1;
            }

        } else if (getDirection() == WEST) {
            if (!isFreeStanding) {
                height += 2;
                width += 1;
                g.translate(-1, 0);
            }

            gradient = new LinearGradientPaint(0.0f, 3.0f, 0.0f, height - 2,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{new Color(0xffffff),
                        new Color(0xcfccdd),
                        new Color(0xffffff)},
                    MultipleGradientPaint.CycleMethod.REFLECT);

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width-1, height-1);

            //draw border
            g2d.setColor(r_borderNormal);
            g2d.drawRect(0, 0, width - 1, height - 1);

             if (mouseActive == true)
                paintHorizontal(g2d);
            

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.BLACK);

            int[] xPoints = {(width / 2 - width / 4), (width / 2 - width / 4) + arrowHeight,
                (width / 2 + arrowHeight / 2) - 3, (width / 2 - width / 4) + arrowHeight, (width / 2 - width / 4)};
            int[] yPoints = {height / 2+1, (height / 2 - height / 4)+1, height / 2+1, (height / 2 + height / 4)+1, height / 2+1};


            g2d.fillPolygon(xPoints, yPoints, 5);

             if (!isFreeStanding) {
                height -= 2;
                width -= 1;
                g.translate(1, 0);
            }

        }
    }


    private void paintVertical(Graphics2D g2d) {
        Graphics2D g2g = (Graphics2D) g2d.create();
        LinearGradientPaint gradient;

        gradient = new LinearGradientPaint(3.0f, 0.0f, getWidth() - 2, 0.0f,
                        new float[]{0.0f, 0.6f, 1.0f},
                        new Color[]{new Color(0xffffff),
                            new Color(0xffca2e),
                            new Color(0xffffff)},
                        MultipleGradientPaint.CycleMethod.REFLECT);

            g2g.setComposite(AlphaComposite.SrcOver.derive(alpha));

            g2g.setPaint(gradient);
            g2g.fillRect(0, 0, getWidth()-1, getHeight()-1);

            g2g.setColor(r_borderOver);
            g2g.drawRect(0, 0, getWidth()-1, getHeight()-1);
            g2g.dispose();
    }

    private void paintHorizontal(Graphics2D g2d) {
        Graphics2D g2g = (Graphics2D) g2d.create();
        LinearGradientPaint gradient;

        gradient = new LinearGradientPaint(0.0f, 3.0f, 0.0f, getHeight()-2,
                        new float[]{0.0f, 0.6f, 1.0f},
                        new Color[]{new Color(0xffffff),
                            new Color(0xffca2e),
                            new Color(0xffffff)},
                        MultipleGradientPaint.CycleMethod.REFLECT);

            g2g.setComposite(AlphaComposite.SrcOver.derive(alpha));
  
            g2g.setPaint(gradient);
            g2g.fillRect(0, 0, getWidth()-1, getHeight()-1);

            g2g.setColor(r_borderOver);
            g2g.drawRect(0, 0, getWidth()-1, getHeight()-1);
            g2g.dispose();
    }



    @Override
    public Dimension getPreferredSize() {
        if (getDirection() == NORTH) {
            return new Dimension(buttonWidth, buttonWidth + 2);
        } else if (getDirection() == SOUTH) {
            return new Dimension(buttonWidth, buttonWidth + 2);
        } else if (getDirection() == EAST) {
            return new Dimension(buttonWidth + 2, buttonWidth);
        } else if (getDirection() == WEST) {
            return new Dimension(buttonWidth + 2, buttonWidth);
        } else {
            return new Dimension(0, 0);
        }
    }

    
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

   
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public int getButtonWidth() {
	    return buttonWidth;
	}

    public void timingEvent(float alpha) {
        this.alpha = alpha;
        repaint(0, 0, getWidth(), getHeight());
    }

    public void begin() {}

    public void end() {}

    public void repeat() {}

    protected class RichArrowButtonListener extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {

            mouseActive = true;
            if (in.isRunning()) {
                in.stop();
            }
            in.setStartFraction(0.0f);
            in.setStartDirection(Direction.FORWARD);
            in.start();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (in.isRunning()) {
                in.stop();
            }
            in.setStartFraction(1.0f);
            in.setStartDirection(Direction.BACKWARD);
            in.start();

        }
    }

    
    
}
