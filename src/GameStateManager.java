import java.io.*;
import java.util.Arrays;

public class GameStateManager {

    public static void saveGameState(MancalaPosition position, boolean isPlayer1Turn, int mode, int difficulty, int heuristic, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println(Arrays.toString(position.board)); // Save board state
            writer.println(isPlayer1Turn);                  // Save whose turn it is
            writer.println(mode);                           // Save game mode
            // Save difficulty only if it's Player vs AI mode
            if (mode == 1) { // Assuming mode == 1 is Player vs AI
                writer.println(difficulty);
                writer.println(heuristic);
            }
            writer.println(MancalaGameSearch.possbileHint);
            if (mode != 1){
              writer.println(MancalaGameSearch.possbileHintP2);
            }
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    public static Object[] loadGameState(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String boardLine = reader.readLine(); // Read board state
            String turnLine = reader.readLine();  // Read turn
            String modeLine = reader.readLine();  // Read game mode

            int[] board = Arrays.stream(boardLine.substring(1, boardLine.length() - 1).split(","))
                    .map(String::trim)
                    .mapToInt(Integer::parseInt)
                    .toArray();
            boolean isPlayer1Turn = Boolean.parseBoolean(turnLine);
            int mode = Integer.parseInt(modeLine);

            // Default difficulty value if not set (for Player vs Player)
            int difficulty = -1;
            int heuristic = 1;
            // Load difficulty if mode is Player vs AI
            if (mode == 1) {
                String difficultyLine = reader.readLine(); // Read difficulty for Player vs AI
                difficulty = Integer.parseInt(difficultyLine);
                String heuristicLine = reader.readLine(); // Read difficulty for Player vs AI
                heuristic = Integer.parseInt(heuristicLine);
            }
            MancalaGameSearch.possbileHint =Integer.parseInt(reader.readLine()) ;
          if (mode != 1){
            MancalaGameSearch.possbileHintP2 = Integer.parseInt(reader.readLine()) ;
          }
            return new Object[]{new MancalaPosition(board, isPlayer1Turn), isPlayer1Turn, mode, difficulty, heuristic};
        } catch (IOException e) {
            System.err.println("Error loading game: " + e.getMessage());
            return null;
        }
    }
}
