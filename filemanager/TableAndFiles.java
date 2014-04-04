/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import filemanager.TableAndFiles.RowListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Mladen
 */
class TableAndFiles {

    private JTable filesTable;
    private TableData t_data;
    private JScrollPane ps;

    TableAndFiles() {

        UIManager.put("Table.focusCellHighlightBorder",
                new LineBorder(Color.ORANGE, 1));


        t_data = new TableData();

        //ovde redefinišemo metodu klase JTabel jer kada prozor promeni veličinu
        //podrazumevano se iscrta samo ćelija u kojoj je editor bio aktivan
        //a mi hoćemo da se ceo red iscrta da bi vratili ekstenziju
        //koja je sklonjena kada se editor aktivirao, jer se metoda
        //cancelCellEditing() ne poziva kada promenimo veličinu prozora       
        filesTable = new JTable() {
            @Override
            public void columnMarginChanged(ChangeEvent e) {
                if (isEditing()) {
                    getCellEditor().cancelCellEditing();
                }
                super.columnMarginChanged(e);
            }
            // <editor-fold defaultstate="collapsed" desc="proba-Za selekciju imena sa crvenom bojom">
/* public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
             Component c = super.prepareRenderer(renderer, row, column);
             Object value = getValueAt(row, column);//Component c = super.prepareRenderer(renderer, row, column);
            
             BitSet selectedRows = listener.getSelectedRows();
            
             boolean rowIsSelected = false;
             boolean hasFocus = false;
            
             boolean rowIsLead =
             (selectionModel.getLeadSelectionIndex() == row);
             boolean colIsLead =
             (columnModel.getSelectionModel().getLeadSelectionIndex() == column);
            
             hasFocus = (rowIsLead && colIsLead) && isFocusOwner();
            
             /* if (!this.isRowSelected(row)) {
             c.setForeground(Color.BLACK);
             c.setBackground(Color.WHITE);
             }
            
             if (selectedRows.get(row) && hasFocus) {
            
             c.setForeground(Color.RED);
             c.setBackground(new Color(0xffda6c));
             } else if(selectedRows.get(row)) {
             c.setForeground(Color.RED);
             } else if (this.isRowSelected(row)) {
             c.setForeground(Color.WHITE);
             c.setBackground(new Color(0xffda6c));
             }*/
            /*
             if (selectedRows != null) {
             rowIsSelected = selectedRows.get(row);
             } else {
             rowIsSelected = true;
             }
             //return c;
             return renderer.getTableCellRendererComponent(this, value, this.isRowSelected(row), hasFocus, row, column);
            
             }*/// </editor-fold>
        };


        filesTable.setShowGrid(false);
        filesTable.setIntercellSpacing(new Dimension(0, 0));
        filesTable.setRowHeight(18);
        filesTable.getTableHeader().setReorderingAllowed(false);

        filesTable.setAutoCreateColumnsFromModel(false);

        filesTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        filesTable.setModel(t_data);
        filesTable.putClientProperty("Table.autoStartsEdit", Boolean.FALSE);

        filesTable.setSelectionBackground(new Color(0xffda6c));



        filesTable.addMouseListener(new RowListener());

        for (int k = 0; k < t_data.getColumnCount(); k++) {
            TableColumn column;
            TableRowRenderer renderer = new TableRowRenderer();
            TableCellEditor editor = new TableRowEditor(new JLabelTextField());
            renderer.setHorizontalAlignment(
                    TableData.t_columns[k].getColumnAlign());
            if (k == 0) {
                column = new TableColumn(k,
                        TableData.t_columns[k].getColumnWidth(), renderer, editor);
            } else {
                column = new TableColumn(k,
                        TableData.t_columns[k].getColumnWidth(), renderer, null);
            }
            column.setHeaderRenderer(createDefaultRenderer());
            filesTable.addColumn(column);

        }


        JTableHeader header = filesTable.getTableHeader();
        header.addMouseListener(new ColumnListener());


        filesTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Open");

        Action openFileAction = new AbstractAction("Open") {
            File selectedRow;

            public void actionPerformed(ActionEvent e) {
                selectedRow = t_data.getFileForRow(filesTable.getSelectedRow());

                if (selectedRow.isFile()) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop dt = Desktop.getDesktop();
                        try {
                            dt.open(selectedRow);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Greška prilikom otvaranja datoteke " + selectedRow.getName(),
                                    "File Commander", JOptionPane.WARNING_MESSAGE);
                        }
                    } else { // radi samo u windowsu
                        try {
                            FileCommander.p = FileCommander.r.exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " + selectedRow.getAbsolutePath());
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Greška prilikom otvaranja datoteke " + selectedRow.getName(),
                                    "File Commander", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    
                } else {
                    Thread runner = new Thread() {
                        @Override
                        public void run() {

                            t_data.setData(selectedRow);
                            {
                                Runnable runnable = new Runnable() {
                                    public void run() {
                                        filesTable.tableChanged(new TableModelEvent(t_data));
                                        t_data.makeSelection();

                                    }
                                };
                                SwingUtilities.invokeLater(runnable);
                            }
                        }
                    };
                    runner.start();
                }

            }
        };
        filesTable.getActionMap().put("Open", openFileAction);

