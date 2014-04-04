/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Rich;

import Rich.borders.RichButtonBorder;
import Rich.borders.RichProgressBarBorder;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import javax.swing.UIDefaults;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalButtonUI;

import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.border.LineBorder;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.MetalBorders;

import javax.swing.plaf.basic.BasicDesktopPaneUI;
import javax.swing.plaf.metal.MetalDesktopIconUI;

/**
 *
 * @author Mladen
 */
public class RichLF extends MetalLookAndFeel {

    @Override
    public String getID() { return "Rich"; }
    @Override
    public String getName() { return "Rich Look and Feel"; }
    @Override
    public String getDescription() { return "Custom Look and Feel for application"; }

    @Override
    public boolean isNativeLookAndFeel() { return false; }
    @Override
    public boolean isSupportedLookAndFeel() { return true; }

    @Override
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);

        putDefault(table, "ButtonUI");
        putDefault(table, "ScrollBarUI");
        putDefault(table, "ProgressBarUI");
        //putDefault(table, "DesktopPaneUI");
        
    }
    
    protected void putDefault(UIDefaults table, String uiKey) {
        try {
            String className = "Rich.Rich" + uiKey;
            Class buttonClass = Class.forName(className);
            table.put(uiKey, className);
            table.put(className, buttonClass);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);

        /*BorderUIResource border = new
        BorderUIResource(new LineBorder(new Color(0xc2c0d0), 1));*/

        ColorUIResource borderNormal =
                new ColorUIResource(207, 204, 221);
        ColorUIResource borderOver =
                new ColorUIResource(254, 225, 138);
        
        BorderUIResource ProgressBarOvalBorder =
                new BorderUIResource(new RichProgressBarBorder(2, 5, new Color(254, 240, 124)));
        DimensionUIResource progressBarDim = new DimensionUIResource(150, 14);
      
        
        BorderUIResource buttonOvalBorder = 
                new BorderUIResource(new RichButtonBorder(1, 5, new Color(200, 200, 200)));
        BorderUIResource buttonOvalBorderOver = 
                new BorderUIResource(new RichButtonBorder(1, 5, new Color(255, 218, 108)));
        InsetsUIResource buttonMargin = new InsetsUIResource(5, 16, 5, 16);
        BorderUIResource buttonOvalFocuslBorder = 
                new BorderUIResource(new RichButtonBorder(1, 5, new Color(254, 240, 124)));
        
        ColorUIResource desktopPaneColor =
                new ColorUIResource(Color.WHITE);
        

        Object[] defaults = {
            "ScrollBar.width", new Integer(17),
            "ScrollBar.BorderNormal", borderNormal,
            "ScrollBar.BorderOver", borderOver,
            
            "ProgressBar.horizontalSize", progressBarDim,
            "ProgressBar.Border", ProgressBarOvalBorder,
            
            "Button.Border", buttonOvalBorder,
            "Button.BorderOver", buttonOvalBorderOver,
            "Button.margin", buttonMargin,
            "Button.FocusBorder", buttonOvalFocuslBorder,
                
            "Desktop.background", desktopPaneColor
        };
        table.putDefaults(defaults);
    }
}

