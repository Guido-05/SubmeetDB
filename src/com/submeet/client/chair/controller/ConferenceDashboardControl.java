package com.submeet.client.chair.controller;

import com.submeet.client.account.controller.HomeControl;
import com.submeet.client.chair.view.*;
import com.submeet.client.common.ErrorPopupView;
import com.submeet.client.common.SuccessPopupView;
import com.submeet.client.entity.EntityConference;
import com.submeet.client.entity.EntityPaper;
import com.submeet.client.entity.EntityUser;
import com.submeet.client.utility.DeadlineValidator;
import com.submeet.client.utility.EmailSender;
import com.submeet.client.utility.SystemNotification;
import com.submeet.dbmsboundary.DBMSBoundary;

import java.awt.print.Paper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class ConferenceDashboardControl {

    private EntityConference conference;
    private ChairConferenceDashboardView chairConferenceDashboardView;
    private HomeControl homeControl;

    /**
     * Constructor that initializes the control with a conference ID,
     * retrieves the conference data, and creates the view
     * 
     * @param conferenceId The ID of the conference to display
     */
    public ConferenceDashboardControl(int conferenceId, HomeControl homeControl) {
        // Set home control
        this.homeControl = homeControl;

        // Get conference data from the database
        this.conference = DBMSBoundary.getConference(conferenceId);

        // Create and show the conference dashboard view
        if (this.conference != null) {
            this.chairConferenceDashboardView = new ChairConferenceDashboardView(this, this.conference);
        } else {
            System.err.println("Failed to load conference with ID: " + conferenceId);
        }

        // Set up buttons listeners
        this.setupButtonListeners();

    }

    private void setupButtonListeners() {
        // Set up the home and back button listeners
        // TODO: togliere listner e chiamare i metodi dalla view
        chairConferenceDashboardView.setButtonAssignPapersListener(e -> paperAssignment());
        chairConferenceDashboardView.setButtonInvitePCMemberListener(e -> invitePCMembers());
        chairConferenceDashboardView.setButtonInviteEditorListener(e -> inviteEditor());
    }

    public void openPaperInReviewView(int paperId) {
        // Get the paper information from the database
        Map<String, Object> paperInfo = DBMSBoundary.getReviewedPaper(paperId);

        if (paperInfo != null) {
            // Create and show the reviewed paper view
            PaperInReviewView paperInReviewView = new PaperInReviewView(this, paperInfo);
            paperInReviewView.setVisible(true);
            chairConferenceDashboardView.setVisible(false);
        }
    }

    public void openFinalVersionView(int paperId) {
        // Get the paper information from the database
        Map<String, Object> paperInfo = DBMSBoundary.getReviewedPaper(paperId);

        if (paperInfo != null) {
            // Create and show the reviewed paper view
            FinalVersionView finalVersionView = new FinalVersionView(this, paperInfo, paperId);
            finalVersionView.setVisible(true);
            chairConferenceDashboardView.setVisible(false);
        }
    }

    public void openPaperToBeReviewedView(int paperId) {
        // Get the paper information from the database
        Map<String, String> paperInfo = DBMSBoundary.getPaperToBeReviewed(paperId);

        if (paperInfo != null) {
            // Create and show the paper to be reviewed view
            PaperToBeReviewedView paperToBeReviewedView = new PaperToBeReviewedView(this, paperId);
            // Set paper title
            if (paperInfo.containsKey("title")) {
                paperToBeReviewedView.setPaperTitle(paperInfo.get("title"));
            }
            // Set author name
            if (paperInfo.containsKey("name") && paperInfo.containsKey("surname")) {
                String authorName = paperInfo.get("name");
                String authorSurname = paperInfo.get("surname");
                paperToBeReviewedView.setAuthorName(authorName + " " + authorSurname);
            }

            paperToBeReviewedView.setVisible(true);
            chairConferenceDashboardView.setVisible(false);  // TODO: provare a cambiare con dispose
        }
    }

    public void returnToHome() {
        chairConferenceDashboardView.dispose();
        homeControl.returnToHome();
    }

    public void goBack() {
        chairConferenceDashboardView.dispose();
        homeControl.navigateToChairView();
    }

    public void showConferenceDashboardView() {
        chairConferenceDashboardView.setVisible(true);
    }

    public void paperAssignment() {
        new PaperAssignmentView(chairConferenceDashboardView, this);
    }

    public void downloadLog() {
        InputStream dump = DBMSBoundary.getConferenceLog(conference.getConferenceId());
        try {
            String fileName = "log_conferenza_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";

            String userHome = System.getProperty("user.home");
            Path downloadPath = Paths.get(userHome, "Downloads", fileName);

            Files.copy(dump, downloadPath, StandardCopyOption.REPLACE_EXISTING);
            SystemNotification.showNotification("Submeet", "log conferenza scaricato in " + downloadPath.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void invitePCMembers() {
        // Check if invitation sending is allowed based on deadline
        if (!DeadlineValidator.canSendInvitation(conference)) {
            return; // Deadline validation failed, error popup already shown
        }

        // Get the list of users from the database
        ArrayList<EntityUser> userList = DBMSBoundary.getUserList(conference.getConferenceId(), "Reviewer");

        if (userList != null && !userList.isEmpty()) {
            // Create the PC member invite view
            new PCMemberInviteView(this, chairConferenceDashboardView, userList);

        } else {
            // Show error message if no users are available
            new ErrorPopupView("Non sono disponibili utenti per l'invito.");
        }
    }

    private void inviteEditor() {
        // Check if invitation sending is allowed based on deadline
        if (!DeadlineValidator.canSendInvitation(conference)) {
            return; // Deadline validation failed, error popup already shown
        }

        // Check if the conference has already an editor
        if(!DBMSBoundary.getConferenceEditor(conference.getConferenceId()))
        {
            // Get the list of users from the database
            ArrayList<EntityUser> userList = DBMSBoundary.getUserList(conference.getConferenceId(), "Editor");

            if (userList != null && !userList.isEmpty()) {
                // Create the editor invite view
                new EditorInviteView(this, chairConferenceDashboardView, userList);

            } else {
                // Show error message if no users are available
                new ErrorPopupView("Non sono disponibili utenti per l'invito.");
            }
        } else {
            new ErrorPopupView("La conferenza ha già un editore.");
        }

    }


    public void sendPCMemberInvite(LinkedList<Integer> selectedUsers, LinkedList<String> selectedEmails)
    {
        // Check if invitation sending is allowed based on deadline
        if (!DeadlineValidator.canSendInvitation(conference)) {
            return; // Deadline validation failed, error popup already shown
        }

        if (!selectedUsers.isEmpty()) {
            // Create PC member invites in the database
            boolean invitesCreated = DBMSBoundary.createPCMemberInvite(
                    new LinkedList<>(selectedUsers), // Create a copy to avoid modifying the original
                    conference.getConferenceId(),
                    conference.getTitle()
            );

            // Send emails to the selected users
            if (invitesCreated) {
                String subject = "Invito a partecipare come membro del PC alla conferenza " + conference.getTitle();
                String content = "Sei stato invitato a partecipare come membro del PC alla conferenza " +
                        conference.getTitle() + ".\n\nAccedi alla piattaforma SubMeet per accettare o rifiutare l'invito.";

                for (String email : selectedEmails) {
                    EmailSender.sendEmail(email, subject, content);
                }

                // Close the invite view
                new SuccessPopupView("Inviti ai membri del PC creati con successo.");
            } else {
                // Show error message
                new ErrorPopupView("Errore nella creazione degli inviti ai membri del PC.");
            }
        } else {
            // Show error message if no users are selected
            new ErrorPopupView("Nessun utente selezionato.");
        }
    }


    public void createEditorInvite(int selectedUserId, String selectedEmail) {
        // Check if invitation sending is allowed based on deadline
        if (!DeadlineValidator.canSendInvitation(conference)) {
            return; // Deadline validation failed, error popup already shown
        }

        if (selectedUserId != -1) {
            // Create editor invite in the database
            boolean inviteCreated = DBMSBoundary.createEditorInvite(
                    selectedUserId,
                    conference.getConferenceId(),
                    conference.getTitle()
            );

            // Send email to the selected user
            if (inviteCreated) {
                String subject = "Invito a partecipare come editore alla conferenza " + conference.getTitle();
                String content = "Sei stato invitato a partecipare come editore alla conferenza " +
                        conference.getTitle() + ".\n\nAccedi alla piattaforma SubMeet per accettare o rifiutare l'invito.";

                EmailSender.sendEmail(selectedEmail, subject, content);

                // Close the invite view
                new SuccessPopupView("Invito all'editore creato con successo.");
            } else {
                // Show error message
                new ErrorPopupView("Errore nella creazione dell'invito all'editore.");
            }
        } else {
            // Show error message if no user is selected
            new ErrorPopupView("Nessun utente selezionato.");
        }
    }

    /**
     * Returns the conference entity
     * 
     * @return The conference entity
     */
    public EntityConference getConference() {
        return this.conference;
    }

    /**
     * Assigns reviewers to a paper
     * @param paperId The paper ID
     * @param reviewersId The list of reviewer IDs
     */
    public boolean assignReviewersToPaper(int paperId, LinkedList<Integer> reviewersId) {
        // Check if paper assignment is allowed based on deadline
        if (!DeadlineValidator.canAssignPaper(conference)) {
            return false; // Deadline validation failed, error popup already shown
        }

        return DBMSBoundary.insertPaperReviewers(paperId, reviewersId);
    }

    /**
     * Opens the manual paper assignment view
     * @param paperId The paper ID
     */
    public void manualPaperAssignment(int paperId) {
        ArrayList<EntityUser> reviewers = DBMSBoundary.getConferencePCMembers(conference.getConferenceId());

        if (reviewers != null && !reviewers.isEmpty()) {
            new ManualPaperAssignmentView(this, reviewers, paperId);
        } else {
            new ErrorPopupView("Non ci sono revisori disponibili per questa conferenza.");
        }
    }

    public boolean acceptFinalVersion(int paperId) {
        if(DBMSBoundary.acceptPaper(paperId)) {
            new SuccessPopupView("Paper accettato con successo.");
            showConferenceDashboardView();
            return true;
        } else {
            new ErrorPopupView("Errore durante l'accettazione del paper.");
        }
        return false;
    }

    public boolean rejectFinalVersion(int paperId) {
        if(DBMSBoundary.rejectPaper(paperId)) {
            new SuccessPopupView("Paper rifiutato con successo.");
            showConferenceDashboardView();
            return true;
        } else {
            new ErrorPopupView("Errore durante il rifiuto del paper.");
        }
        return false;
    }

    public void assignPaper(String methodName) {
        // Check if paper assignment is allowed based on deadline
        if (!DeadlineValidator.canAssignPaper(conference)) {
            return; // Deadline validation failed, error popup already shown
        }

        ArrayList<EntityPaper> papers = DBMSBoundary.getUnassignedPaper(conference.getConferenceId());
        ArrayList<EntityUser> reviewers = DBMSBoundary.getConferencePCMembers(conference.getConferenceId());
        int maxReviewersPerPaper = Math.min(conference.getReviewerForPaper(), reviewers.size());

        for (EntityPaper paper : papers) {
            int paperId = paper.getPaperId();
            List<String> paperSpecs = DBMSBoundary.getPaperSpecializations(paperId);

            Map<Integer, Integer> reviewerScores = new HashMap<>();

            for (EntityUser reviewer : reviewers) {
                int reviewerId = reviewer.getUserId();

                if (DBMSBoundary.getReviewerConflict(reviewerId, paperId)) {
                    continue; // Skip reviewer if conflict
                }

                int score = 0;

                switch (methodName) {
                    case "MECCANISMO DI OFFERTA" -> {
                        Integer pref = DBMSBoundary.getReviewerPreference(reviewerId, paperId);
                        if (pref != null) score = pref;
                    }
                    case "ASSEGNAZIONE PER COMPETENZE" -> {
                        List<String> reviewerSpecs = DBMSBoundary.getReviewerSpecializations(reviewerId);
                        for (String s : paperSpecs) {
                            if (reviewerSpecs.contains(s)) {
                                score++;
                            }
                        }
                    }
                    case "ASSEGNAZIONE PER PAROLA CHIAVE" -> {
                        String paperTitle = paper.getTitle().toLowerCase();
                        for (String s : paperSpecs) {
                            if (paperTitle.contains(s)) {
                                score++;
                            }
                        }
                    }
                }

                // Subtract the number of reviews from the score
                score -= DBMSBoundary.getReviewListCount(reviewerId, conference.getConferenceId());

                // Add review to list
                reviewerScores.put(reviewerId, score);
            }

            // Ordina per punteggio decrescente e prendi max N
            LinkedList<Integer> topReviewers = reviewerScores.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                    .limit(maxReviewersPerPaper)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(LinkedList::new));

            if (!topReviewers.isEmpty()) {
                assignReviewersToPaper(paperId, topReviewers);
            }
        }

        new SuccessPopupView("Assegnazione completata con successo usando: " + methodName);

        // Update dashboard with new data
        conference = DBMSBoundary.getConference(conference.getConferenceId());
        chairConferenceDashboardView.dispose();
        chairConferenceDashboardView = new ChairConferenceDashboardView(this, conference);
    }
}