        ps = new JScrollPane();
        ps.setBorder(new EmptyBorder(0, 0, 0, 0));
        ps.getViewport().setBackground(filesTable.getBackground());
        ps.getViewport().add(filesTable);

        t_data.setTable(filesTable);

    }

    protected JScrollPane getPS() {
        return ps;
    }

    protected JTable getTable() {
        return filesTable;
    }

    protected TableData getTableData() {
        return t_data;
    }

    protected TableCellRenderer createDefaultRenderer() {

        DefaultTableCellRenderer label = new DefaultTableCellRenderer() {
            private int col = 0;
            LinearGradientPaint gradientSel = null;
            LinearGradientPaint gradientUnSel = null;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                Rectangle clip = g.getClipBounds();

                gradientSel = new LinearGradientPaint(0.0f, 0.0f, 0.0f, getHeight(),
                        new float[]{0.0f, 0.5f, 1.0f},
                        new Color[]{new Color(0xffffff),
                            new Color(0xffda6c),
                            new Color(0xffffff)},
                        MultipleGradientPaint.CycleMethod.REFLECT);

                gradientUnSel = new LinearGradientPaint(0.0f, 0.0f, 0.0f, getHeight(),
                        new float[]{0.0f, 0.5f, 1.0f},
                        new Color[]{new Color(0xffffff),
                            new Color(0xcfccdd),
                            new Color(0xffffff)},
                        MultipleGradientPaint.CycleMethod.REFLECT);



                if (t_data.sortCol == col) {
                    g2d.setPaint(gradientSel);
                } else {
                    g2d.setPaint(gradientUnSel);
                }

                g2d.fillRect(clip.x, clip.y, clip.width, clip.height);
                g2d.dispose();
                super.paintComponent(g);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (table != null) {
                    JTableHeader header = table.getTableHeader();
                    if (header != null) {
                        setCol(column);
                        setFont(new Font("Arial", Font.BOLD, 12));
                    }
                }

                setText((value == null) ? "" : value.toString());


                if (t_data.sortCol == col) {
                    setBorder(new CompoundBorder(new LineBorder(new Color(0xffe7a1)), new EmptyBorder(0, 2, 0, 0)));
                } else {
                    setBorder(new CompoundBorder(new LineBorder(new Color(0xd4d2d8)), new EmptyBorder(0, 2, 0, 0)));
                }

                return this;
            }

            protected void setCol(int c) {
                col = c;
            }
        };

        return label;
    }

    class ColumnListener extends MouseAdapter {

        private TableColumnModel colModel;
        private TableColumn column;
        private int index;
        private JLabel renderer;

        ColumnListener() {
            colModel = filesTable.getColumnModel();
            column = colModel.getColumn(t_data.sortCol);
            index = column.getModelIndex();
            renderer = (JLabel) column.getHeaderRenderer();
            renderer.setIcon(t_data.getColumnIcon(index));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
            int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();

            if (modelIndex < 0 || (modelIndex == t_data.getColumnCount() - 1)) {
                return;
            }
            if (t_data.sortCol == modelIndex) {
                t_data.sortAsc = !t_data.sortAsc;
            } else {
                t_data.sortCol = modelIndex;
            }

            for (int i = 0; i < t_data.getColumnCount() - 1; i++) {
                column = colModel.getColumn(i);
                index = column.getModelIndex();
                renderer = (JLabel) column.getHeaderRenderer();
                renderer.setIcon(t_data.getColumnIcon(index));
            }
            filesTable.getTableHeader().repaint();


            Thread runner = new Thread() {
                @Override
                public void run() {

                    final File selectedFile = t_data.getFileForRow(filesTable.getSelectedRow());
                    t_data.sortData();
                    {
                        Runnable runnable = new Runnable() {
                            public void run() {
                                filesTable.tableChanged(new TableModelEvent(t_data));

                                if (selectedFile != null) {
                                    t_data.updateSelectOnSort(selectedFile);
                                }

                            }
                        };
                        SwingUtilities.invokeLater(runnable);
                    }
                }
            };
            runner.start();
        }
    }

    class RowListener extends MouseAdapter {

        private File tableRow;
        private int sameRow = -1;
        private File fRow = null;
        private MouseEvent me;
        public static final int START_EDIT = 100;
        private Timer time;

        RowListener() {

            time = new Timer(650, new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    filesTable.editCellAt(sameRow, 0,
                            new EventObject(new MouseEvent(me.getComponent(), START_EDIT, me.getWhen(), me.getModifiers(),
                            me.getX(), me.getY(), me.getClickCount(), false)));

                    if (filesTable.isEditing()) {
                        TableRowEditor edit = (TableRowEditor) filesTable.getCellEditor();
                        JLabelTextField field = (JLabelTextField) edit.getComponent();
                        field.getTextField().requestFocusInWindow();
                        field.getTextField().selectAll();
                    }
                }
            });
            time.setRepeats(false);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Point origin;
            int row = 0;
            int column;

            if (SwingUtilities.isLeftMouseButton(e)) {

                if (e.getClickCount() == 2) {

                    if (time.isRunning()) {
                        time.stop();
                    }


                    origin = e.getPoint();
                    row = filesTable.rowAtPoint(origin);
                    column = filesTable.columnAtPoint(origin);
                    if (row == -1 || column == -1) {
                        return; // nije pronađena ćelija
                    } else {

                        final int nRow = row;

                        tableRow = t_data.getFileForRow(row);

                        if (tableRow.isFile()) {

                            if (Desktop.isDesktopSupported()) {
                                Desktop dt = Desktop.getDesktop();
                                try {
                                    dt.open(tableRow);
                                } catch (IOException ex) {
                                    JOptionPane.showMessageDialog(null,
                                            "Greška prilikom otvaranja datoteke " + tableRow.getName(),
                                            "File Commander", JOptionPane.WARNING_MESSAGE);
                                }
                            } else { // radi samo u windowsu
                                try {
                                    FileCommander.p = FileCommander.r.exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " + tableRow.getAbsolutePath());
                                } catch (IOException ex) {
                                    JOptionPane.showMessageDialog(null,
                                            "Greška prilikom otvaranja datoteke " + tableRow.getName(),
                                            "File Commander", JOptionPane.WARNING_MESSAGE);
                                }
                            }
                            return;
                        }

                        if (row == 0 && t_data.hasParent()) {
                            tableRow = t_data.getParent();
                        } else {

                            try {
                                if (tableRow.listFiles().length > 0) {
                                    tableRow.listFiles()[0].getName(); // provera da li postoji zabrana za ocitavanje direktorijuma
                                    // primer: neophodna administratorska prava

                                }
                            } catch (Exception se) {
                                
                                JOptionPane.showMessageDialog(null, "Nije dozvoljeno ili ne mogu da pročitam datoteku");
                                return;
                            }
                        }

                        t_data.changeIconOnDoubleClick(nRow, t_data);

                        Thread runner = new Thread() {
                            @Override
                            public void run() {

                                t_data.setData(tableRow);

                                {
                                    Runnable runnable = new Runnable() {
                                        public void run() {
                                            if (!NameAndSeparatorComp.isInThePath(tableRow, FileCommander.activePanel)) {
                                              
                                                new NameAndSeparatorComp(tableRow, FileCommander.activePanel);

                                            }
                                            
                                            filesTable.tableChanged(new TableModelEvent(t_data));
                                            t_data.makeSelection();
                                            
                                            if (FileCommander.activePanel == FileCommander.pathPanel) {
                                                FileCommander.ds1.setDisc(tableRow);
                                            } else {
                                                FileCommander.ds2.setDisc(tableRow);
                                            }
                                            
                                        }
                                    };
                                    SwingUtilities.invokeLater(runnable);
                                }
                            }
                        };
                        runner.start();

                    }
                } else if (e.getClickCount() == 1) {

                    NameAndSeparatorComp.cancelPressedBtn();
                    me = e;
                    origin = e.getPoint();
                    row = filesTable.rowAtPoint(origin);
                    column = filesTable.columnAtPoint(origin);
                    if (row == -1 || column == -1) {
                        return; // no cell found
                    }

                    //red sa dve horizontalne tačke (roditelj-direktorijum)
                    //nije dozvoljeno editovati
                    if (!t_data.allowToOperateWith(t_data.getFileForRow(row))) {
                        sameRow = -1;
                        return;
                    }


                    if (sameRow != row) {
                        sameRow = row;
                        fRow = t_data.getFileForRow(row);
                        return;
                    }

                    if (fRow.compareTo(t_data.getFileForRow(row)) != 0) {
                        fRow = t_data.getFileForRow(row);
                        return;
                    }

                    //počni da edituješ
                    time.start();
                    
                }
            }
        }
    }
}

