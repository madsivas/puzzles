#include <stdio.h>
#include "chess_board.h"

ChessBoard defaultChessBoard()
{
	return newChessBoard(DEFAULT_DIM, DEFAULT_DIM);
}

int main(int argc, char *argv)
{
	printf("Homework 2, main in chess_board.c\n");
	return 0;
} /* end main() */

ChessBoard copyChessBoard(ChessBoard cb)
{
	ChessBoard newcb = cb;
	return newcb;
	/*
	rowMax_ = cbGetRowMax(cb);
	colMax_ = cbGetColMax(cb);
	totalMoves_ = rowMax_ * colMax_;

	copyBoard(cb);
	*/

	// DO NOT INIT.. this could be a partially constructed board obj
}

ChessBoard newChessBoard(int row, int col)
{
	ChessBoard cb;
	int i, j;

	cb.rowMax_ = row;
	cb.colMax_ = col;
	cb.totalMoves_ = cb.rowMax_ * cb.colMax_;

	// initialize the chess board
	for (i = 0; i < cb.rowMax_; i++) {
		for (j = 0; j < cb.colMax_; j++) {
			cb.board_[i][j] = FREE;
		}
	}
	return cb;
}

/*
	private void copyBoard(ChessBoard cb) {
	  int row = cb.getRowMax();
	  int col = cb.getColMax();
	  int[][] board = cb.getBoard();

		board_ = new int[rowMax_][colMax_];

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				board_[i][j] = board[i][j];
			}
		}
	}
*/

void cbSetName(ChessBoard *cb, const char *name)
{
  sprintf(cb->name_, "%s", name);
}

int **cbGetBoard(ChessBoard *cb)
{
	return cb->board_;
}

int cbGetRowMax(ChessBoard *cb)
{
	return cb->rowMax_;
}

int cbGetColMax(ChessBoard *cb)
{
	return cb->colMax_;
}

	/** get a list of valid moves from the given position (row, col).
	 *  There are 8 possible moves as follows:
	 *   (row-2, col-1),
	 *   (row-2, col+1),
	 *   (row-1, col-2),
	 *   (row-1, col+2),
	 *   (row+2, col-1),
	 *   (row+2, col+1),
	 *   (row+1, col-2),
	 *   (row+1, col+2)
	 *  This method checks if each of the 8 moves are within the chess board
	 *  and returns a list of nodes (a sub-set of the 8 possible moves).
	 */
