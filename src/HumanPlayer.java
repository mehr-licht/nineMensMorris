public class HumanPlayer extends Player {
		
	public HumanPlayer(String name, Token player, int numPiecesPerPlayer) throws GameException {
		super(name, player, numPiecesPerPlayer);
		this.name = name;
	}

	@Override
	public boolean isAI() {
		return false;
	}
  @Override
  public boolean isRandom() {
    return false;
  }
}
