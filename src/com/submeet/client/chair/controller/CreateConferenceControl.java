package com.submeet.client.chair.controller;

import com.submeet.client.account.controller.HomeControl;
import com.submeet.client.chair.view.ConferenceCreationView;
import com.submeet.client.common.ErrorPopupView;
import com.submeet.client.common.SuccessPopupView;
import com.submeet.dbmsboundary.DBMSBoundary;

import java.util.Date;

public class CreateConferenceControl {
    private ConferenceCreationView conferenceCreationView;
    private HomeControl homeControl;

    public CreateConferenceControl(HomeControl homeControl) {
        this.homeControl = homeControl;
        this.conferenceCreationView = new ConferenceCreationView(this);

        conferenceCreationView.setVisible(true);
    }

    public void returnToHome() {
        conferenceCreationView.dispose();
        homeControl.returnToHome();
    }

    public void goBack() {
        conferenceCreationView.dispose();
        homeControl.navigateToChairView();
    }

    public void createConference(String conferenceName, String interestAreas, String languages, int maxReviewers, Date submissionDate, Date reviewDate, Date finalVersionDate, Date conferenceDate) {
        // Check data validity
        if (!checkConferenceData(conferenceName, interestAreas, languages, submissionDate, reviewDate, finalVersionDate, conferenceDate)) {
            return;
        }

        // Split interest areas and languages by semicolon
        String[] specializationArray = interestAreas.split(";");
        String[] languageArray = languages.split(";");

        // Create conference using DBMSBoundary
        boolean success = DBMSBoundary.createConference(
                conferenceName,
                maxReviewers,
                new java.sql.Date(conferenceDate.getTime()),
                new java.sql.Date(submissionDate.getTime()),
                new java.sql.Date(reviewDate.getTime()),
                new java.sql.Date(finalVersionDate.getTime()),
                new java.sql.Date(submissionDate.getTime()), // TODO: fixare (non urgente)
                new java.sql.Date(reviewDate.getTime()),     // TODO: fixare (non urgente)
                specializationArray,
                languageArray
        );

        if (success) {
            // Show success message using SuccessPopupView
            new SuccessPopupView("Conferenza creata con successo!");

            // Close the view and return to HomeChairView
            conferenceCreationView.dispose();
            homeControl.navigateToChairView();
        } else {
            // Show error message using ErrorPopupView
            new ErrorPopupView("Errore durante la creazione della conferenza.");
        }
    }

    public boolean checkConferenceData(String conferenceName, String interestAreas, String languages, Date submissionDate, Date reviewDate, Date finalVersionDate, Date conferenceDate) {
        // Check if required fields are empty
        if (conferenceName == null || conferenceName.trim().isEmpty()) {
            new ErrorPopupView("Il nome della conferenza non può essere vuoto.");
            return false;
        }

        if (interestAreas == null || interestAreas.trim().isEmpty()) {
            new ErrorPopupView("Le aree specialistiche non possono essere vuote.");
            return false;
        }

        if (languages == null || languages.trim().isEmpty()) {
            new ErrorPopupView("Le lingue non possono essere vuote.");
            return false;
        }

        // Check date hierarchy: submissionDate < reviewDate < finalVersionDate < conferenceDate
        if (submissionDate.equals(reviewDate)) {
            new ErrorPopupView("La data di scadenza delle sottomissioni non può essere uguale alla data di scadenza delle revisioni.");
            return false;
        }
        if (!submissionDate.before(reviewDate)) {
            new ErrorPopupView("La data di scadenza delle sottomissioni deve essere precedente alla data di scadenza delle revisioni.");
            return false;
        }

        if (reviewDate.equals(finalVersionDate)) {
            new ErrorPopupView("La data di scadenza delle revisioni non può essere uguale alla data di scadenza delle versioni finali.");
            return false;
        }
        if (!reviewDate.before(finalVersionDate)) {
            new ErrorPopupView("La data di scadenza delle revisioni deve essere precedente alla data di scadenza delle versioni finali.");
            return false;
        }

        if (finalVersionDate.equals(conferenceDate)) {
            new ErrorPopupView("La data di scadenza delle versioni finali non può essere uguale alla data della conferenza.");
            return false;
        }
        if (!finalVersionDate.before(conferenceDate)) {
            new ErrorPopupView("La data di scadenza delle versioni finali deve essere precedente alla data della conferenza.");
            return false;
        }

        return true;
    }
}
