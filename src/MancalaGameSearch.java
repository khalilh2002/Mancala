import java.util.*;
import java.util.stream.IntStream;

public class MancalaGameSearch {

  public static final boolean DEBUG = false;
  private int heuristicType = 1; // Par défaut, utiliser l'heuristique simple


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
    int difficulty = 2;
    // Default to Medium difficulty

    if (choice == 1) {
      // New game
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

      // Si Human vs AI, sélectionner la difficulté de l'IA
      if (mode == 1) {
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

      // Nouvelle étape : Sélection de l'heuristique
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

      // Appliquer l'heuristique sélectionnée
      setHeuristicType(heuristicChoice);

      current = startingPosition;
    } else {
      // Charger un jeu sauvegardé
      System.out.print("Enter the filename of the saved game: ");
      scanner.nextLine(); // Consume newline
      String fileName = scanner.nextLine();
      Object[] loadedState = GameStateManager.loadGameState(fileName);
      if (loadedState == null) {
        System.out.println("Failed to load game. Starting a new game instead.");
        return; // Exit or default to new game
      }
      current = (Position) loadedState[0];
      isPlayer1Turn = (boolean) loadedState[1];
      mode = (int) loadedState[2];
      if (mode == 1) {
        difficulty = (int) loadedState[3];// Load difficulty if it's a Human vs AI game
        heuristicType = (int) loadedState[4];
      }
    }

    while (true) {
      printPosition(current, mode); // Print board after each move

      // Check game-ending conditions before each turn
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
      if (isPlayer1Turn) {
        System.out.println("Player 1's move:");
        Move playerMove = createMove(0, 5, ((MancalaPosition) current).board);

        if (playerMove == null) { // Check if the player pressed 'S' to save
          System.out.print("Do you want to save the game? (Yes/No): ");
          String saveOption = scanner.next().toLowerCase();
          if (saveOption.equals("yes")) {
            System.out.print("Enter a filename to save the game: ");
            String fileName = scanner.next();
            GameStateManager.saveGameState((MancalaPosition) current, isPlayer1Turn, mode, difficulty, heuristicType,fileName);
            System.out.println("Game saved successfully.");
          } else {
            System.out.println("Continuing without saving...");
          }
          continue; // Skip the turn and repeat the loop
        } else {
          current = makeMove(current, true, playerMove); // Apply Player 1's move
          isPlayer1Turn = !isPlayer1Turn; // Toggle turn after valid move
        }
      }

      // Handle AI's turn (or Player 2's turn in Human vs Human)
      if (!isPlayer1Turn) {
        if (mode == 1) {
          System.out.println("AI's move:");
          Vector result = alphaBeta(0, current, false, difficulty); // Pass difficulty to AI
          Position bestMove = (Position) result.elementAt(1);

          if (bestMove == null) { // No valid moves (shouldn't happen if game-ending checks work)
            System.out.println("AI cannot move. Ending game.");
            break;
          }

          current = bestMove; // Apply AI's move
        } else {
          System.out.println("Player 2's move:");
          Move playerMove = createMove(7, 12, ((MancalaPosition) current).board);
          if (playerMove == null) { // Check if the player pressed 'S' to save
            System.out.print("Do you want to save the game? (Yes/No): ");
            String saveOption = scanner.next().toLowerCase();
            if (saveOption.equals("yes")) {
              System.out.print("Enter a filename to save the game: ");
              String fileName = scanner.next();
              GameStateManager.saveGameState((MancalaPosition) current, isPlayer1Turn, mode, difficulty, heuristicType,fileName);
              System.out.println("Game saved successfully.");
            } else {
              System.out.println("Continuing without saving...");
            }
            continue; // Skip the turn and repeat the loop
          } else {
            current = makeMove(current, false, playerMove); // Apply Player 2's move
          }
        }

        isPlayer1Turn = !isPlayer1Turn; // Toggle turn after valid move
      }
    }
  }




  public Move createMove(int start, int end, int[] board) {
    Scanner scanner = new Scanner(System.in);
    int pit;
    while (true) {
      System.out.print("Choose a pit (" + start + "-" + end + ") or press 'S' to save: ");
      String input = scanner.nextLine().toUpperCase();

      if (input.equals("S")) {
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
        System.out.println("Invalid input! Please select a valid pit or press 'S' to save.");
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
