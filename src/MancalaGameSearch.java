import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class MancalaGameSearch extends GameSearch {
  private Scanner scanner = new Scanner(System.in);

  @Override
  public boolean drawnPosition(Position p) {
    MancalaPosition pos = (MancalaPosition) p;
    return pos.getBoard()[0] + pos.getBoard()[13] == 48;
  }



  @Override
  public boolean wonPosition(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;
    return pos.getBoard()[0] > 24 || pos.getBoard()[13] > 24;
  }

  @Override
  public float positionEvaluation(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;
    float playerStore = player ? pos.getBoard()[0] : pos.getBoard()[13];
    float opponentStore = player ? pos.getBoard()[13] : pos.getBoard()[0];
    return playerStore - opponentStore;
  }

  public void playPvP(MancalaPosition start) {
    Player player1 = new HumanPlayer("Player 1", true);
    Player player2 = new HumanPlayer("Player 2", false);
    playGame(start, player1, player2);
  }

  public void playPvAI(MancalaPosition start) {
    Player player1 = new HumanPlayer("Player", true);
    Player player2 = new AIPlayer("AI", false, this); // Pass this to use search logic
    playGame(start, player1, player2);
  }

  private void playGame(MancalaPosition position, Player player1, Player player2) {
    while (true) {
      printPosition(position); // Display the current board state

      // Check game-over conditions
      if (drawnPosition(position)) {
        System.out.println("It's a draw!");
        break;
      }
      if (wonPosition(position, true)) {
        System.out.println(player1.getName() + " wins!");
        break;
      }
      if (wonPosition(position, false)) {
        System.out.println(player2.getName() + " wins!");
        break;
      }

      // Current player's turn
      if (position.isPlayerTurn()) {
        System.out.println(player1.getName() + "'s turn.");
        position = player1.makeMove(position);
      } else {
        System.out.println(player2.getName() + "'s turn.");
        position = player2.makeMove(position);
      }
    }
  }

  // Add the method for printing the position
  @Override
  public void printPosition(Position position) {
    MancalaPosition mancalaPosition = (MancalaPosition) position;
    int[] board = mancalaPosition.getBoard();

    // Format the board for display
    System.out.println("Player 2: " + formatRow(board, 7, 12));
    System.out.println("P2 Store: " + board[13] + "                    P1 Store: " + board[0]);
    System.out.println("Player 1: " + formatRow(board, 1, 6));
  }

  private String formatRow(int[] board, int start, int end) {
    StringBuilder row = new StringBuilder();
    for (int i = start; i <= end; i++) {
      row.append(board[i]).append(" ");
    }
    return row.toString().trim();
  }

  @Override
  public Position[] possibleMoves(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;
    List<Position> moves = new ArrayList<>();
    int start = player ? 1 : 7;
    int end = player ? 6 : 12;
    for (int i = start; i <= end; i++) {
      if (pos.getBoard()[i] > 0) {
        MancalaPosition newPos = (MancalaPosition) makeMove(pos, player, new MancalaMove(i));
        moves.add(newPos);
      }
    }
    return moves.toArray(new Position[0]);
  }

  @Override
  public Position makeMove(Position p, boolean player, Move move) {
    MancalaPosition pos = (MancalaPosition) p;
    MancalaMove mancalaMove = (MancalaMove) move;
    int[] board = pos.getBoard().clone(); // Clone the board to avoid side effects
    int pitIndex = mancalaMove.pitIndex;



    // Validate the selected pit
    if (pitIndex < 1 || pitIndex > 12 || board[pitIndex] <= 0) {
      throw new IllegalArgumentException("Invalid pit index: " + pitIndex);
    }

    // Sowing stones
    int stones = board[pitIndex];
    board[pitIndex] = 0;
    int currentIndex = pitIndex;

    while (stones > 0) {
      currentIndex = (currentIndex + 1) % 14;

      // Skip the opponent's store
      if ((player && currentIndex == 13) || (!player && currentIndex == 0)) {
        continue;
      }

      board[currentIndex]++;
      stones--;
    }

    // Check capture
    if (currentIndex > 0 && currentIndex < 13 && board[currentIndex] == 1) {
      int oppositeIndex = 12 - currentIndex; // Opposite pit
      if ((player && currentIndex <= 6) || (!player && currentIndex >= 7)) {
        board[player ? 0 : 13] += board[currentIndex] + board[oppositeIndex];
        board[currentIndex] = 0;
        board[oppositeIndex] = 0;
      }
    }

    // Determine if the player gets another turn
    boolean nextPlayerTurn = (player && currentIndex == 0) || (!player && currentIndex == 13) ? player : !player;

    return new MancalaPosition(board, nextPlayerTurn);
  }




  @Override
  public boolean reachedMaxDepth(Position p, int depth) {
    return depth >= 10;
  }

  @Override
  public Move createMove() {
    System.out.println("Enter a pit index (1-6 for Player 1, 7-12 for Player 2):");
    int pitIndex = scanner.nextInt();
    return new MancalaMove(pitIndex);
  }

  public Move getBestMove(Position position) {
    Vector evaluation = alphaBeta(0, position, PROGRAM); // Assuming AI is PROGRAM (false)

    // Retrieve the best position
    Position bestPosition = (Position) evaluation.elementAt(1);
    if (bestPosition == null) {
      throw new IllegalStateException("No valid moves available.");
    }

    // Compare each possible move to find the one that matches the resulting position
    Position[] possiblePositions = possibleMoves(position, PROGRAM);
    for (int i = 0; i < possiblePositions.length; i++) {
      if (bestPosition.equals(possiblePositions[i])) {
        // Return the move corresponding to the matching position
        int pitIndex = PROGRAM ? i + 1 : i + 7; // Adjust index for Player 2
        return new MancalaMove(pitIndex);
      }
    }

    throw new IllegalStateException("Best move not found.");
  }



}
