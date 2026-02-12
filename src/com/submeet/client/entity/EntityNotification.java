package com.submeet.client.entity;

import java.util.Date;

public class EntityNotification {

    private int notificId;

    private int userId;

    private String type;

    private int accepted;

    private boolean visualized;

    private int conferenceId;

    private int paperId;

    private String text;

    private Date date;

    public int getNotificId() {
        return notificId;
    }

    public void setNotificId(int notificId) {
        this.notificId = notificId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAccepted() {
        return accepted;
    }

    public void setAccepted(int accepted) {
        this.accepted = accepted;
    }

    public boolean isVisualized() {
        return visualized;
    }

    public void setVisualized(boolean visualized) {
        this.visualized = visualized;
    }

    public int getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(int conferenceId) {
        this.conferenceId = conferenceId;
    }

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(Date date) { this.date = date; }

    public Date getDate() { return date; }

}
