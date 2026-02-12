package com.submeet.client.autore.view;

import javax.swing.*;

public class ConfirmPopupView {

    /**
     * Displays a confirmation dialog with the given message.
     * @param message The message to display.
     * @return true if user clicks "Yes", false otherwise.
     */
    public static boolean show(String message) {
        int result = JOptionPane.showConfirmDialog(
                null,
                message,
                "Conferma",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
}
