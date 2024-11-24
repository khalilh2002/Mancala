public class MancalaPosition extends Position {
  public int[] board;
  public boolean playerTurn;
  private int lastPit; // New field to track the last pit where a stone was placed

  public MancalaPosition(int[] board, boolean playerTurn) {
    this.board = board.clone();
    this.playerTurn = playerTurn;
    this.lastPit = -1; // Initialize with an invalid value
  }

  // Getter and Setter for lastPit
  public int getLastPit() {
    return lastPit;
  }

  public void setLastPit(int lastPit) {
    this.lastPit = lastPit;
  }
}
