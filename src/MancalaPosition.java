import java.util.Arrays;

public class MancalaPosition extends Position {
  public int[] board; // Represent the board as an array of pits and stores
  public boolean playerTurn; // true if it's the player's turn, false if it's the computer's

  public MancalaPosition(int[] board, boolean playerTurn) {
    this.board = board.clone();
    this.playerTurn = playerTurn;
  }

  @Override
  public String toString() {
    // Return a string representation of the board for debugging
    return "Board: " + Arrays.toString(board) + " | Player turn: " + playerTurn;
  }
}
