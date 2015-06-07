typedef struct Node {
	int row_;
	int col_;
} Node;

char *convertToString(Node *n) {
	char *str[250];
	sprintf(str, "Node: (row, col) = (%d, %d)", n->row_, n->col_);
	return str;
}
