package com.submeet.client.account.view;

import com.submeet.client.account.controller.AuthControl;
import com.submeet.client.common.ErrorPopupView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VerificationPupupView extends JDialog {

    public VerificationPupupView(String email, AuthControl authControl) {
        JDialog dialog = new JDialog(this, "Verifica Email", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 255, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel messageLabel = new JLabel("<html>Un codice di verifica è stato inviato a:<br>" + email + "<br>Inserisci il codice per completare la registrazione</html>");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(messageLabel, gbc);

        JTextField codeField = new JTextField(10);
        codeField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(codeField, gbc);

        final boolean[] result = {false};
        final boolean[] dialogClosed = {false};

        JButton verifyButton = new JButton("Verifica");
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredCode = codeField.getText().trim();
                if (authControl.verifyCode(enteredCode)) {
                    result[0] = true;
                    dialogClosed[0] = true;
                    dialog.dispose();
                } else {
                    new ErrorPopupView("Codice errato. Riprova.");
                    codeField.setText("");
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(verifyButton, gbc);

        JButton cancelButton = new JButton("Annulla");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogClosed[0] = true;
                dialog.dispose();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(cancelButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
