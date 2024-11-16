import static java.lang.System.exit;

public class Main {
  public static void main(String[] args) {
    int[] initialBoard = {
      0, 4, 4, 4, 4, 4, 4, // Player side
      4, 4, 4, 4, 4, 4, 0  // Computer side
    };

    MancalaPosition start = new MancalaPosition(initialBoard, true);
    MancalaGameSearch game = new MancalaGameSearch();
    game.printPosition(start);
    game.playGame(start, true); // Let the human player start
  }
}
