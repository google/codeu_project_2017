package codeu.chat.client.maingui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.File;
import java.math.BigInteger;

import codeu.chat.client.ClientContext;
import codeu.chat.common.*;

// NOTE: JPanel is serializable, but there is no need to serialize MessagePanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class MessagePanel extends JPanel{

  // These objects are modified by the Conversation Panel.
  private final JLabel messageOwnerLabel = new JLabel("Owner:", JLabel.RIGHT);
  private final JLabel messageConversationLabel = new JLabel("Conversation:", JLabel.LEFT);
  private final DefaultListModel<String> messageListModel = new DefaultListModel<>();

  private final ClientContext clientContext;

  private final JFileChooser fileChooser = new JFileChooser();
  private File file;

  public MessagePanel(ClientContext clientContext) {
    super(new GridBagLayout());
    this.clientContext = clientContext;
    initialize();
  }

  // External agent calls this to trigger an update of this panel's contents.
  public void update(ConversationSummary owningConversation) {

    final User u = (owningConversation == null) ? null : clientContext.user.lookup(owningConversation.owner);

    messageOwnerLabel.setText("Owner: " + ((u==null) ? ((owningConversation==null) ? "" : owningConversation.owner) : u.name));

    messageConversationLabel.setText("Conversation: " + owningConversation.title);

    getAllMessages(owningConversation);
  }

  private void initialize() {

    // This panel contains the messages in the current conversation.
    // It has a title bar with the current conversation and owner,
    // then a list panel with the messages, then a button bar.

    this.setLayout(new BorderLayout());
    this.setPreferredSize(new Dimension(500,500));

    // Initialize File Chooser options
    this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    file = null;

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
    titleOwnerPanelC.gridx = 0;
    titleOwnerPanelC.gridy = 1;
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
    titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // User List panel.
    final JPanel listShowPanel = new JPanel(new BorderLayout());

    // messageListModel is an instance variable so Conversation panel
    // can update it.
    final JList<String> userList = new JList<>(messageListModel);
    userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    userList.setSelectedIndex(-1);

    final JScrollPane userListScrollPane = new JScrollPane(userList);
    listShowPanel.add(userListScrollPane, BorderLayout.CENTER);

    // Button panel
    final JPanel buttonPanel = new JPanel(new GridBagLayout());

    final JButton addButton = new JButton("Add");
    final JButton addFileButton = new JButton("File");
    final JButton updateButton = new JButton("Update");
    final JTextArea sendMessageTextArea = new JTextArea(3,20);
    final JScrollPane sendMessageScrollPane = new JScrollPane(sendMessageTextArea);


    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    GridBagConstraints buttonPanelConstrains = new GridBagConstraints();
    buttonPanelConstrains.fill = GridBagConstraints.BOTH;
    buttonPanelConstrains.insets = new Insets(0,2,0,2);
    
    buttonPanelConstrains.weightx = 0.1;
    buttonPanelConstrains.gridx = 0;
    buttonPanelConstrains.gridy = 0;
    buttonPanel.add(updateButton,buttonPanelConstrains);

    buttonPanelConstrains.weightx = 0.7;
    buttonPanelConstrains.gridx = 1;
    buttonPanel.add(sendMessageScrollPane,buttonPanelConstrains);

    buttonPanelConstrains.weightx = 0.1;
    buttonPanelConstrains.gridx = 2;
    buttonPanel.add(addFileButton,buttonPanelConstrains);

    buttonPanelConstrains.weightx = 0.1;
    buttonPanelConstrains.gridx = 3;
    buttonPanel.add(addButton,buttonPanelConstrains);

    this.add(titlePanel, BorderLayout.NORTH);
    this.add(listShowPanel, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);

    // User click Messages Add button - prompt for message body and add new Message to Conversation
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!clientContext.conversation.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.");
        } else {
          final String messageText = sendMessageTextArea.getText();
          if (messageText != null && messageText.length() > 0) {
            // Implement sending message with file here:
            clientContext.message.addMessage(
                clientContext.user.getCurrent().id,
                clientContext.conversation.getCurrentId(),
                messageText,
                clientContext.conversation.getPublicKey());
            sendMessageTextArea.setText("");
            MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent());

            // Set file to null when message is sent
            file = null;
          }
        }
      }
    });

    // User click Messages Update button - update all messages in conversation (still in develop)
    updateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!clientContext.conversation.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.");
        } else {
          clientContext.conversation.updateAllConversations(true);
          MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent());
        }
      }
    });

    // User click Messages Add File button - choose a file to send
    addFileButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!clientContext.conversation.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.");
        } else {
          int returnVal = fileChooser.showOpenDialog(MessagePanel.this.getParent());
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            System.out.println(file.getName());
          } else {
            // File Chooser canceled
            file = null;
          }
        }
      }
    });
  }

  // Populate ListModel
  private void getAllMessages(ConversationSummary conversation) {
    messageListModel.clear();

    for (final Message m : clientContext.message.getConversationContents(conversation)) {
      // Display author name if available.  Otherwise display the author UUID.
      final String authorName = clientContext.user.getName(m.author);
      try {
        BigInteger encryptedContent = RSA.keyToBigInteger(m.content);
        Conversation currentConversation = clientContext.conversation.getCurrentConversation();
        System.out.println("getAllMessages " + currentConversation);
        m.content = RSA.messageToString(RSA.decrypt(encryptedContent, currentConversation.SecretKey()));
        final String displayString = String.format("@%s: %s",
                ((authorName == null) ? m.author : authorName), m.content);

        messageListModel.addElement(displayString);
      }catch (NumberFormatException ex){
        final String displayString = String.format("@%s: %s",
                ((authorName == null) ? m.author : authorName), m.content);

        messageListModel.addElement(displayString);
      }
    }
  }
}