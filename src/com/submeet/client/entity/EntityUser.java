package com.submeet.client.entity;

import java.util.ArrayList;

public class EntityUser {

    private int userId;

    private String name;

    private String surname;

    private String email;

    private String password;

    private String nationality;

    private ArrayList<String> specializations;

    private int[] createdConferenceIds;

    private int[] reviewerConferenceIds;

    private int[] authorConferenceIds;

    private int[] editorConferenceIds;

    private int[] subReviewPaperIds;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public ArrayList<String> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(ArrayList<String> specializations) {
        this.specializations = specializations;
    }

    public int[] getCreatedConferenceIds() {
        return createdConferenceIds;
    }

    public void setCreatedConferenceIds(int[] createdConferenceIds) {
        this.createdConferenceIds = createdConferenceIds;
    }

    public int[] getReviewerConferenceIds() {
        return reviewerConferenceIds;
    }

    public void setReviewerConferenceIds(int[] reviewerConferenceIds) {
        this.reviewerConferenceIds = reviewerConferenceIds;
    }

    public int[] getAuthorConferenceIds() {
        return authorConferenceIds;
    }

    public void setAuthorConferenceIds(int[] authorConferenceIds) {
        this.authorConferenceIds = authorConferenceIds;
    }

    public int[] getEditorConferenceIds() {
        return editorConferenceIds;
    }

    public void setEditorConferenceIds(int[] editorConferenceIds) {
        this.editorConferenceIds = editorConferenceIds;
    }

    public int[] getSubReviewPaperIds() {
        return subReviewPaperIds;
    }

    public void setSubReviewPaperIds(int[] subReviewPaperIds) {
        this.subReviewPaperIds = subReviewPaperIds;
    }

}
