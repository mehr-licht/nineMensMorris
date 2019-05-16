public class Evaluation {

  private int R04_numPlayerPieces = 0; //R4
  private int emptyCells = 0;
  private int R44_numPlayerPieces = 0; //R44_numPlayerPieces
/*
fase 1
  Closed morris
  Morrises number                           [done]
  Number of   blocked opp. pieces
  Pieces number                             [done]
  Number of 2 pieces configurations         [done]
  Number of 3 pieces configurations
na fase 2:
  Closed morris
  Morrises number                           [done]
  Number of blocked opp. pieces
  Pieces number                             [done]
  Opened morris
  Double morris
  Winning configuration
fase3
  2 pieces configurations                   [done]
  3 pieces configurations
  Closed morris
  Winning configuration
 */
  private int R1_numPlayerMills = 0;//R1_numPlayerMills
  private int R2_numPlayerTwoPieceConf = 0;//R2_numPlayerTwoPieceConf
  private int R03 = 0;//Number of player opp. pieces
  private int R05 = 0;//player just made a mill
  private int R06 = 0;//Number of player 3 pieces configurations
  private int R07= 0;//player opened morris
  private int R08_numPlayerDoubleMorris = 0;//player double morris  [done]
  private int R09= 0;//player winning configuration
  private int R11_numOppMills = 0;//R1_numOppMills
  private int R22_numOppTwoPieceConf = 0; //R2_numOppTwoPieceConf
  private int R33 = 0;//Number of   blocked opp. pieces
  private int R55= 0;//opponent just made a mill
  private int R66= 0;//Number of opponent 3 pieces configurations
  private int R77 = 0;//opponent opened morris
  private int R88_numOpponentDoubleMorris = 0;//opponent double morris [done]
  private int R99 = 0;//oponent wining configuration
  private int score=0;
  private int coef=0;

  public void Evaluation (){
  }


  public int getR1_numPlayerMills() {
    return R1_numPlayerMills;
  }

  public void setR1_numPlayerMills(int r01) {
    R1_numPlayerMills = r01;
  }

  public int getR2_numPlayerTwoPieceConf() {
    return R2_numPlayerTwoPieceConf;
  }

  public void setR2_numPlayerTwoPieceConf(int r02) {
    R2_numPlayerTwoPieceConf = r02;
  }

  public int getR03() {
    return R03;
  }

  public void setR03(int r03) {
    R03 = r03;
  }

  public int getR04_numPlayerPieces() {
    return R04_numPlayerPieces;
  }

  public void setR04_numPlayerPieces(int r04) {
    R04_numPlayerPieces = r04;
  }

  public int getR05() {
    return R05;
  }

  public void setR05(int r05) {
    R05 = r05;
  }

  public int getR06() {
    return R06;
  }

  public void setR06(int r06) {
    R06 = r06;
  }

  public int getR07() {
    return R07;
  }

  public void setR07(int r07) {
    R07 = r07;
  }

  public int getR08_numPlayerDoubleMorris() {
    return R08_numPlayerDoubleMorris;
  }

  public void setR08_numPlayerDoubleMorris(int r08) {
    R08_numPlayerDoubleMorris = r08;
  }

  public int getR09() {
    return R09;
  }

  public void setR09(int r09) {
    R09 = r09;
  }

  public int getR11_numOppMills() {
    return R11_numOppMills;
  }

  public void setR11_numOppMills(int r11) {
    R11_numOppMills = r11;
  }

  public int getR22_numOppTwoPieceConf() {
    return R22_numOppTwoPieceConf;
  }

  public void setR22_numOppTwoPieceConf(int r22) {
    R22_numOppTwoPieceConf = r22;
  }

  public int getR33() {
    return R33;
  }

  public void setR33(int r33) {
    R33 = r33;
  }

  public int getR44_numPlayerPieces() {
    return R44_numPlayerPieces;
  }

  public void setR44_numPlayerPieces(int r44) {
    R44_numPlayerPieces = r44;
  }

  public int getR55() {
    return R55;
  }

  public void setR55(int r55) {
    R55 = r55;
  }

  public int getR66() {
    return R66;
  }

  public void setR66(int r66) {
    R66 = r66;
  }

  public int getR77() {
    return R77;
  }

  public void setR77(int r77) {
    R77 = r77;
  }

  public int getR88_numOpponentDoubleMorris() {
    return R88_numOpponentDoubleMorris;
  }

  public void setR88_numOpponentDoubleMorris(int r88) {
    R88_numOpponentDoubleMorris = r88;
  }

  public int getR99() {
    return R99;
  }

  public void setR99(int r99) {
    R99 = r99;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getCoef() {
    return coef;
  }

  public void setCoef(int coef) {
    this.coef = coef;
  }



  public int getEmptyCells() {
    return emptyCells;
  }

  public void setEmptyCells(int emptyCells) {
    this.emptyCells = emptyCells;
  }

  public int getR44_numOpponentPieces() {
    return R44_numPlayerPieces;
  }

  public void setR44_numOpponentPieces(int R44_numPlayerPieces) {
    this.R44_numPlayerPieces = R44_numPlayerPieces;
  }
}
