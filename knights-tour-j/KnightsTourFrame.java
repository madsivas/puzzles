/** Swing version to paint the chess board and knight's tour on it.
 */
package hw2j;

import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/** Displays a framed area.  When the user clicks within
 *  the area, this program displays the knight's tour with that
 *  starting point. */

public class KnightsTourFrame extends JApplet {
  JLabel label;

	private static final int startRow_ = 0;
	private static final int startCol_ = 0;
	private static final int START_LEVEL_RECURSE = 1;
	private static final int MAX_LEVEL_RECURSE = 4;

  private static final boolean DEBUG = false; //true;
  protected static final int DEBUG_LEVEL = 1;


  //Called only when this is run as an applet.
  public void init() {
    buildUI(getContentPane());
  }

  void buildUI(Container container) {
    container.setLayout(new BoxLayout(container,
                      BoxLayout.Y_AXIS));

    ChessBoardPanel chessBoard = new ChessBoardPanel(this);
    container.add(chessBoard);

    label = new JLabel("Click on a square to start tour.");
    container.add(label);

    //Align the left edges of the components.
    chessBoard.setAlignmentX(LEFT_ALIGNMENT);
    label.setAlignmentX(LEFT_ALIGNMENT); //unnecessary, but doesn't hurt
  }

  public void updateLabel(Point point) {
    label.setText("Click occurred at coordinate ("
            + point.x + ", " + point.y + ").");
  }

  //Called only when this is run as an application.
  public static void main(String[] args) {
    JFrame f = new JFrame("Knight's Tour");
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    KnightsTourFrame controller_ = new KnightsTourFrame();
    controller_.buildUI(f.getContentPane());

    f.pack();
    f.setVisible(true);
  }

	public Node chooseNextNode(ChessBoard chbd, Node currNode)
  {
    if (DEBUG_LEVEL >= 1) {
      System.err.println("ComputeNextNode: currNode: " + currNode.toString());
    }

    ChessBoard localBd = new ChessBoard(chbd);
    localBd.setName("ChooseNextNode_LOCAL_BOARD");

    Vector nextNodes = localBd.getMoves(currNode);

    int size = nextNodes.size();

    if (size == 1) {
      return (Node)nextNodes.elementAt(0);
    }

	  int minMoves = 99; // init to a high value

	  Node thisNode;
	  int numMoves[] = new int[size];
    Vector minNodesV = new Vector();

	  for (int i = 0; i < size; i++) {
      thisNode = (Node)nextNodes.elementAt(i);
      numMoves[i] = localBd.getNumMoves(thisNode);

      if (numMoves[i] < minMoves) {
        // found a new min # moves
        minMoves = numMoves[i];
        // clear old list and track which children have new minMoves
        minNodesV = new Vector();
        minNodesV.addElement(thisNode);
      } else if (numMoves[i] == minMoves) {
        minNodesV.addElement(thisNode);
      }
	  } // end for i..

    if (minNodesV == null || minNodesV.size() == 0) {
      throw new RuntimeException("How did this happen???");
    }

    int minSz = minNodesV.size();

    if (minSz == 1) { // no tie, one node clearly wins
      thisNode = (Node)minNodesV.elementAt(0);
      return thisNode;
    }

    int cost[] = new int[minSz];
    for (int i = 0; i < minSz; i++) {
      thisNode = (Node)minNodesV.elementAt(i);
      cost[i] = computeCost(localBd, thisNode, START_LEVEL_RECURSE);
    }

    int chosenIdx = getMinCostIndex(cost);
    Node retNode = (Node)minNodesV.elementAt(chosenIdx);
    if (DEBUG_LEVEL >= 1) {
      System.err.println("ChooseNextNode: Returning node: " + retNode.toString());
    }

    return retNode;

  } // end method chooseNextNode()

  public int computeCost(ChessBoard cBoard, Node currNode, int level) {
    ChessBoard lb = new ChessBoard(cBoard); // make a local copy
    lb.setName("ComputeCost_LOCAL_BOARD_" + level);
    Node tempNode;

    int cost = lb.getNumMoves(currNode);
    if (level == MAX_LEVEL_RECURSE) {
      if (DEBUG_LEVEL >= 1) {
        System.err.println("ComputeCost: Pre-maturely reached level: " + level);
      }
      return cost;
    }

    lb.doMove(currNode);

    Vector nextNodes = lb.getMoves(currNode);
    int sz = nextNodes.size();
    for (int i = 0; i < sz; i++) {
      tempNode = (Node)nextNodes.elementAt(i);
      cost = cost + computeCost(lb, tempNode, level+1);
    }
    
    if (DEBUG_LEVEL >= 1) {
      System.err.println("ComputeCost: Normally reached level: " + level);
    }
    return cost;
    
  } // end method computeCost()

  private int getMinCostIndex(int[] cost) {
    int minCostIdx = 0;
    int minCost = cost[0];

    if (DEBUG_LEVEL >= 2) {
      System.err.println(">>>getMinCostIndex: input cost array: ");
    }

    for (int i = 1; i < cost.length; i++) {
      if (DEBUG_LEVEL >= 2) {
        System.err.println("cost[" + i + "]: " + cost[i]);
      }
      if (cost[i] > 0 && cost[i] < minCost) {
        minCost = cost[i];
        minCostIdx = i;
      }
    }
    
    if (DEBUG_LEVEL >= 2) {
      System.err.println("<<<getMinCostIndex: returning minCostIdx: " + minCostIdx);
    }
    return minCostIdx;
  } // end method getMinCostIndex()

} // end class KnightsTourFrame
