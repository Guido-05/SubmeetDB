package com.submeet.client.editore.controller;

import com.submeet.client.account.controller.HomeControl;
import com.submeet.client.common.ErrorPopupView;
import com.submeet.client.common.SuccessPopupView;
import com.submeet.client.editore.view.EditorConferenceDashboardView;
import com.submeet.client.entity.EntityConference;
import com.submeet.client.utility.SystemNotification;
import com.submeet.dbmsboundary.DBMSBoundary;

import java.util.List;
import java.util.Map;

public class EditorConferenceDashboardControl {
    private EntityConference conference;
    private HomeControl homeControl;
    List<Map<String, Object>> finalVersions;

    public HomeControl getHomeControl() {
        return homeControl;
    }

    public EditorConferenceDashboardControl(EntityConference conference, List<Map<String, Object>> finalVersions, HomeControl homeControl) {
        this.conference = conference;
        this.homeControl = homeControl;
        this.finalVersions = finalVersions;

        this.showEditorConferenceDashboardView();
    }

    public void showEditorConferenceDashboardView() {

        new EditorConferenceDashboardView(conference, finalVersions, this);
    }

    public boolean downloadPaper(int paperId, String paperTitle ) {
        // TODO: chiamarlo saveArticle
        try {
            // Get the paper data from the database
            java.io.InputStream paperStream = DBMSBoundary.getPaperFile(paperId);

            if (paperStream == null) {
                return false;
            }

            // Create a file chooser dialog
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Save Paper");

            // Set default file name
            fileChooser.setSelectedFile(new java.io.File("paper_" + paperId + "_" + paperTitle.replaceAll(" ", "_") + ".pdf"));

            // Show the dialog and get the result
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();

                // Save the file
                try (java.io.FileOutputStream outputStream = new java.io.FileOutputStream(fileToSave)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = paperStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    SystemNotification.showNotification("Submeet Download", "Il paper " + paperTitle + " è stato salvato con successo.");

                    return true;
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        SystemNotification.showNotification("Submeet Download", "Errore durante il download del paper.");

        return false;
    }

    public boolean uploadTemplate() {
        try {
            // Create a file chooser dialog
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Select Template File");

            // Show the dialog and get the result
            int userSelection = fileChooser.showOpenDialog(null);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File fileToUpload = fileChooser.getSelectedFile();

                // Upload the template to the database
                boolean success = DBMSBoundary.uploadTemplateFile(conference.getConferenceId(), fileToUpload);

                if (success) {
                    new SuccessPopupView("Template caricato con successo.");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new ErrorPopupView("Errore durante il caricamento del template.");
        return false;
    }


}
