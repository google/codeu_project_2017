package codeu.chat.common;

public final class ConversationSummaryListView implements ListView<ConversationSummary> {

  @Override
  public String display(ConversationSummary value) {
    return value.title;
  }
}
