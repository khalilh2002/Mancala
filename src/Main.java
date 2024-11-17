import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Welcome to Mancala!");
    System.out.println("Select Game Mode:");
    System.out.println("1. Player vs Player");
    System.out.println("2. Player vs AI");
    int choice = scanner.nextInt();

    int[] initialBoard = {
            0, 4, 4, 4, 4, 4, 4, // Player 1 side
            4, 4, 4, 4, 4, 4, 0  // Player 2/AI side
    };

    MancalaPosition start = new MancalaPosition(initialBoard, true);
    MancalaGameSearch game = new MancalaGameSearch();

    if (choice == 1) {
      game.playPvP(start);
    } else if (choice == 2) {
      game.playPvAI(start);
    } else {
      System.out.println("Invalid choice. Exiting.");
    }

    scanner.close();
  }
}
