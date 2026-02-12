package com.submeet.client.revisore.controller;

import com.submeet.client.account.controller.HomeControl;
import com.submeet.client.common.ErrorPopupView;
import com.submeet.client.common.SuccessPopupView;
import com.submeet.client.entity.EntityConference;
import com.submeet.client.entity.EntityPaper;
import com.submeet.client.entity.EntityReview;
import com.submeet.client.revisore.view.*;
import com.submeet.client.utility.AppSession;
import com.submeet.client.utility.DeadlineValidator;
import com.submeet.client.utility.EmailSender;
import com.submeet.dbmsboundary.DBMSBoundary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviewerConferenceControl {
    private EntityConference conference;
    private HomeControl homeControl;
    private ConferenceView conferenceView;
    private boolean isSubReview = false;

    // Costructor for when is called for a sub review
    public ReviewerConferenceControl(HomeControl homeControl) {
        this.homeControl = homeControl;
        this.isSubReview = true;
    }

    public ReviewerConferenceControl(EntityConference conference, HomeControl homeControl) {
        this.conference = conference;
        this.homeControl = homeControl;

        this.showConferenceView();
    }


    public void showConferenceView() {
        // Create a new ConferenceView
        ConferenceView conferenceView = new ConferenceView();
        this.conferenceView = conferenceView;

        // Update the view with conference information
        updateConferenceView(conferenceView, conference, homeControl);

        // Show the view
        conferenceView.setVisible(true);
    }

    /**
     * Updates the ConferenceView with the conference information
     * @param conferenceView The view to update
     * @param conference The conference information
     * @param homeControl The HomeControl instance for navigation
     */
    private void updateConferenceView(ConferenceView conferenceView, EntityConference conference, HomeControl homeControl) {
        // TODO spostare questa logica dentro la classe updateConferenceView
        // Format dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Set conference name
        conferenceView.setConferenceName(conference.getTitle());

        // Set interest areas (specializations)
        if (conference.getSpecializations() != null && !conference.getSpecializations().isEmpty()) {
            conferenceView.setInterestAreas(String.join(", ", conference.getSpecializations()));
        } else {
            conferenceView.setInterestAreas("N/A");
        }

        // Set conference date
        if (conference.getScheduleDate() != null) {
            conferenceView.setConferenceDate(dateFormat.format(conference.getScheduleDate()));
        } else {
            conferenceView.setConferenceDate("N/A");
        }

        // Set submission expiration date
        if (conference.getSubmissionDeadline() != null) {
            conferenceView.setSubmissionExpirationDate(dateFormat.format(conference.getSubmissionDeadline()));
        } else {
            conferenceView.setSubmissionExpirationDate("N/A");
        }

        // Set review expiration date
        if (conference.getReviewDeadline() != null) {
            conferenceView.setReviewExpirationDate(dateFormat.format(conference.getReviewDeadline()));
        } else {
            conferenceView.setReviewExpirationDate("N/A");
        }

        // Set final version expiration date
        if (conference.getFinalVersionDeadline() != null) {
            conferenceView.setFinalVersionExpirationDate(dateFormat.format(conference.getFinalVersionDeadline()));
        } else {
            conferenceView.setFinalVersionExpirationDate("N/A");
        }

        // Set languages
        if (conference.getLanguages() != null && !conference.getLanguages().isEmpty()) {
            conferenceView.setLanguages(String.join(", ", conference.getLanguages()));
        } else {
            conferenceView.setLanguages("N/A");
        }

        // Set max reviewer per paper
        if (conference.getReviewerForPaper() > 0) {
            conferenceView.setMaxReviewer(String.valueOf(conference.getReviewerForPaper()));
        } else {
            conferenceView.setMaxReviewer("N/A");
        }

        // Set home button action
        conferenceView.setHomeButtonAction(e -> homeControl.returnToHome());

        // Set back button action
        conferenceView.setBackButtonAction(e -> {
            conferenceView.dispose();
            homeControl.navigateToReviewerView();
        });

        conferenceView.setDashboardReviewButtonAction(e -> {
            conferenceView.dispose();
            this.showReviewerDashboardView();
        });
    }

    public EntityConference getConference() {
        return this.conference;
    }

    public void showReviewerDashboardView() {
        List<Map<String, Object>> reviews = DBMSBoundary.getReviewList(AppSession.getInstance().getUserId(), conference.getConferenceId());

        new ReviewerDashboardView(this, reviews);
        this.conferenceView.dispose();
    }

    /**
     * Returns to the home screen
     */
    public void returnToHome() {
        if (homeControl != null) {
            homeControl.returnToHome();
        }
    }

    /**
     * Returns to the reviewer view
     */
    public void returnToReviewerView() {
        if (homeControl != null) {
            homeControl.navigateToReviewerView();
        }
    }


    public void showNewReviewView(String paperTitle, int paperId, int revisionId) {
        new NewReviewView(this, paperTitle, paperId, revisionId);
    }


    public void showCompletedReviewView(int revisionId) {
        // Get the completed review from the database
        EntityReview review = DBMSBoundary.getCompletedReview(revisionId);

        if (review != null) {
            // Create and show the completed review view
            new CompletedReviewView(this, review, this.isSubReview);
        }
    }


    public void showOtherReviewView(int paperId, String paperTitle) {
        Map<String, Object> reviews = DBMSBoundary.getOthersReviewList(paperId);

        if (reviews != null) {
            new OtherReviewView(this, paperTitle, reviews);
        }
    }


    public void showOfferAssignView() {
        ArrayList<EntityPaper> unassignedPapers = DBMSBoundary.getUnassignedPaper(conference.getConferenceId());

        new OfferAssignView(this, unassignedPapers, conference.getTitle());
    }


    public void showOnGoingReviewView(int paperId, String paperTitle, int reviewId, boolean isSubReview) {
        new OnGoingReviewView(this, paperId, paperTitle, reviewId, isSubReview);
    }


    public void showSubReviewerEmailView(NewReviewView newReviewView, int paperId, String paperTitle) {
        List<Map<String, Object>> subReviewerList = DBMSBoundary.getAvailableSubReviewer(paperId);

        new SubReviewerEmailView(newReviewView, subReviewerList, this, paperId, paperTitle);
    }

    public void showDelegatedReviewView(String paperTitle, int delegatorId) {
        int revisionId = DBMSBoundary.getSubReviewId(paperTitle, delegatorId);

        // TODO: penso si possa usare il generico get Review poichè l'id della revisione lo troo precisamente con la query sopra
        Map<String, Object> delegatedReview = DBMSBoundary.getDelegatedReviewInfo(revisionId, AppSession.getInstance().getUserId());

        new DelegatedReviewView(this, delegatedReview);
    }

    /**
     * Downloads a paper file and saves it to the local file system
     * @param paperId The ID of the paper to download
     * @return true if the download was successful, false otherwise
     */
    public boolean downloadPaper(int paperId, String paperTitle ) {
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

                    return true;
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void startReview(int paperId, String paperTitle, int reviewId) {
        if(DBMSBoundary.setReviewStart(reviewId)) {
            new SuccessPopupView("Revisione avviata con successo.");
            this.showOnGoingReviewView(paperId, paperTitle, reviewId, false);
        } else {
            new ErrorPopupView("Errore durante l'avvio della revisione.");
        }
    }

    public boolean submitReview(int reviewerId, int paperId, String reviewComment, int rating, String privateComment, int reviewId) {
        // Check if review submission is allowed based on deadline
        if (!DeadlineValidator.canSubmitReview(conference)) {
            return false; // Deadline validation failed, error popup already shown
        }

        if(DBMSBoundary.updateReview(reviewerId, paperId, reviewComment, rating, privateComment)) {
            new SuccessPopupView("Revisione inviata con successo.");
            this.showCompletedReviewView(reviewId);
            return true;
        } else {
            new ErrorPopupView("Errore durante l'invio della revisione.");
            return false;
        }
    }


    public void sendDelegationInvite(List<Integer> userIds, List<String> subReviewerEmails, int paperId, String paperTitle) {
        for(int i = 0; i < userIds.size(); i++) {
            int userId = userIds.get(i);
            String email = subReviewerEmails.get(i);

            boolean success = DBMSBoundary.createSubReviewerInvite(userId, paperId, paperTitle, AppSession.getInstance().getUserId(), conference.getConferenceId());

            EmailSender.sendEmail(email,
                    "Invito a partecipare come sotto revisore per il paper " + paperTitle,
                    "Sei stato invitato a partecipare come sotto revisore alla conferenza " +
                            conference.getTitle() + ", per efffetture la sottorevisione del paper " + paperTitle +
                            ".\n\nAccedi alla piattaforma SubMeet per accettare o rifiutare l'invito.");

            if(!success) {
                new ErrorPopupView("Errore durante l'invio dell'invito al sottopartecipante.");
                return;
            }
        }
        new SuccessPopupView("Invito al sottopartecipante inviato con successo.");
    }

    public void confirmInterest(List<Integer> paperIds, List<Integer> preferences, List<Boolean> conflicts) {
        if (paperIds == null || preferences == null || conflicts == null
                || paperIds.size() != preferences.size()
                || paperIds.size() != conflicts.size()) {
            throw new IllegalArgumentException("List sizes must match and not be null.");
        }

        boolean allSaved = true;
        for (int i = 0; i < paperIds.size(); i++) {
            int paperId = paperIds.get(i);
            int pref = preferences.get(i);
            boolean conflict = conflicts.get(i);

            // Salvataggio nel database
            boolean saved = DBMSBoundary.updateUserInterest(AppSession.getInstance().getUserId(), paperId, pref, conflict);
            if (!saved) {
                allSaved = false;
                System.err.printf("Errore salvataggio preference paperId=%d%n", paperId);
            }
        }

        if (allSaved) {
            new SuccessPopupView("Preferenze e conflitti salvati correttamente!");
        } else {
            new ErrorPopupView("Si sono verificati errori durante il salvataggio.");
        }

        this.showReviewerDashboardView();
    }
}
