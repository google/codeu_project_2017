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

package codeu.chat.client.commandline;

import codeu.chat.common.ListView;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Page up and down through a list of objects and allow user to select one.
// T must implement ListViewable. This gives access to listView(), which produces
// the human-readable string that identifies the object in the list.
public final class ListNavigator<T> {

  private final List<T> selection = new ArrayList<T>();
  private final Scanner lineScanner;
  private int top;
  private int bottom;
  private final int pageSize;
  private boolean hasInt;
  private int intValue;
  private String choice;

  private final ListView<T> mView;

  // Creates a ListNavigator of the desired ListViewable subclass
  public ListNavigator(ListView<T> view, Iterable<T> objectList, Scanner scanner, int pageSize) {
    mView = view;

    this.pageSize = pageSize;
    this.lineScanner = scanner;
    for (final T e : objectList) {
      this.selection.add(e);
    }
    moveUp();  // set top and bottom for first page.
  }

  // Run the chooser. Returns true when a selection has been made, or false on an error
  // or if the user cancels the operation.
  public boolean chooseFromList() {
    if (selection.size() == 0) {
      System.out.println("ERROR: selection is empty - cannot select");
      return false;
    } else {
      while (true) {
        displayChoices();
        issuePrompt();
        getChoice();
        if (indexSelected()) {
          return true;
        } else if (moveDownSelected()) {
          moveDown();
        } else if (moveUpSelected()) {
          moveUp();
        } else if (cancelSelected()) {
          return false;
        } else {
          System.out.println("Poor choice - try again.");
        }
      }
    }
  }

  // Get the selectes object from the chooser.
  // Should be called after chooseFromList returns true (otherwise returns null).
  public T getSelectedChoice() {
    return (hasInt) ? selection.get(intValue - 1) : null;
  }

  // Print a prompt that tells the user their options:
  // 1) <n>  the index of a list item that is currently on view. Return the object.
  // 2) '+'  page down
  // 3) '-'  page back up
  // 4) '*'  cancel (return null)
  private void issuePrompt() {
    System.out.format("Enter index (%s)%s%s or '*' to cancel: ",
        (top != bottom) ? String.format("%d-%d", top, bottom) : String.format("%d", top),
        (canMoveUp()) ? " or '-' to back up" : "",
        (canMoveDown()) ? " or '+' to advance" : "");
  }

  // Display a set of entries with index numbers.
  // Number of entries displayed is determined by pageSize.
  private void displayChoices() {
    for (int i = top; i <= bottom; i++) {
      var value = mView.display(selection.get(i - 1));
      System.out.format(" [%d]: %s\n", i, value);
    }
  }

  // Process user's response to issuePrompt() prompt.
  private void getChoice() {
    final Scanner tokenScanner = new Scanner(lineScanner.nextLine());
    choice = tokenScanner.nextLine().trim();
    try {
      intValue = Integer.parseInt(choice);
      hasInt = true;
    } catch (NumberFormatException ex) {
      hasInt = false;
    }
    tokenScanner.close();
  }

  // Unseen entries precede the current view.
  private boolean canMoveUp() {
    return top > 1;
  }

  // Unseen entries follow the current view.
  private boolean canMoveDown() {
    return bottom < selection.size();
  }

  private boolean indexSelected() {
    return hasInt && (intValue >= top) && (intValue <= bottom);
  }

  private boolean moveDownSelected() {
    return choice.equals("+") && canMoveDown();
  }

  private boolean moveUpSelected() {
    return choice.equals("-") && canMoveUp();
  }

  private boolean cancelSelected() {
    return choice.equals("*");
  }

  // Move the view up one page (or to top of page).
  private void moveUp() {
    top = Math.max(top - pageSize, 1);
    bottom = Math.min(top + pageSize - 1, selection.size());
  }

  // Move the view down one page (or to bottom of page).
  private void moveDown() {
    if (selection.size() == 0) {
      return;
    }
    bottom = Math.min(bottom + pageSize, selection.size());
    top = bottom - pageSize + 1;
  }
}
