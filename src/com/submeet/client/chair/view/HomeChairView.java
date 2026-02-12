package com.submeet.client.chair.view;



import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import com.submeet.client.account.controller.HomeControl;
import com.submeet.client.chair.controller.ConferenceDashboardControl;

/**
 *
 * @author aless
 */
public class HomeChairView extends javax.swing.JFrame {
    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(HomeChairView.class.getName());

    private HomeControl homeControl;

    /**
     * Creates new form HomeChairView
     */
    public HomeChairView() {
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(470, 500);
        // Imposta dimensioni percentuali iniziali
        adjustColumnWidths();
        // Riallinea le percentuali ad ogni resize della tabella
        panelConference.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustColumnWidths();
            }
        });

        // Initialize HomeControl
        this.homeControl = new HomeControl();
        setVisible(true);
    }

    /**
     * Creates new form HomeChairView with a HomeControl instance
     * @param homeControl The HomeControl instance to use
     */
    public HomeChairView(HomeControl homeControl) {
        this();
        this.homeControl = homeControl;
    }

    /**
     * Creates new form HomeChairView with a HomeControl instance and a list of conferences
     * @param homeControl The HomeControl instance to use
     * @param conferenceList The list of conferences to display
     */
    public HomeChairView(HomeControl homeControl, List<Map<String, Object>> conferenceList) {
        this(homeControl);

        // Populate the table with conference data
        if (conferenceList != null && !conferenceList.isEmpty()) {
            DefaultTableModel model = (DefaultTableModel) tableConference.getModel();

            // Clear existing rows
            model.setRowCount(0);

            // Add conference data to the table
            for (Map<String, Object> conference : conferenceList) {
                Integer conferenceId = (Integer) conference.get("conferenceId");
                String title = (String) conference.get("title");
                model.addRow(new Object[]{conferenceId, title});
            }
        }
    }

    /**
     * Imposta la larghezza delle colonne in percentuale: 30% codice, 70% nome
     */
    private void adjustColumnWidths() {
        // Disabilita l'auto‐resize
        tableConference.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // Larghezza disponibile nel viewport
        int totalWidth = panelConference.getViewport().getWidth();
        TableColumnModel colModel = tableConference.getColumnModel();
        // Colonna CODICE = 30%
        colModel.getColumn(0).setPreferredWidth((int)(totalWidth * 0.15));
        // Colonna NOME = 70%
        colModel.getColumn(1).setPreferredWidth((int)(totalWidth * 0.85));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        labelLogo = new javax.swing.JLabel();
        labelCMS = new javax.swing.JLabel();
        panelBarra = new javax.swing.JPanel();
        homeButton = new javax.swing.JButton();
        panelTitle = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        createConferenceButton = new javax.swing.JButton();
        panelConference = new javax.swing.JScrollPane();
        tableConference = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        labelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/SubMeet_140x140.png"))); // NOI18N

        labelCMS.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        labelCMS.setForeground(new java.awt.Color(36, 105, 186));
        labelCMS.setText("CONFERENCE MANAGEMENT SYSTEM");

        panelBarra.setBackground(new java.awt.Color(204, 204, 204));
        panelBarra.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelBarra.setPreferredSize(new java.awt.Dimension(233, 23));

        homeButton.setBackground(new java.awt.Color(204, 204, 204));
        homeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-home-16.png"))); // NOI18N
        homeButton.setBorder(null);
        homeButton.setBorderPainted(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setPreferredSize(new java.awt.Dimension(20, 20));
        homeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeButtonActionPerformed(evt);
            }
        });

        panelTitle.setBackground(new java.awt.Color(153, 153, 255));
        panelTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("HOME CHAIR");

        javax.swing.GroupLayout panelTitleLayout = new javax.swing.GroupLayout(panelTitle);
        panelTitle.setLayout(panelTitleLayout);
        panelTitleLayout.setHorizontalGroup(
                panelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelTitleLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                                .addContainerGap())
        );
        panelTitleLayout.setVerticalGroup(
                panelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTitleLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel10))
        );

        javax.swing.GroupLayout panelBarraLayout = new javax.swing.GroupLayout(panelBarra);
        panelBarra.setLayout(panelBarraLayout);
        panelBarraLayout.setHorizontalGroup(
                panelBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelBarraLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(panelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(homeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14))
        );
        panelBarraLayout.setVerticalGroup(
                panelBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(homeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        createConferenceButton.setBackground(new java.awt.Color(153, 153, 255));
        createConferenceButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        createConferenceButton.setText("CREA CONFERENZA");
        createConferenceButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        createConferenceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createConferenceButtonActionPerformed(evt);
            }
        });

        panelConference.setBackground(new java.awt.Color(255, 255, 255));
        panelConference.setBorder(null);

        tableConference.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                },
                new String [] {
                        "CODICE", "NOME CONFERENZA"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tableConference.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tableConference.setGridColor(new java.awt.Color(102, 102, 255));
        tableConference.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableConferenceMouseClicked(evt);
            }
        });
        panelConference.setViewportView(tableConference);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBarra, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addComponent(labelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(12, 12, 12)
                                                .addComponent(labelCMS))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(28, 28, 28)
                                                .addComponent(panelConference, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(28, 28, 28)
                                                .addComponent(createConferenceButton)))
                                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(labelCMS)))
                                .addGap(18, 18, 18)
                                .addComponent(panelBarra, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addComponent(panelConference, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
                                .addComponent(createConferenceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 44, 44))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>

    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Call the returnToHome method in HomeControl
        homeControl.returnToHome();
        // Dispose this view
        this.dispose();
    }

    private void createConferenceButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Call the conferenceCreationDashboard method in HomeControl
        homeControl.conferenceCreationDashboard();
        // Dispose this view
        this.dispose();
    }

    private void tableConferenceMouseClicked(java.awt.event.MouseEvent evt) {
        // Get the selected row index
        // TODO: spostare creazione nell'home control
        int selectedRow = tableConference.getSelectedRow();
        if (selectedRow >= 0) {
            // Get the conference ID from the first column
            Integer conferenceId = (Integer) tableConference.getValueAt(selectedRow, 0);
            // Create a new ConferenceDashboardControl with the selected conference ID
            new ConferenceDashboardControl(conferenceId, homeControl);
            // Dispose this view
            this.dispose();
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JButton createConferenceButton;
    private javax.swing.JButton homeButton;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelCMS;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JPanel panelBarra;
    private javax.swing.JScrollPane panelConference;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JTable tableConference;
// End of variables declaration
}
