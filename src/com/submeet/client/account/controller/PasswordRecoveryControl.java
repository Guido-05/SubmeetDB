package com.submeet.client.account.controller;

import com.submeet.client.account.view.LoginView;
import com.submeet.client.account.view.PasswordRecoveryView;
import com.submeet.client.common.ErrorPopupView;
import com.submeet.client.common.SuccessPopupView;
import com.submeet.client.utility.EmailSender;
import com.submeet.dbmsboundary.DBMSBoundary;

public class PasswordRecoveryControl {
    private final AuthControl authControl;
    private PasswordRecoveryView passwordRecoveryView;

    public PasswordRecoveryControl(AuthControl authControl) {
        this.authControl = authControl;

        passwordRecoveryView = new PasswordRecoveryView(this);
        passwordRecoveryView.setVisible(true);
    }

    public boolean checkEmailExists(String email) {
        return DBMSBoundary.checkEmailExists(email);
    }

    public void sendPassword(String email) {
        String password = DBMSBoundary.getPassword(email);
        if (password != null) {
            // Send password via email
            EmailSender.sendEmail(
                email,
                "SubMeet - Recupero Password",
                "Gentile utente,\n\nLa tua password è: " + password + "\n\nGrazie per utilizzare SubMeet."
            );

            // Show success popup
            new SuccessPopupView("Password inviata con successo all'indirizzo email fornito.");

            // Return to login view
            passwordRecoveryView.dispose();
            new LoginView(authControl);
        } else {
            new ErrorPopupView("Si è verificato un errore nel recupero della password.");
        }
    }
}
