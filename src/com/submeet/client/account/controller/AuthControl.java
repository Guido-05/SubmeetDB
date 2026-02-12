package com.submeet.client.account.controller;

import com.submeet.client.account.view.LoginView;
import com.submeet.client.account.view.SignUpView;
import com.submeet.client.account.view.VerificationPupupView;
import com.submeet.client.common.ErrorPopupView;
import com.submeet.client.common.SuccessPopupView;
import com.submeet.client.notifiche.controller.NotifyDaemonControl;
import com.submeet.client.utility.AppSession;
import com.submeet.client.utility.EmailSender;
import com.submeet.dbmsboundary.DBMSBoundary;

import java.io.*;
import java.nio.file.*;
import java.util.Random;


public class AuthControl {
    private String verificationCode;
    private boolean isCodeVerified = false;

    public AuthControl() {
    }

    public void createSignupView() {
        new SignUpView(this);
    }

    public void checkLoginCredentials(String email, String password, LoginView loginView) {
        if(DBMSBoundary.checkCredentials(email, password))
        {
            // Save email
            saveEmailToFile(email);

            // Create HomeControl and HomeRouterView
            HomeControl homeControl = new HomeControl();
            homeControl.createHomeRouterView();

            // Close the login view
            loginView.dispose();

            // Start the demon to check for new notifications
            NotifyDaemonControl notifyDaemonControl = new NotifyDaemonControl(AppSession.getInstance().getUserId());
            notifyDaemonControl.start();
        }
        else
        {
            new ErrorPopupView("Login failed");
        }
    }

    // check signup credential and create new user
    public boolean checkSignupCredentials(String name, String surname, String email, String nationality, String password, String repPassword, String[] specializations) {

        String message = DBMSBoundary.insertNewUser(name, surname, email, nationality, password, specializations);

        switch (message) {
            case "success":
                new SuccessPopupView("Registrazione completata con successo!");
                new LoginView(this);
                return true;
            case "email_exists":
                new ErrorPopupView("L'email è già in uso");
                return false;
            case "password_regex":
                new ErrorPopupView("La password deve contenere almeno 7 caratteri,\nuna lettera maiuscola, una minuscola,\n un numero e un carattere speciale");
                return false;
            case "email_regex":
                new ErrorPopupView("L'email deve essere valida");
                return false;
            case "insert_error":
                new ErrorPopupView("Errore durante l'inserimento dei dati");
                return false;
            default:
                return false;
        }
    }

    public void createPasswordRecoveryControl() {
        new PasswordRecoveryControl(this);
    }

    public void logout() {
        AppSession.getInstance().setUserId(-1);
        new LoginView(this);
    }

    public String getLastEmail() {
        try {
            Path dataFile = Paths.get(System.getenv("APPDATA"), "submeet", "submeet.data");
            if (Files.exists(dataFile)) {
                return Files.readString(dataFile).trim();
            }
        } catch (IOException e) {
            new ErrorPopupView("Error reading saved login data");
        }
        return "";
    }

    private void saveEmailToFile(String email) {
        try {
            Path dataDir = Paths.get(System.getenv("APPDATA"), "submeet");
            Path dataFile = dataDir.resolve("submeet.data");
            Files.createDirectories(dataDir);
            Files.writeString(dataFile, email);
        } catch (IOException e) {
            new ErrorPopupView("Error saving login data");
        }
    }

    /**
     * Generates a random 8-digit verification code
     * @return 8-digit code as a string
     */
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 10000000 + random.nextInt(90000000); // Ensures 8 digits
        verificationCode = String.valueOf(code);
        return verificationCode;
    }

    /**
     * Sends a verification code to the user's email
     * @param name User's name
     * @param email User's email address
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendVerificationEmail(String name, String email) {
        if (verificationCode == null || verificationCode.isEmpty()) {
            generateVerificationCode();
        }

        String emailSubject = "Codice di verifica per la registrazione a SubMeet";
        String emailContent = "Ciao " + name + ",\n\n" +
                "Grazie per esserti registrato a SubMeet. Per completare la registrazione, inserisci il seguente codice:\n\n" +
                verificationCode + "\n\n" +
                "Se non hai richiesto questa registrazione, ignora questa email.\n\n" +
                "Cordiali saluti,\nIl team di SubMeet";

        EmailSender.sendEmail(email, emailSubject, emailContent);
        return true;
    }

    /**
     * Verifies if the entered code matches the generated verification code
     * @param enteredCode The code entered by the user
     * @return true if the codes match, false otherwise
     */
    public boolean verifyCode(String enteredCode) {
        if (verificationCode == null || verificationCode.isEmpty()) {
            return false;
        }
        this.isCodeVerified = true;
        return enteredCode.equals(verificationCode);
    }

    public void showVerificationPopupView(String email) {
        new VerificationPupupView(email, this);
    }

    public boolean isCodeVerified() {
        return isCodeVerified;
    }
}
