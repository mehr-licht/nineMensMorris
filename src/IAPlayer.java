import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class IAPlayer extends Player {

  protected Random rand;
  public int numberOfMoves = 0; // TODO TESTING
  public int movesThatRemove = 0;
  private int depth;
  private Token opponentPlayer;
  private Move currentBestMove;
  public int bestScore = 0;
  static final int maxScore = 1000000;



  public IAPlayer(String name, Token player, int numPiecesPerPlayer, int depth)
      throws GameException {
    super(name, player, numPiecesPerPlayer);
    rand = new Random();
    if (depth < 1) {
      throw new GameException("" + getClass().getName() + " - Invalid Minimax Player Depth");
    }
    this.depth = depth;
    opponentPlayer = (player == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
  }

  @Override
  public boolean isAI() {
    return true;
  }

  private void applyMove(Move move, Token player, Board gameBoard, int gamePhase)
      throws GameException {

    // Try this move for the current player
    Position position = gameBoard.getPosition(move.destIndex);
    position.setAsOccupied(player);

    if (gamePhase == Game.PLACING_PHASE) {
      gameBoard.incNumPiecesOfPlayer(player);
    } else {
      gameBoard.getPosition(move.srcIndex).setAsUnoccupied();
    }

    if (move.removePieceOnIndex != -1) { // this move removed a piece from opponent
      Position removed = gameBoard.getPosition(move.removePieceOnIndex);
      removed.setAsUnoccupied();
      gameBoard.decNumPiecesOfPlayer(getOpponentToken(player));
    }
  }

  private void undoMove(Move move, Token player, Board gameBoard, int gamePhase)
      throws GameException {
    // Undo move
    Position position = gameBoard.getPosition(move.destIndex);
    position.setAsUnoccupied();

    if (gamePhase == Game.PLACING_PHASE) {
      gameBoard.decNumPiecesOfPlayer(player);
    } else {
      gameBoard.getPosition(move.srcIndex).setAsOccupied(player);
    }

    if (move.removePieceOnIndex != -1) {
      Token opp = getOpponentToken(player);
      gameBoard.getPosition(move.removePieceOnIndex).setAsOccupied(opp);
      gameBoard.incNumPiecesOfPlayer(opp);
    }
  }

  private Token getOpponentToken(Token player) {
    if (player == playerToken) {
      return opponentPlayer;
    } else {
      return playerToken;
    }
  }

  public int getIndexToPlacePiece(Board gameBoard) {
    numberOfMoves = 0; // TODO TESTING
    movesThatRemove = 0; // TODO TESTING

    try {
      List<Move> moves =
          generateMoves(gameBoard, playerToken, Game.PLACING_PHASE); // sorted already

      for (Move move : moves) {
        applyMove(move, playerToken, gameBoard, Game.PLACING_PHASE);
        move.score +=
            alphaBeta(opponentPlayer, gameBoard, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        undoMove(move, playerToken, gameBoard, Game.PLACING_PHASE);
      }

      Collections.sort(moves, new IAPlayer.HeuristicComparatorMax());

      // if there are different moves with the same score it returns one of them randomly
      List<Move> bestMoves = new ArrayList<Move>();
      int bestScore = moves.get(0).score;
      bestMoves.add(moves.get(0));
      for (int i = 1; i < moves.size(); i++) {
        if (moves.get(i).score == bestScore) {
          bestMoves.add(moves.get(i));
        } else {
          break;
        }
      }
      currentBestMove = bestMoves.get(rand.nextInt(bestMoves.size()));
      return currentBestMove.destIndex;
    } catch (GameException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    Log.error("Should not get here");
    return -1;
  }

  public int getIndexToRemovePieceOfOpponent(Board gameBoard) {
    return currentBestMove.removePieceOnIndex;
  }

  public Move getPieceMove(Board gameBoard, int gamePhase) throws GameException {
    numberOfMoves = 0; // TODO TESTING
    movesThatRemove = 0; // TODO TESTING

    try {

      List<Move> moves =
          generateMoves(
              gameBoard, playerToken, getGamePhase(gameBoard, playerToken)); // sorted already

      for (Move move : moves) {
        applyMove(move, playerToken, gameBoard, Game.MOVING_PHASE);
        move.score +=
            alphaBeta(opponentPlayer, gameBoard, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        undoMove(move, playerToken, gameBoard, Game.MOVING_PHASE);
      }

      Collections.sort(moves, new IAPlayer.HeuristicComparatorMax());

      // if there are different moves with the same score it returns one of them randomly
      List<Move> bestMoves = new ArrayList<Move>();
      int bestScore = moves.get(0).score;
      bestMoves.add(moves.get(0));
      for (int i = 1; i < moves.size(); i++) {
        if (moves.get(i).score == bestScore) {
          bestMoves.add(moves.get(i));
        } else {
          break;
        }
      }
      currentBestMove = bestMoves.get(rand.nextInt(bestMoves.size()));
      return currentBestMove;
    } catch (GameException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    Log.error("Should not get here");
    return null;
  }

  private int alphaBeta(Token player, Board gameBoard, int depth, int alpha, int beta) {

    int gameOver;
    List<Move> childMoves;

    try {
      int gamePhase = getGamePhase(gameBoard, player);

      if (depth == 0) { // depth reached, evaluate score
        return evaluate(gameBoard, gamePhase);
      } else if ((gameOver = checkGameOver(gameBoard)) != 0) { // gameover
        return gameOver;
      } else if ((childMoves = generateMoves(gameBoard, player, gamePhase)).isEmpty()) {
        if (player
            == playerToken) { // IT SHOULD RETURN DIFFERENT VALUES RIGHT? IF THE BOT DOESN'T HAVE
                              // ANY POSSIBLE MOVES, THEN THE PLAYER WINS, AND RETURNS MAX VALUE???
          return -maxScore;
        } else {
          return maxScore;
        }
      } else {

        for (Move move : childMoves) {

          applyMove(move, player, gameBoard, gamePhase);

          if (player == playerToken) { // maximizing player
            alpha = Math.max(alpha, alphaBeta(opponentPlayer, gameBoard, depth - 1, alpha, beta));

            if (beta <= alpha) {
              undoMove(move, player, gameBoard, gamePhase);
              break; // cutoff
            }
          } else { //  minimizing player
            beta = Math.min(beta, alphaBeta(playerToken, gameBoard, depth - 1, alpha, beta));
            if (beta <= alpha) {
              undoMove(move, player, gameBoard, gamePhase);
              break; // cutoff
            }
          }
          undoMove(move, player, gameBoard, gamePhase);
        }

        if (player == playerToken) {
          return alpha;
        } else {
          return beta;
        }
      }
    } catch (GameException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    Log.error("SHOULD NOT GET HERE!");
    return -1;
  }

  private void preEvaluation(Board gameBoard, Evaluation eval) throws GameException {

    for (int i = 0; i < Board.NUM_MILL_COMBINATIONS; i++) {
      try {
        Position[] row = gameBoard.getMillCombination(i);
        for (int j = 0; j < Board.NUM_POSITIONS_IN_EACH_MILL; j++) {
          if (row[j].getPlayerOccupyingIt() == playerToken) {
            eval.setPlayerPieces(eval.getPlayerPieces()+1);
          } else if (row[j].getPlayerOccupyingIt() == Token.NO_PLAYER) {
            eval.setEmptyCells(eval.getEmptyCells()+1);
          } else {
            eval.setOpponentPieces(eval.getOpponentPieces()+1);
          }
        }
      } catch (GameException e) {
        e.printStackTrace();
      }
      if (eval.getPlayerPieces() == 3) {
        eval.setR01(eval.getR01()+1);
      } else if (eval.getPlayerPieces() == 2 && eval.getEmptyCells() == 1) {
        eval.setR11(eval.getR11()+1);
      } else if (eval.getPlayerPieces() == 1 && eval.getEmptyCells() == 2) {
        eval.setScore(eval.getScore()+1);
      } else if (eval.getOpponentPieces() == 3) {
        eval.setR11(eval.getR11()+1);
      } else if (eval.getOpponentPieces() == 2 && eval.getEmptyCells() == 1) {
        eval.setR22(eval.getR22()+1);
      } else if (eval.getOpponentPieces() == 1 && eval.getEmptyCells() == 2) {
        eval.setScore(eval.getScore()-1);
      }

      // vai ver se peca esta na intersecção de mills duplos :
      // só há mills duplos basculando nas posicoes seguintes
      // peso maior: mill do meio pode bascular para ambos os lados
      Token playerInPos = gameBoard.getPosition(i).getPlayerOccupyingIt();
      if (i == 4 || i == 10 || i == 13 || i == 19) {
        if (playerInPos == playerToken) {
          eval.setScore(eval.getScore()+2);
        } else if (playerInPos != Token.NO_PLAYER) {
          eval.setScore(eval.getScore()-2);
        }
        // peso menor: mills dos lados só pode bascular para o mill do meio
      } else if (i == 1 || i == 9 || i == 14 || i == 22 || i == 7 || i == 11 || i == 12
          || i == 16) {
        if (playerInPos == playerToken) {
          eval.setScore(eval.getScore()+1);
        } else if (playerInPos != Token.NO_PLAYER) {
          eval.setScore(eval.getScore()-1);
        }
      }
    }
  }

  private int evaluate(Board gameBoard, int gamePhase) throws GameException {
    //int score = 0;
 //   int R1_numPlayerMills = 0, R1_numOppMills = 0;
   // int R2_numPlayerTwoPieceConf = 0, R2_numOppTwoPieceConf = 0;

 //   for (int i = 0; i < Board.NUM_MILL_COMBINATIONS; i++) {
    Evaluation eval = new Evaluation();
    preEvaluation(gameBoard, eval);


    //}

    /**
     * Version 0.1 Depth: 2, MAX_MOVES: 100 => 53% win vs 6% random win Depth: 3, MAX_MOVES: 100 =>
     * 82% win vs 0% random win
     */
    //		score += 100*R1_numPlayerMills + 10*R2_numPlayerTwoPieceConf;
    //		score -= 100*R1_numOppMills + 10*R2_numOppTwoPieceConf;
    //		score += 10*R2_numPlayerTwoPieceConf;
    //		score -= 10*R2_numOppTwoPieceConf;

    /**
     * Version 0.2 Depth: 2, MAX_MOVES: 100 => 57% win vs 5% random win Depth: 3, MAX_MOVES: 100 =>
     * 83% win vs 0% random win Depth: 4, MAX_MOVES: 100 => 91% win vs 0% random win
     */
    //int coef;
    // number of mills
    if (gamePhase == Game.PLACING_PHASE) {
      eval.setCoef(80);
    } else if (gamePhase == Game.MOVING_PHASE) {
      eval.setCoef(120);
    } else {
      eval.setCoef(180);
    }
    eval.setScore(eval.getScore()+ (eval.getCoef() *  eval.getR01())) ;
    eval.setScore(eval.getScore()- (eval.getCoef()* eval.getR11())) ;

    // number of pieces
    if (gamePhase == Game.PLACING_PHASE) {
      eval.setCoef(10);
    } else if (gamePhase == Game.MOVING_PHASE) {
      eval.setCoef(8);
    } else {
      eval.setCoef(6);
    }
    eval.setScore(eval.getScore() + ( eval.getCoef() * gameBoard.getNumberOfPiecesOfPlayer(playerToken)));
    eval.setScore(eval.getScore() - (eval.getCoef() * gameBoard.getNumberOfPiecesOfPlayer(opponentPlayer)));

    // number of 2 pieces and 1 free spot configuration
    if (gamePhase == Game.PLACING_PHASE) {
      eval.setCoef(12);
    } else {
      eval.setCoef(10);
    }
    eval.setScore(eval.getScore() + (eval.getCoef() * eval.getR02()));
    eval.setScore(eval.getScore() - (eval.getCoef() * eval.getR22()));

    if (gamePhase == Game.PLACING_PHASE) {
      eval.setCoef(10);
    } else {
      eval.setCoef(25);
    }
    return eval.getScore();
  }

  private void checkMove(Board gameBoard, Token player, List<Move> moves, Move move)
      throws GameException {
    boolean madeMill = false;
    for (int i = 0; i < Board.NUM_MILL_COMBINATIONS; i++) { // check if piece made a mill
      int playerPieces = 0;
      boolean selectedPiece = false;
      Position[] row = gameBoard.getMillCombination(i);

      for (int j = 0; j < Board.NUM_POSITIONS_IN_EACH_MILL; j++) {

        if (row[j].getPlayerOccupyingIt() == player) {
          playerPieces++;
        }
        if (row[j].getPositionIndex() == move.destIndex) {
          selectedPiece = true;
        }
      }

      if (playerPieces == 3 && selectedPiece) { // made a mill - select piece to remove
        madeMill = true;

        for (int l = 0; l < Board.NUM_POSITIONS_OF_BOARD; l++) {
          Position pos = gameBoard.getPosition(l);

          if (pos.getPlayerOccupyingIt() != player
              && pos.getPlayerOccupyingIt() != Token.NO_PLAYER) {
            move.removePieceOnIndex = l;

            // add a move for each piece that can be removed, this way it will check what's the best
            // one to remove
            moves.add(move);
            movesThatRemove++; // TODO TESTING
          }
        }
      }
      selectedPiece = false;
    }

    if (!madeMill) { // don't add repeated moves
      moves.add(move);
    } else {
      madeMill = false;
    }
  }

  private List<Move> generateMoves(Board gameBoard, Token player, int gamePhase)
      throws GameException {
    List<Move> moves = new ArrayList<Move>();
    Position position, adjacentPos;

    try {
      if (gamePhase == Game.PLACING_PHASE) {
        for (int i = 0;
            i < Board.NUM_POSITIONS_OF_BOARD;
            i++) { // Search for empty cells and add to the List
          Move move = new Move(-7, -1, -1, Move.PLACING);

          if (!(position = gameBoard.getPosition(i)).isOccupied()) {
            position.setAsOccupied(player);
            move.destIndex = i;
            checkMove(gameBoard, player, moves, move);
            position.setAsUnoccupied();
          }
        }
      } else if (gamePhase == Game.MOVING_PHASE) {
        for (int i = 0; i < Board.NUM_POSITIONS_OF_BOARD; i++) {

          if ((position = gameBoard.getPosition(i)).getPlayerOccupyingIt()
              == player) { // for each piece of the player
            int[] adjacent = position.getAdjacentPositionsIndexes();

            for (int j = 0; j < adjacent.length; j++) { // check valid moves to adjacent positions
              Move move = new Move(i, -1, -1, Move.MOVING);
              adjacentPos = gameBoard.getPosition(adjacent[j]);

              if (!adjacentPos.isOccupied()) {
                adjacentPos.setAsOccupied(player);
                move.destIndex = adjacent[j];
                position.setAsUnoccupied();
                checkMove(gameBoard, player, moves, move);
                position.setAsOccupied(player);
                adjacentPos.setAsUnoccupied();
              }
            }
          }
        }
      } else if (gamePhase == Game.FLYING_PHASE) {
        List<Integer> freeSpaces = new ArrayList<Integer>();
        List<Integer> playerSpaces = new ArrayList<Integer>();

        for (int i = 0; i < Board.NUM_POSITIONS_OF_BOARD; i++) {
          if ((position = gameBoard.getPosition(i)).getPlayerOccupyingIt() == player) {
            playerSpaces.add(i);
          } else if (!position.isOccupied()) {
            freeSpaces.add(i);
          }
        }

        // for every piece the player has on the board
        for (int n = 0; n < playerSpaces.size(); n++) {
          Position srcPos = gameBoard.getPosition(playerSpaces.get(n));
          srcPos.setAsUnoccupied();

          // each empty space is a valid move
          for (int j = 0; j < freeSpaces.size(); j++) {
            Move move = new Move(srcPos.getPositionIndex(), -1, -1, Move.MOVING);
            Position destPos = gameBoard.getPosition(freeSpaces.get(j));
            destPos.setAsOccupied(player);
            move.destIndex = freeSpaces.get(j);
            checkMove(gameBoard, player, moves, move);
            destPos.setAsUnoccupied();
          }
          srcPos.setAsOccupied(player);
        }
      }
    } catch (GameException e) {
      e.printStackTrace();
      System.exit(-1);
    }

    /** => V.0.2 */
    // if depth > 3, rate the moves and sort them.
    // When depth is 3 or less, this overhead doesn't compensate the time lost
    if (depth > 3) {
      for (Move move : moves) {
        Token removedPlayer = Token.NO_PLAYER;
        position = gameBoard.getPosition(move.destIndex);

        // Try this move for the current player
        position.setAsOccupied(player);

        if (gamePhase == Game.PLACING_PHASE) {
          gameBoard.incNumPiecesOfPlayer(player);
        } else {
          gameBoard.getPosition(move.srcIndex).setAsUnoccupied();
        }

        if (move.removePieceOnIndex != -1) { // this move removed a piece from opponent
          Position removed = gameBoard.getPosition(move.removePieceOnIndex);
          removedPlayer = removed.getPlayerOccupyingIt();
          removed.setAsUnoccupied();
          gameBoard.decNumPiecesOfPlayer(removedPlayer);
        }

        move.score = evaluate(gameBoard, gamePhase);

        // Undo move
        position.setAsUnoccupied();

        if (gamePhase == Game.PLACING_PHASE) {
          gameBoard.decNumPiecesOfPlayer(player);
        } else {
          gameBoard.getPosition(move.srcIndex).setAsOccupied(player);
        }

        if (move.removePieceOnIndex != -1) {
          gameBoard.getPosition(move.removePieceOnIndex).setAsOccupied(removedPlayer);
          gameBoard.incNumPiecesOfPlayer(removedPlayer);
        }
      }

      if (player == playerToken) {
        Collections.sort(moves, new IAPlayer.HeuristicComparatorMax());
      } else {
        Collections.sort(moves, new IAPlayer.HeuristicComparatorMin());
      }
    }

    /** V.0.2 <= */
    numberOfMoves += moves.size();
    return moves;
  }

  private class HeuristicComparatorMax implements Comparator<Move> {

    public int compare(Move t, Move t1) {
      return t1.score - t.score;
    }
  }

  private class HeuristicComparatorMin implements Comparator<Move> {

    public int compare(Move t, Move t1) {
      return t.score - t1.score;
    }
  }

  public int getGamePhase(Board gameBoard, Token player) {
    int gamePhase = Game.PLACING_PHASE;
    try {
      if (gameBoard.getNumTotalPiecesPlaced() == (Game.NUM_PIECES_PER_PLAYER * 2)) {
        gamePhase = Game.MOVING_PHASE;
        if (gameBoard.getNumberOfPiecesOfPlayer(player) <= 3) {
          gamePhase = Game.FLYING_PHASE;
        }
      }
    } catch (GameException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    return gamePhase;
  }

  private int checkGameOver(Board gameBoard) {
    if (gameBoard.getNumTotalPiecesPlaced() == (Game.NUM_PIECES_PER_PLAYER * 2)) {
      try {
        if (gameBoard.getNumberOfPiecesOfPlayer(playerToken) <= Game.MIN_NUM_PIECES) {
          return -maxScore;
        } else if (gameBoard.getNumberOfPiecesOfPlayer(opponentPlayer) <= Game.MIN_NUM_PIECES) {
          return maxScore;
        } else {
          return 0;
        }
      } catch (GameException e) {
        e.printStackTrace();
      }
    }
    return 0;
  }


  /*
  	public boolean doubleMill(Board gameBoard, Token pl) throws GameException {
  		int tmp;
  		for (int i = 0; i < Board.NUM_POSITIONS_OF_BOARD; i++) {
  			tmp = -1;
  			if (howManyInThree(i, pl)[0] == 3) {
  				tmp = howManyInThree(i, pl)[1];
  				int parallel = gameBoard.getParallelMills(tmp)[0];
  				if ((gameBoard.getMillCombination(parallel)[0]).getPlayerOccupyingIt()
  						== pl && (gameBoard.getMillCombination(parallel)[1]).getPlayerOccupyingIt()
  						== Token.NO_PLAYER && (gameBoard.getMillCombination(parallel)[2]).getPlayerOccupyingIt()
  						== pl ) {
  					return  true;
  				}
  			}
  		}
  		return false;
  	}

  	public boolean openMill(Board gameBoard, Token pl) throws GameException {
  		int tmp;
  		for (int i = 0; i < Board.NUM_POSITIONS_OF_BOARD; i++) {

  			if (howManyInThree(i, pl)[0] == 2) {
  				Position[] row = gameBoard.getMillCombination(i);
  				tmp = howManyInThree(i, pl)[1];
  				int parallel = gameBoard.getParallelMills(tmp)[0];
  				if (row[1].getPlayerOccupyingIt() == Token.NO_PLAYER && (gameBoard.getMillCombination(parallel)[1]).getPlayerOccupyingIt()
  						== pl ) {
  					return  true;
  				}
  			}
  		}
  		return false;
  	}


    public int totalMills(Token player) throws GameException {
      int count = 0, tmp;
      Vector counted = new Vector();
      for (int i = 0; i < Board.NUM_MILL_COMBINATIONS; i++) {
        Position[] row = gameBoard.getMillCombination(i);
        int pos = row[0].getPositionIndex();

        tmp = whichMillMade(pos, player);
        System.out.println("tmp=" + tmp);
        if (tmp >= 0) {
          if (!counted.contains(tmp)) {
            count++;
            counted.add(tmp);
            System.out.println("contou pos " + pos + " na mill " + i);
          }
        }
      }
      System.out.println("count:" + count);
      counted.forEach((n) -> System.out.println(n));
      return count;
    }

    public int whichMillMade(int dest, Token player) throws GameException {
      int[] tmp = howManyInThree(dest, player);
      if (tmp[0] == Board.NUM_POSITIONS_IN_EACH_MILL) return tmp[1];
      return -1;
    }

    public boolean madeAMill(int dest, Token player) throws GameException {
      return (howManyInThree(dest, player)[0] == Board.NUM_POSITIONS_IN_EACH_MILL);
    }

    public boolean conjuntoDe2em3(int dest, Token player) throws GameException {
      return (howManyInThree(dest, player)[0] == Board.NUM_POSITIONS_IN_EACH_MILL - 1);
    }

    public int[] howManyInThree(int dest, Token player) throws GameException {
      int maxNumPlayerPiecesInRow = 0, tmp = -1;
      int[] result = new int[2];
      for (int i = 0; i < Board.NUM_MILL_COMBINATIONS; i++) {
        Position[] row = gameBoard.getMillCombination(i);
        for (int j = 0; j < Board.NUM_POSITIONS_IN_EACH_MILL; j++) {
          if (row[j].getPositionIndex() == dest) {
            int playerPiecesInThisRow = numPiecesFromPlayerInRow(row, player);
            if (playerPiecesInThisRow > maxNumPlayerPiecesInRow) {
              maxNumPlayerPiecesInRow = playerPiecesInThisRow;
              tmp = i;
            }
          }
        }
      }
      result[0] = maxNumPlayerPiecesInRow;
      result[1] = tmp;
      return result;
    }

  */ }
