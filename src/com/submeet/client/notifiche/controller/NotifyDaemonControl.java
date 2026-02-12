package com.submeet.client.notifiche.controller;

import com.submeet.client.utility.AppSession;
import com.submeet.client.utility.SystemNotification;
import com.submeet.dbmsboundary.DBMSBoundary;

import java.util.concurrent.*;

public class NotifyDaemonControl {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private int currentNotificationCount = 0;
    private final int userId;

    public NotifyDaemonControl(int userId) {
        this.userId = userId;
        // Set initial notification count
        this.currentNotificationCount = DBMSBoundary.getUserNotificationsCount(userId);
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            int sessionUserId = AppSession.getInstance().getUserId();

            if (sessionUserId == -1 || sessionUserId != userId) {
                stop(); // Stop the task if user logged out or switched
                return;
            }

            int notificationCount = DBMSBoundary.getUserNotificationsCount(userId);

            if (notificationCount > this.currentNotificationCount) {
                this.currentNotificationCount = notificationCount;

                // Send system notification
                SystemNotification.showNotification("Submeet", "Hai una nuova notifica!");
            }

        }, 0, 10, TimeUnit.SECONDS); // Check every 10 seconds
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
