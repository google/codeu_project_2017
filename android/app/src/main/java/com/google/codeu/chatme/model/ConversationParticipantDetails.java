package com.google.codeu.chatme.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConversationParticipantDetails {

    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("photoUrl")
    @Expose
    private String photoUrl;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}