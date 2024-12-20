import java.util.*;
import java.util.stream.IntStream;

public class MancalaGameSearch {

  public static final boolean DEBUG = false;
  private int heuristicType = 1; // Par défaut, utiliser l'heuristique simple
  public static int possbileHint = 3 ;
  public static int possbileHintP2 = 3 ;

  public boolean drawnPosition(Position p) {
    MancalaPosition pos = (MancalaPosition) p;
    return Arrays.stream(pos.board, 0, 6).sum() == 0 && Arrays.stream(pos.board, 7, 13).sum() == 0;
  }

  public boolean wonPosition(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;
    return isGameOver(pos) && calculateScore(pos, player) > 0;
  }

  public void setHeuristicType(int heuristicType) {
    if (heuristicType == 1 || heuristicType == 2) {
      this.heuristicType = heuristicType;
    } else {
      throw new IllegalArgumentException("Invalid heuristic type. Must be 1 or 2.");
    }
  }



  public float positionEvaluation(Position p, boolean player) {
    MancalaPosition pos = (MancalaPosition) p;

    switch (heuristicType) {
      case 1: // Heuristique simple : différence des scores
        return calculateScore(pos, player);

      case 2: // Heuristique avancée : différence des scores + graines restantes
        int seedsOnPlayerSide = player
                ? Arrays.stream(pos.board, 0, 6).sum()
                : Arrays.stream(pos.board, 7, 13).sum();
        return calculateScore(pos, player) + 0.1f * seedsOnPlayerSide;

      default:
        throw new IllegalStateException("Unexpected heuristic type: " + heuristicType);
    }
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
      System.out.println("Player 1: " + pos.board[6]); // Human's scoring pit
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



  private Vector easyAIMove(int depth, Position p, boolean player) {
    Position[] possibleMoves = possibleMoves(p, player);

    // If no moves are available, return the evaluation
    if (possibleMoves.length == 0) {
      Vector result = new Vector();
      result.addElement(positionEvaluation(p, player));
      result.addElement(null);
      return result;
    }

    Random random = new Random();

    // 80% chance to make a random move, 20% chance to use alpha-beta pruning
    boolean useRandomMove = random.nextInt(100) < 90;

    if (useRandomMove) {
      // Randomly select a move from the available moves
      int randomIndex = random.nextInt(possibleMoves.length);
      Position selectedMove = possibleMoves[randomIndex];

      Vector result = new Vector();
      result.addElement(positionEvaluation(selectedMove, player));
      result.addElement(selectedMove);
      return result;
    } else {
      // Use alpha-beta pruning with a shallow depth for "easy" difficulty
      int maxDepth = 2; // Shallow depth for easier AI
      return alphaBetaHelper(depth, p, player, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, maxDepth);
    }
  }


  private Vector mediumAIMove(int depth, Position p, boolean player) {
    Random random = new Random();

    // 70% chance to use alpha-beta pruning, 30% chance to make a random move
    boolean useAlphaBeta = random.nextInt(100) < 70;

    if (useAlphaBeta) {
      // Use alpha-beta pruning with a moderate depth
      int maxDepth = 4; // Medium-level depth
      return alphaBetaHelper(depth, p, player, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, maxDepth);
    } else {
      // Make a random move like Easy AI
      return easyAIMove(depth,p, player);
    }
  }

  // Add this method to MancalaGameSearch
  public int getHint(Position p, boolean player) {
    if (player && (MancalaGameSearch.possbileHint<1) ){
      return -2;
    }
    if ((!player) && (MancalaGameSearch.possbileHintP2<1) ){
      return -2;
    }

    Vector result = alphaBeta(0, p, player, 3); // Depth 3 for a reasonably good suggestion
    Position bestMove = (Position) result.elementAt(1);

    if (bestMove != null) {
      MancalaPosition current = (MancalaPosition) p;
      MancalaPosition suggested = (MancalaPosition) bestMove;

      // Find the move that differentiates the current board from the suggested board
      int start = player ? 0 : 7;
      int end = player ? 5 : 12;

      for (int i = start; i <= end; i++) {
        if (current.board[i] != suggested.board[i]) {
          if (player){

            MancalaGameSearch.possbileHint--;
          }else {

            MancalaGameSearch.possbileHintP2--;
          }
          return i; // Return the pit index to play
        }
      }
    }

    return -1; // Return -1 if no valid move is found
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

  private void applyCaptureLogic(int[] board, int currentIndex, boolean player) {
    if (player && currentIndex >= 0 && currentIndex <= 5 && board[currentIndex] == 1) {
      int oppositeIndex = 12 - currentIndex; // Opponent's pit
      if (board[oppositeIndex] > 0) {
        board[6] += board[currentIndex] + board[oppositeIndex]; // Player 1's scoring pit
        board[currentIndex] = 0;
        board[oppositeIndex] = 0;
      }
    } else if (!player && currentIndex >= 7 && currentIndex <= 12 && board[currentIndex] == 1) {
      int oppositeIndex = 12 - currentIndex; // Opponent's pit
      if (board[oppositeIndex] > 0) {
        board[13] += board[currentIndex] + board[oppositeIndex]; // Player 2's scoring pit
        board[currentIndex] = 0;
        board[oppositeIndex] = 0;
      }
    }
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

    // Apply capture logic after distributing stones
    applyCaptureLogic(newBoard, currentIndex, player);

    // Check if the last seed landed in the player's scoring pit (Condition 2)
    boolean nextTurn = (player && currentIndex == 6) || (!player && currentIndex == 13);

    // Return updated position with lastPit set for extra turn logic
    MancalaPosition newPosition = new MancalaPosition(newBoard, nextTurn ? player : !player);
    newPosition.setLastPit(currentIndex); // Track the last pit where the stone landed
    return newPosition;
  }



  public void playGame(Position startingPosition, boolean humanPlayFirst) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Choose an option:");
    System.out.println("1. Start a New Game");
    System.out.println("2. Load a Saved Game");
    int choice;
    do {
      System.out.print("Enter your choice (1 or 2): ");
      while (!scanner.hasNextInt()) {
        System.out.println("Invalid input, please enter 1 or 2.");
        scanner.next(); // Clear invalid input
      }
      choice = scanner.nextInt();
      if (choice != 1 && choice != 2) {
        System.out.println("Invalid option, try again!");
      }
    } while (choice != 1 && choice != 2);

    Position current;
    boolean isPlayer1Turn = humanPlayFirst;
    int mode;
    int difficulty = 2; // Default to Medium difficulty

    if (choice == 1) {
      // New game setup
      System.out.println("Choose mode:");
      System.out.println("1. Human vs AI");
      System.out.println("2. Human vs Human");
      do {
        System.out.print("Enter your choice (1 or 2): ");
        while (!scanner.hasNextInt()) {
          System.out.println("Invalid input, please enter 1 or 2.");
          scanner.next(); // Clear invalid input
        }
        mode = scanner.nextInt();
        if (mode != 1 && mode != 2) {
          System.out.println("Invalid option, try again!");
        }
      } while (mode != 1 && mode != 2);

      if (mode == 1) { // Human vs AI: Set difficulty
        MancalaGameSearch.possbileHintP2 = -1;

        System.out.println("Select AI difficulty:");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
        do {
          System.out.print("Enter AI difficulty (1, 2, or 3): ");
          while (!scanner.hasNextInt()) {
            System.out.println("Invalid input, please enter 1, 2, or 3.");
            scanner.next(); // Clear invalid input
          }
          difficulty = scanner.nextInt();
          if (difficulty < 1 || difficulty > 3) {
            System.out.println("Invalid option, try again!");
          }
        } while (difficulty < 1 || difficulty > 3);
      }

      System.out.println("Select the heuristic to use:");
      System.out.println("1. Simple heuristic (based on score)");
      System.out.println("2. Advanced heuristic (score + seeds remaining)");
      int heuristicChoice;
      do {
        System.out.print("Enter heuristic (1 or 2): ");
        while (!scanner.hasNextInt()) {
          System.out.println("Invalid input, please enter 1 or 2.");
          scanner.next(); // Clear invalid input
        }
        heuristicChoice = scanner.nextInt();
        if (heuristicChoice < 1 || heuristicChoice > 2) {
          System.out.println("Invalid option, try again!");
        }
      } while (heuristicChoice < 1 || heuristicChoice > 2);

      setHeuristicType(heuristicChoice);
      current = startingPosition;
    } else {
      // Load saved game
      System.out.print("Enter the filename of the saved game: ");
      scanner.nextLine(); // Consume newline
      String fileName = scanner.nextLine();
      Object[] loadedState = GameStateManager.loadGameState(fileName);
      if (loadedState == null) {
        System.out.println("Failed to load game. Starting a new game instead.");
        return;
      }
      current = (Position) loadedState[0];
      isPlayer1Turn = (boolean) loadedState[1];
      mode = (int) loadedState[2];
      if (mode == 1) {
        difficulty = (int) loadedState[3];
        heuristicType = (int) loadedState[4];
      }
    }

    while (true) {
      printPosition(current, mode);

      // Check for game-ending conditions
      if (wonPosition(current, false)) {
        System.out.println(mode == 1 ? "AI won!" : "Player 2 won!");
        break;
      } else if (wonPosition(current, true)) {
        System.out.println("Player 1 won!");
        break;
      } else if (drawnPosition(current)) {
        System.out.println("It's a draw!");
        break;
      }

      // Handle Player 1's turn
      // Handle Player 1's turn
      if (isPlayer1Turn) {
        System.out.println("Player 1's move:");
        while (true) {
          Move playerMove = createMove(0, 5, ((MancalaPosition) current).board);

          if (playerMove == null) { // Player chose an option
            System.out.println("Options:");
            System.out.println("1. Save the game (enter 'S')");
            System.out.println("2. Get a hint (enter 'H')");
            System.out.print("Enter your choice: ");
            String optionInput = scanner.next().toUpperCase();

            if (optionInput.equals("S")) {
              System.out.print("Enter a filename to save the game: ");
              String fileName = scanner.next();
              GameStateManager.saveGameState(
                      (MancalaPosition) current, isPlayer1Turn, mode, difficulty, heuristicType, fileName
              );
              System.out.println("Game saved successfully!");
            } else if (optionInput.equals("H")) {
              int hintIndex = getHint(current, true); // true for Player 1
              if (hintIndex == -1) {
                System.out.println("No valid moves available for a hint.");
              } else if (hintIndex == -2) {
                System.out.println("Warning you reached max hint(3)");
              } else {
                System.out.println("Hint: Pick pit " + hintIndex + " as your next move.");
              }
            } else {
              System.out.println("Invalid input! Please enter 'S' to save or 'H' for a hint.");
            }
          } else {
            current = makeMove(current, true, playerMove);
            printPosition(current, mode);

            // After move, check if Player 1 earned an extra turn
            if (didPlayerEarnExtraTurn(current, true)) {
              // Player 1 earned an extra turn, keep playing
              continue; // No turn switch, stay on Player 1
            } else {
              isPlayer1Turn = false; // Move to Player 2's turn
              break;
            }
          }
        }
      }

// Handle AI's turn or Player 2's turn
      if (!isPlayer1Turn) {
        if (mode == 1) { // AI's turn
          System.out.println("AI's move:");
          Vector result = alphaBeta(0, current, false, difficulty);
          Position bestMove = (Position) result.elementAt(1);

          if (bestMove == null) {
            System.out.println("AI cannot move. Ending game.");
            break;
          }

          current = bestMove;
          // After move, check if AI earned an extra turn
          if (didPlayerEarnExtraTurn(current, false)) {
            // AI earned an extra turn, keep playing
            continue; // No turn switch, stay on AI
          } else {
            isPlayer1Turn = true; // Move to Player 1's turn
          }
        } else { // Player 2's move
          System.out.println("Player 2's move:");
          while (true) {
            Move playerMove = createMove(7, 12, ((MancalaPosition) current).board);

            if (playerMove == null) { // Player 2 chose an option
              System.out.println("Options:");
              System.out.println("1. Save the game (enter 'S')");
              System.out.println("2. Get a hint (enter 'H')");
              System.out.print("Enter your choice: ");
              String optionInput = scanner.next().toUpperCase();

              if (optionInput.equals("S")) {
                System.out.print("Enter a filename to save the game: ");
                String fileName = scanner.next();
                GameStateManager.saveGameState(
                        (MancalaPosition) current, isPlayer1Turn, mode, difficulty, heuristicType, fileName
                );
                System.out.println("Game saved successfully.");
              } else if (optionInput.equals("H")) {
                int hintIndex = getHint(current, false); // false for Player 2
                if (hintIndex == -1) {
                  System.out.println("No valid moves available for a hint.");
                } else if (hintIndex == -2) {
                  System.out.println("Warning you reached max hint(3)");
                } else {
                  System.out.println("Hint: Pick pit " + hintIndex + " as your next move.");
                }
              } else {
                System.out.println("Invalid input! Please enter 'S' to save or 'H' for a hint.");
              }
            } else {
              current = makeMove(current, false, playerMove);
              printPosition(current, mode);

              // After move, check if Player 2 earned an extra turn
              if (didPlayerEarnExtraTurn(current, false)) {
                // Player 2 earned an extra turn, keep playing
                continue; // No turn switch, stay on Player 2
              } else {
                isPlayer1Turn = true; // Move to Player 1's turn
                break;
              }
            }
          }
        }
      }

    }
  }





  // Method to check if the player earned an extra turn
  private boolean didPlayerEarnExtraTurn(Position current, boolean isPlayer1Turn) {
    int lastPit = ((MancalaPosition) current).getLastPit(); // Add a method to track the last pit updated
    int mancalaIndex = isPlayer1Turn ? 6 : 13; // Player 1's Mancala is at 6, Player 2's at 13
    return lastPit == mancalaIndex; // Extra turn if the last stone lands in the player's Mancala
  }







  public Move createMove(int start, int end, int[] board) {
    Scanner scanner = new Scanner(System.in);
    int pit;
    while (true) {
      System.out.print("Choose a pit (" + start + "-" + end + ") or enter 'O' for options: ");
      String input = scanner.nextLine().toUpperCase();

      if (input.equals("O")) {
        return null; // Indicating save requested
      }

      try {
        pit = Integer.parseInt(input);
        if (pit < start || pit > end || pit % 7 == 6) {
          System.out.println("Invalid move! Please select a valid pit.");
        } else if (board[pit] == 0) {
          System.out.println("Invalid move! The selected pit has 0 seeds.");
        } else {
          return new MancalaMove(pit); // Valid move
        }
      } catch (NumberFormatException e) {
        System.out.println("Invalid input! Please select a valid pit or enter 'O' for options: ");
      }
    }
  }



  protected Vector alphaBeta(int depth, Position p, boolean player, int difficulty) {
    int maxDepth;
    switch (difficulty) {
      case 1: // Easy AI
        return easyAIMove(depth,p, player);
      case 2: // Medium AI
        return mediumAIMove(depth, p, player);
      case 3: // Hard AI
        maxDepth = 10; // Hard: Deep search
        break;
      default:
        maxDepth = 5; // Default to Medium
    }
    return alphaBetaHelper(depth, p, player, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, maxDepth);
  }


  protected Vector alphaBetaHelper(int depth, Position p, boolean player, float alpha, float beta, int maxDepth) {
    if (reachedMaxDepth(p, depth, maxDepth) || isGameOver((MancalaPosition) p)) {
      Vector result = new Vector();
      result.addElement(positionEvaluation(p, player));
      result.addElement(null);
      return result;
    }

    Vector best = new Vector();
    best.addElement(alpha);

    Position[] moves = possibleMoves(p, player);
    for (Position move : moves) {
      Vector result = alphaBetaHelper(depth + 1, move, !player, -beta, -alpha, maxDepth);
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

  protected boolean reachedMaxDepth(Position p, int depth, int maxDepth) {
    return depth >= maxDepth || isGameOver((MancalaPosition) p);
  }

}
