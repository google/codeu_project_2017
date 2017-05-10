package com.google.codeu.chatme.view.tabs;

import com.google.codeu.chatme.view.create.*;
/**
 * Following MVP design pattern, this interface provides functions
 * which are implemented in {@link ConversationsFragment}
 */

public interface ConversationsView {

    /**
     * Launches {@link CreateConversationActivity}
     */
    void openCreateConversationActivity();
}
