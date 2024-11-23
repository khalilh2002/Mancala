public class Main {
  public static void main(String[] args) {
    int[] initialBoard = {0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0};
    MancalaPosition start = new MancalaPosition(initialBoard, true);
    MancalaGameSearch game = new MancalaGameSearch();
    game.playGame(start, true); // Human goes first
  }
}
