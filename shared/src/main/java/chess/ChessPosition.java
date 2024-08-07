package chess;

import java.util.HashMap;
import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row;
    private int column;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.column = col;
    }

    public boolean isOnBoard(ChessBoard board)
    {
        int row = getRow();
        int col = getColumn();

        boolean rowsInRange = row >= 1 && row <= board.getSize();
        boolean colsInRange = col >= 1 && col <= board.getSize();

        return rowsInRange && colsInRange;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.column;
    }

    @Override
    public String toString() {
        HashMap<Integer,String> indexToAlphabet = new HashMap<>();
        indexToAlphabet.put(1,"a");
        indexToAlphabet.put(2,"b");
        indexToAlphabet.put(3,"c");
        indexToAlphabet.put(4,"d");
        indexToAlphabet.put(5,"e");
        indexToAlphabet.put(6,"f");
        indexToAlphabet.put(7,"g");
        indexToAlphabet.put(8,"h");

        return String.format("(%s,%d)", indexToAlphabet.get(getColumn()), getRow());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessPosition that = (ChessPosition) o;
        return getRow() == that.getRow() && getColumn() == that.getColumn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn());
    }
}
