package com.submeet.client.autore.controller;

import com.submeet.client.account.controller.HomeControl;
import com.submeet.client.autore.view.*;
import com.submeet.client.common.ErrorPopupView;
import com.submeet.client.common.SuccessPopupView;
import com.submeet.client.entity.EntityConference;
import com.submeet.client.entity.EntityReview;
import com.submeet.client.utility.AppSession;
import com.submeet.client.utility.DeadlineValidator;
import com.submeet.dbmsboundary.DBMSBoundary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


public class AuthorConferenceControl {

    private HomeControl homeControl;

    public AuthorConferenceControl(HomeControl homeControl) {
        this.homeControl = homeControl;
    }

    /**
     * Creates and shows the SubmitPaperView for submitting a new paper
     * @param conferenceId The ID of the conference
     */
    public void showSubmitPaperView(int conferenceId) {
        // Get conference information
        EntityConference conference = DBMSBoundary.getConference(conferenceId);

        // Get specializations from the database
        List<Map<Integer, String>> specializations = DBMSBoundary.getSpecializations();

        // Create and show the submit paper view
        SubmitPaperView submitPaperView = new SubmitPaperView(homeControl, this, conference, specializations);
        submitPaperView.setVisible(true);
    }

    /**
     * Submits a new paper to the database
     * @param userId The ID of the user submitting the paper
     * @param conferenceId The ID of the conference
     * @param title The title of the paper
     * @param description The description of the paper
     * @param specializations The specializations as a comma-separated string of IDs
     * @param paperFile The file containing the paper
     */
    public boolean submitPaper(int userId, int conferenceId, String title, String description, String specializations, File paperFile) {
        // Get conference information to check deadline
        EntityConference conference = DBMSBoundary.getConference(conferenceId);

        // Check if paper submission is allowed based on deadline
        if (!DeadlineValidator.canSubmitPaper(conference)) {
            return false; // Deadline validation failed, error popup already shown
        }

        try {
            // Read the file into a byte array
            FileInputStream fileInputStream = new FileInputStream(paperFile);
            byte[] fileBytes = new byte[(int) paperFile.length()];
            fileInputStream.read(fileBytes);
            fileInputStream.close();

            // Insert the paper into the database
            boolean success = DBMSBoundary.insertPaper(userId, conferenceId, title, description, specializations, fileBytes);

            if (success) {
                new SuccessPopupView("Paper inviato con successo!");
                // Show the conference info view again
                showParticipationConferenceInfo(conferenceId);
                return true;
            } else {
                new ErrorPopupView("Errore durante l'invio del paper.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            new ErrorPopupView("Errore durante la lettura del file: " + e.getMessage());
        }
        return false;
    }

    public void getTemplate(int conferenceId)
    {
        // Definisci una directory dove salvare i template in modo permanente
        File templatesDir = new File("conference_templates");
        if (!templatesDir.exists()) {
            templatesDir.mkdir(); // Crea la directory se non esiste
        }

        InputStream input = DBMSBoundary.getTemplate(conferenceId);

        if (input == null) {
            new ErrorPopupView("Nessun template trovato per la conferenza");
            return;
        }

        // Crea un file con nome basato sull'ID della conferenza
        File outputFile = new File(templatesDir, "template_" + conferenceId + ".pdf");

        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            new SuccessPopupView("Template salvato con successo.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newPartecipation(int userId, int conferenceId) {
        if(DBMSBoundary.insertAuthor(userId, conferenceId)) {
            new SuccessPopupView("Partecipazione avvenuta con successo.");
            homeControl.navigateToAuthorConferenceView(this);
        } else {
            new ErrorPopupView("Errore la partecipazione partecipazione.");
        }
    }

    public void showConferenceInfo(int conferenceId) {
        // Ottieni le informazioni della conferenza dal database
        EntityConference conference = DBMSBoundary.getConference(conferenceId);

        // Crea e mostra la vista con le informazioni della conferenza
        NewConferenceInfoView infoView = new NewConferenceInfoView(homeControl, this, conference);
        infoView.setVisible(true);
    }

    public void showParticipationConferenceInfo(int conferenceId) {
        // Ottieni le informazioni della conferenza dal database
        EntityConference conference = DBMSBoundary.getConference(conferenceId);

        // Crea e mostra la vista con le informazioni della conferenza
        ConferenceInfoView infoView = new ConferenceInfoView(homeControl, this, conference);
        infoView.setVisible(true);
    }

    public void showPaperDashboardView(int conferenceId, String conferenceTitle) {
        List<Map<String, Object>> paperList = DBMSBoundary.getUserPapers(AppSession.getInstance().getUserId(), conferenceId);

        PaperDashboardView paperDashboardView = new PaperDashboardView(homeControl, this, paperList, conferenceTitle, conferenceId);
        paperDashboardView.setVisible(true);
    }

    public void showPaperInfoView(int paperId, int conferenceId, String conferenceTitle, String paperState) {
        // TODO: c'è qualcosa che non va
        List<EntityReview> reviews = DBMSBoundary.getPaperInfo(paperId);  // TODO: non mi piace questo nome

        PaperInfoView paperInfoView = new PaperInfoView(homeControl, this, reviews, conferenceId, conferenceTitle, paperId, paperState);
        paperInfoView.setVisible(true);
    }

    public boolean retirePaper(int paperId, int conferenceid, String conferenceTitle ) {
        boolean confirmed = ConfirmPopupView.show("Sei sicuro di voler ritirare il paper?");
        if(confirmed) {
            if(DBMSBoundary.removePaper(paperId)) {
                new SuccessPopupView("Paper ritirato.");
                showPaperDashboardView(conferenceid, conferenceTitle );
                return true;
            }
            else {
                new ErrorPopupView("Errore durante la rimozione del paper.");
                return false;
            }
        }
        return false;
    }

    public boolean submitFinalVersion(int paperId, File finalVersionFile) {
        // Get conference information from paper to check deadline
        int conferenceId = DBMSBoundary.getConferenceIdFromPaper(paperId);
        EntityConference conference = DBMSBoundary.getConference(conferenceId);

        // Check if final version submission is allowed based on deadline
        if (!DeadlineValidator.canSubmitFinalVersion(conference)) {
            return false; // Deadline validation failed, error popup already shown
        }

        if(DBMSBoundary.updatePaperFinalVersion(paperId, finalVersionFile))
        {
            new SuccessPopupView("Versione finale inviata con successo.");
            return true;
        }
        else {
            new ErrorPopupView("Errore durante l'invio della versione finale.");
            return false;
        }
    }
}
