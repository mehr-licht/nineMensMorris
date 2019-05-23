public abstract class Player {
	
	protected String name;
	protected int gamesWon;
	protected int numPieces;
	protected int numPiecesOnBoard;
	protected Token playerToken;
	protected boolean canFly;
	
	protected Player() {
		gamesWon = 0;
		numPiecesOnBoard = 0;
		canFly = false;
	}
	
	protected Player(String name, Token player, int numPiecesPerPlayer) throws GameException {
		this();
		if(player != Token.PLAYER_1 && player != Token.PLAYER_2) {
			throw new GameException(""+getClass().getName()+" - Invalid Player Token: "+player);
		} else {
			this.name = name;
			numPieces = numPiecesPerPlayer;
			playerToken = player;
		}
	}
	
	public void reset() {
		numPiecesOnBoard = 0;
		canFly = false;
	}
	
	public String getName() {
		return name;
	}

	public int raiseNumPiecesOnBoard() {
		canFly = false; // it's still placing pieces
		return ++numPiecesOnBoard;
	}
	
	public int lowerNumPiecesOnBoard() {
		if(--numPiecesOnBoard == 3) {
			canFly = true;
		}
		return numPiecesOnBoard;
	}
	
	public Token getPlayerToken() {
		return playerToken;
	}

	public abstract boolean isAI();

  public abstract boolean isRandom();

}
