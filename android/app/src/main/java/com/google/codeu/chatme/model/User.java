package com.google.codeu.chatme.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public final class User {

    public String id;
    public String name;
    public long timeCreated;

    public User() {
    }

    public User(String name) {
        this.name = name;
        this.timeCreated = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setId(String id) {
        this.id = id;
    }
}
