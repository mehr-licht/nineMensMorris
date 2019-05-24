import java.util.*;

public class Position {
	
	private boolean isOccupied;
	private int positionIndex;
	private Token playerOccupying;
	private int[] adjacentPositionsIndexes;//array com as (2,3 ou 4) posições que são adjacenntes a esta
	/**
	 * conjunto com as 2 mills a que a posição pertence: posição.set(mill1,mill2)
	 * útil para a 3pieceConfig (disposição de peças em L em que adversario nao consegue impedir que se faça mill)
	 */
	private Set<Integer> lIndexes = new HashSet<>();
	
	public Position(int position) {
		isOccupied = false;
		this.positionIndex = position;
		playerOccupying = Token.NO_PLAYER;
	}

  /**
   * Verifica se a posição está ocupada
   * @return verdadeiro ou falso
   */
	public boolean isOccupied() {
		return isOccupied;
	}

  /**
   * Obtem o indice da posição
   * @return indice da posição
   */
	public int getPositionIndex() {
		return positionIndex;
	}

  /**
   * Obtem o jogador a que pertence a peça que ocupa a posição
   * @return jogador a que pertence a peça que ocupa a posição
   */
	public Token getPlayerOccupyingIt() {
		return playerOccupying;
	}

  /**
   * Define a posição como ocupada por um determinado jogador
   * @param player jogador que se define que ocupa a posição
   */
	public void setAsOccupied(Token player) {
		isOccupied = true;
		playerOccupying = player;
	}
	
	/**
	 * Limpa a posição e devolve o token do ultimo jogador que lá esteve
	 * @return
	 */
	public Token setAsUnoccupied() {
		isOccupied = false;
		Token oldPlayer = playerOccupying;
		playerOccupying = Token.NO_PLAYER;
		return oldPlayer;
	}

  /**
   * Adiciona as posições adjacentes a uma Posição que só tem 2 adjacências
   * @param pos1 uma das posições adjacentes
   * @param pos2 outra das posições adjacentes
   */
	public void addAdjacentPositionsIndexes(int pos1, int pos2) {
		adjacentPositionsIndexes = new int[2];
		adjacentPositionsIndexes[0] = pos1;
		adjacentPositionsIndexes[1] = pos2;
	}

  /**
   * Adiciona as posições adjacentes a uma Posição que tem 3 adjacências
   * @param pos1 uma das posições adjacentes
   * @param pos2 outra das posições adjacentes
   * @param pos3 terceira das posições adjacentes
   */
	public void addAdjacentPositionsIndexes(int pos1, int pos2, int pos3) {
		adjacentPositionsIndexes = new int[3];
		adjacentPositionsIndexes[0] = pos1;
		adjacentPositionsIndexes[1] = pos2;
		adjacentPositionsIndexes[2] = pos3;
	}

  /**
   * Adiciona as posições adjacentes a uma Posição que consegue ter 4 adjacências
   * @param pos1 uma das posições adjacentes
   * @param pos2 outra das posições adjacentes
   * @param pos3 terceira das posições adjacentes
   * @param pos4 a última das posições adjacentes
   */
	public void addAdjacentPositionsIndexes(int pos1, int pos2, int pos3, int pos4) {
		adjacentPositionsIndexes = new int[4];
		adjacentPositionsIndexes[0] = pos1;
		adjacentPositionsIndexes[1] = pos2;
		adjacentPositionsIndexes[2] = pos3;
		adjacentPositionsIndexes[3] = pos4;
	}

  /**
   *
   * @return
   */
	public int[] getAdjacentPositionsIndexes() {
		return adjacentPositionsIndexes;
	}

  /**
   *
   * @param posIndex
   * @return
   */
	public boolean isAdjacentToThis(int posIndex) {
		for(int i = 0; i < adjacentPositionsIndexes.length; i++) {
			if(adjacentPositionsIndexes[i]== posIndex) {
				return true;
			}
		}
		return false;
	}


	public void addLIndexes(int mill1, int mill2) {
		lIndexes.add(mill1);
		lIndexes.add(mill2);
	}

	public Set getLIndexes(){
		return this.lIndexes;
	}
}
