/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serializable;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Mladen M
 */
class TableRowEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener, Serializable {

    protected JLabelTextField editorComponent;
    protected JTable table;
    protected TableData t_data;
    protected int row;
    private File file;
    private boolean editConfirm = false;

    TableRowEditor(final JLabelTextField labelField) {
        editorComponent = labelField;
        labelField.getTextField().addActionListener(this);
    }

    /**
     * Vraća referencu na komponentu za editor.
     *
     * @return the editor <code>Component</code>
     */
    public Component getComponent() {

        return editorComponent;
    }

    public Object getCellEditorValue() {

        file = FileUtils.renameFile(file, editorComponent.getTextField().getText());
        return file;

    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return ((MouseEvent) anEvent).getID() == 100;
        }
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    // preimenuj fajl samo kada je enter taster pritisnut
    @Override
    public boolean stopCellEditing() {
        if (editConfirm == false || editorComponent.getTextField().getText().
                equals(editorComponent.getValue())) {
            cancelCellEditing();
            return true;
        }
        fireEditingStopped();
        editConfirm = false;
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        editConfirm = true;
        TableRowEditor.this.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
        //potrebno je da sledeće pozovome jer kada
        //se pokrene editor i pritisnemo mišem neka druga ćelija
        //da otkažemo editovanje ekstenzija se neće videti
        t_data.markRow(row, false);
        fireEditingCanceled();
        refreshRows();
    }

    // kada završimo sa editovanjem reda
    // iscrtamo tabelu da bi vratili
    // ekstenziju nazad u red
    public void refreshRows() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                table.tableChanged(new TableModelEvent(t_data, row));
                table.requestFocusInWindow();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected,
            int row, int column) {

        this.table = table;
        this.row = row;
        if (value instanceof IconData) {

            if (table.getModel() instanceof TableData) {
                t_data = (TableData) table.getModel();
                file = t_data.getFileForRow(row);
                t_data.markRow(row, true);
                refreshRows();
            }

            IconData ivalue = (IconData) value;

            return editorComponent.setValueAndIcon(ivalue.getFileIcon(), file.getName(), this);
        } else {
            return null;
        }
    }
}