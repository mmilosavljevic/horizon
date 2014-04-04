/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

/**
 *
 * @author Mladen M
 */
class JLabelTextField extends JComponent {

    private JLabel iconLabel;
    private JLabel preEditLabel;
    private JTextField nameField;
    private ImageIcon icon;
    private String value;
    
    //promenljiva koja predstavlja instancu našeg editora
    //da bi mogli da pokrenemo cancelCellEditing() metodu kada
    //korisnik pritisne taster ESC na tastaturi da osveži ceo red
    //koji se trenutno edituje
    private TableRowEditor instance;

    JLabelTextField() {
        setLayout(new GridBagLayout());

        iconLabel = new JLabel();
        preEditLabel = new JLabel();
        preEditLabel.setFont(new Font("Arial", Font.BOLD, 11));
        nameField = new JTextField();

        PulsatingBorder border = new PulsatingBorder(nameField);

        nameField.setBorder(new CompoundBorder(new LineBorder(new Color(0xffda6c)), border));
        nameField.setSelectionColor(new Color(0xffda6c));
        nameField.setFont(new Font("Arial", Font.BOLD, 11));

        nameField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    instance.cancelCellEditing();
                }
            }
        });


        PropertySetter setter = new PropertySetter(
                border, "thickness", 0.0f, 1.0f);
        Animator animator = new Animator(1000, Animator.INFINITE,
                Animator.RepeatBehavior.REVERSE, setter);
        animator.start();

        add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 1, 0, 0), 3, 0));
        add(preEditLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 1, 0, 0), 0, 0));
        add(nameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    }

    public Component setValueAndIcon(ImageIcon icon, String value, TableRowEditor editor) {
        this.icon = icon;
        this.value = value;
        instance = editor;
        iconLabel.setIcon(icon);
        preEditLabel.setText(value);
        nameField.setText(value);
        return this;
    }

    public Object getCellValue() {
        return new IconData(icon, nameField.getText(), false);
    }

    public String getValue() {
        return value;
    }

    public JTextField getTextField() {
        return nameField;
    }

    public ImageIcon getIcon() {
        return icon;
    }
}