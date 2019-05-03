import java.util.Random;

public abstract class IAPlayer extends Player {
	
	protected Random rand;
	public int numberOfMoves = 0; // TODO TESTING
	public int movesThatRemove = 0;

	public IAPlayer(String name, Token player, int numPiecesPerPlayer) throws GameException {
		super(name, player, numPiecesPerPlayer);
		rand = new Random();
	}

	@Override
	public boolean isAI() {
		return true;
	}
	
	public abstract int getIndexToPlacePiece(Board gameBoard);
	
	public abstract int getIndexToRemovePieceOfOpponent(Board gameBoard);
	
	public abstract Move getPieceMove(Board gameBoard, int gamePhase) throws GameException;
}
