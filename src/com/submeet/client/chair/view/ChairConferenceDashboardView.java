package com.submeet.client.chair.view;

import com.submeet.client.chair.controller.ConferenceDashboardControl;
import com.submeet.client.entity.EntityConference;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.Map;

public class ChairConferenceDashboardView extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ChairConferenceDashboardView.class.getName());
    private ConferenceDashboardControl control;
    private EntityConference conference;

    /**
     * Creates new form ConferenceDashboardView with controller and conference data
     */
    public ChairConferenceDashboardView(ConferenceDashboardControl control, EntityConference conference) {
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);
        this.control = control;
        this.conference = conference;

        // Set conference title
        fieldConference.setText(conference.getTitle());
        fieldConference.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        // Set submitted papers count
        labelSubmittedPapersNumber.setText(String.valueOf(conference.getSubmittedPapers()));

        // Set up paper lists
        updatePaperLists();

        setVisible(true);
    }

    /**
     * Updates the paper lists based on conference data
     */
    private void updatePaperLists() {
        // Set up the list of papers with at least one review
        DefaultListModel<String> reviewedModel = new DefaultListModel<>();
        if (conference.getUnderReview() != null) {
            for (Map.Entry<Integer, String> entry : conference.getUnderReview().entrySet()) {
                reviewedModel.addElement(entry.getValue());
            }
            labelPaperInReviewNumber.setText(String.valueOf(conference.getUnderReview().size()));
        } else {
            labelPaperInReviewNumber.setText("0");
        }
        listPaperInReview.setModel(reviewedModel);

        // Set up the list of papers with fewer reviewers than required
        DefaultListModel<String> toBeReviewedModel = new DefaultListModel<>();
        if (conference.getUnassigned() != null) {
            for (Map.Entry<Integer, String> entry : conference.getUnassigned().entrySet()) {
                toBeReviewedModel.addElement(entry.getValue());
            }
            labelToBeAssignedPaperNumber.setText(String.valueOf(conference.getUnassigned().size()));
        } else {
            labelToBeAssignedPaperNumber.setText("0");
        }
        listToBeAssigned.setModel(toBeReviewedModel);

        // Set up the list of final versions
        DefaultListModel<String> finalVersionModel = new DefaultListModel<>();
        if (conference.getReviewed() != null) {
            for (Map.Entry<Integer, String> entry : conference.getReviewed().entrySet()) {
                finalVersionModel.addElement(entry.getValue());
            }
            labelReviewedPaperNumber.setText(String.valueOf(conference.getReviewed().size()));
        } else {
            labelReviewedPaperNumber.setText("0");
        }
        listFinalVersions.setModel(finalVersionModel);


        // Handle click on papers in review
        listPaperInReview.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // double click
                    int index = listPaperInReview.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        String selectedTitle = listPaperInReview.getModel().getElementAt(index);
                        onPaperInReviewClicked(selectedTitle);
                    }
                }
            }
        });

        // Handle click on papers to be assigned
        listToBeAssigned.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = listToBeAssigned.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        String selectedTitle = listToBeAssigned.getModel().getElementAt(index);
                        onToBeAssignedPaperClicked(selectedTitle);
                    }
                }
            }
        });

        // Handle click on final versions
        listFinalVersions.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = listFinalVersions.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        String selectedTitle = listFinalVersions.getModel().getElementAt(index);
                        onFinalVersionClicked(selectedTitle);
                    }
                }
            }
        });

    }

    private Integer findPaperIdByTitle(Map<Integer, String> map, String title) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().equals(title)) {
                return entry.getKey();
            }
        }
        return null;
    }


    // TODO: togliere i listner e chiamare direttamente i metodi della control
    /**
     * Sets the assign papers button listener
     */
    public void setButtonAssignPapersListener(ActionListener listener) {
        buttonAssignPapers.addActionListener(listener);
    }

    /**
     * Sets the invite PC member button listener
     */
    public void setButtonInvitePCMemberListener(ActionListener listener) {
        buttonInvitePCMember.addActionListener(listener);
    }

    /**
     * Sets the invite editor button listener
     */
    public void setButtonInviteEditorListener(ActionListener listener) {
        buttonInviteEditor.addActionListener(listener);
    }

    /**
     * Sets the list selection listener for reviewed papers
     */
    public void setListReviewedPaperListener(ListSelectionListener listener) {
        listPaperInReview.addListSelectionListener(listener);
    }

    /**
     * Sets the list selection listener for papers in review
     */
    public void setListPaperInReviewListener(ListSelectionListener listener) {
        listToBeAssigned.addListSelectionListener(listener);
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
        backButton = new javax.swing.JButton();
        panelTitle = new javax.swing.JPanel();
        labelTitle = new javax.swing.JLabel();
        labelConference = new javax.swing.JLabel();
        fieldConference = new javax.swing.JTextField();
        labelSubmittedPapers = new javax.swing.JLabel();
        labelSubmittedPapersNumber = new javax.swing.JLabel();
        labelFinalVersion = new javax.swing.JLabel();
        labelReviewedPaperNumber = new javax.swing.JLabel();
        scrollPanelToBeAssigned = new javax.swing.JScrollPane();
        listToBeAssigned = new javax.swing.JList<>();
        labelPaperInReview = new javax.swing.JLabel();
        labelPaperInReviewNumber = new javax.swing.JLabel();
        scrollPanelFinalVersions = new javax.swing.JScrollPane();
        listFinalVersions = new javax.swing.JList<>();
        labelPaperToBeAssigned = new javax.swing.JLabel();
        labelToBeAssignedPaperNumber = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listPaperInReview = new javax.swing.JList<>();
        buttonAssignPapers = new javax.swing.JButton();
        buttonInvitePCMember = new javax.swing.JButton();
        buttonInviteEditor = new javax.swing.JButton();
        downloadLogButton = new javax.swing.JButton();

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

        backButton.setBackground(new java.awt.Color(204, 204, 204));
        backButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-back-20.png"))); // NOI18N
        backButton.setBorder(null);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        panelTitle.setBackground(new java.awt.Color(153, 153, 255));
        panelTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelTitle.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        labelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTitle.setText("DASHBOARD CONFERENZA");

        javax.swing.GroupLayout panelTitleLayout = new javax.swing.GroupLayout(panelTitle);
        panelTitle.setLayout(panelTitleLayout);
        panelTitleLayout.setHorizontalGroup(
                panelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelTitleLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(9, 9, 9))
        );
        panelTitleLayout.setVerticalGroup(
                panelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTitleLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(labelTitle))
        );

        javax.swing.GroupLayout panelBarraLayout = new javax.swing.GroupLayout(panelBarra);
        panelBarra.setLayout(panelBarraLayout);
        panelBarraLayout.setHorizontalGroup(
                panelBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelBarraLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(panelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(backButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(homeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14))
        );
        panelBarraLayout.setVerticalGroup(
                panelBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(backButton)
                                .addComponent(homeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        labelConference.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelConference.setText("Conferenza");

        fieldConference.setEditable(false);
        fieldConference.setEnabled(false);
        fieldConference.setMaximumSize(new java.awt.Dimension(64, 22));
        fieldConference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldConferenceActionPerformed(evt);
            }
        });

        labelSubmittedPapers.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelSubmittedPapers.setText("Paper sottomessi:");

        labelSubmittedPapersNumber.setText("a");

        labelFinalVersion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelFinalVersion.setText("Versioni Finali");

        labelReviewedPaperNumber.setText("a");

        listToBeAssigned.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        listToBeAssigned.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        listToBeAssigned.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        scrollPanelToBeAssigned.setViewportView(listToBeAssigned);

        labelPaperInReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelPaperInReview.setText("Paper in revisione");

        labelPaperInReviewNumber.setText("a");

        listFinalVersions.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        listFinalVersions.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        listFinalVersions.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listFinalVersions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPanelFinalVersions.setViewportView(listFinalVersions);

        labelPaperToBeAssigned.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelPaperToBeAssigned.setText("Paper da assegnare");

        labelToBeAssignedPaperNumber.setText("a");

        listPaperInReview.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        listPaperInReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        listPaperInReview.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(listPaperInReview);

        buttonAssignPapers.setBackground(new java.awt.Color(153, 153, 255));
        buttonAssignPapers.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonAssignPapers.setText("ASSEGNAZIONE PAPER");
        buttonAssignPapers.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        buttonAssignPapers.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonAssignPapers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAssignPapersActionPerformed(evt);
            }
        });

        buttonInvitePCMember.setBackground(new java.awt.Color(153, 153, 255));
        buttonInvitePCMember.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonInvitePCMember.setText("INVITA MEMBRO PC");
        buttonInvitePCMember.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        buttonInvitePCMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInvitePCMemberActionPerformed(evt);
            }
        });

        buttonInviteEditor.setBackground(new java.awt.Color(153, 153, 255));
        buttonInviteEditor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonInviteEditor.setText("INVITA EDITORE");
        buttonInviteEditor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        buttonInviteEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInviteEditorActionPerformed(evt);
            }
        });

        downloadLogButton.setBackground(new java.awt.Color(153, 153, 255));
        downloadLogButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        downloadLogButton.setText("SCARICA LOG");
        downloadLogButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        downloadLogButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        downloadLogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadLogButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBackgroundLayout = new javax.swing.GroupLayout(panelBackground);
        panelBackground.setLayout(panelBackgroundLayout);
        panelBackgroundLayout.setHorizontalGroup(
                panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBarra, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(31, 31, 31)
                                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                .addComponent(labelPaperInReview)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(labelPaperInReviewNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(labelConference, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(fieldConference, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                .addComponent(labelSubmittedPapers)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(labelSubmittedPapersNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                .addComponent(labelFinalVersion)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(labelReviewedPaperNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(scrollPanelFinalVersions)
                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                .addComponent(labelPaperToBeAssigned)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(labelToBeAssignedPaperNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jScrollPane3)
                                                        .addComponent(scrollPanelToBeAssigned)))
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addComponent(labelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(labelCMS)))
                                .addContainerGap(65, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBackgroundLayout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(buttonAssignPapers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(downloadLogButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(buttonInvitePCMember, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(buttonInviteEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(93, 93, 93))
        );
        panelBackgroundLayout.setVerticalGroup(
                panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(15, 15, 15)
                                                .addComponent(labelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(25, 25, 25)
                                                .addComponent(labelCMS)))
                                .addGap(18, 18, 18)
                                .addComponent(panelBarra, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(labelConference)
                                .addGap(3, 3, 3)
                                .addComponent(fieldConference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelSubmittedPapers)
                                        .addComponent(labelSubmittedPapersNumber))
                                .addGap(18, 18, 18)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelFinalVersion)
                                        .addComponent(labelReviewedPaperNumber))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPanelFinalVersions, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelPaperInReview)
                                        .addComponent(labelPaperInReviewNumber))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelPaperToBeAssigned)
                                        .addComponent(labelToBeAssignedPaperNumber))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPanelToBeAssigned, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonInvitePCMember, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonAssignPapers, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonInviteEditor, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(downloadLogButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(19, Short.MAX_VALUE))
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

    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        control.returnToHome();
    }

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
        control.goBack();
    }

    private void fieldConferenceActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void buttonAssignPapersActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void downloadLogButtonActionPerformed(java.awt.event.ActionEvent evt) {
        control.downloadLog();
    }

    private void buttonInvitePCMemberActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void buttonInviteEditorActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }


    private void onPaperInReviewClicked(String title) {
        Integer paperId = findPaperIdByTitle(conference.getUnderReview(), title);
        if (paperId != null) {
            control.openPaperInReviewView(paperId);
        }
    }


    private void onToBeAssignedPaperClicked(String title) {
        Integer paperId = findPaperIdByTitle(conference.getUnassigned(), title);
        if (paperId != null) {
            control.openPaperToBeReviewedView(paperId);
        }
    }


    private void onFinalVersionClicked(String title) {
        Integer paperId = findPaperIdByTitle(conference.getReviewed(), title);
        if (paperId != null) {
            control.openFinalVersionView(paperId);
        }
    }


    // Variables declaration - do not modify
    private javax.swing.JButton backButton;
    private javax.swing.JButton buttonAssignPapers;
    private javax.swing.JButton buttonInviteEditor;
    private javax.swing.JButton buttonInvitePCMember;
    private javax.swing.JButton downloadLogButton;
    private javax.swing.JTextField fieldConference;
    private javax.swing.JButton homeButton;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labelCMS;
    private javax.swing.JLabel labelConference;
    private javax.swing.JLabel labelFinalVersion;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JLabel labelPaperInReview;
    private javax.swing.JLabel labelPaperInReviewNumber;
    private javax.swing.JLabel labelPaperToBeAssigned;
    private javax.swing.JLabel labelReviewedPaperNumber;
    private javax.swing.JLabel labelSubmittedPapers;
    private javax.swing.JLabel labelSubmittedPapersNumber;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JLabel labelToBeAssignedPaperNumber;
    private javax.swing.JList<String> listFinalVersions;
    private javax.swing.JList<String> listPaperInReview;
    private javax.swing.JList<String> listToBeAssigned;
    private javax.swing.JPanel panelBackground;
    private javax.swing.JPanel panelBarra;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JScrollPane scrollPanelFinalVersions;
    private javax.swing.JScrollPane scrollPanelToBeAssigned;
    // End of variables declaration
}
