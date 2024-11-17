import java.util.Scanner;

public class HumanPlayer extends Player {
    private Scanner scanner = new Scanner(System.in);

    public HumanPlayer(String name, boolean isPlayer1) {
        super(name, isPlayer1);
    }

    @Override
    public MancalaPosition makeMove(MancalaPosition position) {
        while (true) {
            System.out.print("Enter a valid pit index: ");
            int pitIndex = scanner.nextInt();

            // Ensure that the inputted pit index is valid for the current player
            int start = isPlayer1() ? 1 : 7;
            int end = isPlayer1() ? 6 : 12;

            if (pitIndex >= start && pitIndex <= end && position.isValidMove(new MancalaMove(pitIndex), isPlayer1())) {
                return (MancalaPosition) new MancalaGameSearch().makeMove(position, isPlayer1(), new MancalaMove(pitIndex));
            }

            System.out.println("Invalid move! Try again.");
        }
    }

}
