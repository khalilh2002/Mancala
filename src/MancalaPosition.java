import java.util.Arrays;

public class MancalaPosition extends Position {
  private final int[] board;
  private boolean playerTurn; // true for Player 1, false for Player 2/AI

  public MancalaPosition(int[] board, boolean playerTurn) {
    this.board = board;
    this.playerTurn = playerTurn;
  }

  public int[] getBoard() {
    return board;
  }

  public boolean isPlayerTurn() {
    return playerTurn;
  }

  // Check if the game is over
  public boolean isGameOver() {
    boolean allEmptyPlayer1 = true;
    boolean allEmptyPlayer2 = true;

    // Check if Player 1's pits are all empty
    for (int i = 0; i < 6; i++) {
      if (board[i] != 0) {
        allEmptyPlayer1 = false;
        break;
      }
    }

    // Check if Player 2's pits are all empty
    for (int i = 7; i < 13; i++) {
      if (board[i] != 0) {
        allEmptyPlayer2 = false;
        break;
      }
    }




    return allEmptyPlayer1 || allEmptyPlayer2;
  }


  // Handle collecting remaining stones into stores
  public void finalizeBoard() {
    for (int i = 1; i <= 6; i++) {
      board[0] += board[i];
      board[i] = 0;
    }

    for (int i = 7; i <= 12; i++) {
      board[13] += board[i];
      board[i] = 0;
    }
  }


  // Execute a move and update the board state
  public MancalaPosition executeMove(MancalaMove move) {
    int pitIndex = move.pitIndex;
    int stones = board[pitIndex];
    board[pitIndex] = 0;

    int currentIndex = pitIndex;
    while (stones > 0) {
      currentIndex = (currentIndex + 1) % 14;

      // Skip opponent's store
      if (playerTurn && currentIndex == 13) continue;
      if (!playerTurn && currentIndex == 0) continue;

      board[currentIndex]++;
      stones--;
    }

    // Check for capture rule
    if (playerTurn && currentIndex >= 1 && currentIndex <= 6 && board[currentIndex] == 1) {
      board[0] += board[currentIndex] + board[12 - currentIndex];
      board[currentIndex] = board[12 - currentIndex] = 0;
    } else if (!playerTurn && currentIndex >= 7 && currentIndex <= 12 && board[currentIndex] == 1) {
      board[13] += board[currentIndex] + board[12 - currentIndex];
      board[currentIndex] = board[12 - currentIndex] = 0;
    }

    // Determine next player's turn
    boolean nextPlayerTurn = !(playerTurn && currentIndex == 0 || !playerTurn && currentIndex == 13);

    return new MancalaPosition(board, nextPlayerTurn);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    MancalaPosition that = (MancalaPosition) obj;
    return playerTurn == that.playerTurn && Arrays.equals(this.board, that.board);
  }

  @Override
  public int hashCode() {
    return 31 * Arrays.hashCode(board) + (playerTurn ? 1 : 0);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Player 2: ");
    for (int i = 12; i > 6; i--) {
      sb.append(board[i]).append(" ");
    }
    sb.append("\n");
    sb.append("         ").append(board[13]).append("\n");
    sb.append("Player 1: ");
    for (int i = 1; i <= 6; i++) {
      sb.append(board[i]).append(" ");
    }
    sb.append("\n         ").append(board[0]).append("\n");
    return sb.toString();
  }

  public boolean isValidMove(MancalaMove move, boolean isPlayer1) {
    int pitIndex = move.pitIndex;

    // Define the range of pits for each player
    int start = isPlayer1 ? 1 : 7;
    int end = isPlayer1 ? 6 : 12;

    // Ensure the pit is within the valid range and contains stones
    return pitIndex >= start && pitIndex <= end && board[pitIndex] > 0;
  }


}
