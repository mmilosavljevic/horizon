/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rich;

import javax.swing.JDesktopPane;

/**
 *
 * @author Mladen
 */
public class RichDesktopPane extends JDesktopPane {

    public RichDesktopPane() {
        super();
    }

    @Override
    public void updateUI() {
        setUI(new RichDesktopPaneUI());
    }
}
