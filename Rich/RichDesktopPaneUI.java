/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rich;

import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

/**
 *
 * @author Mladen
 */
public class RichDesktopPaneUI extends BasicDesktopPaneUI {

    public static ComponentUI createUI(JComponent c) {
        System.out.println("desktop");
        return new RichDesktopPaneUI();
    }
    
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        System.out.println("desktop");
        c.setOpaque(true);
       c.setBackground(Color.WHITE);
        //desktop.setBackground(UIManager.getColor("Desktop.background"));
    }
}
