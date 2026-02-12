package com.submeet.client.account.view;

import com.submeet.client.account.controller.AuthControl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginView extends JFrame {
    private JTextField fieldEmail;
    private JPasswordField fieldPassword;
    private final AuthControl authController;

    public LoginView(AuthControl authController) {
        this.authController = authController;
        setTitle("Login");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        create();
        setVisible(true);
    }

    private void create() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Header with logo
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon rawIcon = new javax.swing.ImageIcon(getClass().getResource("/resources/Logo.png"));
        Image scaledImage = rawIcon.getImage().getScaledInstance(265, 67, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(logoLabel);
        header.add(Box.createVerticalStrut(30));

        mainPanel.add(header);

        // Input fields with labels
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email label and field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(emailLabel);
        inputPanel.add(Box.createVerticalStrut(5));
        fieldEmail = new JTextField(authController.getLastEmail());
        styleField(fieldEmail);
        inputPanel.add(fieldEmail);
        inputPanel.add(Box.createVerticalStrut(15));

        // Password label and field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(passwordLabel);
        inputPanel.add(Box.createVerticalStrut(5));
        fieldPassword = new JPasswordField();
        styleField(fieldPassword);
        inputPanel.add(fieldPassword);

        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Buttons
        JButton loginBtn = new JButton("ENTRA");
        styleLoginButton(loginBtn);
        loginBtn.addActionListener(e -> loginButton());
        mainPanel.add(loginBtn);
        mainPanel.add(Box.createVerticalStrut(10));

        JLabel oppLabel = new JLabel("Oppure", SwingConstants.CENTER);
        oppLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        oppLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(oppLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        JButton signupBtn = new JButton("REGISTRATI");
        styleSignupButton(signupBtn);
        signupBtn.addActionListener(e -> signupButton());
        mainPanel.add(signupBtn);
        mainPanel.add(Box.createVerticalStrut(20));

        JLabel recoveryLabel = new JLabel("Hai dimenticato la password? Clicca qui per resettarla");
        recoveryLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        recoveryLabel.setForeground(Color.BLUE);
        recoveryLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        recoveryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        recoveryLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                passwordRecoveryLink();
            }
        });
        mainPanel.add(recoveryLabel);

        add(mainPanel);
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(360, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(Color.DARK_GRAY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleLoginButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(250, 35));
        button.setMaximumSize(new Dimension(250, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(0.5F);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(31, 97, 141));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }
        });
    }

    private void styleSignupButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(150, 150, 150));
        button.setForeground(Color.BLACK);
        button.setPreferredSize(new Dimension(250, 35));
        button.setMaximumSize(new Dimension(250, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(0.5F);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(120, 120, 120));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(150, 150, 150));
            }
        });
    }

    private void loginButton() {
        String email = fieldEmail.getText();
        String password = new String(fieldPassword.getPassword());
        authController.checkLoginCredentials(email, password, this);
    }

    private void signupButton() {
        authController.createSignupView();
        dispose();
    }

    private void passwordRecoveryLink() {
        authController.createPasswordRecoveryControl();
        dispose();
    }
}
