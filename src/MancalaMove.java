public class MancalaMove extends Move {
  public int pitIndex; // Index of the pit chosen for the move

  public MancalaMove(int pitIndex) {
    this.pitIndex = pitIndex;
  }

  public String toString() {
    return "Pit Index: " + pitIndex;
  }
}