class TableRowRenderer extends DefaultTableCellRenderer {

    private JLabel label;
    
    public TableRowRenderer() {
        
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int nRow, int nCol) {

        label = (JLabel) super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, nRow, nCol);

        label.setFont(new Font("Arial", Font.BOLD, 11));

        if (value instanceof IconData) {
            IconData ivalue = (IconData) value;

            label.setIcon(ivalue.getFileIconOnDoubleClick());
            label.setText(ivalue.toString());
        } else {
            label.setText((value == null) ? "" : value.toString());
        }

        if (nCol == 2 || nCol == 3) {
            label.setFont(new Font("Arial", Font.BOLD, 12));
        }

        if (nRow % 2 == 0 && !isSelected) {
            label.setBackground(Color.WHITE);
            label.setForeground(Color.BLACK);
        } else if (!isSelected) {
            label.setBackground(new Color(0xf5f5f8));
            setForeground(Color.BLACK);
        }

        if (isSelected) {
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.BOLD, 13));
        }

        return label;
    }
}

class ColumnProp {

    private String c_title;
    private int c_width;
    private int c_aligment;

    ColumnProp(String title, int width, int alingment) {
        c_title = title;
        c_width = width;
        c_aligment = alingment;
    }

    protected String getColumnTitle() {
        return c_title;
    }

