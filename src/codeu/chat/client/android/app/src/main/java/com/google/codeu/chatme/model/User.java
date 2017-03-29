package com.google.codeu.chatme.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Time;

/**
 * Created by Yash on 3/29/2017.
 */

@IgnoreExtraProperties
public class User {

    public String id;
    public String name;
    public long timeCreated;

    public User() {
    }

    public User(String name, long timeCreated) {
        this.name = name;
        this.timeCreated = timeCreated;
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
}
