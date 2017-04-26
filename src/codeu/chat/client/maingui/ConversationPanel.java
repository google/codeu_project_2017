package codeu.chat.client.maingui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import codeu.chat.client.ClientContext;
import codeu.chat.common.ConversationSummary;

// NOTE: JPanel is serializable, but there is no need to serialize ConversationPanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class ConversationPanel extends JPanel {

  private final ClientContext clientContext;
  private final MessagePanel messagePanel;

  public ConversationPanel(ClientContext clientContext, MessagePanel messagePanel) {
    super();
    this.clientContext = clientContext;
    this.messagePanel = messagePanel;
    initialize();
  }

  private void initialize() {

    // This panel contains from top to bottom: a title bar,
    // a list of conversations, and a button bar.
    this.setLayout(new BorderLayout());
    this.setPreferredSize(new Dimension(200,0));
    this.setBorder(BorderFactory.createMatteBorder(0,0,0,1,Color.BLACK));

    // Title
    final JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    final JLabel titleLabel = new JLabel("Conversations", JLabel.CENTER);
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    titlePanel.add(titleLabel);

    // Conversation list
    final JPanel listShowPanel = new JPanel(new BorderLayout());

    final DefaultListModel<String> listModel = new DefaultListModel<>();
    final JList<String> objectList = new JList<>(listModel);
    objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    objectList.setSelectedIndex(-1);

    final JScrollPane listScrollPane = new JScrollPane(objectList);
    listShowPanel.add(listScrollPane, BorderLayout.CENTER);

    // Button bar
    final JPanel buttonPanel = new JPanel();

    final JButton updateButton = new JButton("Update");
    final JButton addButton = new JButton("Add");

    updateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonPanel.add(updateButton);
    buttonPanel.add(addButton);

    this.add(titlePanel, BorderLayout.NORTH);
    this.add(listShowPanel, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);

    // User clicks Conversations Update button.
    updateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ConversationPanel.this.getAllConversations(listModel);
      }
    });

    // User clicks Conversations Add button.
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (clientContext.user.hasCurrent()) {
          final String s = (String) JOptionPane.showInputDialog(ConversationPanel.this, "Enter title:", "Add Conversation", JOptionPane.PLAIN_MESSAGE,null, null, "");
          if (s != null && s.length() > 0) {
            clientContext.conversation.startConversation(s, clientContext.user.getCurrent().id);
            ConversationPanel.this.getAllConversations(listModel);
            
            messagePanel.update(clientContext.conversation.getCurrent());
          }
        } else {
          JOptionPane.showMessageDialog(ConversationPanel.this, "You are not signed in.");
        }
      }
    });

    // User clicks on Conversation - Set Conversation to current and fill in Messages panel.
    objectList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (objectList.getSelectedIndex() != -1) {
          final int index = objectList.getSelectedIndex();
          final String data = objectList.getSelectedValue();
          final ConversationSummary cs = ConversationPanel.this.lookupByTitle(data, index);

          clientContext.conversation.setCurrent(cs);
          messagePanel.update(cs);
        }
      }
    });

    getAllConversations(listModel);
  }

  // Populate ListModel - updates display objects.
  private void getAllConversations(DefaultListModel<String> convDisplayList) {

    clientContext.conversation.updateAllConversations(false);
    convDisplayList.clear();

    for (final ConversationSummary conv : clientContext.conversation.getConversationSummaries()) {
      convDisplayList.addElement(conv.title);
    }
  }

  // Locate the Conversation object for a selected title string.
  // index handles possible duplicate titles.
  private ConversationSummary lookupByTitle(String title, int index) {

    int localIndex = 0;
    for (final ConversationSummary cs : clientContext.conversation.getConversationSummaries()) {
      if ((localIndex >= index) && cs.title.equals(title)) {
        return cs;
      }
      localIndex++;
    }
    return null;
  }
}
