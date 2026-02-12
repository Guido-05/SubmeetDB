package com.submeet.client.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EntityConference {

    private int conferenceId;
    private String title;

    private List<String> languages;
    private List<String> specializations;
    private int reviewerForPaper;

    private Date scheduleDate;
    private Date submissionDeadline;
    private Date reviewDeadline;
    private Date finalVersionDeadline;
    private Date invitationDeadline;
    private Date paperAssignmentDeadline;

    private int submittedPapers;
    private String log;

    private HashMap<Integer, String> reviewed;
    private HashMap<Integer, String> underReview;
    private HashMap<Integer, String> unassigned;

    // Getters & Setters

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

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<String> specializations) {
        this.specializations = specializations;
    }

    public int getReviewerForPaper() {
        return reviewerForPaper;
    }

    public void setReviewerForPaper(int reviewerForPaper) {
        this.reviewerForPaper = reviewerForPaper;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Date getSubmissionDeadline() {
        return submissionDeadline;
    }

    public void setSubmissionDeadline(Date submissionDeadline) {
        this.submissionDeadline = submissionDeadline;
    }

    public Date getReviewDeadline() {
        return reviewDeadline;
    }

    public void setReviewDeadline(Date reviewDeadline) {
        this.reviewDeadline = reviewDeadline;
    }

    public Date getFinalVersionDeadline() {
        return finalVersionDeadline;
    }

    public void setFinalVersionDeadline(Date finalVersionDeadline) {
        this.finalVersionDeadline = finalVersionDeadline;
    }

    public Date getInvitationDeadline() {
        return invitationDeadline;
    }

    public void setInvitationDeadline(Date invitationDeadline) {
        this.invitationDeadline = invitationDeadline;
    }

    public Date getPaperAssignmentDeadline() {
        return paperAssignmentDeadline;
    }

    public void setPaperAssignmentDeadline(Date paperAssignmentDeadline) {
        this.paperAssignmentDeadline = paperAssignmentDeadline;
    }

    public int getSubmittedPapers() {
        return submittedPapers;
    }

    public void setSubmittedPapers(int submittedPapers) {
        this.submittedPapers = submittedPapers;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public HashMap<Integer, String> getReviewed() {
        return reviewed;
    }

    public void setReviewed(HashMap<Integer, String> reviewed) {
        this.reviewed = reviewed;
    }

    public HashMap<Integer, String> getUnderReview() {
        return underReview;
    }

    public void setUnderReview(HashMap<Integer, String> underReview) {
        this.underReview = underReview;
    }

    public HashMap<Integer, String> getUnassigned() {
        return unassigned;
    }

    public void setUnassigned(HashMap<Integer, String> unassigned) {
        this.unassigned = unassigned;
    }
}
