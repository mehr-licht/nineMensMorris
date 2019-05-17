public class Evaluation {




  private Coefs coef=new Coefs();
  private int R04_numPlayerPieces = 0; //R4
  private int emptyCells = 0;
  private int R04_numOpponentPieces = 0; //R04_numOpponentPieces
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
  private int R01_numPlayerMills = 0;
  private int R02_numPlayerTwoPieceConf = 0;
  private int R03_numBlockedPlayerPieces = 0;
  private int R05_playerJustMadeAMill = 0;
  private int R06_player3PiecesConfigurations = 0;
  private int R07_playerOpenedMills = 0;
  private int R08_numPlayerDoubleMorris = 0;
  private int R09_playerWinningConfiguration= 0;
  private int R01_numOppMills = 0;
  private int R02_numOppTwoPieceConf = 0;
  private int R03_numBlockedOpponentPieces = 0;
  private int R05_opponentJustMadeAMill = 0;
  private int R06_opponent3PiecesConfigurations = 0;
  private int R07_opponentOpenedMills = 0;
  private int R08_numOpponentDoubleMorris = 0;
  private int R09_opponentWinningConfiguration = 0;
  private int R10_playerInIntersection =0;
  private int R10_opponentInIntersection = 0;
  private int R00_playerAsideIntersection =0;
  private int R00_opponentAsideIntersection = 0;
  private int score=0;
  //private int coef=0;

  public void setCoefs(int R1,int R2,int R3,int R4, int R5, int R6){
   this.coef.setR1(R1);
    this.coef.setR2(R2);
    this.coef.setR3(R3);
    this.coef.setR4(R4);
    this.coef.setR5(R5);
    this.coef.setR6(R6);

  }


  public void Evaluation (){
    this.coef = new Coefs();
  }

  public void incR01_numPlayerMills(int inc){
    R01_numPlayerMills+=inc;
  }

  public void incR01_numOppMills(int inc){
    R01_numOppMills +=inc;
  }
  public void incR02_numPlayerTwoPieceConf (int inc){
    R02_numPlayerTwoPieceConf +=inc;
  }
  public void incR02_numOppTwoPieceConf(int inc){
    R02_numOppTwoPieceConf+=inc;
  }
  public void incR03_numBlockedPlayerPieces(int inc){
    R03_numBlockedPlayerPieces +=inc;
  }
  public void incR03_numBlockedOpponentPieces(int inc){
    R03_numBlockedOpponentPieces+=inc;
  }
  public void incR04_numPlayerPieces(int inc){
    R04_numPlayerPieces +=inc;
  }
  public void incR04_numOpponentPieces(int inc){
    R04_numOpponentPieces+=inc;
  }
  public void incR05_playerJustMadeAMill(int inc){
    R05_playerJustMadeAMill+=inc;
  }
  public void incR05_opponentJustMadeAMill(int inc){
    R05_opponentJustMadeAMill+=inc;
  }
  public void incR06_player3PiecesConfigurations(int inc){
    R06_player3PiecesConfigurations+=inc;
  }
  public void incR06_opponent3PiecesConfigurations(int inc){
    R06_opponent3PiecesConfigurations+=inc;
  }
  public void incR07_playerOpenedMills(int inc){
    R07_playerOpenedMills+=inc;
  }
  public void incR07_opponentOpenedMills(int inc){
    R07_opponentOpenedMills+=inc;
  }
  public void incR08_numPlayerDoubleMorris(int inc){
    R08_numPlayerDoubleMorris+=inc;
  }
  public void incR08_numOpponentDoubleMorris(int inc){
    R08_numOpponentDoubleMorris+=inc;
  }
  public void incR09_playerWinningConfiguration(int inc){
    R09_playerWinningConfiguration+=inc;
  }
  public void incR09_opponentWinningConfiguration(int inc){
    R09_opponentWinningConfiguration+=inc;
  }
  public void incR00_playerAsideIntersection(int inc){
    R00_playerAsideIntersection+=inc;
  }
  public void incR00_opponentAsideIntersection(int inc){
    R00_opponentAsideIntersection+=inc;
  }


  public int getR01_numPlayerMills() {
    return R01_numPlayerMills;
  }

  public void setR01_numPlayerMills(int r01) {
    R01_numPlayerMills = r01;
  }

  public int getR02_numPlayerTwoPieceConf() {
    return R02_numPlayerTwoPieceConf;
  }

  public void setR02_numPlayerTwoPieceConf(int r02) {
    R02_numPlayerTwoPieceConf = r02;
  }

  public int getR03_numBlockedPlayerPieces() {
    return R03_numBlockedPlayerPieces;
  }

  public void setR03_numBlockedPlayerPieces(int r03_numBlockedPlayerPieces) {
    R03_numBlockedPlayerPieces = r03_numBlockedPlayerPieces;
  }

  public int getR04_numPlayerPieces() {
    return R04_numPlayerPieces;
  }

  public void setR04_numPlayerPieces(int r04) {
    R04_numPlayerPieces = r04;
  }

  public int getR05_playerJustMadeAMill() {
    return R05_playerJustMadeAMill;
  }

  public void setR05_playerJustMadeAMill(int r05_playerJustMadeAMill) {
    R05_playerJustMadeAMill = r05_playerJustMadeAMill;
  }

  public int getR06_player3PiecesConfigurations() {
    return R06_player3PiecesConfigurations;
  }

  public void setR06_player3PiecesConfigurations(int r06_player3PiecesConfigurations) {
    R06_player3PiecesConfigurations = r06_player3PiecesConfigurations;
  }

  public int getR07_playerOpenedMills() {
    return R07_playerOpenedMills;
  }

  public void setR07_playerOpenedMills(int r07_playerOpenedMills) {
    R07_playerOpenedMills = r07_playerOpenedMills;
  }

  public int getR08_numPlayerDoubleMorris() {
    return R08_numPlayerDoubleMorris;
  }

  public void setR08_numPlayerDoubleMorris(int r08) {
    R08_numPlayerDoubleMorris = r08;
  }


  public int getR01_numOppMills() {
    return R01_numOppMills;
  }

  public void setR01_numOppMills(int r11) {
    R01_numOppMills = r11;
  }

  public int getR02_numOppTwoPieceConf() {
    return R02_numOppTwoPieceConf;
  }

  public void setR02_numOppTwoPieceConf(int r22) {
    R02_numOppTwoPieceConf = r22;
  }

  public int getR03_numBlockedOpponentPieces() {
    return R03_numBlockedOpponentPieces;
  }

  public void setR03_numBlockedOpponentPieces(int r33) {
    R03_numBlockedOpponentPieces = r33;
  }

  public int getR04_numOpponentPieces() {
    return R04_numOpponentPieces;
  }

  public void setR04_numOpponentPieces(int r44) {
    R04_numOpponentPieces = r44;
  }

  public int getR05_opponentJustMadeAMill() {
    return R05_opponentJustMadeAMill;
  }

  public void setR05_opponentJustMadeAMill(int r55) {
    R05_opponentJustMadeAMill = r55;
  }

  public int getR06_opponent3PiecesConfigurations() {
    return R06_opponent3PiecesConfigurations;
  }

  public void setR06_opponent3PiecesConfigurations(int r66) {
    R06_opponent3PiecesConfigurations = r66;
  }

  public int getR07_opponentOpenedMills() {
    return R07_opponentOpenedMills;
  }

  public void setR07_opponentOpenedMills(int r77) {
    R07_opponentOpenedMills = r77;
  }

  public int getR08_numOpponentDoubleMorris() {
    return R08_numOpponentDoubleMorris;
  }

  public void setR08_numOpponentDoubleMorris(int r88) {
    R08_numOpponentDoubleMorris = r88;
  }

  public int getR09_playerWinningConfiguration() {
    return R09_playerWinningConfiguration;
  }

  public void setR09_playerWinningConfiguration(int r99) {
    R09_playerWinningConfiguration = r99;
  }

  public int getR09_opponentWinningConfiguration() {
    return R09_opponentWinningConfiguration;
  }

  public void setR09_opponentWinningConfiguration(int r99) {
    R09_opponentWinningConfiguration = r99;
  }

  public int getR10_playerInIntersection() {
    return R10_playerInIntersection;
  }

  public void setR10_playerInIntersection(int r10_playerInIntersection) {
    R10_playerInIntersection = r10_playerInIntersection;
  }

  public int getR10_opponentInIntersection() {
    return R10_opponentInIntersection;
  }

  public void setR10_opponentInIntersection(int r1010_opponentInIntersection) {
    R10_opponentInIntersection = r1010_opponentInIntersection;
  }

  public int getR00_playerAsideIntersection() {
    return R00_playerAsideIntersection;
  }

  public void setR00_playerAsideIntersection(int r00_playerAsideIntersection) {
    R00_playerAsideIntersection = r00_playerAsideIntersection;
  }

  public int getR00_opponentAsideIntersection() {
    return R00_opponentAsideIntersection;
  }

  public void setR00_opponentAsideIntersection(int r0000_opponentAsideIntersection) {
    R00_opponentAsideIntersection = r0000_opponentAsideIntersection;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public Coefs getCoef() {
    return coef;
  }



  public int getEmptyCells() {
    return emptyCells;
  }

  public void setEmptyCells(int emptyCells) {
    this.emptyCells = emptyCells;
  }

  public int getR4_numOpponentPieces() {
    return R04_numOpponentPieces;
  }

  public void setR4_numOpponentPieces(int R4_numPlayerPieces) {
    this.R04_numOpponentPieces = R4_numPlayerPieces;
  }
}
