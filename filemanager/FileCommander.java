/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;

/**
 *
 * @author Mladen
 */
public class FileCommander extends JFrame implements WindowListener,
        ActionListener, KeyListener {

    //pomoćne promenljive
    private TableAndFiles table1;
    private TableAndFiles table2;
    private TableAndFiles helperTable;
    
    private Icons icons;
    
    private int row = -1;
    private int row1 = -1;
    
    private File rootUser;
    private File[] roots;
    
    private File destFile;
    
    private boolean tableFocusOwner;
    private boolean table1FocusOwner;
    
    private boolean moveOrDelete = false;
    
    private File newDir;
    
    private boolean dirCreated = false;
    
    private boolean unzipOperation = false;
    
    //omogućava aplikaciji da interaguje sa okruženjem u kome se izvršava aplikacija
    //trenutno koristim samo otvaranje aplikacija u windowsu kao "Desktop" alternativa
    public static Runtime r;
    public static Process p;
    
    //ne želimo da osvežimo sadržaj tabele prilikom prvog starta programa
    private boolean notFirstStart = false;
    
    //izbegavamo osvežavanje tabela kad je prozor aktivan (događaj- windowActivated)
    //za vreme premeštanja datoteke zbog dosta poziva za osvežavanje tabele u reloadAndRepaintTables metodi
    private boolean isDialogActive = false;
    
    private File[] selectedRows;
    private int[] selection;
    
    //************************************************************************************
    protected static JPanel pathPanel;
    protected static JPanel pathPanel2;
    protected static JPanel activePanel;    
    //************************************************************************************
    
    private JPanel buttonsPanel;
    
    private JPanel iconsPanel;
    
    private JPanel rootsPanel;
    
    private JPanel leftRootsPanel;
    private JPanel rightRootsPanel;
    
    private JPanel tableOnePanel;
    private JPanel tableTwoPanel;
    
    private JPanel discSpace1Panel;
    private JPanel discSpace2Panel;
    
    private MainButton parent;
    private MainButton newFolder;
    private MainButton move;
    private MainButton copy;
    //private JButton rename;
    private MainButton delete;
    private MainButton archive;
    private MainButton extract;
    private MainButton refreshButton;
    
    private ButtonGroup btnGroup1;
    private ButtonGroup btnGroup2;
    
    private ImageIcon upIcon;
    private ImageIcon newDirIcon;
    private ImageIcon moveIcon;
    private ImageIcon copyIcon;
    //private ImageIcon editIcon;
    private ImageIcon deleteIcon;
    private ImageIcon packIcon;
    private ImageIcon unpackIcon;
    private ImageIcon refreshIcon;
    
    private static FileUtils dialog;  
    private JOptionPane dialogPane = null;
    private static Archives archiveOp;
    
    //ispod tabela grafički prikazuje zauzet i slobodan prostor na hard-disku
    protected static DiscSpace ds1;
    protected static DiscSpace ds2;
    
    private OvalToggleButton[] toggles1;
    private OvalToggleButton[] toggles2;
    
    private File lastChosenLeftRoot;
    private File lastChosenRightRoot;
    
    private static JFrame topParent;
       
    public FileCommander() {
        super("File Commander");
       
        topParent = this;
        r = Runtime.getRuntime();
        
        icons = new Icons();
                       
        addWindowListener(FileCommander.this);

        //podešavamo veličinu prozora aplikacije pri pokretanju
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(dim.width / 2 + dim.width / 4, dim.height / 2 + dim.height / 4);

        rootUser = new File(System.getProperty("user.home"));
        
        lastChosenLeftRoot = FileUtils.getRootFile(rootUser);
        
        lastChosenRightRoot = FileUtils.getRootFile(rootUser);

        ds1 = new DiscSpace(FileUtils.getRootFile(rootUser));
        
        ds2 = new DiscSpace(FileUtils.getRootFile(rootUser));
       
        table1 = new TableAndFiles();
        table1.getTableData().setData(rootUser);
        
        table2 = new TableAndFiles();    
        table2.getTableData().setData(rootUser);
    
        buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 45)));

        iconsPanel = new JPanel();
        iconsPanel.setOpaque(false);
        iconsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));

        dialog = new FileUtils(this);
        archiveOp = new Archives(this);

        //Ikone za dugmad za operacije nad datotekama
        upIcon = new ImageIcon(getClass().getResource("/filemanager/resources/Up.png"));
        newDirIcon = new ImageIcon(getClass().getResource("/filemanager/resources/NewFolder.png"));
        moveIcon = new ImageIcon(getClass().getResource("/filemanager/resources/Move.png"));
        copyIcon = new ImageIcon(getClass().getResource("/filemanager/resources/Copy.png"));
        //editIcon = new ImageIcon(getClass().getResource("/filemanager/resources/Edit.png"));
        deleteIcon = new ImageIcon(getClass().getResource("/filemanager/resources/Delete.png"));
        packIcon = new ImageIcon(getClass().getResource("/filemanager/resources/pack.png"));
        unpackIcon = new ImageIcon(getClass().getResource("/filemanager/resources/unpack.png"));
        refreshIcon = new ImageIcon(getClass().getResource("/filemanager/resources/refreshIcon.png"));

        parent = new MainButton("Povratak");
        parent.setToolTipText("Povratak na prethodni direktorijum");
        parent.setIcon(upIcon);
        parent.addActionListener(FileCommander.this);

        newFolder = new MainButton("Omotnica");
        newFolder.setToolTipText("Napravi novi direktorijum");
        newFolder.setIcon(newDirIcon);
        newFolder.addActionListener(FileCommander.this);

        move = new MainButton("Premesti");
        move.setToolTipText("Premesti izabranu datoteku ili direktorijum");
        move.setIcon(moveIcon);
        move.addActionListener(FileCommander.this);

        copy = new MainButton("Kopiraj");
        copy.setToolTipText("Kopiraj izabranu datoteku ili direktorijum");
        copy.setIcon(copyIcon);
        copy.addActionListener(FileCommander.this);

        /*rename = new JButton();
        rename.setBorderPainted(false);
        rename.setContentAreaFilled(false);
        rename.setPreferredSize(new Dimension(32, 32));
        rename.setIcon(editIcon);
        rename.addActionListener(FileManager.this);*/

        delete = new MainButton("Obriši");
        delete.setToolTipText("Obriši izabranu datoteku ili direktorijum");
        delete.setIcon(deleteIcon);
        delete.addActionListener(FileCommander.this);

        archive = new MainButton("Zapakuj");
        archive.setToolTipText("Zapakuj izabranu datoteku ili direktorijum");
        archive.setIcon(packIcon);
        archive.addActionListener(FileCommander.this);

        extract = new MainButton("Raspakuj");
        extract.setToolTipText("Raspakuj izabranu datoteku ili direktorijum");
        extract.setIcon(unpackIcon);
        extract.addActionListener(FileCommander.this);

        refreshButton = new MainButton("Osveži");
        refreshButton.setToolTipText("Ponovo učitaj korene diskove");
        refreshButton.setIcon(refreshIcon);

        refreshButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                Thread refreshThread = new Thread() {

                    public void run() {
                        setRootsPanel(leftRootsPanel, btnGroup1, "table1");
                        setRootsPanel(rightRootsPanel, btnGroup2, "table2");
                        rootsPanel.validate();

                        refreshState(toggles1, "table1");
                        refreshState(toggles2, "table2");

                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                rootsPanel.repaint();
                            }
                        });
                    }
                };
                refreshThread.start();
                reloadAndRepaintTables();
            }
        });

        
        iconsPanel.add(parent);
        iconsPanel.add(newFolder);
        iconsPanel.add(move);
        iconsPanel.add(copy);
        iconsPanel.add(delete);
        iconsPanel.add(archive);
        iconsPanel.add(extract);
        //iconsPanel.add(refreshButton);


        btnGroup1 = new ButtonGroup();
        btnGroup2 = new ButtonGroup();
        
        pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));        
        pathPanel.setBackground(Color.WHITE);       
        //pathPanel.setBorder(new LineBorder(new Color(200, 200, 200), 2));
        /*pathPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 2, 0, 2),
                new LineBorder(new Color(200, 200, 200), 2)));*/     
        pathPanel.setPreferredSize(new Dimension(getWidth()/2, 25));
        //pathPanel.add(new NameAndSeparatorComp( new File(System.getProperty("user.home"))));
        
        
        pathPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pathPanel2.setBackground(Color.WHITE);             
        //pathPanel.setBorder(new LineBorder(new Color(200, 200, 200), 2));
        /*pathPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 2, 0, 2),
                new LineBorder(new Color(200, 200, 200), 2)));*/        
        pathPanel2.setPreferredSize(new Dimension(getWidth()/2, 25));
        //pathPanel2.add(new NameAndSeparatorComp( new File(System.getProperty("user.home"))));
        
        activePanel = pathPanel;
        initPathPanels();
        
        JPanel rootsGridPanel = new JPanel();
        rootsGridPanel.setOpaque(false);
        rootsGridPanel.setLayout(new BoxLayout(rootsGridPanel, BoxLayout.X_AXIS));
 
        //<editor-fold defaultstate="collapsed" desc="comment">
        /* rootsPanel = new JPanel();
         * rootsPanel.setOpaque(false);
         * //rootsPanel.setLayout(new GridLayout(1, 2));
         * rootsPanel.setLayout(new BoxLayout(rootsPanel, BoxLayout.X_AXIS));
         * rootsPanel.add(Box.createRigidArea(new Dimension(2, 0)));
         * 
         * leftRootsPanel = new JPanel();
         * leftRootsPanel.setOpaque(false);
         * leftRootsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
         * //setRootsPanel(leftRootsPanel, btnGroup1, "table1");
         * 
         * rightRootsPanel = new JPanel();
         * //rightRootsPanel.setOpaque(false);
         * rightRootsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
         * setRootsPanel(rightRootsPanel, btnGroup2, "table2");
         * 
         * //rootsPanel.add(leftRootsPanel);
         * rootsPanel.add(pathPanel);
         * //rootsPanel.add(rightRootsPanel);*/
        //</editor-fold>
        
        rootsGridPanel.add(pathPanel);
        rootsGridPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        rootsGridPanel.add(pathPanel2);

        buttonsPanel.add(iconsPanel);
        buttonsPanel.add(Box.createVerticalStrut(20));
        //buttonsPanel.add(rootsGridPanel);
        buttonsPanel.add(getLayeredPane().add(rootsGridPanel, 210));
        buttonsPanel.add(Box.createVerticalStrut(5));

        JPanel tablesPanel = new JPanel();
        tablesPanel.setOpaque(false);
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.X_AXIS));

        setContentPane(new GradientPanel());

        add(buttonsPanel, BorderLayout.NORTH);

        tableOnePanel = new JPanel();
        tableOnePanel.setOpaque(false);
        tableOnePanel.setLayout(new BoxLayout(tableOnePanel, BoxLayout.Y_AXIS));

        discSpace1Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        discSpace1Panel.setOpaque(false);
        discSpace1Panel.add(ds1);
        discSpace1Panel.setMaximumSize(new Dimension(getWidth(), 22));

        tableTwoPanel = new JPanel();
        tableTwoPanel.setOpaque(false);
        tableTwoPanel.setLayout(new BoxLayout(tableTwoPanel, BoxLayout.Y_AXIS));

        discSpace2Panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        discSpace2Panel.setOpaque(false);
        discSpace2Panel.add(ds2);
        discSpace2Panel.setMaximumSize(new Dimension(getWidth(), 22));

        tableOnePanel.add(table1.getPS());
        tableOnePanel.add(Box.createVerticalStrut(5));
        tableOnePanel.add(discSpace1Panel);

        tableTwoPanel.add(table2.getPS());
        tableTwoPanel.add(Box.createVerticalStrut(5));
        tableTwoPanel.add(discSpace2Panel);


        tablesPanel.add(tableOnePanel);
        tablesPanel.add(Box.createHorizontalStrut(5));
        tablesPanel.add(tableTwoPanel);


        add(tablesPanel, BorderLayout.CENTER);
        add(Box.createVerticalStrut(100), BorderLayout.SOUTH);


        table1.getTable().addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent fe) {

                row = table1.getTable().getSelectedRow();
                row = row == -1 ? 0 : row;
                row1 = -1;

                destFile = table2.getTableData().getCurrentDir();
                helperTable = table1;
                tableFocusOwner = true;
                table1FocusOwner = false;

                if (FileCommander.this.isActive()) {
                    selectedRows = table1.getTableData().getSelectedFiles();
                }

            }

            public void focusGained(FocusEvent fe) {
                activePanel = pathPanel;
                table2.getTable().clearSelection();
            }
        });

        table2.getTable().addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent fe) {

                row1 = table2.getTable().getSelectedRow();
                row1 = row1 == -1 ? 0 : row1;
                row = -1;

                destFile = table1.getTableData().getCurrentDir();
                helperTable = table2;
                tableFocusOwner = false;
                table1FocusOwner = true;

                if (FileCommander.this.isActive()) {
                    selectedRows = table2.getTableData().getSelectedFiles();
                }

            }

            public void focusGained(FocusEvent fe) {
                activePanel = pathPanel2;
                table1.getTable().clearSelection();
            }
        });

        table1.getTable().requestFocusInWindow();
        table1.getTable().setRowSelectionInterval(0, 0);

        table1.getTable().addKeyListener(FileCommander.this);
        table2.getTable().addKeyListener(FileCommander.this);

        setLocationRelativeTo(null);

    }
    
    public static JFrame getTopJFrame() {
        return topParent;
    }
    
    private void initPathPanels() {
       //activePanel = pathPanel;
        NameAndSeparatorComp.setInteractModel(table1, table2);
        
            new NameAndSeparatorComp(null, pathPanel);
            new NameAndSeparatorComp(FileUtils.getRootFile(rootUser), pathPanel);
            new NameAndSeparatorComp(rootUser.getParentFile(), pathPanel);
            new NameAndSeparatorComp(rootUser, pathPanel);
            new NameAndSeparatorComp(null, pathPanel2);
            new NameAndSeparatorComp(FileUtils.getRootFile(rootUser), pathPanel2);
            new NameAndSeparatorComp(rootUser.getParentFile(), pathPanel2);
            new NameAndSeparatorComp(rootUser, pathPanel2);
        
    }

    //postavljamo dugmad za root diskove za levi i desni panel iznad tabele
    private JPanel setRootsPanel(JPanel rootsPanel, ButtonGroup btg, String table) {
        rootsPanel.removeAll();

        File rootFile = FileUtils.getRootFile(rootUser);

        roots = File.listRoots();

        OvalToggleButton[] toggleButtons = new OvalToggleButton[roots.length];

        for (int i = 0; i < roots.length; i++) {
            OvalToggleButton toggleButton = new OvalToggleButton(roots[i].toString(), icons.getIcon(roots[i]), false);
            toggleButton.setForeground(Color.WHITE);
            toggleButton.setActionCommand(table + " " + roots[i].toString());
            toggleButton.addActionListener(this);

            // korisno samo prilikom starta aplikacije
            if (roots[i].equals(rootFile)) {

                toggleButton.setSelected(true);

            }

            toggleButtons[i] = toggleButton;

            btg.add(toggleButton);
            rootsPanel.add(toggleButton);
        }

        if (table.equals("table1")) {
            toggles1 = toggleButtons;
        } else {
            toggles2 = toggleButtons;
        }

        return rootsPanel;
    }

    //kada dodamo novi disk dok je aplikacija aktivna i osvežimo panele za root
    //diskove hoćemo da zadržimo prethodno stanje izabranih diskova
    private void refreshState(OvalToggleButton[] toggleButtons, String table) {

        for (int i = 0; i < toggleButtons.length; i++) {
            if (table.equals("table1")) {
                if (toggleButtons[i].getText().equals(lastChosenLeftRoot.toString())) {


                    toggleButtons[i].setSelected(true);
                    break;
                } else if (!lastChosenLeftRoot.canRead()
                        && toggleButtons[i].getText().equals(FileUtils.getRootFile(rootUser).toString())) {
                    lastChosenLeftRoot = FileUtils.getRootFile(rootUser);
                    toggleButtons[i].setSelected(true);
                    break;
                }

            } else if (table.equals("table2")) {
                if (toggleButtons[i].getText().equals(lastChosenRightRoot.toString())) {


                    toggleButtons[i].setSelected(true);
                    break;
                } else if (!lastChosenRightRoot.canRead()
                        && toggleButtons[i].getText().equals(FileUtils.getRootFile(rootUser).toString())) {
                    lastChosenRightRoot = FileUtils.getRootFile(rootUser);
                    toggleButtons[i].setSelected(true);
                    break;
                }

            }
        }
    }

    public void actionPerformed(final ActionEvent e) {

        if (helperTable == null) {
            helperTable = table1;
        }

        String tab = null;

        if (e.getActionCommand().indexOf("table1") >= 0) {
            helperTable = table1;
            tab = "table1";
        } else if (e.getActionCommand().indexOf("table2") >= 0) {
            helperTable = table2;
            tab = "table2";
        }

        final int j;
        final String str;

        if (tab != null) {
            for (int i = 0; i < roots.length; i++) {

                if (e.getActionCommand().equalsIgnoreCase(tab + " " + roots[i].toString())) {
                    j = i;
                    str = tab;
                    Thread runner = new Thread() {

                        public void run() {
                            if (roots[j].canRead()) {
                                if (roots[j].listFiles().length <= 0 || roots[j].listFiles() == null) {
                                    ((OvalToggleButton) e.getSource()).getModel().setSelected(false);
                                    return;
                                }
                            } else {
                                refreshButton.doClick();
                                return;
                            }

                            helperTable.getTableData().setData(roots[j]);


                            Runnable runnable = new Runnable() {

                                public void run() {
                                    helperTable.getTable().tableChanged(new TableModelEvent(helperTable.getTableData()));
                                    helperTable.getTable().setRowSelectionInterval(0, 0);
                                    if (str.equals("table1")) {
                                        table2.getTable().clearSelection();
                                        ds1.setDisc(roots[j]);
                                        lastChosenLeftRoot = roots[j];
                                    } else if (str.equals("table2")) {
                                        table1.getTable().clearSelection();
                                        ds2.setDisc(roots[j]);
                                        lastChosenRightRoot = roots[j];
                                    }
                                }
                            };
                            SwingUtilities.invokeLater(runnable);
                        }
                    };
                    runner.start();
                    return;
                }
            }
        }



        if (e.getSource() == newFolder) {
            File currentDir;

            String input = (String) JOptionPane.showInputDialog(FileCommander.this,
                    "Nova omotnica (direktorijum)", "File Manager", JOptionPane.INFORMATION_MESSAGE,
                    newDirIcon, null, "Unesite ime ovde");

            

            if (input != null && !input.isEmpty()) {
                notFirstStart = false;
            isDialogActive = true;
                if (helperTable == null) {

                    currentDir = table1.getTableData().getCurrentDir();
                    helperTable = table1;
                    newDir = FileUtils.createNewDir(helperTable, input);
                } else {
                    newDir = FileUtils.createNewDir(helperTable, input);
                }
                dirCreated = true;
                reloadAndRepaintTables();              
                isDialogActive = false;
            } else {
                return;
            }

        } else if (e.getSource() == parent) {

            if (helperTable == null) {
                return;
            }
            goUpAndRepaintTable(helperTable);

        } else if (e.getSource() == copy) {


            if ((row < 0 && row1 < 0) || selectedRows == null) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Nema izabranih datoteka");
                return;
            }

            dialogPane = new JOptionPane(
                    "Kopiraj datoteku(e) u\n " + destFile.toString());
            dialogPane.setIcon(copyIcon);
            Object[] options = new String[]{"Ok", "Otkaži"};
            dialogPane.setOptions(options);
            JDialog diag = dialogPane.createDialog(
                    FileCommander.this, "FILE MANAGER");
            diag.setVisible(true);

            Object obj = dialogPane.getValue();
            if (obj == options[0]) {

                notFirstStart = false;
                isDialogActive = true;

                Thread copyRunner = new Thread() {

                    @Override
                    public void run() {
                        try {
                            dialog.calculateSize(selectedRows);
                            dialog.copyFilePath(selectedRows, destFile);
                            reloadAndRepaintTables();

                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(FileCommander.this, ex);
                        }
                    }
                };
                copyRunner.start();
                FileUtils.operationProgressDialog.setVisible(true);
                isDialogActive = false;
            } else {
                return;
            }
        } else if (e.getSource() == move) {

            if ((row < 0 && row1 < 0) || selectedRows == null) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Nema izabranih datoteka");
                return;
            }

            moveOrDelete = true;

            dialogPane = new JOptionPane(
                    "Premesti datoteku(e) u\n " + destFile.toString());
            dialogPane.setIcon(moveIcon);
            Object[] options = new String[]{"Ok", "Otkaži"};
            dialogPane.setOptions(options);
            JDialog diag = dialogPane.createDialog(
                    FileCommander.this, "FILE MANAGER");
            diag.setVisible(true);

            Object obj = dialogPane.getValue();

            if (obj == options[0]) {

                notFirstStart = false;
                isDialogActive = true;

                dialog.setTableToUpdate(helperTable);

                Thread copyRunner = new Thread() {

                    @Override
                    public void run() {
                        try {

                            dialog.calculateSize(selectedRows);
                            dialog.moveFilePath(selectedRows, destFile);
                            reloadAndRepaintTables();

                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(FileCommander.this, ex);
                        }
                    }
                };
                copyRunner.start();
                FileUtils.operationProgressDialog.setVisible(true);


                isDialogActive = false;
            } else {
                return;
            }
        } else if (e.getSource() == delete) {

            if ((row < 0 && row1 < 0) || selectedRows == null) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Nema izabranih datoteka");
                return;
            }

            moveOrDelete = true;

            if (selectedRows.length > 1) {
                dialogPane = new JOptionPane("Da li zaista hoćete da obrišete"
                        + " izabrane stavke");
            } else {
                if (selectedRows[0].isFile()) {
                    dialogPane = new JOptionPane("Da li zaista hoćete da obrišete"
                            + " izabranu datoteku " + selectedRows[0].getName());
                } else if (selectedRows[0].isDirectory()) {

                    try {
                        if (selectedRows[0].listFiles().length == 0) {
                            dialogPane = new JOptionPane("Da li zaista hoćete da obrišete"
                                    + " izabrani direktorijum " + selectedRows[0].getName());
                        } else {
                            dialogPane = new JOptionPane("Direktorijum " + selectedRows[0].getName() + " nije prazan!"
                                    + System.getProperty("line.separator") + "Da li zaista hoćete da obrišete direktorijum "
                                    + "zajedno sa njegovim sadržajem?");
                        }

                    } catch (Exception se) {
                        JOptionPane.showMessageDialog(this, "Nije dozvoljeno ili ne mogu da čitam datoteku");
                        return;
                    }


                }
            }


            dialogPane.setIcon(deleteIcon);
            Object[] options = new String[]{"Ok", "Otkaži"};
            dialogPane.setOptions(options);
            JDialog diag = dialogPane.createDialog(
                    FileCommander.this, "FILE MANAGER");
            diag.setVisible(true);

            Object obj = dialogPane.getValue();

            if (obj == options[0]) {
                notFirstStart = false;
                isDialogActive = true;

                dialog.setTableToUpdate(helperTable);

                Thread copyRunner = new Thread() {

                    @Override
                    public void run() {

                        dialog.getTotalNumberOfFiles(selectedRows);
                        dialog.deleteFiles(selectedRows);

                    }
                };
                copyRunner.start();
                FileUtils.operationProgressDialog.setVisible(true);
                reloadAndRepaintTables();

            } else {
                return;
            }
        } else if (e.getSource() == archive) {
            if ((row < 0 && row1 < 0) || selectedRows == null) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Nema izabranih datoteka");
                return;
            }

            archiveOp.prepareSourceAndDestination(selectedRows, destFile, true);
            reloadAndRepaintTables();


        } else if (e.getSource() == extract) {
            if ((row < 0 && row1 < 0) || selectedRows == null) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Nema izabranih datoteka");
                return;
            }

            archiveOp.prepareSourceAndDestination(selectedRows, destFile, false);
            unzipOperation = true;
            reloadAndRepaintTables();

        }

    }

    public void keyTyped(KeyEvent ke) {
    }

    public void keyPressed(KeyEvent ke) {
        FocusListener[] lst = ((JTable) ke.getSource()).getFocusListeners();
        if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
            for (int i = 0; i < lst.length; i++) {
                FocusListener focusListener = lst[i];
                focusListener.focusLost(new FocusEvent(((JTable) ke.getSource()), ke.getID()));

            }
            delete.doClick();
        }
    }

    public void keyReleased(KeyEvent ke) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    //pokrećemo ovu metodu samo nakon prvog pokretanja programa
    //jer je tabela već ažurirana priliko prvog starta programa  
    public void windowActivated(WindowEvent e) {
        if (notFirstStart) {                   
            reloadAndRepaintTables();
        }
    }

    //postavljamo promenljivu -firstStart- na vrednost -true-
    //da bi se tabele ažurirale kada prozor postane aktivan prozor
    public void windowDeactivated(WindowEvent e) {
        if (!isDialogActive) {           
            notFirstStart = true;
        }

    }

    //metoda da ponovo učita direktorijume i osveži tabele
    //nakon aktiviranja prozora ili premeštanja i kopiranja datoteka
    //takođe metoda vodi računa da redovi izabrani pre osvežavanja budu
    //ponovo izabrani
    public void reloadAndRepaintTables() {

        if (helperTable == null) {
            return;
        }

        final JTable tableSel = helperTable.getTable();


        if (tableSel.getSelectedRows().length > 0) {
            selection = tableSel.getSelectedRows();
            
        }

        final int minRow = tableSel.getSelectionModel().getMinSelectionIndex();
        final int maxRow = tableSel.getSelectionModel().getMaxSelectionIndex();


        Thread runner = new Thread() {

            @Override
            public void run() {
                
                final TableData t_data1 = table1.getTableData();
                final TableData t_data2 = table2.getTableData();
                
                t_data1.refreshTable();
                t_data2.refreshTable();
                
                NameAndSeparatorComp.clearPaths();
                t_data1.refreshPaths(pathPanel);
                t_data2.refreshPaths(pathPanel2);

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        table1.getTable().tableChanged(new TableModelEvent(t_data1));
                        table2.getTable().tableChanged(new TableModelEvent(t_data2));

                        if (dirCreated) {
                            int index = helperTable.getTableData().getFileIndex(newDir);
                            
                            if (index >= 0) {
                                Rectangle rect = helperTable.getTable().getCellRect(index, 0, true);
                                helperTable.getTable().scrollRectToVisible(rect);
                                helperTable.getTable().changeSelection(index, 0, false, false);
                            }
                            dirCreated = false;
                        } else if (unzipOperation) {

                            int index = helperTable.getTableData().getFileIndex(selectedRows[0]);
                            Rectangle rect = helperTable.getTable().getCellRect(index, 0, true);
                            helperTable.getTable().scrollRectToVisible(rect);
                            helperTable.getTable().changeSelection(index, 0, false, false);

                            unzipOperation = false;
                        } else if (moveOrDelete) {
                            int rowCount = tableSel.getRowCount();

                            if (rowCount - 1 >= maxRow) {
                                tableSel.setRowSelectionInterval(minRow, minRow);
                            } else if (rowCount > 0) {

                                tableSel.setRowSelectionInterval(rowCount - 1, rowCount - 1);

                            }
                            moveOrDelete = false;
                        } else {
                            selection = tableSel.getSelectedRows();//just in case if directory changes
                            if (selection != null && selection.length > 0) {
                                for (int i = 0; i < selection.length; i++) {
                                    tableSel.addRowSelectionInterval(selection[i], selection[i]);
                                }
                            } else {
                                tableSel.addRowSelectionInterval(0, 0);
                            }

                        }

                    }
                });
            }
        };
        runner.setPriority(Thread.MIN_PRIORITY);
        runner.start();
        tableSel.requestFocusInWindow();

    }

