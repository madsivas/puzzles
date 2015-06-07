#include <stdio.h>
#include "node.h"
#include "knights_tour.h"

static char nodeStr[250];

int main(int argc, char *argv)
{
	printf("Homework 2, main in knights_tour.c\n");

	write_to_file();
	return 0;
} /* end main() */

int write_to_file()
{
	FILE *fptr;
	const char *fname = "hw2_test.html";
	if ((fptr = fopen(fname, "w")) == NULL) {
		printf("Could not open file for writing\n");
		return -1;
	}
	test_print(fptr);
	fclose(fptr);
	return 0;
}

char *nodeToString(Node *n)
{
	sprintf(nodeStr, "Node: (row, col) = (%d, %d)", n->row_, n->col_);
	return nodeStr;
}

void test_print(FILE *fptr)
{
	fprintf(fptr, "<html>\r\n<H4>%d times Hello World!</H4>\r\n", 100);
	fprintf(fptr, "<H5>Another line from test_print</H5></html>\r\n");
	return;
}