    protected int getColumnWidth() {
        return c_width;
    }

    protected int getColumnAlign() {
        return c_aligment;
    }
}

class IconData {

    private ImageIcon fileIcon;
    private boolean changeIcon;
    private String fileName;
    private Icons icons = new Icons();

    public IconData(ImageIcon icon, String data, boolean change) {
        fileIcon = icon;
        fileName = data;
        changeIcon = change;
    }

    public ImageIcon getFileIcon() {
        return fileIcon;
    }

    public boolean getState() {
        return changeIcon;
    }

    public ImageIcon getFileIconOnDoubleClick() {
        if (changeIcon) {
            changeIcon = false;
            return icons.hourglassIcon;
        }
        return fileIcon;
    }

    @Override
    public String toString() {
        return fileName;
    }
}

final class TableData extends AbstractTableModel {

    static final public ColumnProp[] t_columns = {
        new ColumnProp(" Ime", 250, JLabel.LEADING),
        new ColumnProp(" Ekstenzija", 100, JLabel.LEADING),
        new ColumnProp(" Veličina", 100, JLabel.LEADING),
        new ColumnProp(" Datum", 100, JLabel.LEADING),
        new ColumnProp(" Dozvola", 50, JLabel.LEADING),};
    private Icons icons;
    //tabela koja koristi ovaj model
    private JTable modelsTable;
    // lista sortiranih direktorijuma i datoteka
    private ArrayList<File> list;
    // direktorijum roditelj
    private File parentDir;
    /*odvajamo direktorijume i datoteke zbog bržeg sortiranja
     i naknadno nakon sortiranja ubacujemo u listu sortirane direktorijume
     i datoteke*/
    private File[] dirs;
    private File[] files;
    private File[] roots;
    /*koristimo kolekciju TreeMap zbog prirodnog sortiranja svojih ključeva,
     posebno za direktorijume i posebno za datoteke. U ovoj mapi ključevi su direktorijumi i datoteke
     a vrednosti su svojstva ovih ključeva(primer: ekstenzija, datum promene, veličina,...) zavisno od toga
     u kojoj smo trenutno koloni-koju kolonu sortiramo*/
    private TreeMap<File, Object> filesMap;
    private TreeMap<File, Object> dirsMap;
    /*u ove kolekcije posebno smeštamo sortirane direktorijume i datokeke prema
     prema odgovarajućim komparatorima. Zatim se ovako soritani direktorijumi
     i datoteke spajaju u listu i po potrebi na početak liste ubacuje direktorijum roditelj*/
    private ArrayList<Map.Entry<File, Object>> listFiles;
    private ArrayList<Map.Entry<File, Object>> listDirs;
    // da li trenutno otvoreni direktorijum ima roditelja da bi mogli
    //da se popnemo jedan direktorijum gore
    private boolean hasParent;
    //poslednji otvoreni direktorijum
    private File lastOpenDir;
    //koja kolona se trenutno sortira
    protected int sortCol = 0;
    //da li sortiramo od manje ka većoj vrednosti
    //ili obrnuto
    protected boolean sortAsc = true;
    //red čiju vrednost trenutno menjamo (primer: ime datoteke)
    private int editingRow;
    // da li se menja vrednost reda (primer: ime datoteke)
    private boolean isEditing = false;
    /*ako ponovo sortiramo direktorijum u kome se već nalazimo
     pritiskom na zaglavlje kolone izbegavamo ponovo sortiranje direktorijuma
     i sortiramo samo datoteke jer su direktorijumi već sortirani prilikom otvaranje
     direktorijuma u kome se trenutno nalazimo*/
    private boolean sortingSameDir;
    //kolona koju hoćemo da sortiramo
    private int columnToSort;
    /*ove dve promenljive se koriste da obeležimo zadnji otvoren direktorijum
     nakon što smo pregledali njegov sadržaj i pritisnemo dugme za povratak nazad
     tj. jedan direktorijum gore (primer: otvorimo direktorijum MLADEN i pregledamo njegov sadržaj
     kada pritisnemo dugme za povratak nazad direktorijum MLADEN će biti izabran)*/
    private File upDir;
    private boolean backSelect = false;
    /*kada otvaramo direktorijum menjamo ikonicu (peščani sat) da bi stavili korisniku
     do znanja da treba da sačeka dok se učita sadržaj direktorijuma*/
    private int rowToChangeIcon = -1;
    private boolean changeIcon = false;

