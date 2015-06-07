
typedef struct ChessBoard {
	int rowMax_;
	int colMax_;
	int totalMoves_;
	int numMovesDone_;
	int **board_;
	char *name_;
} ChessBoard;

#define DEFAULT_DIM (8)
#define FREE (0)
#define SUFFIX ".html"

#define BG_COLOR "white"
#define FONT_COLOR "black"

FILE file_;

ChessBoard copyChessBoard(ChessBoard cb);
ChessBoard newChessBoard(int row, int col);
//void cbInit(ChessBoard *cb);
void cbSetName(ChessBoard *cb, const char *name);
void cbSetName(ChessBoard *cb, const char *name);
int **cbGetBoard(ChessBoard *cb);
int cbGetRowMax(ChessBoard *cb);
int cbGetColMax(ChessBoard *cb);
