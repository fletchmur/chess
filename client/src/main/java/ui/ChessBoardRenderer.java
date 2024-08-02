package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashMap;

public class ChessBoardRenderer {
    private record Pair(ChessGame.TeamColor color, ChessPiece.PieceType type) {};
    private static final HashMap<Pair,String> pieceTypeStringMap = new HashMap<Pair,String>();

    private static final String EMPTY = EscapeSequences.EMPTY;
    private static final String LIGHT_SQUARE_COLOR = EscapeSequences.SET_BG_COLOR_LIGHT_BROWN;
    private static final String DARK_SQUARE_COLOR = EscapeSequences.SET_BG_COLOR_BROWN;
    private static final String WHITE = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private static final String BLACK = EscapeSequences.SET_TEXT_COLOR_BLACK;

    private static final String[] header = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
    private static final String[] side = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};

    static {
        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING), WHITE + EscapeSequences.WHITE_KING);
        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN), WHITE + EscapeSequences.WHITE_QUEEN);
        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP), WHITE + EscapeSequences.WHITE_BISHOP);
        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK), WHITE + EscapeSequences.WHITE_ROOK);
        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT), WHITE + EscapeSequences.WHITE_KNIGHT);
        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), WHITE + EscapeSequences.WHITE_PAWN);

        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING), BLACK + EscapeSequences.BLACK_KING);
        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN), BLACK + EscapeSequences.BLACK_QUEEN);
        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP), BLACK + EscapeSequences.BLACK_BISHOP);
        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK), BLACK + EscapeSequences.BLACK_ROOK);
        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT), BLACK + EscapeSequences.BLACK_KNIGHT);
        pieceTypeStringMap.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), BLACK + EscapeSequences.BLACK_PAWN);
    }

    private final ChessGame.TeamColor perspective;
    private ChessBoard board;
    private String nextSquareColor = DARK_SQUARE_COLOR;

    public ChessBoardRenderer(ChessBoard board,ChessGame.TeamColor perspective) {
        this.perspective = perspective;
        this.board = board;
    }

    public String render() {
        StringBuilder builder = new StringBuilder();
        builder.append(EscapeSequences.ERASE_SCREEN);

        builder.append(drawHeader());
        for(int i = 0; i < 8; i++) {
            int rowIndex = perspective == ChessGame.TeamColor.BLACK ? i+1 : 8-i;
            builder.append(drawRow(rowIndex));
        }
        builder.append(drawHeader());
        return builder.toString();
    }

    private String drawHeader() {
        StringBuilder builder = new StringBuilder();
        builder.append(EscapeSequences.SET_BG_COLOR_DULL_BLUE).append(EMPTY).append(EscapeSequences.SET_TEXT_COLOR_WHITE);
        builder.append(EscapeSequences.SET_TEXT_FAINT);
        for(int i = 0; i < header.length; i++){
            int index = perspective == ChessGame.TeamColor.BLACK ? 7-i : i;
            builder.append(header[index]);
        }
        builder.append(EMPTY).append(EscapeSequences.RESET_BG_COLOR).append("\n");
        return builder.toString();
    }

    private String drawSide(int row) {
        String builder = EscapeSequences.SET_BG_COLOR_DULL_BLUE + EscapeSequences.SET_TEXT_COLOR_WHITE +
                EscapeSequences.SET_TEXT_FAINT +
                side[row - 1];
        return builder;

    }

    private String drawRow(int row) {
        StringBuilder builder = new StringBuilder();
        builder.append(drawSide(row));
        for(int j = 0; j < 8; j++){
            int col = perspective == ChessGame.TeamColor.BLACK ? 8-j : j+1;
            ChessPosition position = new ChessPosition(row,col);
            ChessPiece piece = board.getPiece(position);
            builder.append(drawSquare(piece,getNextSquareColor()));
        }
        builder.append(drawSide(row));
        builder.append(EscapeSequences.RESET_BG_COLOR).append(EscapeSequences.RESET_TEXT_COLOR);
        builder.append("\n");
        getNextSquareColor();

        return builder.toString();
    }

    private String drawSquare(ChessPiece piece,String bgColor) {
        String pieceString = pieceTypeToString(piece);
        return bgColor + pieceString + EscapeSequences.RESET_BG_COLOR;
    }

    private String pieceTypeToString(ChessPiece piece) {

        if(piece == null) {
            return EMPTY + EscapeSequences.RESET_TEXT_COLOR;
        }

        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();
        return pieceTypeStringMap.get(new Pair(pieceColor, type)) + EscapeSequences.RESET_TEXT_COLOR;
    }

    private String getNextSquareColor() {
        String newColor = switch (nextSquareColor) {
            case LIGHT_SQUARE_COLOR -> DARK_SQUARE_COLOR;
            case DARK_SQUARE_COLOR -> LIGHT_SQUARE_COLOR;
            default -> EscapeSequences.RESET_BG_COLOR;
        };

        nextSquareColor = newColor;
        return nextSquareColor;
    }
}