    TableData() {
        icons = new Icons();
    }

    protected void setData(File dir) {
        sortingSameDir = false;

        if (dir != null) {
            roots = null;
            dirs = null;
            files = null;

            if (parentDir != null && dir.equals(parentDir)) {
                upDir = lastOpenDir;
                backSelect = true;
            }

            lastOpenDir = dir;

            dirs = FileUtils.filterDirs(dir);
            files = FileUtils.filterFiles(dir);

            if (dir.getParent() != null) {
                parentDir = dir.getParentFile();
                hasParent = true;
            } else {
                parentDir = null;
                hasParent = false;
            }
            sortData();
        } else {
            parentDir = null;
            list = new ArrayList<File>();
            list.addAll(Arrays.asList(File.listRoots()));

        }
        changeIcon = false;
    }

    //promenimo ikonicu direktorijuma koji trenutno otvaramo
    //da stavimo korisniku do znanja da treba da saceka jer se
    //datoteke (pretezno .exe tipa) u direktorijumu sporo ucitavaju(krivci su ikonice datoteka)
    protected void changeIconOnDoubleClick(int row, TableData t_data) {
        rowToChangeIcon = row;
        changeIcon = true;
        modelsTable.tableChanged(new TableModelEvent(t_data, rowToChangeIcon, rowToChangeIcon, 0, TableModelEvent.UPDATE));
        modelsTable.setRowSelectionInterval(rowToChangeIcon, rowToChangeIcon);
    }

