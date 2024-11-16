public class MancalaGameSearch extends GameSearch {

  private final Boolean Debug = false;

  /*
  *         @params MancalaPosition
  *         @return total score if total > 0 player is wining else losing or draw if 0
  * */
  public int calculateScore(MancalaPosition position){
    int playerScore = position.board[0];
    int computerScore = position.board[13];

    if (Debug){
      System.out.println("---calculateScore---");
      System.out.println("Player Score: " + playerScore);
      System.out.println("Computer Score: " + computerScore);
    }

    return playerScore - computerScore ;
  }





  @Override
  public boolean drawnPosition(Position p) {
    return calculateScore((MancalaPosition) p) == 0; // return true if calculateScore==0
  }



  @Override
  public boolean wonPosition(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;
    if (!isGameOver(pos)) return false;
    int score = calculateScore(pos);
    return (player && score > 0) || (!player && score < 0);
  }

  /*
  *       if the sides has at least one the games is not over
  * */
  private boolean isGameOver(MancalaPosition pos) {
    boolean isGameOver = true;
    for (int i = 1; i < 7; i++) {
      if (pos.board[i] == 0) {
        isGameOver = false;
        break;
      }
    }

    for (int i = 7; i < 13; i++) {
      if (pos.board[i] == 0) {
        isGameOver = false;
        break;
      }
    }
    return isGameOver;
  }

  @Override
  public float positionEvaluation(Position p, boolean player) {
    return calculateScore((MancalaPosition) p);
  }

  @Override
  public void printPosition(Position p) {
    MancalaPosition pos = (MancalaPosition) p;
    System.out.println("Player Score: " + calculateScore(pos));
    for (int i = 0; i < 7; i++) {
      System.out.print(" |" + pos.board[i]+"| ");
    }
    System.out.println();

    System.out.println("Computer Score: " + calculateScore(pos));
    for (int i = 7; i < 14; i++) {
      System.out.print(" |" + pos.board[i]+"| ");

    }
    System.out.println();


  }

  @Override
  public Position[] possibleMoves(Position p, boolean player) {
    return new Position[0];
  }

  @Override
  public Position makeMove(Position p, boolean player, Move move) {
    MancalaMove mov = (MancalaMove) move;
    MancalaPosition pos = (MancalaPosition) p;
    MancalaPosition newPos = new MancalaPosition(pos.board , player);
    for (int i = 1; i <= pos.board[mov.pitIndex]; i++) {
      newPos.board[mov.pitIndex+i]++;
    }

    return newPos;
  }

  @Override
  public boolean reachedMaxDepth(Position p, int depth) {
    return false;
  }

  @Override
  public Move createMove() {
    return null;
  }
}