//metoda koja se poziva pritiskom na dugme -Parent- za povratak na roditelj-direktorijum
    private void goUpAndRepaintTable(final TableAndFiles table) {
        final File parentFile = table.getTableData().getParent();
        
        if (parentFile == null) {
            return;
        }
        
        Thread runner = new Thread() {

            @Override
            public void run() {
                        
                table.getTableData().setParent();
               
                
                {
                    Runnable runnable = new Runnable() {

                        public void run() {
                            
                             if (!NameAndSeparatorComp.isInThePath(parentFile, activePanel)) {
                     
                                                new NameAndSeparatorComp(parentFile, activePanel);
                                            }
                            
                            table.getTable().tableChanged(new TableModelEvent(table.getTableData()));
                            table.getTableData().makeSelection();                          
                           
                        }
                    };
                    SwingUtilities.invokeLater(runnable);
                }
            }
        };
        runner.start();
    }

    private static class GradientPanel extends JPanel {

        private BufferedImage gradientImage;

        GradientPanel() {
            super(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {

            LinearGradientPaint gradient = null;

            gradient = new LinearGradientPaint(0.0f, 0.0f, 0.0f, getHeight(),
                    new float[]{0.0f, 0.45f, 0.67f, 1.0f},
                    new Color[]{new Color(0x100053),
                        new Color(0x2200b5),
                        new Color(0x2200b5),
                        new Color(0x100053)});

            if (gradientImage == null
                    || gradientImage.getHeight() != getHeight()) {
                gradientImage = GraphicsUtilities.createCompatibleImage(1, getHeight());
                Graphics2D g2d = (Graphics2D) gradientImage.getGraphics();
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, 1, getHeight());
                g2d.dispose();
            }

            g.drawImage(gradientImage, 0, 0,
                    getWidth(), getHeight(), null);

        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            LookAndFeel rich = new Rich.RichLF();
            UIManager.LookAndFeelInfo info =
                    new UIManager.LookAndFeelInfo(rich.getName(),
                    rich.getClass().getName());
            UIManager.installLookAndFeel(info);
            UIManager.setLookAndFeel(rich);
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.toString());
        }

        //UIManager.put("ScrollBarUI", "Rich.RichScrollBarUI");


        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                FileCommander fm = new FileCommander();
                fm.setVisible(true);
                fm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }
}