    private void addParent() {
        if (parentDir != null) {
            list.add(0, parentDir);
        }
    }

    public int getRowCount() {
        return list.size();
    }

    public int getColumnCount() {
        return t_columns.length;
    }

    protected boolean hasParent() {
        return parentDir != null;
    }

    @Override
    public String getColumnName(int column) {
        return t_columns[column].getColumnTitle();
    }

    @Override
    public boolean isCellEditable(int nRow, int nCol) {
        if (nCol == 0) {
            return true;
        }
        return false;
    }

    public Object getValueAt(int nRow, int nCol) {

        if (nRow < 0 || nRow >= getRowCount()) {
            return "";
        }

        if (nRow == 0 && parentDir != null) {
            hasParent = true;
        } else {
            hasParent = false;
        }

        switch (nCol) {
            case 0:
                try {
                    if (changeIcon && nRow < list.size()) {
                        rowToChangeIcon = -1;
                        return hasParent ? new IconData(icons.dir_up, "..", false)
                                : new IconData(icons.getIcon(list.get(nRow)), FileUtils.getFileName(list.get(nRow)), true);
                    }
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }

                return hasParent ? new IconData(icons.dir_up, "..", false)
                        : new IconData(icons.getIcon(list.get(nRow)), FileUtils.getFileName(list.get(nRow)), false);
            case 1:
                return hasParent ? FileUtils.getFileExt(lastOpenDir) : getExt(nRow);

            case 2:
                return hasParent ? FileUtils.getFileSize(lastOpenDir, 0) : FileUtils.getFileSize(list.get(nRow), 3);

            case 3:
                return hasParent ? FileUtils.getFileDate(lastOpenDir) : FileUtils.getFileDate(list.get(nRow));

            case 4:

                return hasParent ? FileUtils.getFilePermission(lastOpenDir) : FileUtils.getFilePermission(list.get(nRow));

        }
        return "";

    }

    protected void setTable(JTable table) {
        modelsTable = table;
    }

    public File getFileForRow(int row) {
        if (row < 0) {
            return null;
        }
        return list.get(row);
    }

    public File getCurrentDir() {
        return lastOpenDir;
    }

    public int getFileIndex(File f) {
        if (f == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (f.compareTo(list.get(i)) == 0) {
                return i;
            }
        }
        return -1;
    }

    protected File[] getSelectedFiles() {
        int[] selectedRows = modelsTable.getSelectedRows();

        if (selectedRows.length == 0) {
            return null;
        }

        File[] sFiles;
        int j = 0;


        if (!allowToOperateWith(list.get(selectedRows[0]))) {
            j = 1;
            if (selectedRows.length <= 1) {
                return null;
            }
        }

        sFiles = new File[selectedRows.length - j];

        for (int i = 0; i < selectedRows.length; i++) {
            sFiles[i] = list.get(selectedRows[i + j]);
        }

        return sFiles;
    }

    protected void removeFromList(File file) {
        if (list.contains(file)) {
            list.remove(file);

        }
    }

    protected void addToList(File file) {
        list.add(file);
        sortData();
    }

    //osveži trenutno otvoreni direktorijum
    protected void refreshTable() {

        if (lastOpenDir.exists()) {
            setData(lastOpenDir);
        } else {
            if (lastOpenDir == null) {
                setData(new File(System.getProperty("user.home")));
                return;
            }
            setData(getFirstLiveParent(lastOpenDir.getParentFile()));

        }
    }

