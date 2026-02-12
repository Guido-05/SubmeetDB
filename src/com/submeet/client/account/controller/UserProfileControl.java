package com.submeet.client.account.controller;

import com.submeet.client.account.view.UserProfileView;
import com.submeet.client.common.ErrorPopupView;
import com.submeet.client.common.SuccessPopupView;
import com.submeet.client.entity.EntityUser;
import com.submeet.client.utility.AppSession;
import com.submeet.dbmsboundary.DBMSBoundary;

public class UserProfileControl {
    private UserProfileView userProfileView;
    private EntityUser currentUser;

    public UserProfileControl() {
        // Get the current user from the database
        currentUser = DBMSBoundary.getUserInfo(AppSession.getInstance().getUserId());
        showUserProfileView();
    }

    public void showUserProfileView() {
        userProfileView = new UserProfileView(this);
        userProfileView.setVisible(true);
    }

    public EntityUser getCurrentUser() {
        return currentUser;
    }

    public void navigateToHomeView() {
        HomeControl homeControl = new HomeControl();
        homeControl.createHomeRouterView();
        userProfileView.dispose();
    }

    public void logout() {
        // Create a new AuthControl and show the login view
        AuthControl authControl = new AuthControl();  // TODO: cercare di passar direttamente la authocontrol
        authControl.logout();
        userProfileView.dispose();
    }

    public void checkPassword(String currentPassword, String newPassword, String confirmPassword) {
        // Check if fields are empty
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            new ErrorPopupView("Tutti i campi devono essere compilati");
            return;
        }

        // Check if the current password is correct
        if (!currentPassword.equals(DBMSBoundary.getUserPassword(currentUser.getUserId()))) {
            new ErrorPopupView("Current password is incorrect");
            return;
        }

        // Check if the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            new ErrorPopupView("New password and confirm password do not match");
            return;
        }

        // Change the password
        DBMSBoundary.changeUserPassword(currentUser.getUserId(), newPassword);
        new SuccessPopupView("Password changed successfully");
    }

    public void modifyUserData(String name, String surname, String email, String nationality, String specializations) {
        // Validate input data
        // TODO: la mail non va modificata
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || nationality.isEmpty()) {
            new ErrorPopupView("All fields are required");
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            new ErrorPopupView("Invalid email format");
            return;
        }

        // Check if email exists (only if it's different from the current email)
        if (!email.equals(currentUser.getEmail()) && DBMSBoundary.checkEmailExists(email)) {
            new ErrorPopupView("Email already exists");
            return;
        }

        // Split specializations and languages by semicolon
        String[] specializationsArray = specializations.isEmpty() ? new String[0] : specializations.split(",");

        // Trim whitespace from each specialization and language
        for (int i = 0; i < specializationsArray.length; i++) {
            specializationsArray[i] = specializationsArray[i].trim();
        }

        // Update user data in the database
        boolean success = DBMSBoundary.updateUserData(
            currentUser.getUserId(),
            name,
            surname,
            email,
            nationality,
            specializationsArray
        );

        if (success) {
            // Update current user object
            currentUser = DBMSBoundary.getUserInfo(currentUser.getUserId());
            new SuccessPopupView("User data updated successfully");

            // Refresh the view
            userProfileView.dispose();
            showUserProfileView();
        } else {
            new ErrorPopupView("Failed to update user data");
        }
    }
}
