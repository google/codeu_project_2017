package codeu.chat.bot.tictactoe;

import codeu.chat.common.Message;
import codeu.chat.util.Uuid;

import java.util.Random;

/**
 * Created by angus on 6/2/17.
 */
public class TicTacToeGame {
    private static final int BOARD_SIZE = 3;
    private static final int PLAYER_ID = 1;
    private static final int BOT_ID = 2;

    private Uuid player; // Holds the player's UUID to make sure other players cannot interfere with the game.

    private Random rand;
    private boolean isBotTurn;
    private int[][] board;
    private int markCount;

    public TicTacToeGame() {
        rand = new Random();

        init();
    }

    /**
     * This function is responsible in setting up game-specific initialization.
     */
    public void init() {
        player = null;

        isBotTurn = true;
        board = new int[BOARD_SIZE][BOARD_SIZE];
        markCount = 0;
    }

    /**
     *
     * @return 0 if game is still going on. PLAYER_ID if player wins, BOT_ID if bot wins. -1 if tied.
     */
    private int findWinner() {
        // Check for both the bot and the player.
        for (int player = PLAYER_ID; true; player = BOT_ID) {
            // Check if the player has a horizontal win.
            for (int col = 0; col < BOARD_SIZE; col++) {
                for (int row = 0; row < BOARD_SIZE;) {
                    if (board[row][col] != player) break;

                    row++;

                    if (row == 3) return player;
                }
            }

            // Check if the player has a vertical win.
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE;) {
                    if (board[row][col] != player) break;

                    col++;

                    if (col == 3) return player;
                }
            }

            // Diagonal wins can only work for odd board sizes.
            if (BOARD_SIZE % 2 != 0) {
                // Check if the player has a diagonal win (in descending order).
                for (int i = 0; i < BOARD_SIZE; i++) {
                    if (board[i][i] != player) break;

                    i++;

                    if (i == 3) return player;
                }

                // Check if the player has a diagonal win (in ascending order).
                for (int i = 0; i < BOARD_SIZE; i++) {
                    if (board[i][BOARD_SIZE - 1 - i] != player) break;

                    i++;

                    if (i == 3) return player;
                }
            }

            // Check if the player has a diamond win (works only for board size 3)
            if (BOARD_SIZE == 3 &&
                    board[0][1] == player &&
                    board[1][0] == player &&
                    board[1][2] == player &&
                    board[2][1] == player
                ) {
                return player;
            }

            // If the for loop gets here and there hasn't been a return value, that means
            // no one won yet.
            if (player == BOT_ID) break;
        }

        // Check for tied condition.
        if (markCount == BOARD_SIZE * BOARD_SIZE) return -1;

        // Indicate that the game is still going on.
        return 0;
    }

    private void botMakeMove() {
        while (true) {
            int row = rand.nextInt(BOARD_SIZE);
            int col = rand.nextInt(BOARD_SIZE);

            // Make sure the spot generated is available.
            if (board[row][col] != 0) continue;

            board[row][col] = BOT_ID;
            markCount++;
            isBotTurn = false;
            break;
        }
    }

    private String boardToString() {
        StringBuilder str = new StringBuilder();

        str.append("\n  ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            str.append(i);
        }
        str.append("\n");

        for (int row = 0; row < BOARD_SIZE; row++) {
            str.append((char) ('A' + row));
            str.append(" ");
            for (int col = 0; col < BOARD_SIZE; col++) {
                switch (board[row][col]) {
                    case PLAYER_ID:
                        str.append("O");
                        break;
                    case BOT_ID:
                        str.append("X");
                        break;
                    default:
                        str.append(" ");
                        break;
                }
            }
            str.append("\n");
        }

        return str.toString();
    }

    /**
     * This function is responsible in handling the bot's actions from the user input.
     * @param chat
     * @return if the game is still active.
     */
    public boolean waitForAction(TicTacToeChat chat) {
        Message input = chat.getLastUserMessage();
        // Ignore the input if there's nothing new, or if the sender isn't the player.
        if (input == null) return true;

        // If the game doesn't exist yet, and someone said start, begin the game.
        if (player == null && input.content.equalsIgnoreCase("start")) {
            System.out.println("Starting game!");

            player = input.author;

            chat.addMessage("--- Tic Tac Toe --- \nStarting game! I'm X, you're O.");

            if (rand.nextBoolean()) {
                chat.addMessage("It is my turn first!");
                botMakeMove();

            } else {
                chat.addMessage("You're starting first!" +
                        "To mark a spot, follow the coordinate system provided by the board, " +
                        "using the letter first. e.g. A1, B2, C3, etc!");
                isBotTurn = false;
            }

            chat.addMessage(boardToString());
            return true;
        }

        // Check if the player sent a message, but didn't start the game.
        if (player == null) {
            chat.addMessage("Type 'start' to start a new game!");
            return true;
        }

        // If any other person sent a message, ignore it.
        if (input.author.id() != player.id()) return true;

        if (isBotTurn) {
            chat.addMessage("Wait for your turn!");
            return true;
        }

        // Handle user input.
        if (input.content.length() == 2) {
            int row = input.content.charAt(0) - 'A';
            int col = Character.getNumericValue(input.content.charAt(1));

            if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
                chat.addMessage("Invalid input. Please follow the coordinate system provided by the board, " +
                        "using the letter first. e.g. A1, B2, C3, etc!");
                chat.addMessage(boardToString());
                return true;
            }

            if (board[row][col] != 0) {
                chat.addMessage("That spot is already marked, please choose a different spot!");
                chat.addMessage(boardToString());
                return true;
            }

            board[row][col] = PLAYER_ID;
            markCount++;
            isBotTurn = true;
            botMakeMove();
            chat.addMessage(boardToString());
            // Do not return here, we need to check for winner.
        }

        // Must return true if the game is still going on.
        int winner = findWinner();

        // Check if the game is tied.
        if (winner == -1) {
            chat.addMessage("--- The game is tied! Play again! ---");
            return false;
        // Check if someone won.
        } else if (winner != 0) {
            chat.addMessage("--- " + (winner == PLAYER_ID ? "You" : "Bot") + " won this round! Play again! ---");
            return false;
        }

        return true;
    }
}
