// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.client.simplegui;

import java.awt.*;
import java.awt.event.*; 

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;  

import codeu.chat.client.ClientContext;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.Uuid;

// NOTE: JPanel is serializable, but there is no need to serialize MessagePanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class MessagePanel extends JPanel {

  // These objects are modified by the Conversation Panel.
  private final JLabel messageOwnerLabel = new JLabel("Owner:", JLabel.RIGHT);
  private final JLabel messageConversationLabel = new JLabel("Conversation:", JLabel.LEFT);
  private final DefaultListModel<String> messageListModel = new DefaultListModel<>();

  private final ClientContext clientContext;

  public MessagePanel(ClientContext clientContext) {
    super(new GridBagLayout());
    this.clientContext = clientContext;
    initialize();
  }

  // External agent calls this to trigger an update of this panel's contents.
  public void update(ConversationSummary owningConversation) {

    final User u = (owningConversation == null) ?
        null :
        clientContext.user.lookup(owningConversation.owner);

    messageOwnerLabel.setText("Owner: " +
        ((u==null) ?
            ((owningConversation==null) ? "" : owningConversation.owner) :
            u.name));

    messageConversationLabel.setText("Conversation: " + owningConversation.title);

    getAllMessages(owningConversation);
  }

  private void initialize() {

    // This panel contains the messages in the current conversation.
    // It has a title bar with the current conversation and owner,
    // then a list panel with the messages, then a button bar.

    // Title bar - current conversation and owner
    final JPanel titlePanel = new JPanel(new GridBagLayout());
    final GridBagConstraints titlePanelC = new GridBagConstraints();
    
    final JPanel titleConvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    final GridBagConstraints titleConvPanelC = new GridBagConstraints();
    titleConvPanelC.gridx = 0;
    titleConvPanelC.gridy = 0;
    titleConvPanelC.anchor = GridBagConstraints.PAGE_START;

    final JPanel titleOwnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    final GridBagConstraints titleOwnerPanelC = new GridBagConstraints();
    titleOwnerPanelC.gridwidth = 3;  
    titleOwnerPanelC.gridx = 0;
    titleOwnerPanelC.gridy = 3;
    titleOwnerPanelC.anchor = GridBagConstraints.PAGE_START;

    // messageConversationLabel is an instance variable of Conversation panel
    // can update it.
    messageConversationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    titleConvPanel.add(messageConversationLabel);

    // messageOwnerLabel is an instance variable of Conversation panel
    // can update it.
    messageOwnerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    titleOwnerPanel.add(messageOwnerLabel);

    titlePanel.add(titleConvPanel, titleConvPanelC);
    titlePanel.add(titleOwnerPanel, titleOwnerPanelC);
    titleOwnerPanel.setBackground(new Color(102, 162, 237));
    titleConvPanel.setBackground(new Color(102, 162, 237));
    titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // User List panel.
    final JPanel listShowPanel = new JPanel();
    final GridBagConstraints listPanelC = new GridBagConstraints();
    
    // Panel for JScrollPane.
    final JPanel scrollPanel = new JPanel();
    final GridBagConstraints scrollPanelC = new GridBagConstraints();
	
    // Search panel and text field
    final JPanel searchPanel = new JPanel();
    final GridBagConstraints searchPanelC = new GridBagConstraints();
    final JTextField textFieldSearch = new JTextField(20);

    final JButton addButtonSearch = new JButton("Search Messages");
    searchPanel.add(textFieldSearch); //Adds the message box before the "Search Message" button
    searchPanel.add(addButtonSearch);
    
    // messageListModel is an instance variable so Conversation panel
    // can update it.
    final JList<String> userList = new JList<>(messageListModel);
    userList.setVisibleRowCount(-1);
    userList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    userList.setVisibleRowCount(15);
    userList.setSelectedIndex(-1);

    final JScrollPane userListScrollPane = new JScrollPane(userList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //

    userListScrollPane.setMinimumSize(new Dimension(500, 400));
    userListScrollPane.setPreferredSize(new Dimension(500, 400));
    scrollPanel.add(userListScrollPane);

    // Button panel and text field
    final JPanel messageTextPanel = new JPanel();
    final GridBagConstraints messageTextPanelC = new GridBagConstraints();
    final JPanel buttonPanel = new JPanel();
    final GridBagConstraints buttonPanelC = new GridBagConstraints();
    final JTextArea textField = new JTextArea(3,40);
    final JScrollPane newMessagePane = new JScrollPane(textField);


    textField.setLineWrap(true);
    textField.setWrapStyleWord(true);

    final JButton sendButton = new JButton("Send Message");
    final JButton updateButton = new JButton("Refresh Messages");
    
    messageTextPanel.add(newMessagePane); //Adds the message box before the "Send Message" button
    buttonPanel.add(sendButton);
    buttonPanel.add(updateButton);

    // Placement of title, list panel, buttons, and current user panel.
    titlePanelC.gridx = 0;
    titlePanelC.gridy = 0;
    titlePanelC.gridwidth = 10;
    titlePanelC.gridheight = 1;
    titlePanelC.fill = GridBagConstraints.HORIZONTAL;
    titlePanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    listPanelC.gridx = 0;
    listPanelC.gridy = 1;
    listPanelC.gridwidth = 10;
    listPanelC.gridheight = 8;
    listPanelC.fill = GridBagConstraints.BOTH;
    listPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
    listPanelC.weighty = 0.8;
    
    searchPanelC.gridx = 0;
    searchPanelC.gridy = 2;
    searchPanelC.gridwidth = 10;
    searchPanelC.gridheight = 1;
    searchPanelC.fill = GridBagConstraints.BOTH;
    searchPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
    searchPanelC.weighty = 0.8;
    
    scrollPanelC.gridx = 0;
    scrollPanelC.gridy = 3;
    scrollPanelC.gridwidth = 10;
    scrollPanelC.gridheight = 8;
    scrollPanelC.fill = GridBagConstraints.BOTH;
    scrollPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
    scrollPanelC.weighty = 0.8;

    messageTextPanelC.gridx = 0;
    messageTextPanelC.gridy = 11;
    messageTextPanelC.gridwidth = 10;
    messageTextPanelC.gridheight = 1;
    messageTextPanelC.fill = GridBagConstraints.HORIZONTAL;
    messageTextPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    buttonPanelC.gridx = 0;
    buttonPanelC.gridy = 12;
    buttonPanelC.gridwidth = 10;
    buttonPanelC.gridheight = 1;
    buttonPanelC.fill = GridBagConstraints.HORIZONTAL;
    buttonPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
	
    this.add(titlePanel, titlePanelC);
    this.add(searchPanel, searchPanelC); 
    this.add(listShowPanel, listPanelC);
    this.add(scrollPanel, scrollPanelC);
    this.add(messageTextPanel,messageTextPanelC);
    this.add(buttonPanel, buttonPanelC);
    
    titlePanel.setBackground(new Color(102, 162, 237));
    listShowPanel.setBackground(new Color(102, 162, 237));
    scrollPanel.setBackground(new Color(102, 162, 237));
    messageTextPanel.setBackground(new Color(102,162,237));
    buttonPanel.setBackground(new Color(102, 162, 237));
    searchPanel.setBackground(new Color(102, 162, 237));
	
    // User click Messages Add button - prompt for message body and add new Message to Conversation

    //Send Message button is pressed
    sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!clientContext.user.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You are not signed in.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (!clientContext.conversation.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
          final String messageText = textField.getText().trim();
          if (messageText != null && messageText.length() > 0) {
          	textField.setText(""); //clears the text field after use
            clientContext.message.addMessage(
              clientContext.user.getCurrent().id,
              clientContext.conversation.getCurrentId(),
              messageText);
            MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent());
          }
        }
      }
    });
    
    // Responds if user enters ENTER or RETURN the message sends
    textField.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      	if((int) e.getKeyChar()==13 || (int) e.getKeyChar()==10){
      	  if (!clientContext.user.hasCurrent()) {
      	    JOptionPane.showMessageDialog(MessagePanel.this, "You are not signed in.", "Error", JOptionPane.ERROR_MESSAGE);
      	  } else if (!clientContext.conversation.hasCurrent()) {
      	    JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.", "Error", JOptionPane.ERROR_MESSAGE);
      	  } else {
      	    final String messageText = textField.getText().trim(); //trim ensures the user cannot enter a string of only whitespaces      
      	      if (messageText != null && messageText.length() > 0) {
      	        textField.setText(""); //clears the text field after use
      	        clientContext.message.addMessage(
      	          clientContext.user.getCurrent().id,
      	          clientContext.conversation.getCurrentId(),
      	          messageText);
      	        MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent());
      	      }
      	    }
      	  }
      	} 
      	@Override
      	public void keyPressed(KeyEvent e) {}
      	@Override
      	public void keyReleased(KeyEvent e) {}
      });
    
    // Code to adjust the scroll bar for the message window down to the bottom - https://stackoverflow.com/questions/6379061/how-to-auto-scroll-to-bottom-in-java-swing is source  
    userListScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
      @Override  
      public void adjustmentValueChanged(AdjustmentEvent e) {
        if(userListScrollPane.getVerticalScrollBar().getValueIsAdjusting()==true){
        } else {  
          e.getAdjustable().setValue(e.getAdjustable().getMaximum());
        }  
      }
    });
    
    // Search Messages button is pressed
    addButtonSearch.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!clientContext.user.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You are not signed in.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (!clientContext.conversation.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
          final String searchQueryTextBox = textFieldSearch.getText().trim(); //trim ensures the user cannot enter a string of only whitespaces     
      	  String searchQuery = searchQueryTextBox.toUpperCase(); 
          if (searchQuery != null && searchQuery.length() > 0) {
          	textFieldSearch.setText(""); //clears the text field after use
          	
            List<Message> messages = new ArrayList<Message>(); 
            Uuid currentConversationId = clientContext.conversation.getCurrentId(); 
            Uuid userSearching = clientContext.user.getCurrent().id;
 
            messages = clientContext.message.searchMessages(currentConversationId, userSearching, searchQuery);
            
            if(messages.size()==0){
              //there are no messages found for the query, so a popup should display saying that
              JOptionPane.showMessageDialog(MessagePanel.this, "The search query for " + searchQueryTextBox + " in the current conversation yielded no results. Please note that to search a conversation, you must be a participant in it.", "Search Results", JOptionPane.ERROR_MESSAGE);
            } else {
              // display the messages list, since messages were found
              JPanel popUp = new JPanel();
              String[] messagesArray = new String[messages.size()]; 
                
              for(int i=0; i<messages.size(); i++){
                Message currentMessage = messages.get(i); 
                String authorName = clientContext.user.getName(currentMessage.author);
                String currentConversation = clientContext.conversation.getCurrent().title; 
                  
                  messagesArray[i] = String.format("%s: [%s in %s]: %s",
                  ((authorName == null) ? currentMessage.author : authorName), currentMessage.creation, currentConversation, currentMessage.content);
              }  

              JList<String> searchResult = new JList<String>(messagesArray);
              final JTextArea searchResultField = new JTextArea(8,15);
              final JScrollPane messagesPane = new JScrollPane(searchResultField);


              textField.setLineWrap(true);
              textField.setWrapStyleWord(true);

              for (int index = 0; index < searchResult.getModel().getSize(); index++) {
                searchResultField.append(searchResult.getModel().getElementAt(index));
              }

              /*
              JList<String> searchResult = new JList<String>(messagesArray); 
>>>>>>> e8afd99e21d4d3d0e93e14d8e00aaac9ec229127
              JScrollPane messagesPane = new JScrollPane(searchResult);
              messagesPane.setMinimumSize(new Dimension(250, 200));
              messagesPane.setPreferredSize(new Dimension(250, 200));
              */
                
              popUp.add(messagesPane); 
              JOptionPane.showMessageDialog(MessagePanel.this, popUp, "Search Results", JOptionPane.PLAIN_MESSAGE);
            }
          }
        }
      }
    });
    
    // Responds if user enters ENTER or RETURN the search query is sent
    textFieldSearch.addKeyListener(new KeyListener() {        
      @Override
      public void keyTyped(KeyEvent e) {
      	if((int) e.getKeyChar()==13 || (int) e.getKeyChar()==10){
      	  if (!clientContext.user.hasCurrent()) {
      	    JOptionPane.showMessageDialog(MessagePanel.this, "You are not signed in.", "Error", JOptionPane.ERROR_MESSAGE);
      	  } else if (!clientContext.conversation.hasCurrent()) {
      	    JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.", "Error", JOptionPane.ERROR_MESSAGE);
      	  } else {
      	    final String searchQueryTextBox = textFieldSearch.getText().trim(); //trim ensures the user cannot enter a string of only whitespaces     
      	    String searchQuery = searchQueryTextBox.toUpperCase(); 
      	    if (searchQuery != null && searchQuery.length() > 0) {
      	      textFieldSearch.setText(""); //clears the text field after use
          	
              List<Message> messages = new ArrayList<Message>(); 
              Uuid currentConversationId = clientContext.conversation.getCurrentId(); 
              Uuid userSearching = clientContext.user.getCurrent().id;
 
              messages = clientContext.message.searchMessages(currentConversationId, userSearching, searchQuery);
            
              if(messages.size()==0){
                //there are no messages found for the query, so a popup should display saying that
                JOptionPane.showMessageDialog(MessagePanel.this, "The search query for " + searchQueryTextBox + " in the current conversation yielded no results. Please note that to search a conversation, you must be a participant in it.", "Search Results", JOptionPane.ERROR_MESSAGE);
              } else {
                // display the messages list, since messages were found
                JPanel popUp = new JPanel();
                String[] messagesArray = new String[messages.size()]; 
                
                for(int i=0; i<messages.size(); i++){
                  Message currentMessage = messages.get(i); 
                  String authorName = clientContext.user.getName(currentMessage.author);
                  String currentConversation = clientContext.conversation.getCurrent().title; 
                  
                  messagesArray[i] = String.format("%s: [%s in %s]: %s",
                  ((authorName == null) ? currentMessage.author : authorName), currentMessage.creation, currentConversation, currentMessage.content);
                }  
                
                JList<String> searchResult = new JList<String>(messagesArray); 
                JScrollPane messagesPane = new JScrollPane(searchResult); 
                messagesPane.setMinimumSize(new Dimension(250, 200));
                messagesPane.setPreferredSize(new Dimension(250, 200));
                
                popUp.add(messagesPane); 
                JOptionPane.showMessageDialog(MessagePanel.this, popUp, "Search Results", JOptionPane.PLAIN_MESSAGE);
              }
            }
      	  }
        }
	  } 
      @Override
      public void keyPressed(KeyEvent e) {}
      @Override
      public void keyReleased(KeyEvent e) {}
    });
     
  updateButton.addActionListener(new ActionListener() {
     @Override
      public void actionPerformed(ActionEvent e) {
        if (!clientContext.user.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You are not signed in.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (!clientContext.conversation.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
          // Update the messages when the user presses the "update" button, so users can see other user's messages that were sent to the server 
          MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent());
        }
      }
  });
  
  // Panel is set up. If there is a current conversation, Populate the conversation list.
  getAllMessages(clientContext.conversation.getCurrent());
}


