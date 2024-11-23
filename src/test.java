public interface test {
    boolean drawnPosition(Position p);

    boolean wonPosition(Position p, boolean player);

    float positionEvaluation(Position p, boolean player);

    void printPosition(Position p);

    Position[] possibleMoves(Position p, boolean player);

    Position makeMove(Position p, boolean player, Move m);

    Move createMove();
}
