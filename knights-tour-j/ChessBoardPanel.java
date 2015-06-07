/** Swing version to paint the chess board and knight's tour on it.
 */
package hw2j;

import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class ChessBoardPanel extends JPanel {
  Point startPoint_ = null;
  Point tmpPoint_ = null;
  KnightsTourFrame controller_;
  protected ChessBoard chbd_;
	private static final int DIM = 8;
	private static int TOTAL_MOVES;
	protected Graphics thisGraphics_;

  Dimension preferredSize_ = new Dimension(500,500);
  int rectWidth_ = 450;
  int rectHeight_ = 450;
  int cellWidth_ = rectWidth_/9;
  int cellHeight_ = rectHeight_/9;
  boolean tourStarted_ = false;
  int[][] board_;

  public ChessBoardPanel(KnightsTourFrame controller_) {
    this.controller_ = controller_;

    Border raisedBevel = BorderFactory.createRaisedBevelBorder();
    Border loweredBevel = BorderFactory.createLoweredBevelBorder();
    Border compound = BorderFactory.createCompoundBorder
                (raisedBevel, loweredBevel);
    setBorder(compound);

    int x = 50, y = 50;
    startPoint_ = new Point(x, y);
    repaint();

    addMouseListener(new myMouseAdapter());

    /*
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (tmpPoint_ == null) {
          tmpPoint_ = new Point(x, y);
        } else {
          tmpPoint_.x = x;
          tmpPoint_.y = y;
        }
        //System.err.println("user clicked: (" + x + ", " + y + ")");
        Point currCell = findClickedCell(tmpPoint_);
        if (currCell == null) {
          return;
        }

        int cellx = currCell.x/cellWidth_ - 1;
        int celly = currCell.y/cellHeight_ - 1;

        tourStarted_ = false;
        repaint();
        //Node startNode = new Node(0, 0);
        Node startNode = new Node(celly, cellx);
        startTour(startNode);
      }
    }); */
  }

  public Dimension getPreferredSize() {
    return preferredSize_;
  }

  public Point findClickedCell(Point clickPoint) {
    Point currCell = startPoint_; // initialize to starting point.

    if (!isWithinCell(startPoint_, clickPoint, rectWidth_, rectHeight_)) {
      return null;
    }

    Point upperLeft;

    for (int ht = startPoint_.x; ht < rectWidth_; ht+= cellWidth_) {
      for (int wid = startPoint_.y; wid < rectHeight_; wid+= cellHeight_) {
        upperLeft = new Point(wid, ht);
        //System.err.println("findClickedCell: upperLeft is (" + wid + ", " + ht + ")");
        if (isWithinCell(upperLeft, clickPoint, cellWidth_, cellHeight_)) {
          return upperLeft;
        }
      } // end for ht
    } // end for wid

    return null; // shd never get here
  }

  public boolean isWithinCell(Point upperLeft, Point p, int cellWid, int cellHt) {
    if (p.x > upperLeft.x && p.x <= upperLeft.x + cellWid &&
        p.y > upperLeft.y && p.y <= upperLeft.y + cellHt)
    {
      //System.err.println("isWithinCell returns true");
      return true;
    }
    System.err.println("isWithinCell returns false");
    return false;
  }

	/** Given current position, compute the next move */
	protected void startTour(Node startNode) {
	  chbd_ = new ChessBoard(DIM, DIM);

    TOTAL_MOVES = chbd_.getTotalMoves();

    chbd_.doMove(startNode); // mark starting node as TAKEN

    Node nextNode;
    Node currNode = startNode;

    long a, b, c, d;

    a = System.currentTimeMillis();

	  while (chbd_.getNumMovesDone() < TOTAL_MOVES) {
      c = System.currentTimeMillis();
      nextNode = controller_.chooseNextNode(chbd_, currNode);
      chbd_.doMove(nextNode);
      tourStarted_ = true;
      paintComponent(thisGraphics_);
      //repaint();
      d = System.currentTimeMillis();

      currNode = nextNode;
    } // end while()

    b = System.currentTimeMillis();
    return;
  } // end startTour()

  public void paintComponent(Graphics g) {
    super.paintComponent(g);  //paint background

    paintChessBoard(g);
    thisGraphics_ = g;
  } // end paintComponent()

  public void paintChessBoard(Graphics g) {
    //Paint a filled rectangle at user's chosen point.
    if (startPoint_ == null) {
      return;
    }

    Point currCell = startPoint_; // initialize to starting point.

    if (tourStarted_) {
      board_ = chbd_.getBoard();
    }

    Color saveColor;

    for (int ht = startPoint_.x; ht < rectWidth_; ht+= cellWidth_) {
      for (int wid = startPoint_.y; wid < rectHeight_; wid+= cellHeight_) {
        g.drawRect(wid, ht, cellWidth_, cellHeight_);
        if (g.getColor().equals(Color.white)) {
          g.setColor(Color.black);
        } else {
          g.setColor(Color.white);
        }

        g.fillRect(wid, ht, cellWidth_, cellHeight_);
 
        saveColor = g.getColor();
        if (tourStarted_) {
          fillCellWithData(g, wid, ht, cellWidth_, cellHeight_);
        }
        g.setColor(saveColor);
      }
      if (g.getColor().equals(Color.white)) {
        g.setColor(Color.black);
      } else {
        g.setColor(Color.white);
      }
    }
  } // end paintChessBoard()

  public void fillCellWithData(Graphics g, int wid, int ht, int cellWidth, int cellHt) {
    Point upperLeft = new Point(wid, ht);

    int fillWidth = cellWidth - 16;
    int fillHeight = cellHt - 16;

    int cellx = ht/cellHt - 1;
    int celly = wid/cellWidth - 1;

    int val = board_[cellx][celly];
    String str = (val == ChessBoard.FREE? " " : "" + val);

    //System.err.println("Cell position: cell-x: " + cellx + ", cell-y: " + celly);
    System.err.println("board_ (" + cellx + ", " + celly + ") = " + str);
    g.setColor(Color.red);
    g.fillRect(wid + 8, ht + 8, fillWidth, fillHeight);
    g.setColor(Color.black);
    Font f = g.getFont();
    g.setFont(f.deriveFont(Font.BOLD, (float)12));
    g.drawString(str, wid + 20, ht + 20);
    return;
  } // end fillCellWithData()

  class myMouseAdapter extends MouseAdapter {
    Point tmpPoint_ = null;
    public void mousePressed(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      if (tmpPoint_ == null) {
        tmpPoint_ = new Point(x, y);
      } else {
        tmpPoint_.x = x;
        tmpPoint_.y = y;
      }
      //System.err.println("user clicked: (" + x + ", " + y + ")");
      Point currCell = findClickedCell(tmpPoint_);
      if (currCell == null) {
        return;
      }
      // Here's the knight's tour
      int cellx = currCell.x/cellWidth_ - 1;
      int celly = currCell.y/cellHeight_ - 1;

      tourStarted_ = false;
      repaint();
      //Node startNode = new Node(0, 0);
      Node startNode = new Node(celly, cellx);
      controller_.updateLabel((Point)startNode);
      startTour(startNode);
    }

  } // end inner class

} // end class ChessBoardPanel

