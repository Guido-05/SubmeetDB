package com.submeet.client.entity;

import java.util.Date;

public class EntityPaper {

    private int paperId;

    private int conferenceId;

    private String title;

    private String state;

    private Date submitionDate;

    private boolean isFinalVersion;

    private byte fileData;

    private String authorName;

    private String authorSurname;

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public int getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(int conferenceId) {
        this.conferenceId = conferenceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getSubmitionDate() {
        return submitionDate;
    }

    public void setSubmitionDate(Date submitionDate) {
        this.submitionDate = submitionDate;
    }

    public boolean isFinalVersion() {
        return isFinalVersion;
    }

    public void setFinalVersion(boolean isFinalVersion) {
        this.isFinalVersion = isFinalVersion;
    }

    public byte getFileData() {
        return fileData;
    }

    public void setFileData(byte fileData) {
        this.fileData = fileData;
    }

    public String getAuthorName() { return authorName; }

    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorSurname() { return authorSurname; }

    public void setAuthorSurname(String authorSurname) { this.authorSurname = authorSurname; }

}