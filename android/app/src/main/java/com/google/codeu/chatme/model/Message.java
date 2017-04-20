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

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getConversation() {
        return conversation;
    }

    public String getContent() {
        return content;
    }

    public long getTimeCreated() {
        return timeCreated;
    }
}
