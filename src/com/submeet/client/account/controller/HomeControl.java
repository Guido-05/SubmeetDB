package com.submeet.client.account.controller;

import com.submeet.client.account.view.HomeRouterView;
import com.submeet.client.autore.controller.AuthorConferenceControl;
import com.submeet.client.autore.view.AvailableConferenceView;
import com.submeet.client.autore.view.ConferencePartecipationView;
import com.submeet.client.chair.controller.CreateConferenceControl;
import com.submeet.client.chair.view.HomeChairView;
import com.submeet.client.editore.controller.EditorConferenceDashboardControl;
import com.submeet.client.editore.view.HomeEditorView;
import com.submeet.client.entity.EntityConference;
import com.submeet.client.notifiche.controller.NotificationCenterControl;
import com.submeet.client.revisore.controller.ReviewerConferenceControl;
import com.submeet.client.revisore.view.HomeReviewerView;
import com.submeet.client.utility.AppSession;
import com.submeet.dbmsboundary.DBMSBoundary;

import java.util.List;
import java.util.Map;

public class HomeControl {
    private HomeRouterView homeRouterView;

    public HomeControl() {
    }

    public void createHomeRouterView() {
        homeRouterView = new HomeRouterView();
        setupButtonListeners();
        homeRouterView.setVisible(true);
    }

    public void conferenceCreationDashboard() {
        // Create the ConferenceCreationControl and show the view
        new CreateConferenceControl(this);
        homeRouterView.dispose();
    }

    public void returnToHome() {
        // Create a new HomeRouterView and set it up
        createHomeRouterView();
    }

    private void setupButtonListeners() {
        // Set action listeners for buttons using the methods in HomeRouterView
        homeRouterView.setChairButtonListener(e -> navigateToChairView());
        homeRouterView.setReviewerButtonListener(e -> navigateToReviewerView());
        homeRouterView.setAuthorButtonListener(e -> navigateToAuthorView(new AuthorConferenceControl(this)));
        homeRouterView.setEditorButtonListener(e -> navigateToEditorView());
        homeRouterView.setProfileButtonListener(e -> navigateToUserProfileView());
        homeRouterView.setNotificButtonListener(e -> navigateToNotificationCenterView());
    }

    public void navigateToChairView() {
        // Get the current user ID from the session
        int userId = AppSession.getInstance().getUserId();

        // Get the list of conferences for the current user with the "chair" role
        List<Map<String, Object>> conferenceList = DBMSBoundary.getConferenceList(userId, "Chair");

        // Create the HomeChairView with the conference list
        new HomeChairView(this, conferenceList);
        homeRouterView.dispose();
    }

    public void navigateToReviewerView() {
        // Get the current user ID from the session
        int userId = AppSession.getInstance().getUserId();

        // Get the list of conferences for the current user with the "Reviewer" role
        List<Map<String, Object>> conferenceList = DBMSBoundary.getConferenceList(userId, "Reviewer");

        // Get the list of papers for the current user as a subreviewer
        List<Map<String, Object>> subReviewerPaperList = DBMSBoundary.getSubReviewerPaper(userId);

        // Create the HomeReviewerView with the conference list and subreviewer papers
        new HomeReviewerView(this, conferenceList, subReviewerPaperList);
        homeRouterView.dispose();
    }

    // TODO: cambiare questi nomi
    public void navigateToAuthorView(AuthorConferenceControl authorConferenceControl) {
        int userId = AppSession.getInstance().getUserId();

        List<Map<String, Object>> conferenceList = DBMSBoundary.getAvailableConference(userId);

        AvailableConferenceView authorView = new AvailableConferenceView(this, authorConferenceControl, conferenceList);
        authorView.setConferencePartecipationButton(e -> {
            navigateToAuthorConferenceView(authorConferenceControl);
            authorView.dispose();
        });
        homeRouterView.dispose();
    }

    public void navigateToAuthorConferenceView(AuthorConferenceControl authorConferenceControl) {
        ConferencePartecipationView partecipationView = new ConferencePartecipationView(this, authorConferenceControl, DBMSBoundary.getConferenceList(AppSession.getInstance().getUserId(), "Author"));
        partecipationView.setAvailableConferenceButton(e -> {
            navigateToAuthorView(authorConferenceControl);
            partecipationView.dispose();
        });
    }

    public void navigateToEditorView() {
        int userId = AppSession.getInstance().getUserId();

        List<Map<String, Object>> conferenceList = DBMSBoundary.getConferenceList(userId, "Editor");

        new HomeEditorView(this, conferenceList);
        homeRouterView.dispose();
    }

    public void navigateToUserProfileView() {
        new UserProfileControl();
        homeRouterView.dispose();
    }

    public void navigateToNotificationCenterView() {
        // Get the current user ID from the session
        int userId = AppSession.getInstance().getUserId();

        // Create the notification center controller
        NotificationCenterControl notificationCenterView = new NotificationCenterControl(userId);
        notificationCenterView.showNotificationCenterView();

        // Dispose the home router view
        homeRouterView.dispose();
    }

    /**
     * Opens the conference details view for a specific conference
     * @param conferenceId The ID of the conference to display
     */
    public void createReviewerDashboardControl(int conferenceId) {
        EntityConference conference = DBMSBoundary.getConference(conferenceId);

        if (conference != null) {
            new ReviewerConferenceControl(conference, this);
        }
    }

    public void openEditorConferenceDetails(int conferenceId) {
        // TODO: Questa query va dentro il costruttore di EditorConferenceDashboardView
        EntityConference conference = DBMSBoundary.getConference(conferenceId);
        List<Map<String, Object>> finalVersions = DBMSBoundary.getAcceptedPapers(conferenceId);

        if (conference != null) {
            new EditorConferenceDashboardControl(conference, finalVersions, this);
        }
    }

    public void openOnGoingSubReview(int paperId, String paperTitle) {
        ReviewerConferenceControl reviewerConferenceControl = new ReviewerConferenceControl(this);
        reviewerConferenceControl.showOnGoingReviewView(paperId, paperTitle, DBMSBoundary.getReviewId(AppSession.getInstance().getUserId(), paperId, true), true);
    }

    public void openCompletedSubReview(int paperId) {
        ReviewerConferenceControl reviewerConferenceControl = new ReviewerConferenceControl(this);
        int reviewId = DBMSBoundary.getReviewId(AppSession.getInstance().getUserId(), paperId, true);
        reviewerConferenceControl.showCompletedReviewView(reviewId);
    }
}
