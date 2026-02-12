package com.submeet.client.revisore.view;

import com.submeet.client.account.controller.HomeControl;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class HomeReviewerView extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(HomeReviewerView.class.getName());
    private HomeControl homeControl;
    private List<Map<String, Object>> conferenceList;
    private List<Map<String, Object>> subReviewerPaperList;

    /**
     * Creates new form HomeReviewerView with conference list and subreviewer papers
     * @param homeControl The HomeControl instance
     * @param conferenceList The list of conferences for the user as a reviewer
     * @param subReviewerPaperList The list of papers for the user as a subreviewer
     */
    public HomeReviewerView(HomeControl homeControl, List<Map<String, Object>> conferenceList, List<Map<String, Object>> subReviewerPaperList) {
        this.homeControl = homeControl;
        this.conferenceList = conferenceList;
        this.subReviewerPaperList = subReviewerPaperList;

        initComponents();
        setLocationRelativeTo(null);
        setSize(470, 550);
        setResizable(false);

        // Display the conference list in the table
        displayConferenceList();

        // Display the subreviewer papers
        displaySubReviewerPapers();

        // Imposta dimensioni percentuali iniziali
        adjustColumnWidths();
        // Riallinea le percentuali ad ogni resize della tabella
        scrollPanelConferences.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustColumnWidths();
            }
        });

        // Connect the homeButton to the returnToHome method in HomeControl
        homeButton.addActionListener(e -> homeControl.returnToHome());

        // Add mouse listener to the conference table to handle conference selection
        tableConferences.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double click
                    int selectedRow = tableConferences.getSelectedRow();
                    if (selectedRow != -1) {
                        // Get the conference ID from the selected row
                        int conferenceId = (int) tableConferences.getValueAt(selectedRow, 0);
                        // Call the method in HomeControl to handle conference selection
                        HomeReviewerView.this.openConferenceDetails(conferenceId);
                    }
                }
            }
        });

        setVisible(true);
    }

    private void adjustColumnWidths() {
        // Disabilita l'auto‐resize
        tableConferences.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // Larghezza disponibile nel viewport
        int totalWidth = scrollPanelConferences.getViewport().getWidth();
        TableColumnModel colModel = tableConferences.getColumnModel();
        // Colonna CODICE = 30%
        colModel.getColumn(0).setPreferredWidth((int)(totalWidth * 0.15));
        // Colonna NOME = 70%
        colModel.getColumn(1).setPreferredWidth((int)(totalWidth * 0.85));
    }

    /**
     * Displays the conference list in the table
     */
    private void displayConferenceList() {
        DefaultTableModel model = (DefaultTableModel) tableConferences.getModel();
        model.setRowCount(0); // Clear the table

        if (conferenceList != null && !conferenceList.isEmpty()) {
            for (Map<String, Object> conference : conferenceList) {
                model.addRow(new Object[]{
                    conference.get("conferenceId"),
                    conference.get("title")
                });
            }
        }
    }

    /**
     * Displays the subreviewer papers in the panels
     */
    private void displaySubReviewerPapers() {
        if (subReviewerPaperList == null || subReviewerPaperList.isEmpty()) {
            labelNumberCompletedSubReview.setText("0");
            labelNumberOnGoingSubReview.setText("0");
            return;
        }

        int completedCount = 0;
        int ongoingCount = 0;

        // Clear the panels before adding new content
        panelSubReview.removeAll();
        panelOnGoingSubREview.removeAll();

        // Set layout for panels
        panelSubReview.setLayout(new javax.swing.BoxLayout(panelSubReview, javax.swing.BoxLayout.Y_AXIS));
        panelOnGoingSubREview.setLayout(new javax.swing.BoxLayout(panelOnGoingSubREview, javax.swing.BoxLayout.Y_AXIS));

        for (Map<String, Object> paper : subReviewerPaperList) {
            String state = (String) paper.get("state");
            String title = (String) paper.get("title");
            Integer paperId = (Integer) paper.get("paperId");

            // Create panels for the paper
            javax.swing.JPanel paperPanelOnGoing = new javax.swing.JPanel();
            javax.swing.JPanel paperPanelCompleted = new javax.swing.JPanel();

            paperPanelOnGoing.setBackground(new java.awt.Color(240, 240, 240));
            paperPanelCompleted.setBackground(new java.awt.Color(240, 240, 240));
            paperPanelOnGoing.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            paperPanelCompleted.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            paperPanelOnGoing.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
            paperPanelCompleted.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

            // Create a label for the paper ID
            javax.swing.JLabel idLabel = new javax.swing.JLabel("ID: " + paperId);
            idLabel.setFont(new java.awt.Font("Segoe UI", 1, 11));

            // Create a label for the paper title
            javax.swing.JLabel titleLabel = new javax.swing.JLabel(title != null ? title : "No title");
            titleLabel.setFont(new java.awt.Font("Segoe UI", 0, 11));

            // Add the paper panel to the appropriate container
            if (state != null && state.equalsIgnoreCase("Done")) {
                paperPanelCompleted.add(idLabel);
                paperPanelCompleted.add(titleLabel);
                completedCount++;
                panelSubReview.add(paperPanelCompleted);
                panelSubReview.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 5)));
            } else {
                paperPanelOnGoing.add(idLabel);
                paperPanelOnGoing.add(titleLabel);
                ongoingCount++;
                panelOnGoingSubREview.add(paperPanelOnGoing);
                panelOnGoingSubREview.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 5)));
            }

            // Handle click on intem
            paperPanelOnGoing.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        selectOnGoingSubReview(paperId, title);
                    }
                }
            });

            paperPanelCompleted.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        selectCompletedSubReview(paperId);
                    }
                }
            });
        }

        // Update the labels with the counts
        labelNumberCompletedSubReview.setText(String.valueOf(completedCount));
        labelNumberOnGoingSubReview.setText(String.valueOf(ongoingCount));

        // Refresh the panels
        panelSubReview.revalidate();
        panelSubReview.repaint();
        panelOnGoingSubREview.revalidate();
        panelOnGoingSubREview.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        panelBackground = new javax.swing.JPanel();
        labelLogo = new javax.swing.JLabel();
        labelCMS = new javax.swing.JLabel();
        panelBarra = new javax.swing.JPanel();
        homeButton = new javax.swing.JButton();
        labelTitle = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        scrollPanelConferences = new javax.swing.JScrollPane();
        tableConferences = new javax.swing.JTable();
        labelCompletedSubReview = new javax.swing.JLabel();
        labelNumberCompletedSubReview = new javax.swing.JLabel();
        scrollPanelCompletedSubReview = new javax.swing.JScrollPane();
        panelSubReview = new javax.swing.JPanel();
        labelOnGoingSubReview = new javax.swing.JLabel();
        labelNumberOnGoingSubReview = new javax.swing.JLabel();
        scrollPanelOnGoingSubReview = new javax.swing.JScrollPane();
        panelOnGoingSubREview = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelBackground.setBackground(new java.awt.Color(255, 255, 255));

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

        labelTitle.setBackground(new java.awt.Color(153, 153, 255));
        labelTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("HOME REVISORE");

        javax.swing.GroupLayout labelTitleLayout = new javax.swing.GroupLayout(labelTitle);
        labelTitle.setLayout(labelTitleLayout);
        labelTitleLayout.setHorizontalGroup(
                labelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        labelTitleLayout.setVerticalGroup(
                labelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, labelTitleLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel10))
        );

        javax.swing.GroupLayout panelBarraLayout = new javax.swing.GroupLayout(panelBarra);
        panelBarra.setLayout(panelBarraLayout);
        panelBarraLayout.setHorizontalGroup(
                panelBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelBarraLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(labelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(homeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14))
        );
        panelBarraLayout.setVerticalGroup(
                panelBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(labelTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(homeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        scrollPanelConferences.setBackground(new java.awt.Color(255, 255, 255));
        scrollPanelConferences.setBorder(null);
        scrollPanelConferences.setToolTipText("");

        tableConferences.setModel(new javax.swing.table.DefaultTableModel(
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
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableConferences.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tableConferences.setGridColor(new java.awt.Color(102, 102, 255));
        scrollPanelConferences.setViewportView(tableConferences);

        labelCompletedSubReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelCompletedSubReview.setText("Sotto-Revisioni completate:");

        labelNumberCompletedSubReview.setText("labelNumberCompletedSubreview");

        scrollPanelCompletedSubReview.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelSubReview.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelSubReviewLayout = new javax.swing.GroupLayout(panelSubReview);
        panelSubReview.setLayout(panelSubReviewLayout);
        panelSubReviewLayout.setHorizontalGroup(
                panelSubReviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 369, Short.MAX_VALUE)
        );
        panelSubReviewLayout.setVerticalGroup(
                panelSubReviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 100, Short.MAX_VALUE)
        );

        scrollPanelCompletedSubReview.setViewportView(panelSubReview);

        labelOnGoingSubReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelOnGoingSubReview.setText("Sotto-Revisioni in corso:");

        labelNumberOnGoingSubReview.setText("2");

        scrollPanelOnGoingSubReview.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelOnGoingSubREview.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelOnGoingSubREviewLayout = new javax.swing.GroupLayout(panelOnGoingSubREview);
        panelOnGoingSubREview.setLayout(panelOnGoingSubREviewLayout);
        panelOnGoingSubREviewLayout.setHorizontalGroup(
                panelOnGoingSubREviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 371, Short.MAX_VALUE)
        );
        panelOnGoingSubREviewLayout.setVerticalGroup(
                panelOnGoingSubREviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 100, Short.MAX_VALUE)
        );

        scrollPanelOnGoingSubReview.setViewportView(panelOnGoingSubREview);

        javax.swing.GroupLayout panelBackgroundLayout = new javax.swing.GroupLayout(panelBackground);
        panelBackground.setLayout(panelBackgroundLayout);
        panelBackgroundLayout.setHorizontalGroup(
                panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBarra, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addComponent(labelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(12, 12, 12)
                                                .addComponent(labelCMS))
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(28, 28, 28)
                                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                .addComponent(labelOnGoingSubReview)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(labelNumberOnGoingSubReview, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(scrollPanelConferences, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                                                                .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                        .addComponent(labelCompletedSubReview)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(labelNumberCompletedSubReview, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addComponent(scrollPanelCompletedSubReview))
                                                        .addComponent(scrollPanelOnGoingSubReview))))
                                .addContainerGap(59, Short.MAX_VALUE))
        );
        panelBackgroundLayout.setVerticalGroup(
                panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(labelCMS)))
                                .addGap(18, 18, 18)
                                .addComponent(panelBarra, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addComponent(scrollPanelConferences, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelCompletedSubReview)
                                        .addComponent(labelNumberCompletedSubReview))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPanelCompletedSubReview, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelOnGoingSubReview)
                                        .addComponent(labelNumberOnGoingSubReview))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPanelOnGoingSubReview, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(34, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>

    private void openConferenceDetails(int conferenceId) {
        homeControl.createReviewerDashboardControl(conferenceId);
        this.dispose();
    }

    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Call returnToHome method in HomeControl if it's not null
        this.dispose();
    }

    private void selectOnGoingSubReview(int paperId, String paperTitle) {
        homeControl.openOnGoingSubReview(paperId, paperTitle);
        this.dispose();
    }

    private void selectCompletedSubReview(int paperId) {
        homeControl.openCompletedSubReview(paperId);
        this.dispose();
    }

    // Variables declaration - do not modify
    private javax.swing.JButton homeButton;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel labelCMS;
    private javax.swing.JLabel labelCompletedSubReview;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JLabel labelNumberCompletedSubReview;
    private javax.swing.JLabel labelNumberOnGoingSubReview;
    private javax.swing.JLabel labelOnGoingSubReview;
    private javax.swing.JPanel labelTitle;
    private javax.swing.JPanel panelBackground;
    private javax.swing.JPanel panelBarra;
    private javax.swing.JPanel panelOnGoingSubREview;
    private javax.swing.JPanel panelSubReview;
    private javax.swing.JScrollPane scrollPanelCompletedSubReview;
    private javax.swing.JScrollPane scrollPanelConferences;
    private javax.swing.JScrollPane scrollPanelOnGoingSubReview;
    private javax.swing.JTable tableConferences;
    // End of variables declaration
}
