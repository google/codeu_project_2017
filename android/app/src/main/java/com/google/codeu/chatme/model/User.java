package com.google.codeu.chatme.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public final class User {

    private String id;
    private String fullName;
    private String username;
    private long timeCreated;
    private String photoUrl;

    public User() {
    }

    public User(String username) {
        this.username = username;
        this.timeCreated = System.currentTimeMillis();
    }

    public User(String id, String username, String fullName, String photoUrl) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setId(String id) {
        this.id = id;
    }
}
