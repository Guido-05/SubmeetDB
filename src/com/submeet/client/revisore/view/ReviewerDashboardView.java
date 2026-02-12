package com.submeet.client.revisore.view;

import com.submeet.client.entity.EntityConference;
import com.submeet.client.entity.EntityUser;
import com.submeet.client.revisore.controller.ReviewerConferenceControl;
import com.submeet.client.utility.AppSession;
import com.submeet.dbmsboundary.DBMSBoundary;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ReviewerDashboardView extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ReviewerDashboardView.class.getName());
    private ReviewerConferenceControl reviewerConferenceControl;
    private List<Map<String, Object>> reviewList;

    /**
     * Creates new form ReviewerDashboardView
     */
    public ReviewerDashboardView(ReviewerConferenceControl reviewerConferenceControl, List<Map<String, Object>> reviewList) {
        this.reviewerConferenceControl = reviewerConferenceControl;
        this.reviewList = reviewList;
        initComponents();

        // Populate fields with data from reviewList and reviewerConferenceControl
        populateFields();

        this.setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    /**
     * Populates the UI components with data from reviewList and reviewerConferenceControl
     */
    private void populateFields() {
        if (reviewerConferenceControl != null && reviewerConferenceControl.getConference() != null) {
            // Set conference name
            fieldConferenceName.setText(reviewerConferenceControl.getConference().getTitle());
            fieldConferenceName.setDisabledTextColor(new java.awt.Color(0, 0, 0));

            // Set chair name (using a placeholder since getChair is not available)
            int chairId = DBMSBoundary.getConferenceChair(reviewerConferenceControl.getConference().getConferenceId());
            EntityUser chairInfo = DBMSBoundary.getUserInfo(chairId);
            String chairName = chairInfo.getName() + " " + chairInfo.getSurname();
            fieldChairName.setText(chairName);
            fieldChairName.setDisabledTextColor(new java.awt.Color(0, 0, 0));

            // Set review expiration date if available
            if (reviewerConferenceControl.getConference().getReviewDeadline() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                labelSetReviewExpiration.setText(dateFormat.format(reviewerConferenceControl.getConference().getReviewDeadline()));
            } else {
                labelSetReviewExpiration.setText("N/A");
            }
        }

        if (reviewList != null) {
            // Count reviews by status
            int newReviewCount = 0;
            int ongoingReviewCount = 0;
            int completedReviewCount = 0;
            int delegatedReviewCount = 0;

            // Lists for different review types
            java.util.List<String> newReviews = new java.util.ArrayList<>();
            java.util.List<String> ongoingReviews = new java.util.ArrayList<>();
            java.util.List<String> completedReviews = new java.util.ArrayList<>();
            java.util.List<String> delegatedReviews = new java.util.ArrayList<>();

            // Process each review in the list
            for (Map<String, Object> review : reviewList) {
                String status = review.get("state") != null ? review.get("state").toString() : "";
                String title = review.get("title") != null ? review.get("title").toString() : "Untitled";

                if ("new".equalsIgnoreCase(status)) {
                    newReviewCount++;
                    newReviews.add(title);
                } else if ("on_going".equalsIgnoreCase(status)) {
                    ongoingReviewCount++;
                    ongoingReviews.add(title);
                } else if ("done".equalsIgnoreCase(status)) {
                    completedReviewCount++;
                    completedReviews.add(title);
                } else if ("delegated".equalsIgnoreCase(status)) {
                    delegatedReviewCount++;
                    delegatedReviews.add(title);
                }
            }

            // Update count labels
            labelNewReviewNumber.setText(String.valueOf(newReviewCount));
            labelDelegatedReviewNumber.setText(String.valueOf(delegatedReviewCount));
            labelNumberOfOnGoingReview.setText(String.valueOf(ongoingReviewCount));
            labelCompletedReviewNumber.setText(String.valueOf(completedReviewCount));
            labelAssignedReviewNumber.setText(String.valueOf(reviewList.size()));

            // Update lists
            listNewReview.setModel(new javax.swing.AbstractListModel<String>() {
                String[] strings = newReviews.toArray(new String[0]);
                public int getSize() { return strings.length; }
                public String getElementAt(int i) { return strings[i]; }
            });

            // Add mouse listener to handle clicks on new reviews
            listNewReview.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) { // Double-click
                        int index = listNewReview.locationToIndex(evt.getPoint());
                        if (index >= 0) {
                            String selectedTitle = listNewReview.getModel().getElementAt(index);
                            // Find the paper ID for the selected title
                            for (Map<String, Object> review : reviewList) {
                                String status = review.get("state") != null ? review.get("state").toString() : "";
                                String title = review.get("title") != null ? review.get("title").toString() : "Untitled";
                                int paperId = review.get("paperId") != null ? Integer.parseInt(review.get("paperId").toString()) : -1;
                                int reviewId = review.get("revisionId") != null ? Integer.parseInt(review.get("revisionId").toString()) : -1;

                                if ("new".equalsIgnoreCase(status) && title.equals(selectedTitle) && paperId != -1) {
                                    // Open the new review view
                                    reviewerConferenceControl.showNewReviewView(title, paperId, reviewId);
                                    dispose(); // Close this view
                                    break;
                                }
                            }
                        }
                    }
                }
            });

            listOnGoingReview.setModel(new javax.swing.AbstractListModel<String>() {
                String[] strings = ongoingReviews.toArray(new String[0]);
                public int getSize() { return strings.length; }
                public String getElementAt(int i) { return strings[i]; }
            });

            // Add mouse listener to handle clicks on ongoing reviews
            listOnGoingReview.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) { // Double-click
                        int index = listOnGoingReview.locationToIndex(evt.getPoint());
                        if (index >= 0) {
                            String selectedTitle = listOnGoingReview.getModel().getElementAt(index);
                            // Find the revision ID for the selected title
                            for (Map<String, Object> review : reviewList) {
                                String status = review.get("state") != null ? review.get("state").toString() : "";
                                String title = review.get("title") != null ? review.get("title").toString() : "Untitled";
                                int paperId = review.get("paperId") != null ? Integer.parseInt(review.get("paperId").toString()) : -1;
                                int revisionId = review.get("revisionId") != null ? Integer.parseInt(review.get("revisionId").toString()) : -1;

                                if ("on_going".equalsIgnoreCase(status) && title.equals(selectedTitle) && revisionId != -1) {
                                    // Open the ongoing review view
                                    reviewerConferenceControl.showOnGoingReviewView(paperId, title, revisionId, false);
                                    dispose(); // Close this view
                                    break;
                                }
                            }
                        }
                    }
                }
            });

            listCompletedReview.setModel(new javax.swing.AbstractListModel<String>() {
                String[] strings = completedReviews.toArray(new String[0]);
                public int getSize() { return strings.length; }
                public String getElementAt(int i) { return strings[i]; }
            });

            // Add mouse listener to handle clicks on completed reviews
            listCompletedReview.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) { // Double-click
                        int index = listCompletedReview.locationToIndex(evt.getPoint());
                        if (index >= 0) {
                            String selectedTitle = listCompletedReview.getModel().getElementAt(index);
                            // Find the revision ID for the selected title
                            for (Map<String, Object> review : reviewList) {
                                String status = review.get("state") != null ? review.get("state").toString() : "";
                                String title = review.get("title") != null ? review.get("title").toString() : "Untitled";
                                int revisionId = review.get("revisionId") != null ? Integer.parseInt(review.get("revisionId").toString()) : -1;

                                if ("done".equalsIgnoreCase(status) && title.equals(selectedTitle) && revisionId != -1) {
                                    // Open the completed review view
                                    reviewerConferenceControl.showCompletedReviewView(revisionId);
                                    dispose(); // Close this view
                                    break;
                                }
                            }
                        }
                    }
                }
            });

            listDelegatedReviews.setModel(new javax.swing.AbstractListModel<String>() {
                String[] strings = delegatedReviews.toArray(new String[0]);
                public int getSize() { return strings.length; }
                public String getElementAt(int i) { return strings[i]; }
            });

            // Add mouse listner to handle clicks on delegated reviews
            listDelegatedReviews.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        int index = listDelegatedReviews.locationToIndex(evt.getPoint());
                        if (index >= 0) {
                            String selectedTitle = listDelegatedReviews.getModel().getElementAt(index);
                            // Find the revision ID for the selected title
                            for (Map<String, Object> review : reviewList) {
                                String status = review.get("state") != null ? review.get("state").toString() : "";
                                String title = review.get("title") != null ? review.get("title").toString() : "Untitled";
                                int revisionId = review.get("revisionId") != null ? Integer.parseInt(review.get("revisionId").toString()) : -1;

                                if ("delegated".equalsIgnoreCase(status) && title.equals(selectedTitle) && revisionId != -1 && status.equalsIgnoreCase("delegated")) {
                                    // Open delegated revoew view
                                    reviewerConferenceControl.showDelegatedReviewView(title, AppSession.getInstance().getUserId());
                                    dispose();
                                    break;
                                }
                            }
                        }
                    }
                }
            });
        }
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
        labelConferenceTitle = new javax.swing.JLabel();
        fieldConferenceName = new javax.swing.JTextField();
        labelChair = new javax.swing.JLabel();
        labelReviewExpiration = new javax.swing.JLabel();
        scrollPanelNewReview = new javax.swing.JScrollPane();
        listNewReview = new javax.swing.JList<>();
        labelOnGoingReview = new javax.swing.JLabel();
        labelNumberOfOnGoingReview = new javax.swing.JLabel();
        scrollPanelCompletedReview = new javax.swing.JScrollPane();
        listCompletedReview = new javax.swing.JList<>();
        labelNewReview = new javax.swing.JLabel();
        labelNewReviewNumber = new javax.swing.JLabel();
        scrollPanelOnGoingReview = new javax.swing.JScrollPane();
        listOnGoingReview = new javax.swing.JList<>();
        expressReviewInterestButton = new javax.swing.JButton();
        labelAssignedReview = new javax.swing.JLabel();
        fieldChairName = new javax.swing.JTextField();
        labelSetReviewExpiration = new javax.swing.JLabel();
        labelAssignedReviewNumber = new javax.swing.JLabel();
        labelCompletedReview = new javax.swing.JLabel();
        labelCompletedReviewNumber = new javax.swing.JLabel();
        labelDelegatedReview = new javax.swing.JLabel();
        labelDelegatedReviewNumber = new javax.swing.JLabel();
        scrollPanelDelegatedReviews = new javax.swing.JScrollPane();
        listDelegatedReviews = new javax.swing.JList<>();

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

        labelConferenceTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelConferenceTitle.setText("Conferenza");

        fieldConferenceName.setEditable(false);
        fieldConferenceName.setMaximumSize(new java.awt.Dimension(64, 22));
        fieldConferenceName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldConferenceNameActionPerformed(evt);
            }
        });

        labelChair.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelChair.setText("Chair:");

        labelReviewExpiration.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelReviewExpiration.setText("Scadenza revisioni:");

        listNewReview.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        listNewReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        listNewReview.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        scrollPanelNewReview.setViewportView(listNewReview);

        labelOnGoingReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelOnGoingReview.setText("Revisioni in corso:");

        labelNumberOfOnGoingReview.setText("10");

        listCompletedReview.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        listCompletedReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        listCompletedReview.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listCompletedReview.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPanelCompletedReview.setViewportView(listCompletedReview);

        labelNewReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelNewReview.setText("Nuove revisioni");

        labelNewReviewNumber.setText("10");

        listOnGoingReview.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        listOnGoingReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        listOnGoingReview.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        scrollPanelOnGoingReview.setViewportView(listOnGoingReview);

        expressReviewInterestButton.setBackground(new java.awt.Color(153, 153, 255));
        expressReviewInterestButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        expressReviewInterestButton.setText("<html><center>ESPRIMI INTERESSE<br>REVISIONE</center></html>");
        expressReviewInterestButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        expressReviewInterestButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expressReviewInterestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expressReviewInterestButtonActionPerformed(evt);
            }
        });

        labelAssignedReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelAssignedReview.setText("Revisioni assegnate:");

        fieldChairName.setEditable(false);
        fieldChairName.setMaximumSize(new java.awt.Dimension(64, 22));
        fieldChairName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldChairNameActionPerformed(evt);
            }
        });

        labelSetReviewExpiration.setPreferredSize(new java.awt.Dimension(100, 16));
        labelSetReviewExpiration.setMinimumSize(new java.awt.Dimension(100, 16));
        labelSetReviewExpiration.setMaximumSize(new java.awt.Dimension(100, 16));
        labelSetReviewExpiration.setText("jLabel5");

        labelAssignedReviewNumber.setText("jLabel12");

        labelCompletedReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelCompletedReview.setText("Revisioni completate:");

        labelCompletedReviewNumber.setText("jLabel14");

        labelDelegatedReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelDelegatedReview.setText("Revisioni delegate");

        labelDelegatedReviewNumber.setText("jLabel14");

        listDelegatedReviews.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        listDelegatedReviews.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        listDelegatedReviews.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listDelegatedReviews.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPanelDelegatedReviews.setViewportView(listDelegatedReviews);

        javax.swing.GroupLayout panelBackgroundLayout = new javax.swing.GroupLayout(panelBackground);
        panelBackground.setLayout(panelBackgroundLayout);
        panelBackgroundLayout.setHorizontalGroup(
                panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBarra, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(31, 31, 31)
                                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labelConferenceTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(fieldConferenceName, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(labelChair)
                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                .addGap(2, 2, 2)
                                                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                                .addComponent(labelAssignedReview)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(labelAssignedReviewNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                                .addComponent(labelReviewExpiration)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(labelSetReviewExpiration, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                                        .addComponent(labelNewReview)
                                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                        .addComponent(labelNewReviewNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                                        .addComponent(labelCompletedReview)
                                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                        .addComponent(labelCompletedReviewNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                .addComponent(scrollPanelCompletedReview)
                                                                                .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                                        .addComponent(labelOnGoingReview)
                                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                        .addComponent(labelNumberOfOnGoingReview, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                .addComponent(scrollPanelOnGoingReview)
                                                                                .addComponent(scrollPanelNewReview, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(scrollPanelDelegatedReviews))
                                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                                .addGap(109, 109, 109)
                                                                                .addComponent(expressReviewInterestButton, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                                .addComponent(labelDelegatedReview)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(labelDelegatedReviewNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                        .addComponent(fieldChairName, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addComponent(labelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(labelCMS)))
                                .addContainerGap(39, Short.MAX_VALUE))
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
                                .addComponent(labelConferenceTitle)
                                .addGap(3, 3, 3)
                                .addComponent(fieldConferenceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelChair)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fieldChairName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelReviewExpiration)
                                        .addComponent(labelSetReviewExpiration))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelAssignedReview)
                                        .addComponent(labelAssignedReviewNumber))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labelDelegatedReviewNumber)
                                        .addComponent(labelDelegatedReview, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPanelDelegatedReviews, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelCompletedReview)
                                        .addComponent(labelCompletedReviewNumber))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPanelCompletedReview, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelOnGoingReview)
                                        .addComponent(labelNumberOfOnGoingReview))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPanelOnGoingReview, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelNewReview)
                                        .addComponent(labelNewReviewNumber))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPanelNewReview, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(expressReviewInterestButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(panelBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>


    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Return to home screen using the HomeControl
        if (reviewerConferenceControl != null) {
            // Close this view
            this.dispose();
            // Return to home
            reviewerConferenceControl.returnToHome();
        }
    }

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Go back to the previous screen
        if (reviewerConferenceControl != null) {
            // Show the conference view
            reviewerConferenceControl.showConferenceView();
            // Close this view
            this.dispose();
        }
    }

    private void fieldConferenceNameActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void expressReviewInterestButtonActionPerformed(java.awt.event.ActionEvent evt) {
        reviewerConferenceControl.showOfferAssignView();
        this.dispose();
    }

    private void fieldChairNameActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    // Variables declaration - do not modify
    private javax.swing.JButton backButton;
    private javax.swing.JButton expressReviewInterestButton;
    private javax.swing.JTextField fieldChairName;
    private javax.swing.JTextField fieldConferenceName;
    private javax.swing.JButton homeButton;
    private javax.swing.JLabel labelAssignedReview;
    private javax.swing.JLabel labelAssignedReviewNumber;
    private javax.swing.JLabel labelCMS;
    private javax.swing.JLabel labelChair;
    private javax.swing.JLabel labelCompletedReview;
    private javax.swing.JLabel labelCompletedReviewNumber;
    private javax.swing.JLabel labelConferenceTitle;
    private javax.swing.JLabel labelDelegatedReview;
    private javax.swing.JLabel labelDelegatedReviewNumber;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JLabel labelNewReview;
    private javax.swing.JLabel labelNewReviewNumber;
    private javax.swing.JLabel labelNumberOfOnGoingReview;
    private javax.swing.JLabel labelOnGoingReview;
    private javax.swing.JLabel labelReviewExpiration;
    private javax.swing.JLabel labelSetReviewExpiration;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JList<String> listCompletedReview;
    private javax.swing.JList<String> listDelegatedReviews;
    private javax.swing.JList<String> listNewReview;
    private javax.swing.JList<String> listOnGoingReview;
    private javax.swing.JPanel panelBackground;
    private javax.swing.JPanel panelBarra;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JScrollPane scrollPanelCompletedReview;
    private javax.swing.JScrollPane scrollPanelDelegatedReviews;
    private javax.swing.JScrollPane scrollPanelNewReview;
    private javax.swing.JScrollPane scrollPanelOnGoingReview;
    // End of variables declaration
}
