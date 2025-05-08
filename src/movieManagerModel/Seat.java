package movieManagerModel;

public class Seat {
    private String id;
    private int _row;
    private int _col;

    public Seat() {
    }

    public Seat(String id, int _row, int _col) {
        this.id = id;
        this._row = _row;
        this._col = _col;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRow() {
        return _row;
    }

    public void setRow(int _row) {
        this._row = _row;
    }

    public int getCol() {
        return _col;
    }

    public void setCol(int _col) {
        this._col = _col;
    }
}
