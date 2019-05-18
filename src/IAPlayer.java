import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class IAPlayer extends Player {
  public enum Heuristic {
    R00,
    R01,
    R02,
    R03,
    R04,
    R05,
    R06,
    R07,
    R08,
    R09,
    R10
  }

  Evaluation eval = new Evaluation();
  protected Random rand;
  public int numberOfMoves = 0; // TODO TESTING
  public int movesThatRemove = 0;
  private int depth;
  private Token opponentPlayer;
  private Move currentBestMove;
  public int bestScore = 0;
  static final int maxScore = 1000000;
  static final Heuristic[] DEFAULT_HEURISTICS ={Heuristic.R01, Heuristic.R02,Heuristic.R04};

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
        return evaluateNew(gameBoard, gamePhase, DEFAULT_HEURISTICS);
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

  /**
   * função chamada pelas funções de avaliação para calcular valores das heuristicas
   *
   * @param gameBoard tabuleiro actual
   * @throws GameException
   */
  private void preEvaluation(Board gameBoard) throws GameException {
    this.eval.setR04_numPlayerPieces(0);
    this.eval.setEmptyCells(0);
    this.eval.setR04_numOpponentPieces(0);
    for (int i = 0; i < Board.NUM_MILL_COMBINATIONS; i++) {

      try {
        Position[] row = gameBoard.getMillCombination(i);
        for (int j = 0; j < Board.NUM_POSITIONS_IN_EACH_MILL; j++) {
          if (row[j].getPlayerOccupyingIt() == playerToken) {
            this.eval.setR04_numPlayerPieces(this.eval.getR04_numPlayerPieces() + 1);
          } else if (row[j].getPlayerOccupyingIt() == Token.NO_PLAYER) {
            this.eval.setEmptyCells(this.eval.getEmptyCells() + 1);
          } else {
            this.eval.setR04_numOpponentPieces(this.eval.getR04_numOpponentPieces() + 1);
          }
        }
      } catch (GameException e) {
        e.printStackTrace();
      }
      if (this.eval.getR04_numPlayerPieces() == 3) {
        this.eval.setR01_numPlayerMills(this.eval.getR01_numPlayerMills() + 1);
      } else if (this.eval.getR04_numPlayerPieces() == 2 && this.eval.getEmptyCells() == 1) {
        this.eval.setR01_numOppMills(this.eval.getR01_numOppMills() + 1);
      } else if (this.eval.getR04_numPlayerPieces() == 1 && this.eval.getEmptyCells() == 2) {
        this.eval.setScore(this.eval.getScore() + 1);
      } else if (this.eval.getR04_numOpponentPieces() == 3) {
        this.eval.setR02_numPlayerTwoPieceConf(this.eval.getR02_numPlayerTwoPieceConf() + 1);
      } else if (this.eval.getR04_numOpponentPieces() == 2 && this.eval.getEmptyCells() == 1) {
        this.eval.setR02_numOppTwoPieceConf(this.eval.getR02_numOppTwoPieceConf() + 1);
      } else if (this.eval.getR04_numOpponentPieces() == 1 && this.eval.getEmptyCells() == 2) {
        this.eval.setScore(this.eval.getScore() - 1);
      }

      // vai ver se peca esta na intersecção de mills duplos :
      // só há mills duplos basculando nas posicoes seguintes
      // peso maior: mill do meio pode bascular para ambos os lados
      Token playerInPos = gameBoard.getPosition(i).getPlayerOccupyingIt();
      if (i == 4 || i == 10 || i == 13 || i == 19) {
        if (playerInPos == playerToken) {
          this.eval.setScore(this.eval.getScore() + 2); // oldway
          this.eval.setR10_playerInIntersection(this.eval.getR10_playerInIntersection() + 2);
          // this.eval.setR08_numPlayerDoubleMorris(this.eval.getR08_numPlayerDoubleMorris()+2);//não há
          // mill...
        } else if (playerInPos != Token.NO_PLAYER) {
          this.eval.setScore(this.eval.getScore() - 2); // oldway
          this.eval.setR10_opponentInIntersection(this.eval.getR10_opponentInIntersection() + 2);
          // this.eval.setR08_numOpponentDoubleMorris(this.eval.getR08_numOpponentDoubleMorris()+2);//não há
          // mill...
        }
        // peso menor: mills dos lados só pode bascular para o mill do meio
      } else if (i == 1 || i == 9 || i == 14 || i == 22 || i == 7 || i == 11 || i == 12
          || i == 16) {
        if (playerInPos == playerToken) {
          this.eval.setScore(this.eval.getScore() + 1); // oldway
          this.eval.setR00_playerAsideIntersection(this.eval.getR00_playerAsideIntersection() + 1);
          // this.eval.setR08_numPlayerDoubleMorris(this.eval.getR08_numPlayerDoubleMorris()+1); só
          // é double
          // mill se os 4 estiverem
        } else if (playerInPos != Token.NO_PLAYER) {
          this.eval.setScore(this.eval.getScore() - 1); // oldway
          this.eval.setR00_opponentAsideIntersection(
              this.eval.getR00_opponentAsideIntersection() + 1);
          // this.eval.setR08_numOpponentDoubleMorris(this.eval.getR08_numOpponentDoubleMorris()+1);
          // só é
          // double mill se os 4 estiverem
        }
      }
    }
    this.eval.setR07_opponentOpenedMills(
        this.eval.getR01_numOppMills() - this.eval.getR05_opponentJustMadeAMill());
    this.eval.setR07_playerOpenedMills(
        this.eval.getR01_numPlayerMills() - this.eval.getR05_playerJustMadeAMill());

    // testar
    if (this.opponentPlayer == Token.PLAYER_2) {
      if (doubleMill(gameBoard, Token.PLAYER_1)) this.eval.setR08_numPlayerDoubleMorris(1);
      if (doubleMill(gameBoard, Token.PLAYER_2)) this.eval.setR08_numOpponentDoubleMorris(1);
      this.eval.setR03_numBlockedPlayerPieces(countBlocked(gameBoard, Token.PLAYER_1));
      this.eval.setR03_numBlockedOpponentPieces(countBlocked(gameBoard, Token.PLAYER_2));
    } else {
      if (doubleMill(gameBoard, Token.PLAYER_2)) this.eval.setR08_numPlayerDoubleMorris(1);
      if (doubleMill(gameBoard, Token.PLAYER_1)) this.eval.setR08_numOpponentDoubleMorris(1);
      this.eval.setR03_numBlockedPlayerPieces(countBlocked(gameBoard, Token.PLAYER_2));
      this.eval.setR03_numBlockedOpponentPieces(countBlocked(gameBoard, Token.PLAYER_1));
    }
  }

  /**
   * Nova função de avaliação para uma ou várias heuristicas (e que usa os coeficientes calculados
   * dentro)
   *
   * @param gameBoard tabuleiro actual
   * @param gamePhase fase do jogo
   * @param heuristics array com as heuristicas a considerar nesta avaliaçao
   * @return avaliação
   * @throws GameException
   */
  // temos também o R00, R03, R05, R07, R08, R09 e o R10 com valores
  // falta R6 (possibilidade L)
  private int evaluateNew(Board gameBoard, int gamePhase, Heuristic[] heuristics)
      throws GameException {
    preEvaluation(gameBoard);

    switch (gamePhase) {
      case Game.PLACING_PHASE:
        // meter a 0 os coefs que a romena tem mas nao contabiliza em cada fase?
        this.eval.setCoefs(0, 80, 12, 0, 10, 0, 0, 0, 0, 0);
        break;
      case Game.MOVING_PHASE:
        // meter a 0 os coefs que a romena tem mas nao contabiliza em cada fase?
        this.eval.setCoefs(0, 120, 10, 0, 8, 0, 0, 0, 0, 0);
        break;
      default:
        // meter a 0 os coefs que a romena tem mas nao contabiliza em cada fase?
        this.eval.setCoefs(0, 180, 10, 0, 6, 0, 0, 0, 0, 0);
        break;
    }

    for (Heuristic heuristic : heuristics) {
      switch (heuristic) {
        case R00:
          this.eval.setScore(
              (this.eval.getCoefs().R0
                  * (this.eval.getR00_playerAsideIntersection()
                      - this.eval.getR00_opponentAsideIntersection())));
          break;
        case R01:
          this.eval.setScore(
              (this.eval.getCoefs().R1
                  * (this.eval.getR01_numPlayerMills() - this.eval.getR01_numOppMills())));
          break;
        case R02:
          this.eval.setScore(
              (this.eval.getCoefs().R2
                  * (this.eval.getR02_numPlayerTwoPieceConf()
                      - this.eval.getR02_numOppTwoPieceConf())));
          break;
        case R03:
          this.eval.setScore(
              (this.eval.getCoefs().R3
                  * (this.eval.getR03_numBlockedPlayerPieces()
                      - this.eval.getR03_numBlockedOpponentPieces())));
          break;
        case R04:
          this.eval.setScore(
              (this.eval.getCoefs().R4
                  * (this.eval.getR04_numPlayerPieces() - this.eval.getR04_numOpponentPieces())));
          break;
        case R05:
          this.eval.setScore(
              (this.eval.getCoefs().R5
                  * (this.eval.getR05_playerJustMadeAMill()
                      - this.eval.getR05_opponentJustMadeAMill())));
          break;
        case R06:
          this.eval.setScore(
              (this.eval.getCoefs().R6
                  * (this.eval.getR06_player3PiecesConfigurations()
                      - this.eval.getR06_opponent3PiecesConfigurations())));
          break;
        case R07:
          this.eval.setScore(
              (this.eval.getCoefs().R7
                  * (this.eval.getR07_playerOpenedMills()
                      - this.eval.getR07_opponentOpenedMills())));
          break;
        case R08:
          this.eval.setScore(
              (this.eval.getCoefs().R8
                  * (this.eval.getR08_numPlayerDoubleMorris()
                      - this.eval.getR08_numOpponentDoubleMorris())));
          break;
        case R09:
          this.eval.setScore(
              (this.eval.getCoefs().R9
                  * (this.eval.getR09_playerWinningConfiguration()
                      - this.eval.getR09_opponentWinningConfiguration())));
          break;
        case R10:
          this.eval.setScore(
              (this.eval.getCoefs().R10
                  * (this.eval.getR10_playerInIntersection()
                      - this.eval.getR10_opponentInIntersection())));
          break;
        default:
          break;
      }
    }
    return this.eval.getScore();
  }

  /**
   * função de avaliação inicial com heuristica fixa
   *
   * @param gameBoard tabuleiro actual
   * @param gamePhase fase actual do jogo
   * @return avaliação
   * @throws GameException
   */
  private int evaluate(Board gameBoard, int gamePhase) throws GameException {

    preEvaluation(gameBoard);

    switch (gamePhase) {
      case Game.PLACING_PHASE:
        // meter a 0 os coefs que a romena tem mas nao contabiliza em cada fase?
        this.eval.setCoefs(0,80, 12, 0, 10, 0, 0, 0, 0, 0);
        break;
      case Game.MOVING_PHASE:
        // meter a 0 os coefs que a romena tem mas nao contabiliza em cada fase?
        this.eval.setCoefs(0,120, 10, 0, 8, 0, 0, 0, 0, 0);
        break;
      default:
        // meter a 0 os coefs que a romena tem mas nao contabiliza em cada fase?
        this.eval.setCoefs(0,180, 10, 0, 6, 0, 0, 0, 0, 0);
        break;
    }
    this.eval.setScore(
        this.eval.getScore()
            + (this.eval.getCoefs().R1
                * (this.eval.getR01_numPlayerMills() - this.eval.getR01_numOppMills()))
            + (this.eval.getCoefs().R4
                * (this.eval.getR04_numPlayerPieces() - this.eval.getR04_numOpponentPieces()))
            + (this.eval.getCoefs().R2
                * (this.eval.getR02_numPlayerTwoPieceConf()
                    - this.eval.getR02_numOppTwoPieceConf())));
    // temos também o R00, R03, R05, R07, R08, R09 e o R10 com valores
    // falta R6 (possibilidade L)
    return this.eval.getScore(); // nao necessario ja que this
  }

  private void checkMove(Board gameBoard, Token player, List<Move> moves, Move move)
      throws GameException {
    boolean madeMill = false;
    for (int i = 0; i < Board.NUM_MILL_COMBINATIONS; i++) { // check if piece made a mill
      this.eval.setR04_numPlayerPieces(0);

      // switch player data
      this.eval.setR05_opponentJustMadeAMill(this.eval.getR05_playerJustMadeAMill());
      this.eval.setR05_playerJustMadeAMill(0);

      boolean selectedPiece = false;
      Position[] row = gameBoard.getMillCombination(i);

      for (int j = 0; j < Board.NUM_POSITIONS_IN_EACH_MILL; j++) {

        if (row[j].getPlayerOccupyingIt() == player) {
          this.eval.setR04_numPlayerPieces(this.eval.getR04_numPlayerPieces() + 1);
        }
        if (row[j].getPositionIndex() == move.destIndex) {
          selectedPiece = true;
        }
      }

      if (this.eval.getR04_numPlayerPieces() == 3
          && selectedPiece) { // made a mill - select piece to remove
        madeMill = true;
        this.eval.setR05_playerJustMadeAMill(1); // added to evaluation

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

        move.score = evaluateNew(gameBoard, gamePhase,DEFAULT_HEURISTICS);

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
          this.eval.setR09_playerWinningConfiguration(maxScore);
          return -maxScore;
        } else if (gameBoard.getNumberOfPiecesOfPlayer(opponentPlayer) <= Game.MIN_NUM_PIECES) {
          this.eval.setR09_opponentWinningConfiguration(maxScore);
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

  public boolean doubleMill(Board gameBoard, Token pl) throws GameException {
    int tmp; // array com cada mill e quantos o player tem em cada uma
    for (int i = 0; i < Board.NUM_POSITIONS_OF_BOARD; i++) {
      tmp = -1;
      if (howManyInThree(i, pl, gameBoard)[0] == 3) { // se player tem mill i feita
        tmp = howManyInThree(i, pl, gameBoard)[1]; // quantas peças o player tem na mill i
       // System.out.println(gameBoard.getParallelMills(tmp)[0]);
        int[] parallel = gameBoard.getParallelMills(tmp); // array com os mills paralelos à mill i
        for (int j = 0; j < parallel.length; j++) {
          if ((gameBoard.getMillCombination(parallel[j])[0]).getPlayerOccupyingIt() != pl
              || (gameBoard.getMillCombination(parallel[j])[1]).getPlayerOccupyingIt()
                  != Token.NO_PLAYER
              || (gameBoard.getMillCombination(parallel[j])[2]).getPlayerOccupyingIt() != pl) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
  /*
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
  */

  /**
   * @param dest
   * @param player
   * @param gameBoard
   * @return
   * @throws GameException
   */
  public int[] howManyInThree(int dest, Token player, Board gameBoard) throws GameException {
    int maxNumR04_numPlayerPiecesInRow = 0, tmp = -1;
    int[] result = new int[2];
    for (int i = 0; i < Board.NUM_MILL_COMBINATIONS; i++) {
      Position[] row = gameBoard.getMillCombination(i);
      for (int j = 0; j < Board.NUM_POSITIONS_IN_EACH_MILL; j++) {
        if (row[j].getPositionIndex() == dest) {
          int R04_numPlayerPiecesInThisRow = numPiecesFromPlayerInRow(row, player);
          if (R04_numPlayerPiecesInThisRow > maxNumR04_numPlayerPiecesInRow) {
            maxNumR04_numPlayerPiecesInRow = R04_numPlayerPiecesInThisRow;
            tmp = i;
          }
        }
      }
    }
    result[0] = maxNumR04_numPlayerPiecesInRow;
    result[1] = tmp;
    return result;
  }

  private int numPiecesFromPlayerInRow(Position[] pos, Token player) {
    int counter = 0;
    for (int i = 0; i < pos.length; i++) {
      if (pos[i].getPlayerOccupyingIt() == player) {
        counter++;
      } else if (pos[i].getPlayerOccupyingIt() != Token.NO_PLAYER) {
        counter--;
      }
    }
    return counter;
  }

  /**
   * Conta as peças de um jogador que estão bloqueadas
   *
   * @param gameBoard tabuleiro actual
   * @param player jogador que se quer verificar
   * @return número de peças bloqueadas do jogador que se quer verificar
   * @throws GameException
   */
  private int countBlocked(Board gameBoard, Token player) throws GameException {
    // percorre posições
    int count = 0;
    for (int i = 0; i < Board.NUM_POSITIONS_OF_BOARD; i++) {
      if (occupiedByPlayer(gameBoard, player, i)) {
        count += runThroughAdjacents(gameBoard, i) ? 1 : 0;
      }
    }
    return count;
  }

  /**
   * Verifica se a casa está ocupada por um jogador especifico
   *
   * @param gameBoard tabuleiro actual
   * @param player jogador que se quer verificar
   * @param i indice da posição no tabuleiro
   * @return verdadeiro ou false
   * @throws GameException
   */
  private boolean occupiedByPlayer(Board gameBoard, Token player, int i) throws GameException {
    return (gameBoard.getPosition(i).getPlayerOccupyingIt() == player);
  }

  /**
   * percorre as casas adjacentes da peça
   *
   * @param gameBoard tabuleiro actual
   * @param i indice da posiçao
   * @return verdadeiro ou falso
   * @throws GameException
   */
  private boolean runThroughAdjacents(Board gameBoard, int i) throws GameException {
    // int count=0;
    Position position = gameBoard.getPosition(i);
    int[] adjacent = position.getAdjacentPositionsIndexes();
    for (int j = 0; j < adjacent.length; j++) {
      if (isFree(gameBoard, j)) return false;
    }
    return true;
  }

  /**
   * Verifica se a casa em questão está desocupada
   *
   * @param i indice da posição
   * @return verdadeiro ou falso
   */
  private boolean isFree(Board gameBoard, int i) throws GameException {
    if (gameBoard.getPosition(i).getPlayerOccupyingIt() == Token.NO_PLAYER) return true;
    return false;
  }

  /*
  Lconfig => há 2 peças em 3 numa possivel mill e outras 2 em 3 noutra possivel mill que partilha uma das posicoes
   */

}
