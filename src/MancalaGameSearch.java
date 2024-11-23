import java.util.*;
import java.util.stream.IntStream;

public class MancalaGameSearch {

  public static final boolean DEBUG = false;

  public boolean drawnPosition(Position p) {
    MancalaPosition pos = (MancalaPosition) p;
    return Arrays.stream(pos.board, 0, 6).sum() == 0 && Arrays.stream(pos.board, 7, 13).sum() == 0;
  }

  public boolean wonPosition(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;
    return isGameOver(pos) && calculateScore(pos, player) > 0;
  }

  public float positionEvaluation(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;
    return calculateScore(pos, player);
  }

  public void printPosition(Position p, int mode) {
    MancalaPosition pos = (MancalaPosition) p;

    if (mode == 2) { // Human vs Human
      System.out.println("Player 2: " + pos.board[13]); // Player 2's scoring pit
    } else { // Human vs AI
      System.out.println("Computer: " + pos.board[13]); // AI's scoring pit
    }

    for (int i = 12; i >= 7; i--) System.out.print(pos.board[i] + " ");
    System.out.println();

    for (int i = 0; i <= 5; i++) System.out.print(pos.board[i] + " ");
    System.out.println();

    if (mode == 2) { // Human vs Human
      System.out.println("Player 1: " + pos.board[6]); // Player 1's scoring pit
    } else { // Human vs AI
      System.out.println("Player: " + pos.board[6]); // Human's scoring pit
    }
  }


  public Position[] possibleMoves(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;
    List<Position> moves = new ArrayList<>();

    int start = player ? 0 : 7;
    int end = player ? 5 : 12;

    for (int i = start; i <= end; i++) {
      if (pos.board[i] > 0) {
        moves.add(makeMove(pos, player, new MancalaMove(i)));
      }
    }

    return moves.toArray(new Position[0]);
  }

  public Position makeMove(Position p, boolean player, Move m) {
    MancalaPosition pos = (MancalaPosition) p;
    MancalaMove move = (MancalaMove) m;

    int[] newBoard = pos.board.clone();
    int pitIndex = move.pitIndex;
    int seeds = newBoard[pitIndex];
    newBoard[pitIndex] = 0;

    int currentIndex = pitIndex;
    while (seeds > 0) {
      currentIndex = (currentIndex + 1) % 14;

      // Skip the opponent's scoring pit
      if ((player && currentIndex == 13) || (!player && currentIndex == 6)) {
        continue;
      }

      newBoard[currentIndex]++;
      seeds--;
    }

    // Check if the last seed landed in the player's scoring pit
    if (player && currentIndex == 6) {

    } else if (!player && currentIndex == 13) {

    }

    // Capture logic
    if (player && currentIndex >= 0 && currentIndex <= 5 && newBoard[currentIndex] == 1) {
      int oppositeIndex = 12 - currentIndex; // Opposite pit index
      if (newBoard[oppositeIndex] > 0) {
        newBoard[6] += newBoard[currentIndex] + newBoard[oppositeIndex]; // Add to player's scoring pit
        newBoard[currentIndex] = 0;
        newBoard[oppositeIndex] = 0;
      }
    } else if (!player && currentIndex >= 7 && currentIndex <= 12 && newBoard[currentIndex] == 1) {
      int oppositeIndex = 12 - currentIndex; // Opposite pit index
      if (newBoard[oppositeIndex] > 0) {
        newBoard[13] += newBoard[currentIndex] + newBoard[oppositeIndex]; // Add to AI's scoring pit
        newBoard[currentIndex] = 0;
        newBoard[oppositeIndex] = 0;
      }
    }

    // Check if the player gets another turn
    boolean nextTurn = (player && currentIndex == 6) || (!player && currentIndex == 13);

    return new MancalaPosition(newBoard, nextTurn ? player : !player);
  }

  private boolean isGameOver(MancalaPosition pos) {
    boolean playerSideEmpty = Arrays.stream(pos.board, 0, 6).allMatch(seeds -> seeds == 0);
    boolean computerSideEmpty = Arrays.stream(pos.board, 7, 13).allMatch(seeds -> seeds == 0);
    return playerSideEmpty || computerSideEmpty;
  }

