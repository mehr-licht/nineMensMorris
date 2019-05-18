import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NineMensMorris {
  public Game game;
  public BufferedReader input;
  public static final int MAX_MOVES = 150;
  public static int totalMoves = 0;
  /**
   * padrões regex para comparar com os inputs introduzidos
   */
  static final String MOVE_PATTERN = "^(\\d|1\\d|2[0-3])\\:(\\d|1\\d|2[0-3])$";
  static final String PLACE_PATTERN = "^(\\d|1\\d|2[0-3])$";
  static final String HUMANCPU_PATTERN = "(?i)^(h(uman)?|c(pu)?)$";
  /**
   * Mensagens para pedir os inputs
   */
  static final String MOVE_MSG=" Mover peça, no formato => de:para";
  static final String PLACE_MSG=" place piece on: ";
  static final String MILL_MSG="You made a mill. You can remove a piece of your oponent: ";


  public static void main(String[] args) throws Exception {

    if (args.length != 1) {
      System.out.println("usage NineMensMorris <depth>");
      System.exit(0);
    }

    System.out.println("Nine Men's Morris starting with depth " + args[0]);
    Log.set(Log.LEVEL_ERROR);
    NineMensMorris maingame = new NineMensMorris();
    maingame.input = new BufferedReader(new InputStreamReader(System.in));

    maingame.createGame(Integer.parseInt(args[0]));
  }

  public void createGame(int minimaxDepth) throws IOException, GameException {
    String userInput ;
    userInput = getInput(HUMANCPU_PATTERN,"Player 1: (H)UMAN or (C)PU?").toUpperCase();
    Player p1 = null, p2 = null;
    boolean bothCPU = true;
    int numberGames = 0, fixedNumberGames = 1, numberMoves = 0, draws = 0, p1Wins = 0, p2Wins = 0;

    if (userInput.compareTo("HUMAN") == 0 || userInput.compareTo("H") == 0) {
      p1 = new HumanPlayer("IART", Token.PLAYER_1, Game.NUM_PIECES_PER_PLAYER);
      bothCPU = false;
    } else if (userInput.compareTo("CPU") == 0 || userInput.compareTo("C") == 0) {

      p1 = new IAPlayer("CPU", Token.PLAYER_1, Game.NUM_PIECES_PER_PLAYER, minimaxDepth);
    } else {
      System.out.println("Command unknown");
      System.exit(-1);
    }

    userInput = getInput(HUMANCPU_PATTERN,"Player 2: (H)UMAN or (C)PU?").toUpperCase();

    if (userInput.compareTo("HUMAN") == 0 || userInput.compareTo("H") == 0) {
      p2 = new HumanPlayer("IART", Token.PLAYER_2, Game.NUM_PIECES_PER_PLAYER);
      bothCPU = false;
    } else if (userInput.compareTo("CPU") == 0 || userInput.compareTo("C") == 0) {
      p2 = new IAPlayer("CPU", Token.PLAYER_2, Game.NUM_PIECES_PER_PLAYER, minimaxDepth - 2);
    } else {
      System.out.println("Command unknown");
      System.exit(-1);
    }

    if (bothCPU) {
      numberGames = Integer.parseInt(getInput(PLACE_PATTERN,"Number Of Games?").toUpperCase());
      fixedNumberGames = numberGames;
    } else {
      numberGames = 1;
    }

    game = new Game();
    game.setPlayers(p1, p2);

    long gamesStart = System.nanoTime();
    while (numberGames > 0) {
      if ((numberGames-- % 50) == 0) {
        System.out.println("Games left: " + numberGames);
      }

      while (game.getCurrentGamePhase() == Game.PLACING_PHASE) {
        // System.out.println(Colours.RED+"08_gameLoop"+Colours.RESET);
        while (true) {
          //  System.out.println(Colours.GREEN+"09_innerLoop"+Colours.RESET);
          Player p = game.getCurrentTurnPlayer();
          int boardIndex;

          if (p.isAI()) {
            long startTime = System.nanoTime();
            System.out.println("AI THINKING");
            boardIndex = ((IAPlayer) p).getIndexToPlacePiece(game.gameBoard);
            game.gameBoard.globalIndex = boardIndex;
            long endTime = System.nanoTime();
            game.printGameBoard();
            Log.warn("Number of moves: " + ((IAPlayer) p).numberOfMoves);
            Log.warn("Moves that removed: " + ((IAPlayer) p).movesThatRemove);
            Log.warn("It took: " + (endTime - startTime) / 1000000 + " miliseconds");
            System.out.println(p.getName() + " placed piece on " + boardIndex);

          } else {
            game.printGameBoard();
            boardIndex = Integer.parseInt(getInput(PLACE_PATTERN, p.getName() + PLACE_MSG));
            game.gameBoard.globalIndex = boardIndex;
          }

          if (game.placePieceOfPlayer(boardIndex, p.getPlayerToken())) {
            //  System.out.println(Colours.YELLOW+"00_vai incrementar"+Colours.RESET);
            numberMoves++; // TODO testing
            totalMoves++;
            p.raiseNumPiecesOnBoard();
            // game.totalMills( p.getPlayerToken());  //TODO APAGAR
            if (game.madeAMill(boardIndex, p.getPlayerToken())) {
              // System.out.println(Colours.BLUE+"01_mill"+Colours.RESET);

              game.printGameBoard();
              Token opponentPlayer =
                  (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;

              while (true) {
                //  System.out.println(Colours.PURPLE+"02_while"+Colours.RESET);
                if (p.isAI()) {
                  boardIndex = ((IAPlayer) p).getIndexToRemovePieceOfOpponent(game.gameBoard);
                  game.gameBoard.globalIndex = boardIndex;
                  System.out.println(p.getName() + " removes opponent piece on " + boardIndex);
                } else {
                  System.out.println();
                  boardIndex = Integer.parseInt(getInput(PLACE_PATTERN, MILL_MSG));
                  game.gameBoard.globalIndex = boardIndex;
                }
                if (game.removePiece(boardIndex, opponentPlayer)) {
                  break;
                } else {
                  System.out.println("You can't remove a piece from there. Try again");
                }
              }
            }
            game.updateCurrentTurnPlayer();
            break;
          } else {
            System.out.println("You can't place a piece there. Try again");
          }
        }
      }

      System.out.println("The pieces are all placed. Starting the fun part... ");
      while (!game.isTheGameOver() && numberMoves < NineMensMorris.MAX_MOVES) {

        while (true) {
          // System.out.println(Colours.CYAN+"03_outro while"+Colours.RESET);
          //					System.out.println("Number of moves made: "+numberMoves);
          Player p = game.getCurrentTurnPlayer();
          int srcIndex, destIndex;
          Move move = null;

          if (p.isAI()) {
            long startTime = System.nanoTime();
            System.out.println("AI THINKING");
            move = ((IAPlayer) p).getPieceMove(game.gameBoard, game.getCurrentGamePhase());
            long endTime = System.nanoTime();
            game.printGameBoard();

            System.out.println("Number of moves: " + ((IAPlayer) p).numberOfMoves);
            System.out.println("Moves that removed: " + ((IAPlayer) p).movesThatRemove);
            System.out.println("It took: " + (endTime - startTime) / 1000000 + " miliseconds");
            srcIndex = move.srcIndex;
            destIndex = move.destIndex;
            game.gameBoard.globalIndex = destIndex;
            System.out.println(p.getName() + " moved piece from " + srcIndex + " to " + destIndex);
          } else {
            game.printGameBoard();
            String[] positions = getInput(MOVE_PATTERN, p.getName() + MOVE_MSG).split(":");
            srcIndex = Integer.parseInt(positions[0]);
            destIndex = Integer.parseInt(positions[1]);
            game.gameBoard.globalIndex = destIndex;
            System.out.println("Move piece from " + srcIndex + " to " + destIndex);
          }

          int result;
          if ((result = game.movePieceFromTo(srcIndex, destIndex, p.getPlayerToken()))
              == Game.VALID_MOVE) {
            numberMoves++; // TODO testing
            totalMoves++;
            // game.totalMills( p.getPlayerToken());  //TODO APAGAR
            if (game.madeAMill(destIndex, p.getPlayerToken())) {
              game.printGameBoard();
              Token opponentPlayerToken =
                  (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
              int boardIndex;

              while (true) {
                // System.out.println(Colours.BLINK+"05_outro outro while"+Colours.RESET);
                if (p.isAI()) {
                  boardIndex = move.removePieceOnIndex;
                  game.gameBoard.globalIndex = boardIndex;
                  System.out.println(p.getName() + " removes opponent piece on " + boardIndex);
                } else {
                  game.printGameBoard(); // novo
                  boardIndex = Integer.parseInt(getInput(PLACE_PATTERN, p.getName() + MILL_MSG));
                  game.gameBoard.globalIndex = boardIndex;
                }
                if (game.removePiece(boardIndex, opponentPlayerToken)) {
                  break;
                } else {
                  System.out.println("It couldn't be done! Try again.");
                }
              }
            }

            if (game.isTheGameOver() || numberMoves >= MAX_MOVES) {
              game.printGameBoard();
              break;
            }
            game.updateCurrentTurnPlayer();
          } else {
            System.out.println("Invalid move. Error code: " + result);
          }
        }
      }

      if (!game.isTheGameOver()) {

        System.out.println("Draw!");
        draws++;
      } else {

        System.out.println(
            "Game over. Player " + ((Game) game).getCurrentTurnPlayer().getPlayerToken() + " Won");
        if (game.getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1) {
          p1Wins++;
        } else {
          p2Wins++;
        }
      }
      numberMoves = 0;
      game = new Game();
      p1.reset();
      p2.reset();
      game.setPlayers(p1, p2);
    }
    long gamesEnd = System.nanoTime();
    print_stats_match(gamesStart, gamesEnd);
    if (fixedNumberGames != 1)
      print_stats_tournement(fixedNumberGames, draws, p1Wins, p2Wins, gamesStart, gamesEnd);
  }

  /**
   * Imprime as estatisticas de uma partida
   * @param gamesStart tempo do inicio da partida em nanosegundos (Java Virtual Machine's high-resolution time source)
   * @param gamesEnd tempo do fim da partida em nanosegundos (Java Virtual Machine's high-resolution time source)
   */
  private void print_stats_match(long gamesStart, long gamesEnd) {
    System.out.println(
        "\n match completed in: " + (gamesEnd - gamesStart) / 1000000000 + " seconds\n");
  }

  /**
   * Imprime as estatisticas de um conjunto de partidas(só com CPU vs CPU)
   * @param fixedNumberGames numero de partidas efectuadas
   * @param draws numero de empates
   * @param p1Wins numero de vitorias do player1
   * @param p2Wins numero de vitorias do player2
   * @param gamesStart tempo do inicio de todas as partidas em nanosegundos (Java Virtual Machine's high-resolution time source)
   * @param gamesEnd tempo do fim de todas as partidas em nanosegundos (Java Virtual Machine's high-resolution time source)
   */
  private void print_stats_tournement(
      int fixedNumberGames, int draws, int p1Wins, int p2Wins, long gamesStart, long gamesEnd) {
    System.out.println(
        "\n"
            + fixedNumberGames
            + " games completed in: "
            + (gamesEnd - gamesStart) / 1000000000
            + " seconds");
    System.out.println("Average number of plays: " + (totalMoves / fixedNumberGames));
    System.out.println("Draws: " + draws + " (" + ((float) draws / fixedNumberGames) * 100 + "%)");
    System.out.println(
        "P1 Wins: " + p1Wins + " (" + ((float) p1Wins / fixedNumberGames) * 100 + "%)");
    System.out.println(
        "P2 Wins: " + p2Wins + " (" + ((float) p2Wins / fixedNumberGames) * 100 + "%)\n");
  }

  /**
   * Verifica se o input introduzido está de acordo com o esperado
   *
   * @param matcherStr regex a que tem de obedecer o texto introduzido
   * @param patternStr texto recebido do buffer
   * @return verdadeiro ou falso
   */
  boolean checkInput(String matcherStr, String patternStr) {
    Pattern pattern = Pattern.compile(patternStr);
    Matcher matcher = pattern.matcher(matcherStr);
    Boolean res = matcher.find();
    return res;
  }

  /**
   * Devolve o input introduzido depois de ser aceite
   * @param pattern texto recebido do buffer
   * @param message texto a pedir que se introduza dados
   * @return input introduzido e já validado
   * @throws IOException
   */
  String getInput(String pattern, String message) throws IOException {
    boolean valid = false;
    String res="";
    while (!valid) {
      System.out.println(message);
      res= input.readLine();
      if (checkInput(res, pattern)) {
        valid = true;
      } else {
        System.out.println("Invalid input, please retry:\n");
      }
    }
    return res;
  }

}
