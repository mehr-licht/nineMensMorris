import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class NineMensMorris {
  public Game game;
  public BufferedReader input;
  public static final int MAX_MOVES = 150;
  public static int totalMoves = 0;
 

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("usage NineMensMorris <depth>");
      System.exit(0);
    }

    System.out.println("Nine Men's Morris starting with depth " + args[1]);
    Log.set(Log.LEVEL_ERROR);
    NineMensMorris maingame = new NineMensMorris();
    maingame.input = new BufferedReader(new InputStreamReader(System.in));

    maingame.createLocalGame(Integer.parseInt(args[1]));
  }

  public void createLocalGame(int minimaxDepth) throws IOException, GameException {
    System.out.println("Player 1: (H)UMAN or (C)PU?");
    String userInput = input.readLine();
    userInput = userInput.toUpperCase();
    Player p1 = null, p2 = null;
    boolean bothCPU = true;
    int numberGames = 0, fixedNumberGames = 1, numberMoves = 0, draws = 0, p1Wins = 0, p2Wins = 0;

    if (userInput.compareTo("HUMAN") == 0 || userInput.compareTo("H") == 0) {
      p1 = new HumanPlayer("IART", Token.PLAYER_1, Game.NUM_PIECES_PER_PLAYER);
      bothCPU = false;
    } else if (userInput.compareTo("CPU") == 0 || userInput.compareTo("C") == 0) {

      			p1 = new MinimaxIAPlayer(Token.PLAYER_1, Game.NUM_PIECES_PER_PLAYER, minimaxDepth);
    } else {
      System.out.println("Command unknown");
      System.exit(-1);
    }

    System.out.println("Player 2: (H)UMAN or (C)PU?");
    userInput = input.readLine();
    userInput = userInput.toUpperCase();

    if (userInput.compareTo("HUMAN") == 0 || userInput.compareTo("H") == 0) {
      p2 = new HumanPlayer("IART", Token.PLAYER_2, Game.NUM_PIECES_PER_PLAYER);
      bothCPU = false;
    } else if (userInput.compareTo("CPU") == 0 || userInput.compareTo("C") == 0) {
      p2 = new MinimaxIAPlayer(Token.PLAYER_2, Game.NUM_PIECES_PER_PLAYER, minimaxDepth - 2);
    } else {
      System.out.println("Command unknown");
      System.exit(-1);
    }

    if (bothCPU) {
      System.out.println("Number of games: ");
      userInput = input.readLine();
      numberGames = Integer.parseInt(userInput.toUpperCase());
      fixedNumberGames = numberGames;
    } else {
      numberGames = 1;
    }

    game = new LocalGame();
    ((LocalGame) game).setPlayers(p1, p2);

    long gamesStart = System.nanoTime();
    while (numberGames > 0) {
      if ((numberGames-- % 50) == 0) {
        System.out.println("Games left: " + numberGames);
      }

      while (game.getCurrentGamePhase() == Game.PLACING_PHASE) {
        System.out.println(Colours.RED+"08_gameLoop"+Colours.RESET);
        while (true) {
          System.out.println(Colours.GREEN+"09_innerLoop"+Colours.RESET);
          Player p = ((LocalGame) game).getCurrentTurnPlayer();
          int boardIndex;

          if (p.isAI()) {
            long startTime = System.nanoTime();
       						System.out.println("AI THINKING");
            boardIndex = ((IAPlayer) p).getIndexToPlacePiece(game.gameBoard);
            game.gameBoard.globalIndex=boardIndex;
            long endTime = System.nanoTime();
            					game.printGameBoard();
            Log.warn("Number of moves: " + ((IAPlayer) p).numberOfMoves);
            Log.warn("Moves that removed: " + ((IAPlayer) p).movesThatRemove);
            Log.warn("It took: " + (endTime - startTime) / 1000000 + " miliseconds");
       					System.out.println(p.getName()+" placed piece on "+boardIndex);

          } else {
            game.printGameBoard();
            System.out.println(p.getName() + " place piece on: ");
            userInput = input.readLine();
            userInput = userInput.toUpperCase();
            boardIndex = Integer.parseInt(userInput);
            game.gameBoard.globalIndex=boardIndex;
          }

          if (game.placePieceOfPlayer(boardIndex, p.getPlayerToken())) {
            System.out.println(Colours.YELLOW+"00_vai incrementar"+Colours.RESET);
            numberMoves++; // TODO testing
            totalMoves++;
            p.raiseNumPiecesOnBoard();

            if (game.madeAMill(boardIndex, p.getPlayerToken())) {
              System.out.println(Colours.BLUE+"01_mill"+Colours.RESET);
              //TODO meter aqui um showBoard()
              game.printGameBoard();
              Token opponentPlayer =
                  (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;

              while (true) {
                System.out.println(Colours.PURPLE+"02_while"+Colours.RESET);
                if (p.isAI()) {
                  boardIndex = ((IAPlayer) p).getIndexToRemovePieceOfOpponent(game.gameBoard);
                  game.gameBoard.globalIndex=boardIndex;
                  System.out.println(p.getName()+" removes opponent piece on "+boardIndex);
                } else {
                  System.out.println("You made a mill. You can remove a piece of your oponent: ");
                  userInput = input.readLine();
                  userInput = userInput.toUpperCase();
                  boardIndex = Integer.parseInt(userInput);
                  game.gameBoard.globalIndex=boardIndex;
                }
                if (game.removePiece(boardIndex, opponentPlayer)) {
                  break;
                } else {
                  System.out.println("You can't remove a piece from there. Try again");
                }
              }
            }
            ((LocalGame) game).updateCurrentTurnPlayer();
            break;
          } else {
            System.out.println("You can't place a piece there. Try again");
          }
        }
      }

      	System.out.println("The pieces are all placed. Starting the fun part... ");
      while (!game.isTheGameOver() && numberMoves < NineMensMorris.MAX_MOVES) {

        while (true) {
          System.out.println(Colours.CYAN+"03_outro while"+Colours.RESET);
          //					System.out.println("Number of moves made: "+numberMoves);
          Player p = ((LocalGame) game).getCurrentTurnPlayer();
          int srcIndex, destIndex;
          Move move = null;

          if (p.isAI()) {
            						long startTime = System.nanoTime();
            System.out.println("AI THINKING");
            move = ((IAPlayer) p).getPieceMove(game.gameBoard, game.getCurrentGamePhase());
            						long endTime = System.nanoTime();
            					game.printGameBoard();

             System.out.println("Number of moves: "+((MinimaxIAPlayer)p).numberOfMoves);
            					System.out.println("Moves that removed: "+((MinimaxIAPlayer)p).movesThatRemove);
            						System.out.println("It took: "+ (endTime - startTime)/1000000+" miliseconds");
            srcIndex = move.srcIndex;
            destIndex = move.destIndex;
            game.gameBoard.globalIndex=destIndex;
            						System.out.println(p.getName()+" moved piece from "+srcIndex+" to "+destIndex);
          } else {
            game.printGameBoard();
            						System.out.println(p.getName()+" Mover peça, no formato => de:para");

            userInput = input.readLine();
            userInput = userInput.toUpperCase();
            String[] positions = userInput.split(":");
            srcIndex = Integer.parseInt(positions[0]);
            destIndex = Integer.parseInt(positions[1]);
            game.gameBoard.globalIndex=destIndex;
            System.out.println("Move piece from " + srcIndex + " to " + destIndex);
          }

          int result;
          if ((result = game.movePieceFromTo(srcIndex, destIndex, p.getPlayerToken()))
              == Game.VALID_MOVE) {
            numberMoves++; // TODO testing
            totalMoves++;
            if (game.madeAMill(destIndex, p.getPlayerToken())) {
              game.printGameBoard();
              Token opponentPlayerToken =
                  (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
              int boardIndex;

              while (true) {
                System.out.println(Colours.BLINK+"05_outro outro while"+Colours.RESET);
                if (p.isAI()) {
                  boardIndex = move.removePieceOnIndex;
                  game.gameBoard.globalIndex=boardIndex;
        					System.out.println(p.getName()+" removes opponent piece on "+boardIndex);
                } else {
                  game.printGameBoard();//novo
                   System.out.println("You made a mill! You can remove a piece of your oponent: ");
                  userInput = input.readLine();
                  userInput = userInput.toUpperCase();
                  boardIndex = Integer.parseInt(userInput);
                  game.gameBoard.globalIndex=boardIndex;
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
            ((LocalGame) game).updateCurrentTurnPlayer();
          } else {
            System.out.println("Invalid move. Error code: " + result);
          }
        }
      }

      if (!game.isTheGameOver()) {

        			System.out.println("Draw!");
        draws++;
      } else {

        				System.out.println("Game over. Player "+((LocalGame)game).getCurrentTurnPlayer().getPlayerToken()+" Won");
        if (((LocalGame) game).getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1) {
          p1Wins++;
        } else {
          p2Wins++;
        }
      }
      numberMoves = 0;
      game = new LocalGame();
      p1.reset();
      p2.reset();
      ((LocalGame) game).setPlayers(p1, p2);
    }
    long gamesEnd = System.nanoTime();
    System.out.println(
        fixedNumberGames
            + " games completed in: "
            + (gamesEnd - gamesStart) / 1000000000
            + " seconds");
    System.out.println("Average number of ply: " + (totalMoves / fixedNumberGames));
    System.out.println("Draws: " + draws + " (" + ((float) draws / fixedNumberGames) * 100 + "%)");
    System.out.println(
        "P1 Wins: " + p1Wins + " (" + ((float) p1Wins / fixedNumberGames) * 100 + "%)");
    System.out.println(
        "P2 Wins: " + p2Wins + " (" + ((float) p2Wins / fixedNumberGames) * 100 + "%)");
  }
}