FontMetrics metrics = getFontMetrics(getFont());

  // Populate ListModel
  // TODO: don't refetch messages if current conversation not changed
  private void getAllMessages(ConversationSummary conversation) {
    messageListModel.clear();

    for (final Message m : clientContext.message.getConversationContents(conversation)) {
      // Display author name if available.  Otherwise display the author UUID.
      final String authorName = clientContext.user.getName(m.author);
      final String fullString = String.format("%s: [%s]: %s",
          ((authorName == null) ? m.author : authorName), m.creation, m.content);


      // split lines in panel on appropriate space (last whole word that will fit without scroll bar)
      String currentLine = "";
      String[] words = fullString.split(" ");
      int i = 0;
      while (i < words.length) {
        // build current line word by word until it is too long to fit in panel
        currentLine = "";
        String tryLine = words[i];
        // check if next word would make line too long
        while (metrics.stringWidth(tryLine) < 515 && i < words.length) {
          // if within length limit, add word to current line
          currentLine += words[i] + " ";
          i ++;
          // add next word to check if too long
          if (i < words.length) {
            tryLine = (currentLine + words[i]);
          }
        }
        // if one single word is longer than the panel, add that as its own line (scroll bar will be added)
        if (currentLine.equals("")) {
          currentLine += words[i];
          i++;
        }
         // add one line at a time
          messageListModel.addElement(currentLine);
      }
    }
  }
}
