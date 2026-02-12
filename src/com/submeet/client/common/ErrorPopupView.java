package com.submeet.client.common;

import javax.swing.*;
import java.awt.*;

public class ErrorPopupView extends JDialog {

    public ErrorPopupView(String message) {
        setTitle("Errore");
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(238, 24, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Use HTML to enable text wrapping and set maximum width
        String wrappedMessage = "<html><div style='text-align: center; width: 350px;'>" + 
                               message.replace("\n", "<br>") + "</div></html>";
        JLabel messageLabel = new JLabel(wrappedMessage);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setVerticalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(messageLabel, gbc);

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.setMinimumSize(new Dimension(80, 30));
        okButton.addActionListener(e -> dispose());
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(okButton, gbc);

        add(panel);

        // Set initial size and minimum size
        setSize(400, 250);
        setMinimumSize(new Dimension(300, 150));

        // Pack to fit content, but respect minimum size
        pack();
        if (getWidth() < 400) setSize(400, getHeight());
        if (getHeight() < 250) setSize(getWidth(), 250);

        setVisible(true);
    }
}
