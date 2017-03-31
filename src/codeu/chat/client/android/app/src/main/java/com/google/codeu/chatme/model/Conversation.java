package com.google.codeu.chatme.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public final class Conversation {

    public String id;
    public String owner;
    public long timeCreated;

    private final List<String> participants = new ArrayList<>();

    public Conversation() {
    }

    public Conversation(String owner) {
        this.owner = owner;
        this.participants.add(owner);
        this.timeCreated = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
