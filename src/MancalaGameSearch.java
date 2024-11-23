import java.util.*;
import java.util.stream.IntStream;

public class MancalaGameSearch {

  public static final boolean DEBUG = false;

  public static boolean PROGRAM = false;
  public static boolean HUMAN = true;
  public static int scoreplayer = 0 ;
  public static int scoreAI = 0 ;



  public boolean drawnPosition(Position p) {
    MancalaPosition pos = (MancalaPosition) p;
    return Arrays.stream(pos.board).sum() == 0;
  }

  public boolean wonPosition(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;
    return isGameOver(pos) && calculateScore(pos, player) > 0;
  }

  public float positionEvaluation(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;
    return calculateScore(pos, player);
  }

  public void printPosition(Position p) {
    MancalaPosition pos = (MancalaPosition) p;
    System.out.println("Computer: " + pos.board[13]);
    for (int i = 12; i >= 7; i--) System.out.print(pos.board[i] + " ");
    System.out.println();
    for (int i = 1; i <= 6; i++) System.out.print(pos.board[i] + " ");
    System.out.println("\nPlayer: " + pos.board[0]);
  }

  public Position[] possibleMoves(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;
    List<Position> moves = new ArrayList<>();

    // Player's moves are 1-6, Computer's moves are 7-12
    int start = player ? 1 : 7;
    int end = player ? 6 : 12;

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
      // Skip opponent's store
      if ((player && currentIndex == 13) || (!player && currentIndex == 0)) {
        continue;
      }
      newBoard[currentIndex]++;
      seeds--;
    }

    // Capture logic
    if (player && currentIndex >= 1 && currentIndex <= 6 &&
      newBoard[currentIndex] == 1 && newBoard[13 - currentIndex] > 0) {
      newBoard[0] += newBoard[currentIndex] + newBoard[13 - currentIndex];
      newBoard[currentIndex] = 0;
      newBoard[13 - currentIndex] = 0;
    } else if (!player && currentIndex >= 7 && currentIndex <= 12 &&
      newBoard[currentIndex] == 1 && newBoard[13 - currentIndex] > 0) {
      newBoard[13] += newBoard[currentIndex] + newBoard[13 - currentIndex];
      newBoard[currentIndex] = 0;
      newBoard[13 - currentIndex] = 0;
    }

    // Check if player gets another turn (last seed in own store)
    boolean nextTurn = (player && currentIndex == 0) || (!player && currentIndex == 13);
    return new MancalaPosition(newBoard, nextTurn ? player : !player);
  }

  public Move createMove() {
    Scanner scanner = new Scanner(System.in);
    int pit;
    do {
      System.out.print("Choose a pit (1-6): ");
      pit = scanner.nextInt();
    } while (pit < 1 || pit > 6);
    return new MancalaMove(pit);
  }

  private boolean isGameOver(MancalaPosition pos) {
    boolean playerSideEmpty = Arrays.stream(pos.board, 1, 7).allMatch(seeds -> seeds == 0);
    boolean computerSideEmpty = Arrays.stream(pos.board, 7, 13).allMatch(seeds -> seeds == 0);
    return playerSideEmpty || computerSideEmpty;
  }

  private int calculateScore(MancalaPosition pos, boolean player) {
    return player ? pos.board[0] - pos.board[13] : pos.board[13] - pos.board[0];
  }

  public void playGame(Position startingPosition, boolean humanPlayFirst) {
    Position current = startingPosition;
    while (true) {
      printPosition(current);

      if (wonPosition(current, PROGRAM)) {
        System.out.println("Program won!");
        break;
      } else if (wonPosition(current, HUMAN)) {
        System.out.println("Human won!");
        break;
      } else if (drawnPosition(current)) {
        System.out.println("It's a draw!");
        break;
      }

      if (humanPlayFirst) {
        System.out.println("Your move:");
        Move move = createMove();
        current = makeMove(current, HUMAN, move);
      } else {
        Vector result = alphaBeta(0, current, PROGRAM);
        current = (Position) result.elementAt(1);
      }

      humanPlayFirst = !humanPlayFirst;
    }
  }

  protected Vector alphaBetaHelper(int depth, Position p, boolean player, float alpha, float beta) {
    if (reachedMaxDepth(p, depth) || drawnPosition(p)) {
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
