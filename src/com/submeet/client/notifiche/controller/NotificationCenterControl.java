package com.submeet.client.notifiche.controller;

import com.submeet.client.entity.EntityNotification;
import com.submeet.client.notifiche.view.FilterMenuView;
import com.submeet.client.notifiche.view.NotificationCenterView;
import com.submeet.client.notifiche.view.NotificationView;
import com.submeet.dbmsboundary.DBMSBoundary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NotificationCenterControl {
    List<EntityNotification> notifications;

    public NotificationCenterControl(int userId) {
        this.notifications = getUserNotifications(userId);
    }


    public void showNotificationCenterView() {
        new NotificationCenterView(notifications, this);
    }

    /**
     * Gets notifications for a specific user
     * @param userId The ID of the user
     * @return List of EntityNotification objects
     */
    public List<EntityNotification> getUserNotifications(int userId) {
        // Get notifications from DBMS
        List<Map<String, Object>> notificationsData = DBMSBoundary.getUserNotifications(userId);

        // Convert to EntityNotification objects
        List<EntityNotification> notifications = new ArrayList<>();

        assert notificationsData != null;
        for (Map<String, Object> data : notificationsData) {
            EntityNotification notification = new EntityNotification();

            notification.setNotificId((Integer) data.get("notifId"));
            notification.setText((String) data.get("text"));
            notification.setType((String) data.get("type"));
            notification.setAccepted((int) data.get("accepted"));
            notification.setVisualized((Boolean) data.get("visualized"));
            notification.setDate((Date) data.get("date"));

            notifications.add(notification);
        }

        return notifications;
    }

    /**
     * Shows the full notification in a new view
     * @param notifId The ID of the notification to show
     */
    public void viewNotification(int notifId) {
        // Get notification info from DBMS
        EntityNotification notification = DBMSBoundary.getNotificInfo(notifId);

        // Update notification state to visualized
        DBMSBoundary.updateNotificState(notifId);

        // Create and show NotificationView with the notification
        if (notification != null) {
            new NotificationView(notification);
        }

    }

    public void createFilterMenu(NotificationCenterView notificationCenterView) {
        new FilterMenuView(this, notificationCenterView);
    }

    // Apply filter to notifications
    public void notificationFilter(String filter, NotificationCenterView notificationCenterView) {
        List<EntityNotification> filteredNotifications = new ArrayList<>();

        switch (filter) {
            case "Ripristina":
                filteredNotifications = notifications;
                break;

            case "Visualizzata":
                for (EntityNotification n : notifications) {
                    if (n.isVisualized()) {
                        filteredNotifications.add(n);
                    }
                }
                break;

            case "Non visualizzata":
                for (EntityNotification n : notifications) {
                    if (!n.isVisualized()) {
                        filteredNotifications.add(n);
                    }
                }
                break;

            case "Invito":
                for (EntityNotification n : notifications) {
                    if (n.getType().toLowerCase().contains("invitation")) {
                        filteredNotifications.add(n);
                    }
                }
                break;

            case "Invito PC":
                for (EntityNotification n : notifications) {
                    if (n.getType().equals("invitation PC")) {
                        filteredNotifications.add(n);
                    }
                }
                break;

            case "Invito editore":
                for (EntityNotification n : notifications) {
                    if (n.getType().equals("invitation EDITOR")) {
                        filteredNotifications.add(n);
                    }
                }
                break;

            case "invito sotto revisore":
                for (EntityNotification n : notifications) {
                    if (n.getType().equals("invitation SUBREVIEWER")) {
                        filteredNotifications.add(n);
                    }
                }
                break;
        }

        // Update the view with the filtered notifications
        notificationCenterView.displayNotifications(filteredNotifications);
    }

}
