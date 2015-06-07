/** Swing version to paint the chess board and knight's tour on it.
 */
package hw2j;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class ChessBoardPanel extends JPanel {
  Point tmpPoint_ = null;
  Point startPoint_ = null;
  BoardFrame controller_;
  Dimension preferredSize_ = new Dimension(500,500);
  int rectWidth_ = 450;
  int rectHeight_ = 450;
  int cellWidth_ = rectWidth_/9;
  int cellHeight_ = rectHeight_/9;

  public ChessBoardPanel(BoardFrame controller_) {
    this.controller_ = controller_;

    Border raisedBevel = BorderFactory.createRaisedBevelBorder();
    Border loweredBevel = BorderFactory.createLoweredBevelBorder();
    Border compound = BorderFactory.createCompoundBorder
                (raisedBevel, loweredBevel);
    setBorder(compound);

    int x = 50, y = 50;
    startPoint_ = new Point(x, y);
    repaint();

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
        // Here's the knight's tour
        KnightsTour kt = new KnightsTour();
	      n = new Node(currRow, currCol);
        kt.startTour(n, controller_);
        repaint();
      }
    });
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
          controller_.updateLabel(clickPoint);
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

  public void paintComponent(Graphics g) {
    super.paintComponent(g);  //paint background

    paintChessBoard();
  } // end paintComponent()

  public void paintChessBoard() {
    //Paint a filled rectangle at user's chosen point.
    if (startPoint_ == null) {
      return;
    }

    Point currCell = startPoint_; // initialize to starting point.

    for (int ht = startPoint_.x; ht < rectWidth_; ht+= cellWidth_) {
      for (int wid = startPoint_.y; wid < rectHeight_; wid+= cellHeight_) {
        g.drawRect(wid, ht, cellWidth_, cellHeight_);
        if (g.getColor().equals(Color.white)) {
          g.setColor(Color.black);
        } else {
          g.setColor(Color.white);
        }

        fillCellWithData(g, wid, ht, cellWidth_, cellHeight_);
      }
      if (g.getColor().equals(Color.white)) {
        g.setColor(Color.black);
      } else {
        g.setColor(Color.white);
      }
    }
  } // end paintChessBoard()

  public void fillCellWithData(Graphics g, int wid, int ht,
                               int cellWidth, int cellHt)
  {
    // over-ride in ChessBoard and populate with knight's tour data there.
    g.fillRect(wid, ht, cellWidth, cellHeight);
  }
} // end class ChessBoardPanel
