package codeu.chat.client.commandline;

import codeu.chat.common.ConversationSummary;

public final class ConversationSummaryListView implements ListView<ConversationSummary> {

  @Override
  public String display(ConversationSummary value) {
    return value.title;
  }
}
