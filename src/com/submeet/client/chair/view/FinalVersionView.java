package com.submeet.client.chair.view;

import com.submeet.client.chair.controller.ConferenceDashboardControl;

import java.util.List;
import java.util.Map;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.JSeparator;
import javax.swing.JLabel;


public class FinalVersionView extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FinalVersionView.class.getName());
    private ConferenceDashboardControl control;
    private int paperId;

    /**
     * Creates new form ReviewedPaperView
     */
    public FinalVersionView(ConferenceDashboardControl control, Map<String, Object> paperInfo, int paperId) {
        this.paperId = paperId;
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);

        this.control = control;

        // Set paper title
        if (paperInfo.containsKey("title")) {
            fieldPaperTitle.setText((String) paperInfo.get("title"));
            fieldPaperTitle.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        }

        // Set author name
        if (paperInfo.containsKey("authorName") && paperInfo.containsKey("authorSurname")) {
            String authorName = (String) paperInfo.get("authorName");
            String authorSurname = (String) paperInfo.get("authorSurname");
            fieldAuthor.setText(authorName + " " + authorSurname);
            fieldAuthor.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        }

        // Add reviews to panelReviewer and extract reviewer names
        if (paperInfo.containsKey("reviews")) {
            List<Map<String, Object>> reviews = (List<Map<String, Object>>) paperInfo.get("reviews");
            if (!reviews.isEmpty()) {
                // Extract reviewer names for the textAreaReviewer
                StringBuilder reviewersText = new StringBuilder();
                for (Map<String, Object> review : reviews) {
                    String reviewerName = (String) review.get("reviewerName");
                    String reviewerSurname = (String) review.get("reviewerSurname");
                    reviewersText.append(reviewerName).append(" ").append(reviewerSurname).append("\n");
                }
                textAreaReviewer.setText(reviewersText.toString());
                textAreaReviewer.setDisabledTextColor(new java.awt.Color(0, 0, 0));

                // Add reviews to panelReviewer
                for (Map<String, Object> review : reviews) {
                    String reviewerName = (String) review.get("reviewerName");
                    String reviewerSurname = (String) review.get("reviewerSurname");
                    String reviewComment = (String) review.get("reviewComment");

                    // Create components for the review
                    JTextArea reviewArea = new JTextArea();
                    reviewArea.setEditable(false);
                    reviewArea.setLineWrap(true);
                    reviewArea.setWrapStyleWord(true);
                    reviewArea.setText("Revisore: " + reviewerName + " " + reviewerSurname + "\n\n" + reviewComment);
                    reviewArea.setBackground(Color.WHITE);

                    // Add components to the panel
                    panelReviewer.add(reviewArea);
                    panelReviewer.add(new JSeparator());

                    // Add private comments to panelPrivateComment
                    String privateComment = (String) review.get("privateComment");
                    if (privateComment != null && !privateComment.isEmpty()) {
                        JTextArea commentArea = new JTextArea();
                        commentArea.setEditable(false);
                        commentArea.setLineWrap(true);
                        commentArea.setWrapStyleWord(true);
                        commentArea.setText("Revisore: " + reviewerName + " " + reviewerSurname + "\n\n" + privateComment);
                        commentArea.setBackground(Color.WHITE);

                        // Add components to the panel
                        panelPrivateComment.add(commentArea);
                        panelPrivateComment.add(new JSeparator());
                    }
                }

                // Set stars
                int averageRating = 0;
                for (Map<String, Object> review : reviews) {
                    int rating = (int) review.get("rating");
                    averageRating += rating;
                }
                averageRating = averageRating / reviews.size();

                // Hide all the buttons
                toggleButtonStar1.setVisible(false);
                toggleButtonStar2.setVisible(false);
                toggleButtonStar3.setVisible(false);
                toggleButtonStar4.setVisible(false);
                toggleButtonStar5.setVisible(false);
                // Disable all toggle buttons
                toggleButtonStar1.setEnabled(false);
                toggleButtonStar2.setEnabled(false);
                toggleButtonStar3.setEnabled(false);
                toggleButtonStar4.setEnabled(false);
                toggleButtonStar5.setEnabled(false);

                // Set rating
                if (averageRating >= 1) {
                    toggleButtonStar1.setVisible(true);
                }
                if (averageRating >= 2) {
                    toggleButtonStar2.setVisible(true);
                }
                if (averageRating >= 3) {
                    toggleButtonStar3.setVisible(true);
                }
                if (averageRating >= 4) {
                    toggleButtonStar4.setVisible(true);
                }
                if (averageRating >= 5) {
                    toggleButtonStar5.setVisible(true);
                }
            }
        } else {
            // Hide all the unused fields
            labelRating.setVisible(false);
            toggleButtonStar1.setVisible(false);
            toggleButtonStar2.setVisible(false);
            toggleButtonStar3.setVisible(false);
            toggleButtonStar4.setVisible(false);
            toggleButtonStar5.setVisible(false);
            labelReview.setVisible(false);
            scrollPanelReview.setVisible(false);
            labelPrivateComment.setVisible(false);
            scroolPanelPrivateComment.setVisible(false);

            // Insert reviewers from reviewers field
            List<String> reviewers = (List<String>) paperInfo.get("reviewers");
            StringBuilder reviewersText = new StringBuilder();
            for (String reviewer : reviewers) {
                reviewersText.append(reviewer).append("\n");
            }
            textAreaReviewer.setText(reviewersText.toString());
        }

        this.setVisible(true);
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
        labelAuthor = new javax.swing.JLabel();
        fieldPaperTitle = new javax.swing.JTextField();
        labelRating = new javax.swing.JLabel();
        toggleButtonStar1 = new javax.swing.JToggleButton();
        toggleButtonStar2 = new javax.swing.JToggleButton();
        toggleButtonStar3 = new javax.swing.JToggleButton();
        toggleButtonStar4 = new javax.swing.JToggleButton();
        toggleButtonStar5 = new javax.swing.JToggleButton();
        scroolPanelPrivateComment = new javax.swing.JScrollPane();
        panelPrivateComment = new javax.swing.JPanel();
        scrollPanelReview = new javax.swing.JScrollPane();
        panelReviewer = new javax.swing.JPanel();
        labelReview = new javax.swing.JLabel();
        labelPrivateComment = new javax.swing.JLabel();
        scroolPanelReviewer = new javax.swing.JScrollPane();
        textAreaReviewer = new javax.swing.JTextArea();
        labelReviewer = new javax.swing.JLabel();
        labelPaperTitle1 = new javax.swing.JLabel();
        fieldAuthor = new javax.swing.JTextField();
        acceptButton = new javax.swing.JButton();
        rejectButton = new javax.swing.JButton();

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
        labelTitle.setText("VERSIONE FINALE");

        javax.swing.GroupLayout panelTitleLayout = new javax.swing.GroupLayout(panelTitle);
        panelTitle.setLayout(panelTitleLayout);
        panelTitleLayout.setHorizontalGroup(
                panelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelTitleLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                                .addContainerGap())
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

        labelAuthor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelAuthor.setText("Autore");

        fieldPaperTitle.setEnabled(false);
        fieldPaperTitle.setMaximumSize(new java.awt.Dimension(64, 22));
        fieldPaperTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldPaperTitleActionPerformed(evt);
            }
        });

        labelRating.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        labelRating.setText("Valutazione:");

        toggleButtonStar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-star-24.png"))); // NOI18N
        toggleButtonStar1.setBorder(null);
        toggleButtonStar1.setBorderPainted(false);
        toggleButtonStar1.setContentAreaFilled(false);
        toggleButtonStar1.setFocusPainted(false);
        toggleButtonStar1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-christmas-star-24.png"))); // NOI18N

        toggleButtonStar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-star-24.png"))); // NOI18N
        toggleButtonStar2.setBorder(null);
        toggleButtonStar2.setBorderPainted(false);
        toggleButtonStar2.setContentAreaFilled(false);
        toggleButtonStar2.setFocusPainted(false);
        toggleButtonStar2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-christmas-star-24.png"))); // NOI18N

        toggleButtonStar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-star-24.png"))); // NOI18N
        toggleButtonStar3.setBorder(null);
        toggleButtonStar3.setBorderPainted(false);
        toggleButtonStar3.setContentAreaFilled(false);
        toggleButtonStar3.setFocusPainted(false);
        toggleButtonStar3.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-christmas-star-24.png"))); // NOI18N

        toggleButtonStar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-star-24.png"))); // NOI18N
        toggleButtonStar4.setBorder(null);
        toggleButtonStar4.setBorderPainted(false);
        toggleButtonStar4.setContentAreaFilled(false);
        toggleButtonStar4.setFocusPainted(false);
        toggleButtonStar4.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-christmas-star-24.png"))); // NOI18N

        toggleButtonStar5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-star-24.png"))); // NOI18N
        toggleButtonStar5.setBorder(null);
        toggleButtonStar5.setBorderPainted(false);
        toggleButtonStar5.setContentAreaFilled(false);
        toggleButtonStar5.setFocusPainted(false);
        toggleButtonStar5.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-christmas-star-24.png"))); // NOI18N

        panelPrivateComment.setBackground(new java.awt.Color(255, 255, 255));
        panelPrivateComment.setLayout(new javax.swing.BoxLayout(panelPrivateComment, javax.swing.BoxLayout.Y_AXIS));
        scroolPanelPrivateComment.setViewportView(panelPrivateComment);

        panelReviewer.setBackground(new java.awt.Color(255, 255, 255));
        panelReviewer.setLayout(new javax.swing.BoxLayout(panelReviewer, javax.swing.BoxLayout.Y_AXIS));
        scrollPanelReview.setViewportView(panelReviewer);

        labelReview.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelReview.setText("Revisioni");

        labelPrivateComment.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelPrivateComment.setText("Commenti Privati");

        textAreaReviewer.setEditable(false);
        textAreaReviewer.setColumns(20);
        textAreaReviewer.setRows(5);
        textAreaReviewer.setEnabled(false);
        scroolPanelReviewer.setViewportView(textAreaReviewer);

        labelReviewer.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelReviewer.setText("Revisori");

        labelPaperTitle1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelPaperTitle1.setText("Titolo paper");

        fieldAuthor.setEnabled(false);
        fieldAuthor.setMaximumSize(new java.awt.Dimension(64, 22));
        fieldAuthor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldAuthorActionPerformed(evt);
            }
        });

        acceptButton.setBackground(new java.awt.Color(102, 255, 51));
        acceptButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        acceptButton.setText("ACCETTA");
        acceptButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        acceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptButtonActionPerformed(evt);
            }
        });

        rejectButton.setBackground(new java.awt.Color(255, 0, 0));
        rejectButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        rejectButton.setText("RIFIUTA");
        rejectButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        rejectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rejectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBackgroundLayout = new javax.swing.GroupLayout(panelBackground);
        panelBackground.setLayout(panelBackgroundLayout);
        panelBackgroundLayout.setHorizontalGroup(
                panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBarra, javax.swing.GroupLayout.DEFAULT_SIZE, 899, Short.MAX_VALUE)
                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(114, 114, 114)
                                                .addComponent(labelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(labelCMS))
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(32, 32, 32)
                                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(scroolPanelReviewer, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(labelPaperTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(labelAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(labelReviewer, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                                                .addComponent(labelRating, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(toggleButtonStar1)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(toggleButtonStar2)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(toggleButtonStar3)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(toggleButtonStar4)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(toggleButtonStar5)
                                                                                .addGap(179, 179, 179))
                                                                        .addComponent(labelReview, javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(scrollPanelReview, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(25, 25, 25)
                                                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(scroolPanelPrivateComment, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(labelPrivateComment)))
                                                        .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                .addComponent(fieldAuthor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(fieldPaperTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)))))
                                .addContainerGap(22, Short.MAX_VALUE))
                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                .addGap(175, 175, 175)
                                .addComponent(acceptButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(rejectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(181, 181, 181))
        );
        panelBackgroundLayout.setVerticalGroup(
                panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelBackgroundLayout.createSequentialGroup()
                                                .addGap(15, 15, 15)
                                                .addComponent(labelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBackgroundLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(labelCMS)
                                                .addGap(26, 26, 26)))
                                .addComponent(panelBarra, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(labelPaperTitle1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fieldPaperTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelAuthor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fieldAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(labelRating, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(toggleButtonStar1))
                                        .addComponent(toggleButtonStar2)
                                        .addComponent(toggleButtonStar3)
                                        .addComponent(toggleButtonStar4)
                                        .addComponent(toggleButtonStar5))
                                .addGap(39, 39, 39)
                                .addComponent(labelReviewer)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scroolPanelReviewer, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelReview)
                                        .addComponent(labelPrivateComment))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(scrollPanelReview, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(scroolPanelPrivateComment, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(33, 33, 33)
                                .addGroup(panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(acceptButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(rejectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(22, 22, 22))
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
        this.dispose();
    }

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
        control.showConferenceDashboardView();
        this.dispose();
    }

    private void fieldPaperTitleActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void fieldAuthorActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void acceptButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(control.acceptFinalVersion(paperId)) {
            this.dispose();
        }
    }

    private void rejectButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(control.rejectFinalVersion(paperId)) {
            this.dispose();
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JButton acceptButton;
    private javax.swing.JButton backButton;
    private javax.swing.JTextField fieldAuthor;
    private javax.swing.JTextField fieldPaperTitle;
    private javax.swing.JButton homeButton;
    private javax.swing.JLabel labelAuthor;
    private javax.swing.JLabel labelCMS;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JLabel labelPaperTitle1;
    private javax.swing.JLabel labelPrivateComment;
    private javax.swing.JLabel labelRating;
    private javax.swing.JLabel labelReview;
    private javax.swing.JLabel labelReviewer;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JPanel panelBackground;
    private javax.swing.JPanel panelBarra;
    private javax.swing.JPanel panelPrivateComment;
    private javax.swing.JPanel panelReviewer;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JButton rejectButton;
    private javax.swing.JScrollPane scrollPanelReview;
    private javax.swing.JScrollPane scroolPanelPrivateComment;
    private javax.swing.JScrollPane scroolPanelReviewer;
    private javax.swing.JTextArea textAreaReviewer;
    private javax.swing.JToggleButton toggleButtonStar1;
    private javax.swing.JToggleButton toggleButtonStar2;
    private javax.swing.JToggleButton toggleButtonStar3;
    private javax.swing.JToggleButton toggleButtonStar4;
    private javax.swing.JToggleButton toggleButtonStar5;
    // End of variables declaration
}