public class MancalaPosition extends Position {
  public int[] board;
  public boolean playerTurn;

  public MancalaPosition(int[] board, boolean playerTurn) {
    this.board = board.clone();
    this.playerTurn = playerTurn;
  }
}