Vector getMoves(Node node)
{
	int row, col;
	row = node.row_;
	col = node.col_;

	Vector v = new Vector();

	if (isAvailable(row-2, col-1)) {
		v.addElement(new Node(row-2, col-1));
	}
	if (isAvailable(row-2, col+1)) {
		v.addElement(new Node(row-2, col+1));
	}
	if (isAvailable(row-1, col-2)) {
		v.addElement(new Node(row-1, col-2));
	}
	if (isAvailable(row-1, col+2)) {
		v.addElement(new Node(row-1, col+2));
	}

	if (isAvailable(row+2, col-1)) {
		v.addElement(new Node(row+2, col-1));
	}
	if (isAvailable(row+2, col+1)) {
		v.addElement(new Node(row+2, col+1));
	}
	if (isAvailable(row+1, col-2)) {
		v.addElement(new Node(row+1, col-2));
	}
	if (isAvailable(row+1, col+2)) {
		v.addElement(new Node(row+1, col+2));
	}

	if (DEBUG_LEVEL >= 2) {
		printf(1, "getMoves(): Returning vector (of nodes) size = %d", 100);
	}
	return v;
} // end getMoves

	/** This method is similar to getMoves(), but instead of building an array
	 *  of nodes, it simply counts the number of valid moves from the given
	 *  (row, col) posn. and returns an int.
	 */
	 /*
	public int getNumMoves(Node node) {
    int row = node.getRow();
    int col = node.getCol();
	  int count = 0;

	  if (isAvailable(row-2, col-1)) {
	    count++;
    }
	  if (isAvailable(row-2, col+1)) {
	    count++;
    }
	  if (isAvailable(row-1, col-2)) {
	    count++;
    }
	  if (isAvailable(row-1, col+2)) {
	    count++;
    }

	  if (isAvailable(row+2, col-1)) {
	    count++;
    }
	  if (isAvailable(row+2, col+1)) {
	    count++;
    }
	  if (isAvailable(row+1, col-2)) {
	    count++;
    }
	  if (isAvailable(row+1, col+2)) {
	    count++;
    }
    if (KnightsTour.DEBUG_LEVEL >= 2) {
      System.err.println("Num moves possible from row: " + row + ", Col: " + col +
                         " is: " + count);
    }
    return count;
	} // end getNumMoves

	public void doMove(Node n) {
	  doMove(n.getRow(), n.getCol());
	}

	private void doMove(int row, int col) {
	  numMovesDone_++;
	  board_[row][col] = numMovesDone_;
    if (KnightsTour.DEBUG_LEVEL >= 1) {
      System.err.println(name_ + ": Done # moves: " + numMovesDone_);
    }
	}

  private boolean isAvailable(int row, int col) {
    if (isWithinBoard(row, col) && board_[row][col] == FREE) {
      return true;
    }
    return false;
  }

  private boolean isWithinBoard(int row, int col) {
    if (row >= 0 && row < rowMax_ && col >= 0 && col < colMax_) {
      return true;
    }
    return false;
  }

  public int getTotalMoves() {
    return totalMoves_;
  }

  public int getNumMovesDone() {
    return numMovesDone_;
  }

  private StringBuffer getHeaderInfo(long millis, Node snode) {
    StringBuffer buf = new StringBuffer();

    java.util.Date dt = new java.util.Date();
    String dtTimeStr = convertDate(dt);

    buf.append("<html>\r\n")
       .append("<H4>" + name_ + "</H2>\r\n")
       .append("<B>Start position (row, col): (" + snode.getRow())
       .append(", " + snode.getCol() + ")</B><br>\r\n")
       .append("<B>Generated at: " + dtTimeStr + "</B><br>\r\n")
       .append("<B>Time taken to compute: " + millis + "ms</B><br>\r\n");

    return buf;
  }

  private static String convertDate(java.util.Date dt) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(dt);
    return ((calendar.get(calendar.MONTH)+1) + "/" +
            calendar.get(calendar.DATE) + "/" +
            calendar.get(calendar.YEAR) + " " +
            calendar.get(calendar.HOUR) + ":" +
            calendar.get(calendar.MINUTE) + ":" +
            calendar.get(calendar.SECOND) + " " +
            ((calendar.get(calendar.AM_PM) == calendar.PM) ? "PM" : "AM"));

  }

  private StringBuffer startOuterTable() throws IOException {
    StringBuffer buf = new StringBuffer();
    buf.append("<table cellspacing=4 cellpadding=5 border=0>\r\n");
    buf.append("<tr>\r\n");
    return buf;
  }

  private StringBuffer endOuterTable() throws IOException {
    StringBuffer buf = new StringBuffer();
    buf.append("</tr>\r\n");
    buf.append("</table>\r\n");
    return buf;
  }

  public void writeBoardToFile(long millis, Node snode) throws IOException {
    StringBuffer buf = new StringBuffer();

    buf.append(getHeaderInfo(millis, snode))
       .append(getBoardInHtml());

    file_ = new File(name_ + suffix_);
    fw_ = new FileWriter(file_);

    fw_.write(buf.toString());
    fw_.flush();
 
  } // end method writeBoardToFile()

  private StringBuffer getBoardInHtml() {
    StringBuffer buf = new StringBuffer();
    buf.append("<table cellspacing=0 cellpadding=5 border=1>\r\n");

    String bgcolor  = BG_COLOR;
    String rvideo = FONT_COLOR;

    for (int i = 0; i < rowMax_; i++) {
      buf.append("<tr>\r\n"); // start a new row
      for (int j = 0; j < colMax_; j++) {
        buf.append("<td bgcolor=\"" + bgcolor + "\"><font color=\"" + rvideo + "\">");
        if (board_[i][j] == FREE) {
          buf.append("&nbsp;");
        } else {
          buf.append("" + board_[i][j]);
        }
        buf.append("</font>" + "</td>\r\n");

        // toggle color
        if (bgcolor.equals(FONT_COLOR)) {
          bgcolor = BG_COLOR;
          rvideo = FONT_COLOR;
        } else {
          bgcolor = FONT_COLOR;
          rvideo = BG_COLOR;
        }
      } // end for j
      buf.append("</tr>\r\n");

      if (bgcolor.equals(FONT_COLOR)) {
        bgcolor = BG_COLOR;
        rvideo = FONT_COLOR;
      } else {
        bgcolor = FONT_COLOR;
        rvideo = BG_COLOR;
      }
    } // end for i
    buf.append("</table>\r\n");
    return buf;

  } // end getBoardInHtml()

  public void appendBoardToFile(long millis, Node snode) throws IOException {
    StringBuffer buf = new StringBuffer();

    //buf.append(getHeaderInfo(millis, snode));

    if (file_ == null) {
      file_ = new File(name_ + suffix_);
      fw_ = new FileWriter(file_);
      buf.append(startOuterTable());
    }


    buf.append("<td>\r\n");
    buf.append(getBoardInHtml());
    buf.append("</td>\r\n");

    if (numMovesDone_ >= (rowMax_ * colMax_)) {
      buf.append(endOuterTable());
    }

    fw_.write(buf.toString());
    fw_.flush();
 
  } // end method appendBoardToFile()

} // end class ChessBoard
*/
