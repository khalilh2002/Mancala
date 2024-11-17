public abstract class Player {
    private String name;
    private boolean isPlayer1;

    public Player(String name, boolean isPlayer1) {
        this.name = name;
        this.isPlayer1 = isPlayer1;
    }

    public String getName() {
        return name;
    }

    public boolean isPlayer1() {
        return isPlayer1;
    }

    public abstract MancalaPosition makeMove(MancalaPosition position);
}
