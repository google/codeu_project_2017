package com.google.codeu.chatme.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public final class Message {

    public String id;
    public String author;
    public String conversation;
    public String content;
    public long timeCreated;

    public Message() {
    }

    public Message(String author, String content, String conversation) {
        this.author = author;
        this.content = content;
        this.conversation = conversation;
        this.timeCreated = System.currentTimeMillis();
    }
}
