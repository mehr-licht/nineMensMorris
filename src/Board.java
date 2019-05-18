public class Board {
  public static final int NUM_POSITIONS_OF_BOARD = 24;
  public static final int NUM_MILL_COMBINATIONS = 16;
  public static final int NUM_MILL_PARALLELS = 12;
  public static final int NUM_POSITIONS_IN_EACH_MILL = 3;
  public int globalIndex;//indice da ultima peça colocada

	private static final int[] NO_PARALLELS = {};//array vazio para ser devolvido no caso de não haver mill paralelo ao que se está
  private Position[] boardPositions; //tabuleiro : cada casa e as suas casas adjacentes
  private Position[][] millCombinations; //posições que compõem as mills possiveis: array[indice da mill] [indice da casa]x3
	private int numOfPiecesP1;
	private int numOfPiecesP2;
	private int numberOfTotalPiecesPlaced;

	/**
   * mills paralelas à onde se está:
   * array[indice da mill] [indice da paralela]
   * cada mill pode ter 0, 1 ou 2 que lhe são paralelas
   * as mills com indices entre 12 e 15 (ultimas 4) são as que não têm paralelas
   */
	private int[][] parallelMills;


  public Board() {
    boardPositions = new Position[Board.NUM_POSITIONS_OF_BOARD];
    numOfPiecesP1 = 0;
    numOfPiecesP2 = 0;
    numberOfTotalPiecesPlaced = 0;
    initBoard();
    initMillCombinations();
    initParallelMills();
  }

	/**
	 * Obtem os dados de uma determinada posição do tabuleiro
	 * @param posIndex indice da posição
	 * @return um objecto Posição com os dados da posição do tabuleiro
	 * @throws GameException
	 */
  public Position getPosition(int posIndex) throws GameException {
    if (posIndex >= 0 && posIndex < Board.NUM_POSITIONS_OF_BOARD) {
      return boardPositions[posIndex];
    } else {
      throw new GameException(
          "" + getClass().getName() + " - Invalid Board Position Index: " + posIndex);
    }
  }

	/**
	 * Verifica se a posição está desocupada
	 * @param posIndex indice da posição
	 * @return verdadeiro ou falso
	 * @throws GameException
	 */
  public boolean positionIsAvailable(int posIndex) throws GameException {
    if (posIndex >= 0 && posIndex < Board.NUM_POSITIONS_OF_BOARD) {
      return !boardPositions[posIndex].isOccupied();
    } else {
      throw new GameException(
          "" + getClass().getName() + " - Invalid Board Position Index: " + posIndex);
    }
  }

	/**
	 * incrementa em 1 o número de peças em jogo
	 * @return valor já incrementado
	 */
  public int incNumTotalPiecesPlaced() {
    return ++numberOfTotalPiecesPlaced;
  }

	/**
	 * incrementa em 1 o número de peças em jogo
	 * @param player o jogador em causa
	 * @return valor já incrementado
	 * @throws GameException
	 */
  public int incNumPiecesOfPlayer(Token player) throws GameException {
    if (player == Token.PLAYER_1) {
      return ++numOfPiecesP1;
    } else if (player == Token.PLAYER_2) {
      return ++numOfPiecesP2;
    } else {
      throw new GameException("" + getClass().getName() + " - Invalid Player Token: " + player);
    }
  }

	/**
	 * decrementa em 1 o número de peças em jogo
	 * @param player o jogador em causa
	 * @return valor já decrementado
	 * @throws GameException
	 */
  public int decNumPiecesOfPlayer(Token player) throws GameException {
    if (player == Token.PLAYER_1) {
      return --numOfPiecesP1;
    } else if (player == Token.PLAYER_2) {
      return --numOfPiecesP2;
    } else {
      throw new GameException("" + getClass().getName() + " - Invalid Player Token: " + player);
    }
  }

	/**
	 * Obtem o número de peças que um dado jogador tem em jogo
	 * @param player jogador em causa
	 * @return o número de peças que um dado jogador tem em jogo
	 * @throws GameException
	 */
  public int getNumberOfPiecesOfPlayer(Token player) throws GameException {
    if (player == Token.PLAYER_1) {
      return numOfPiecesP1;
    } else if (player == Token.PLAYER_2) {
      return numOfPiecesP2;
    } else {
      throw new GameException("" + getClass().getName() + " - Invalid Player Token: " + player);
    }
  }

	/**
	 * cria tabuleiro inicial
	 */
	private void initBoard() {
    for (int i = 0; i < Board.NUM_POSITIONS_OF_BOARD; i++) {
      boardPositions[i] = new Position(i);
    }
    // outer square
    boardPositions[0].addAdjacentPositionsIndexes(1, 9);
    boardPositions[1].addAdjacentPositionsIndexes(0, 2, 4);
    boardPositions[2].addAdjacentPositionsIndexes(1, 14);
    boardPositions[9].addAdjacentPositionsIndexes(0, 10, 21);
    boardPositions[14].addAdjacentPositionsIndexes(2, 13, 23);
    boardPositions[21].addAdjacentPositionsIndexes(9, 22);
    boardPositions[22].addAdjacentPositionsIndexes(19, 21, 23);
    boardPositions[23].addAdjacentPositionsIndexes(14, 22);
    // middle square
    boardPositions[3].addAdjacentPositionsIndexes(4, 10);
    boardPositions[4].addAdjacentPositionsIndexes(1, 3, 5, 7);
    boardPositions[5].addAdjacentPositionsIndexes(4, 13);
    boardPositions[10].addAdjacentPositionsIndexes(3, 9, 11, 18);
    boardPositions[13].addAdjacentPositionsIndexes(5, 12, 14, 20);
    boardPositions[18].addAdjacentPositionsIndexes(10, 19);
    boardPositions[19].addAdjacentPositionsIndexes(16, 18, 20, 22);
    boardPositions[20].addAdjacentPositionsIndexes(13, 19);
    // inner square
    boardPositions[6].addAdjacentPositionsIndexes(7, 11);
    boardPositions[7].addAdjacentPositionsIndexes(4, 6, 8);
    boardPositions[8].addAdjacentPositionsIndexes(7, 12);
    boardPositions[11].addAdjacentPositionsIndexes(6, 10, 15);
    boardPositions[12].addAdjacentPositionsIndexes(8, 13, 17);
    boardPositions[15].addAdjacentPositionsIndexes(11, 16);
    boardPositions[16].addAdjacentPositionsIndexes(15, 17, 19);
    boardPositions[17].addAdjacentPositionsIndexes(12, 16);
  }

	/**
	 * Obtem as posições das casas que compõem a mill com o indice que se passa
	 * @param index indice da mill da qual se quer obter as posições
	 * @return array com as 3 posições que compõem a mill
	 * @throws GameException
	 */
  public Position[] getMillCombination(int index) throws GameException {
    if (index >= 0 && index < Board.NUM_MILL_COMBINATIONS) {
      return millCombinations[index];
    } else {
      throw new GameException(
          "" + getClass().getName() + " - Invalid Mill Combination Index: " + index);
    }
  }

	/**
	 * Obtem as mills que são paralelas à que tem o indice passado
	 * @param index indice da mill da qual se quer obter as paralelas
	 * @return array com as mills que são paralelas à que se pede (pode ser vazio, de 1 mill ou de 2 mills)
	 * @throws GameException
	 */
  public int[] getParallelMills(int index) throws GameException {
    if (index >= 0 && index < Board.NUM_MILL_COMBINATIONS) {
      if (index < NUM_MILL_PARALLELS) {
        return parallelMills[index];
      }else{
      	return NO_PARALLELS;
			}
    } else {
      throw new GameException(
          "" + getClass().getName() + " - Invalid Mill Combination Index: " + index);
    }
  }

	/**
	 * cria o array de mills paralelas às outras
	 */
	private void initParallelMills() {
    parallelMills = new int[NUM_MILL_PARALLELS][2];

    parallelMills[0][0] = 4;
    parallelMills[1][0] = 5;
    parallelMills[2][0] = 6;
    parallelMills[3][0] = 7;
    parallelMills[4][0] = 0;
    parallelMills[4][1] = 8;
    parallelMills[5][0] = 1;
    parallelMills[5][1] = 9;
    parallelMills[6][0] = 2;
    parallelMills[6][1] = 10;
    parallelMills[7][0] = 11;
    parallelMills[7][1] = 3;
    parallelMills[8][0] = 4;
    parallelMills[9][0] = 5;
    parallelMills[10][0] = 6;
    parallelMills[11][0] = 7;
  }

	/**
	 * cria o array das mills possiveis de se fazer (indice:3peças)
	 */
	private void initMillCombinations() {
    millCombinations = new Position[Board.NUM_MILL_COMBINATIONS][Board.NUM_POSITIONS_IN_EACH_MILL];

    // outer square
    millCombinations[0][0] = boardPositions[0];
    millCombinations[0][1] = boardPositions[1];
    millCombinations[0][2] = boardPositions[2];
    millCombinations[1][0] = boardPositions[0];
    millCombinations[1][1] = boardPositions[9];
    millCombinations[1][2] = boardPositions[21];
    millCombinations[2][0] = boardPositions[2];
    millCombinations[2][1] = boardPositions[14];
    millCombinations[2][2] = boardPositions[23];
    millCombinations[3][0] = boardPositions[21];
    millCombinations[3][1] = boardPositions[22];
    millCombinations[3][2] = boardPositions[23];
    // middle square
    millCombinations[4][0] = boardPositions[3];
    millCombinations[4][1] = boardPositions[4];
    millCombinations[4][2] = boardPositions[5];
    millCombinations[5][0] = boardPositions[3];
    millCombinations[5][1] = boardPositions[10];
    millCombinations[5][2] = boardPositions[18];
    millCombinations[6][0] = boardPositions[5];
    millCombinations[6][1] = boardPositions[13];
    millCombinations[6][2] = boardPositions[20];
    millCombinations[7][0] = boardPositions[18];
    millCombinations[7][1] = boardPositions[19];
    millCombinations[7][2] = boardPositions[20];
    // inner square
    millCombinations[8][0] = boardPositions[6];
    millCombinations[8][1] = boardPositions[7];
    millCombinations[8][2] = boardPositions[8];
    millCombinations[9][0] = boardPositions[6];
    millCombinations[9][1] = boardPositions[11];
    millCombinations[9][2] = boardPositions[15];
    millCombinations[10][0] = boardPositions[8];
    millCombinations[10][1] = boardPositions[12];
    millCombinations[10][2] = boardPositions[17];
    millCombinations[11][0] = boardPositions[15];
    millCombinations[11][1] = boardPositions[16];
    millCombinations[11][2] = boardPositions[17];
    // others
    millCombinations[12][0] = boardPositions[1];
    millCombinations[12][1] = boardPositions[4];
    millCombinations[12][2] = boardPositions[7];
    millCombinations[13][0] = boardPositions[9];
    millCombinations[13][1] = boardPositions[10];
    millCombinations[13][2] = boardPositions[11];
    millCombinations[14][0] = boardPositions[12];
    millCombinations[14][1] = boardPositions[13];
    millCombinations[14][2] = boardPositions[14];
    millCombinations[15][0] = boardPositions[16];
    millCombinations[15][1] = boardPositions[19];
    millCombinations[15][2] = boardPositions[22];
  }

	/**
	 * Imprime o tabuleiro
	 */
	public void printBoard() {
    System.out.println(showPos(0) + " - - - - - " + showPos(1) + " - - - - - " + showPos(2));
    System.out.println("|           |           |");
    System.out.println(
        "|     " + showPos(3) + " - - " + showPos(4) + " - - " + showPos(5) + "     |");
    System.out.println("|     |     |     |     |");
    System.out.println(
        "|     | " + showPos(6) + " - " + showPos(7) + " - " + showPos(8) + " |     |");
    System.out.println("|     | |       | |     |");
    System.out.println(
        showPos(9)
            + " - - "
            + showPos(10)
            + "-"
            + showPos(11)
            + "       "
            + showPos(12)
            + "-"
            + showPos(13)
            + " - - "
            + showPos(14));
    System.out.println("|     | |       | |     |");
    System.out.println(
        "|     | " + showPos(15) + " - " + showPos(16) + " - " + showPos(17) + " |     |");
    System.out.println("|     |     |     |     |");
    System.out.println(
        "|     " + showPos(18) + " - - " + showPos(19) + " - - " + showPos(20) + "     |");
    System.out.println("|           |           |");
    System.out.println(showPos(21) + " - - - - - " + showPos(22) + " - - - - - " + showPos(23));
  }

	/**
	 * Obtem para uma posição, a peça que a ocupa, com  cor
	 * @param i indice da posição
	 * @return string formatada com cor da peça que ocupa a posição
	 */
  private String showPos(int i) {
    switch (boardPositions[i].getPlayerOccupyingIt()) {
      case PLAYER_1:
        return showChar(i, Colours.GREEN, "X");
      case PLAYER_2:
        return showChar(i, Colours.RED, "O");
      case NO_PLAYER:
        return String.format("%s%d%s", Colours.LOW_INTENSITY, i % 10, Colours.RESET);
      default:
        return null;
    }
  }

	/**
	 * Devolve a string formatada da peça para ser mostrada na posição do tabuleiro
	 * @param position indice da posição no tabuleiro
	 * @param colour cor correspondente ao jogador
	 * @param symbol simbolo correspondente ao jogador
	 * @return string formatada da peça para ser mostrada na posição do tabuleiro
	 */
  private String showChar(int position, String colour, String symbol) {
    if (position == this.globalIndex) {
      return String.format(
          "%s%s%s%s%s", Colours.BLINK, Colours.HIGH_INTENSITY, colour, symbol, Colours.RESET);
      // return 	(i==this.globalIndex)? "BLINK HIGH_INTENSITY GREEN+\"X\"+RESET": "BLUE+\"X\"+RESET"
      // ;
      // return 	(i==this.globalIndex)? "BLINK HIGH_INTENSITY RED+\"O\"+RESET": "CYAN+\"O\"+RESET" ;
    } else {
      return String.format("%s%s%s", Colours.CYAN, symbol, Colours.RESET);
    }
  }

	/**
	 * Obtem o numero total de peças jogadas
	 * @return numero total de peças jogadas
	 */
  public int getNumTotalPiecesPlaced() {
    return numberOfTotalPiecesPlaced;
  }
}
