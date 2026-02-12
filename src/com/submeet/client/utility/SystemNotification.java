package com.submeet.client.utility;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class SystemNotification {

    public static void showNotification(String title, String message) {
        // Verifica che il sistema supporti il SystemTray
        if (!SystemTray.isSupported()) {
            System.out.println("System tray non supportato!");
            return;
        }

        Image image = Toolkit.getDefaultToolkit().createImage("");

        TrayIcon trayIcon = new TrayIcon(image, "Submeet");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Notifica da Submeet");

        SystemTray tray = SystemTray.getSystemTray();

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, MessageType.INFO);

            Thread.sleep(3000);  // attesa di 3 secondi
            tray.remove(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
