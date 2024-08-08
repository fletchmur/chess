package ui;

import chess.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class ChessBoardRenderer {
    private record Pair(ChessGame.TeamColor color, ChessPiece.PieceType type) {};
    private static final HashMap<Pair,String> PIECE_STRING_MAP = new HashMap<Pair,String>();

    private static final String EMPTY = EscapeSequences.EMPTY;
    private static final String LIGHT_SQUARE_COLOR = EscapeSequences.SET_BG_COLOR_LIGHT_BROWN;
    private static final String DARK_SQUARE_COLOR = EscapeSequences.SET_BG_COLOR_BROWN;
    private static final String WHITE = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private static final String BLACK = EscapeSequences.SET_TEXT_COLOR_BLACK;

    private static final String[] HEADER = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
    private static final String[] SIDE = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};

    static {
        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING), EscapeSequences.WHITE_KING);
        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN), EscapeSequences.WHITE_QUEEN);
        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP), EscapeSequences.WHITE_BISHOP);
        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK), EscapeSequences.WHITE_ROOK);
        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT), EscapeSequences.WHITE_KNIGHT);
        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), EscapeSequences.WHITE_PAWN);

        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING), EscapeSequences.BLACK_KING);
        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN), EscapeSequences.BLACK_QUEEN);
        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP), EscapeSequences.BLACK_BISHOP);
        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK), EscapeSequences.BLACK_ROOK);
        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT), EscapeSequences.BLACK_KNIGHT);
        PIECE_STRING_MAP.put(new Pair(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), EscapeSequences.BLACK_PAWN);
    }

    private final ChessGame.TeamColor perspective;
    private ChessBoard board;
    private SquareColor nextSquareColor = SquareColor.LIGHT;
    private enum SquareColor {
        DARK,
        LIGHT
    };

    private ChessPosition selectedPosition = null;
    private Collection<ChessPosition> validSpaces = new HashSet<>();

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

        selectedPosition = null;
        validSpaces = new HashSet<>();

        return builder.toString();
    }

    public String highlight(ChessPosition position,Collection<ChessPosition> validSpaces) {
        selectedPosition = position;
        this.validSpaces = validSpaces;
        return render();
    }

    private String drawHeader() {
        StringBuilder builder = new StringBuilder();
        builder.append(EscapeSequences.SET_BG_COLOR_DULL_BLUE).append(EMPTY).append(EscapeSequences.SET_TEXT_COLOR_WHITE);
        builder.append(EscapeSequences.SET_TEXT_FAINT);
        for(int i = 0; i < HEADER.length; i++){
            int index = perspective == ChessGame.TeamColor.BLACK ? 7-i : i;
            builder.append(HEADER[index]);
        }
        builder.append(EMPTY).append(EscapeSequences.RESET_BG_COLOR).append("\n");
        return builder.toString();
    }

    private String drawSide(int row) {
        String builder = EscapeSequences.SET_BG_COLOR_DULL_BLUE + EscapeSequences.SET_TEXT_COLOR_WHITE +
                EscapeSequences.SET_TEXT_FAINT +
                SIDE[row - 1];
        return builder;
    }

    private String drawRow(int row) {
        StringBuilder builder = new StringBuilder();
        builder.append(drawSide(row));
        for(int j = 0; j < 8; j++){
            int col = perspective == ChessGame.TeamColor.BLACK ? 8-j : j+1;
            ChessPosition position = new ChessPosition(row,col);
            ChessPiece piece = board.getPiece(position);
            builder.append(drawSquare(piece,getNextSquareColor(position),position));
        }
        getNextSquareColor();
        builder.append(drawSide(row));
        builder.append(EscapeSequences.RESET_BG_COLOR).append(EscapeSequences.RESET_TEXT_COLOR);
        builder.append("\n");

        return builder.toString();
    }

    private String drawSquare(ChessPiece piece,String bgColor,ChessPosition position) {
        String pieceString = pieceTypeToString(piece,position);
        return bgColor + pieceString + EscapeSequences.RESET_BG_COLOR;
    }

    private String pieceTypeToString(ChessPiece piece,ChessPosition position) {
        if(piece == null) {
            return EMPTY + EscapeSequences.RESET_TEXT_COLOR;
        }

        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        String renderColor = switch (pieceColor) {
            case WHITE -> WHITE;
            case BLACK -> BLACK;
        };
        if(position.equals(selectedPosition) || validSpaces.contains(position)){
            renderColor = EscapeSequences.SET_TEXT_COLOR_RED;
        }

        ChessPiece.PieceType type = piece.getPieceType();
        return renderColor + PIECE_STRING_MAP.get(new Pair(pieceColor, type)) + EscapeSequences.RESET_TEXT_COLOR;
    }

    private String getNextSquareColor() {
        String newColor = switch (nextSquareColor) {
            case DARK -> DARK_SQUARE_COLOR;
            case  LIGHT -> LIGHT_SQUARE_COLOR;
        };

        nextSquareColor = switch(nextSquareColor) {
            case DARK -> SquareColor.LIGHT;
            case  LIGHT -> SquareColor.DARK;
        };

        return newColor;
    }

    private String getNextSquareColor(ChessPosition position) {
        String color = switch (nextSquareColor) {
            case DARK -> DARK_SQUARE_COLOR;
            case  LIGHT -> LIGHT_SQUARE_COLOR;
        };

        if(validSpaces.contains(position)) {
            color = switch (nextSquareColor) {
                case DARK -> EscapeSequences.SET_BG_COLOR_DULL_DARK_GREEN;
                case LIGHT -> EscapeSequences.SET_BG_COLOR_DULL_GREEN;
            };
        }

        nextSquareColor = switch(nextSquareColor) {
            case DARK -> SquareColor.LIGHT;
            case  LIGHT -> SquareColor.DARK;
        };

        if(position.equals(selectedPosition)) {
            color = EscapeSequences.SET_BG_COLOR_DULL_YELLOW;
        }
        return color;
    }
}
