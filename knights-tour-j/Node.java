package hw2j;

import java.awt.Point;

public class Node extends Point {
  private int row_;
  private int col_;
  private int ccount_;

  public Node(int row, int col) {
    super(row, col);
    row_ = row;
    col_ = col;
    ccount_ = 0;
  }

  public int getRow() {
    return row_;
  }

  public int getCol() {
    return col_;
  }

  public String toString() {
    return ("Node: (row, col) = (" + row_ + ", " + col_ + ")");
  }
}
