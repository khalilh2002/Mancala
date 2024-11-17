public class MancalaMove extends Move {
  public int pitIndex;

  public MancalaMove(int pitIndex) {
    // No hardcoded validation here; validation is done elsewhere
    this.pitIndex = pitIndex;
  }

  @Override
  public String toString() {
    return "Pit: " + pitIndex;
  }
}