    //refresh path panels after main application windows gets focus
    protected void refreshPaths(JPanel panel) {
        
        if (lastOpenDir.exists()) {
            NameAndSeparatorComp.updatePath(lastOpenDir, panel);
            
        } else {
            if (lastOpenDir == null) {
                NameAndSeparatorComp.updatePath(new File(System.getProperty("user.home")), panel);
                return;
            }
            NameAndSeparatorComp.updatePath(getFirstLiveParent(lastOpenDir.getParentFile()), panel);
        }
    }

    //ako je direktorijum obrisan negde u OS-u dok je otvoren u našem programu
    //pronađi prvi postojeći roditelj-direktorijum izbrisanog direktorijuma kada
    //naš program dobije fokus i osveži tabele
    protected File getFirstLiveParent(File file) {
        if (file.exists()) {
            return file;
        } else {
            return getFirstLiveParent(file.getParentFile());
        }
    }

    protected void setParent() {
        if (parentDir != null) {
            setData(parentDir);
        } else {
            setData(lastOpenDir);
        }
    }

    protected File getParent() {
        return parentDir != null ? parentDir : null;
    }

    //nisu dozvoljene sledeće operacije: edit, copy, move, delete,... 
    //nad redom sa dve horizontalne tačke (roditelj-direktorijum)
    protected boolean allowToOperateWith(File toEdit) {
        if (parentDir != null) {
            if (toEdit.equals(parentDir)) {

                return false;
            }
        }
        return true;
    }

    //status za red koji se trenutno edituje
    //i da li se počelo sa editovanjem
    protected void markRow(int row, boolean editStarted) {
        editingRow = row;
        isEditing = editStarted;
    }

    //kada idemo jedan direktorijum gore izabracemo
    //direktorijum u kojem smo upravo bili
    protected void makeSelection() {
        if (backSelect) {
            int index = getFileIndex(upDir);
            if (index >= 0) {
                modelsTable.clearSelection();
                Rectangle rect = modelsTable.getCellRect(index, 0, true);
                modelsTable.scrollRectToVisible(rect);
                modelsTable.changeSelection(index, 0, false, false);
            }
            backSelect = false;
        } else {
            modelsTable.setRowSelectionInterval(0, 0);
        }
    }

    protected void updateSelectOnSort(File selectedFile) {

        int index = getFileIndex(selectedFile);
        if (index >= 0) {
            modelsTable.clearSelection();
            Rectangle rect = modelsTable.getCellRect(index, 0, true);
            modelsTable.scrollRectToVisible(rect);
            modelsTable.changeSelection(index, 0, false, false);
        } else {
            modelsTable.setRowSelectionInterval(0, 0);
        }
    }

    protected String getExt(int row) {
        if (row == editingRow && isEditing) {
            return " ";
        }
        return FileUtils.getFileExt(list.get(row));
    }

    // <editor-fold defaultstate="collapsed" desc="zip">
/*protected void setZipEntries(File zipFile, File[] zipFileEntries) {
     openDir = zipFile;
     files = zipFileEntries;
     parentDir = openDir.getParentFile();
     isArchiveFile = true;
     hasParent = true;
    
     sortData();
     }*/
    //uptate array with file which name
    //is edited in the table
    /*protected int updateArray(File f, int row) {
     dirs[row] = f;
     sortData();
    
     for (int i=0; i<dirs.length; i++) {
     if (dirs[i].equals(f))
     return i;
     }
     return -1;
     }*/

