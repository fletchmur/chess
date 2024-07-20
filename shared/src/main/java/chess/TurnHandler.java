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

    public void setTeamTurn(ChessGame.TeamColor team)
    {
        currentTeam = team;
        opponentTeam = switch (team)
        {
            case WHITE -> ChessGame.TeamColor.BLACK;
            case BLACK -> ChessGame.TeamColor.WHITE;
        };
    }
    
    public boolean myTurn(ChessGame.TeamColor checkTeam) {
        return currentTeam == checkTeam;
    }
    public ChessGame.TeamColor getCurrentTeam() {
        return currentTeam;
    }
    public ChessGame.TeamColor getOpponentTeam() {
        return opponentTeam;
    }
}
