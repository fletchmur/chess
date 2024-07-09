package chess;

public class TurnHandler {

    private ChessGame.TeamColor currentTeam;
    private ChessGame.TeamColor opponentTeam;

    public TurnHandler() {
        currentTeam = ChessGame.TeamColor.WHITE;
        opponentTeam = ChessGame.TeamColor.BLACK;
    }

    public void takeTurn()
    {
        ChessGame.TeamColor temp = currentTeam;
        currentTeam = opponentTeam;
        opponentTeam = temp;
    }
    
    public boolean checkCurrentTeam(ChessGame.TeamColor checkTeam) {
        return currentTeam == checkTeam;
    }
    public boolean checkOpponentTeam(ChessGame.TeamColor checkTeam) {
        return opponentTeam == checkTeam;
    }
    public ChessGame.TeamColor getCurrentTeam() {
        return currentTeam;
    }
    public ChessGame.TeamColor getOpponentTeam() {
        return opponentTeam;
    }
}
