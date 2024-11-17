import java.util.Scanner;

public class MancalaGame {
    private MancalaGameSearch gameSearch = new MancalaGameSearch();
    private Player player1;
    private Player player2;

    private static final int[] INITIAL_BOARD = {
            0, 4, 4, 4, 4, 4, 4, // Player 1's side (index 1-6)
            4, 4, 4, 4, 4, 4, 0  // Player 2's side (index 7-12), and stores (index 0, 13)
    };


    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Mancala!");
        System.out.println("Choose Game Mode:");
        System.out.println("1. Player vs Player");
        System.out.println("2. Player vs AI");

        int choice = scanner.nextInt();

        if (choice == 1) {
            player1 = new HumanPlayer("Player 1", true);
            player2 = new HumanPlayer("Player 2", false);
        } else if (choice == 2) {
            player1 = new HumanPlayer("Player", true);
            player2 = new AIPlayer("AI", false, gameSearch);
        } else {
            System.out.println("Invalid choice. Exiting.");
            return;
        }

        playGame();
    }

    private void playGame() {
        MancalaPosition position = new MancalaPosition(INITIAL_BOARD.clone(), true); // Player 1 starts
        while (true) {
            // Print the board
            gameSearch.printPosition(position);

            // Game-over checks
            if (gameSearch.drawnPosition(position)) {
                System.out.println("It's a draw!");
                break;
            }
            if (gameSearch.wonPosition(position, true)) {
                System.out.println(player1.getName() + " wins!");
                break;
            }
            if (gameSearch.wonPosition(position, false)) {
                System.out.println(player2.getName() + " wins!");
                break;
            }

            // Player turns
            if (position.isPlayerTurn()) {
                System.out.println(player1.getName() + "'s turn.");
                position = player1.makeMove(position);
            } else {
                System.out.println(player2.getName() + "'s turn.");
                position = player2.makeMove(position);
            }
        }
    }


    public static void main(String[] args) {
        MancalaGame game = new MancalaGame();
        game.start();
    }
}