  private int calculateScore(MancalaPosition pos, boolean player) {
    int playerScore = pos.board[6];
    int computerScore = pos.board[13];
    return player ? playerScore - computerScore : computerScore - playerScore;
  }

  public Move createMove() {
    Scanner scanner = new Scanner(System.in);
    int pit;
    do {
      System.out.print("Choose a pit (0-5): ");
      pit = scanner.nextInt();
    } while (pit < 0 || pit > 5);
    return new MancalaMove(pit);
  }

  public void playGame(Position startingPosition, boolean humanPlayFirst) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Choose mode:");
    System.out.println("1. Human vs AI");
    System.out.println("2. Human vs Human");
    int mode;
    do {
      System.out.print("Enter your choice (1 or 2): ");
      mode = scanner.nextInt();
    } while (mode != 1 && mode != 2);

    Position current = startingPosition;
    boolean isPlayer1Turn = humanPlayFirst; // Track turns for Human vs Human mode.

    while (true) {
      printPosition(current,mode);

      // Check game-ending conditions
      if (wonPosition(current, false)) {
        System.out.println(mode == 1 ? "Program won!" : "Player 2 won!");
        break;
      } else if (wonPosition(current, true)) {
        System.out.println("Player 1 won!");
        break;
      } else if (drawnPosition(current)) {
        System.out.println("It's a draw!");
        break;
      }

      // Human vs AI mode
      if (mode == 1) {
        if (humanPlayFirst) {
          System.out.println("Your move:");
          Move move = createMove(0, 5, ((MancalaPosition) current).board); // Human moves from pits 0-5
          current = makeMove(current, true, move);
        } else {
          System.out.println("AI's move:");
          Vector result = alphaBeta(0, current, false); // AI calculates move
          current = (Position) result.elementAt(1);
        }
        humanPlayFirst = !humanPlayFirst; // Toggle turns

        // Human vs Human mode
      } else {
        if (isPlayer1Turn) {
          System.out.println("Player 1's move (choose from pits 0-5):");
          Move move = createMove(0, 5, ((MancalaPosition) current).board); // Pass the board
          current = makeMove(current, true, move);
        } else {
          System.out.println("Player 2's move (choose from pits 7-12):");
          Move move = createMove(7, 12, ((MancalaPosition) current).board); // Pass the board
          current = makeMove(current, false, move);
        }

        isPlayer1Turn = !isPlayer1Turn; // Toggle turns
      }
    }
  }



  public Move createMove(int start, int end, int[] board) {
    Scanner scanner = new Scanner(System.in);
    int pit;

    while (true) {
      System.out.print("Choose a pit (" + start + "-" + end + "): ");
      pit = scanner.nextInt();

      // Validate the chosen pit
      if (pit < start || pit > end || pit % 7 == 6) {
        System.out.println("Invalid move! Please select a valid pit.");
      } else if (board[pit] == 0) {
        System.out.println("Invalid move! The selected pit has 0 seeds.");
      } else {
        return new MancalaMove(pit); // Valid move
      }
    }
  }




  protected Vector alphaBetaHelper(int depth, Position p, boolean player, float alpha, float beta) {
    if (reachedMaxDepth(p, depth) || isGameOver((MancalaPosition) p)) {
      Vector result = new Vector();
      result.addElement(positionEvaluation(p, player));
      result.addElement(null);
      return result;
    }

    Vector best = new Vector();
    best.addElement(alpha);

    Position[] moves = possibleMoves(p, player);
    for (Position move : moves) {
      Vector result = alphaBetaHelper(depth + 1, move, !player, -beta, -alpha);
      float value = -((Float) result.elementAt(0));

      if (value > alpha) {
        alpha = value;
        best = new Vector();
        best.addElement(alpha);
        best.addElement(move);
        for (int i = 1; i < result.size(); i++) {
          best.addElement(result.elementAt(i));
        }
      }

      if (alpha >= beta) break;
    }

    return best;
  }

  protected Vector alphaBeta(int depth, Position p, boolean player) {
    return alphaBetaHelper(depth, p, player, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
  }

  protected boolean reachedMaxDepth(Position p, int depth) {
    return depth > 10 || isGameOver((MancalaPosition) p);
  }
}