    /*protected void selectedRows(BitSet selectedRows) {
     this.selectedRows = selectedRows;
     }
    
     public boolean isRowSelected(int row) {
     if (selectedRows != null) {
    
     return selectedRows.get(row);
     }
     return false;
     }*/// </editor-fold>
    @Override
    public void setValueAt(Object value, int row, int col) {
        int place = row;
        File f = (File) value;

        refreshTable();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(f)) {
                place = i;
                break;
            }
        }


        Rectangle rect = modelsTable.getCellRect(place, 0, true);
        modelsTable.scrollRectToVisible(rect);
        modelsTable.changeSelection(place, 0, false, false);

        markRow(row, false);

        fireTableRowsUpdated(row, row);
    }

    public void sortData() {
        //long start = System.nanoTime() / 1000000;
        
        if (!sortingSameDir || columnToSort != sortCol) {

            filesMap = new TreeMap<File, Object>();
            dirsMap = new TreeMap<File, Object>();
            columnToSort = sortCol;
            switch (sortCol) {
                case 0:        //ime datoteke
                    for (int i = 0; i < dirs.length; i++) {
                        dirsMap.put(dirs[i], dirs[i].toString());
                    }
                    for (int i = 0; i < files.length; i++) {
                        filesMap.put(files[i], files[i].toString());
                    }
                    break;
                case 1:        //ekstenzija
                    for (int i = 0; i < dirs.length; i++) {
                        dirsMap.put(dirs[i], "");
                    }
                    for (int i = 0; i < files.length; i++) {
                        filesMap.put(files[i], FileUtils.getFileExt(files[i]));
                    }
                    break;
                case 2:        //veličina
                    for (int i = 0; i < dirs.length; i++) {
                        dirsMap.put(dirs[i], "Omotnica");
                    }
                    for (int i = 0; i < files.length; i++) {
                        filesMap.put(files[i], files[i].length());
                    }
                    break;
                case 3:        //datum
                    for (int i = 0; i < dirs.length; i++) {
                        dirsMap.put(dirs[i], "Omotnica");
                    }
                    for (int i = 0; i < files.length; i++) {
                        filesMap.put(files[i], files[i].lastModified());
                    }
                    break;
            }
            sortingSameDir = true;

            listDirs = new ArrayList<Map.Entry<File, Object>>(dirsMap.entrySet());
            listFiles = new ArrayList<Map.Entry<File, Object>>(filesMap.entrySet());

            Collections.sort(listDirs, new FileComparatorForDirs());
        }

        Collections.sort(listFiles, new FileComparatorForFiles(sortCol, sortAsc));
        list = new ArrayList<File>(listDirs.size() + listFiles.size());


        for (int i = 0; i < dirsMap.size(); i++) {
            list.add(listDirs.get(i).getKey());
        }
        for (int i = 0; i < filesMap.size(); i++) {
            list.add(listFiles.get(i).getKey());
        }

        addParent();

        /*long end = System.nanoTime() / 1000000;
         long total = end - start;
         System.out.println(total + " ms");*/
    }

    Icon getColumnIcon(int column) {
        if (column == sortCol) {
            return sortAsc ? icons.column_up : icons.column_down;
        }
        return null;
    }

    private static final class FileComparatorForFiles implements Comparator<Map.Entry<File, Object>> {

        protected int t_sortCol;
        protected boolean t_sortAsc;
        File f1;
        File f2;
        Object obj1;
        Object obj2;
        int result = 0;

        private FileComparatorForFiles(int sortCol, boolean sortAsc) {
            t_sortAsc = sortAsc;
            t_sortCol = sortCol;
        }

        public int compare(Map.Entry<File, Object> o1, Map.Entry<File, Object> o2) {
            f1 = o1.getKey();
            f2 = o2.getKey();
            obj1 = o1.getValue();
            obj2 = o2.getValue();

            switch (t_sortCol) {
                case 0:        //ime datoteke

                    result = f1.compareTo(f2);
                    break;
                case 1:        //ekstenzija                 
                    result = ((String) obj1).compareToIgnoreCase((String) obj2);
                    break;
                case 2:        //Veličina
                    result = (Long) obj1 < (Long) obj2 ? -1 : ((Long) obj1 > (Long) obj2 ? 1 : 0);

                    break;
                case 3:        //datum
                    result = (Long) obj1 < (Long) obj2 ? -1 : ((Long) obj1 > (Long) obj2 ? 1 : 0);
                    break;
            }
            //samo obrni redosled
            if (t_sortAsc == false) {
                result = -result;
            }

            return result;

        }
    }

    private static final class FileComparatorForDirs implements Comparator<Map.Entry<File, Object>> {

        private File f1;
        private File f2;
        private int result = 0;

        private FileComparatorForDirs() {
        }

        public int compare(Map.Entry<File, Object> o1, Map.Entry<File, Object> o2) {
            f1 = o1.getKey();
            f2 = o2.getKey();

            result = f1.compareTo(f2);
            return result;
        }
    }
}