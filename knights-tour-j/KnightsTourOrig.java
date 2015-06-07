package hw2j;

import java.io.*;
import java.util.Vector;

public class KnightsTour {
	// TODO - make these command line args
	private static final int startRow_ = 0;
	private static final int startCol_ = 0;
	private static final int DIM = 8;
	private static final int START_LEVEL_RECURSE = 1;
	private static final int MAX_LEVEL_RECURSE = 4;

  private static final boolean DEBUG = false; //true;
  protected static final int DEBUG_LEVEL = 1;
	private static int TOTAL_MOVES;

	public static void main(String args[]) throws IOException {
	  Node n;
	  KnightsTour kt;

	  String boardPrefix = "board_";
    String boardName, boardMid;

    /** Test all start positions for the Knight's Tour.
     */
    for (int currRow = 0; currRow < DIM; currRow++) {
      for (int currCol = 0; currCol < DIM; currCol++) {
        boardMid = (currRow + "_" + currCol);
        boardName = (boardPrefix + boardMid);
        kt = new KnightsTour();
	      n = new Node(currRow, currCol);
        kt.startTour(n, boardName);
      }
    }
	}

	/** Given current position, compute the next move */
	private void startTour(Node startNode, String boardName) throws IOException {
	  ChessBoard chbd = new ChessBoard(DIM, DIM);
    chbd.setName(boardName);

    TOTAL_MOVES = chbd.getTotalMoves();

    chbd.doMove(startNode); // mark starting node as TAKEN

    Node nextNode;
    Node currNode = startNode;

    long a, b, c, d;

    a = System.currentTimeMillis();

	  while (chbd.getNumMovesDone() < TOTAL_MOVES) {
      c = System.currentTimeMillis();
      nextNode = chooseNextNode(chbd, currNode);
      chbd.doMove(nextNode);
      d = System.currentTimeMillis();

      //if (DEBUG) {
      if (startNode.getRow() == 4 && startNode.getCol() == 3) {
        chbd.appendBoardToFile(d-c, startNode);
      }
      currNode = nextNode;
    } // end while()

    b = System.currentTimeMillis();
    chbd.writeBoardToFile(b-a, startNode);

    return;
  } // end startTour()

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

} // end class KnightsTour
