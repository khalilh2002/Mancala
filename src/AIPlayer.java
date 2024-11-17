public class AIPlayer extends Player {
    private MancalaGameSearch gameSearch;

    public AIPlayer(String name, boolean isPlayer1, MancalaGameSearch gameSearch) {
        super(name, isPlayer1);
        this.gameSearch = gameSearch;
    }

    @Override
    public MancalaPosition makeMove(MancalaPosition position) {
        System.out.println("AI is thinking...");
        Move bestMove = gameSearch.getBestMove(position);
        return (MancalaPosition) gameSearch.makeMove(position, isPlayer1(), bestMove);
    }

}
