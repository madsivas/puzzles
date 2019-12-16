from random import shuffle
from optparse import OptionParser

# per level of difficulty, define start and end range of number of clues out of 81 numbers in the grid
# yes, there are overlaps in the ranges, this is by design
level_very_easy = (75, 80)
level_easy = (55, 78)
level_medium = (45, 58)
level_hard = (35, 48)
level_expert = (23, 38)


def get_sudoku_template():
    block11 = [
        ["A", "B", "C"],
        ["D", "E", "F"],
        ["G", "H", "I"],
    ]

    block12 = [
        ["D", "E", "F"],
        ["G", "H", "I"],
        ["A", "B", "C"],
    ]

    block13 = [
        ["G", "H", "I"],
        ["A", "B", "C"],
        ["D", "E", "F"],
    ]

    block21 = [
        ["B", "C", "A"],
        ["E", "F", "D"],
        ["H", "I", "G"],
    ]

    block22 = [
        ["E", "F", "D"],
        ["H", "I", "G"],
        ["B", "C", "A"],
    ]

    block23 = [
        ["H", "I", "G"],
        ["B", "C", "A"],
        ["E", "F", "D"],
    ]

    block31 = [
        ["C", "A", "B"],
        ["F", "D", "E"],
        ["I", "G", "H"],
    ]

    block32 = [
        ["F", "D", "E"],
        ["I", "G", "H"],
        ["C", "A", "B"],
    ]

    block33 = [
        ["I", "G", "H"],
        ["C", "A", "B"],
        ["F", "D", "E"],
    ]

    return [
        block11, block12, block13,
        block21, block22, block23,
        block31, block32, block33
    ]


def replace_grid_symbols(sgrid, rtuples):
    replaced_grid = list()
    for block in sgrid:
        for item in block:
            for (n, i) in enumerate(item):
                for (k, v) in rtuples:
                    if (i == k):
                        item[n] = v
        replaced_grid.append(block)
    return replaced_grid


def pretty_print(g):
    block_count = 0
    for block in g:
        if block_count % 3 == 0:  # next line
            print("\n")
        block_count += 1
        print(block)


def pretty_print_v1(g):
    block_count = 0
    for block in g:
        for item in block:
            buf = "{}\n".format(item)
            print(buf)

        if block_count % 3 == 0:  # next line
            print("\n")
        block_count += 1
        #print(block)


def pretty_print_ambitious(g):
    block_count = 0
    r1 = r2 = r3 = list()

    for block in g:
        if block_count % 3 == 1:  # next line
            print(r1)
            print(r2)
            print(r3)
            block_count = 0
            r1 = r2 = r3 = list()

        for item in block:
            if block_count == 0:
                r1.append(item)
            elif block_count == 1:
                r2.append(item)
            else:
                r3.append(item)

        block_count += 1


def sudoku_gen(level):
    sudoku_grid = get_sudoku_template()

    pretty_print(sudoku_grid)
    symbols = ["A", "B", "C", "D", "E", "F", "G", "H", "I"]

    seed_nums = [i for i in range(1, 10)]  # list numbers between 1 and 9 both inclusive
    print("seed_nums: {}".format(seed_nums))
    shuffle(seed_nums)  # randomly shuffle the order
    print("shuffled seed_nums: {}".format(seed_nums))
    replacement_tuples = list(zip(symbols, seed_nums))
    new_sudoku = replace_grid_symbols(sudoku_grid, replacement_tuples)
    pretty_print(new_sudoku)


if __name__ == '__main__':
    parser = OptionParser()
    (options, args) = parser.parse_args()
    #print("Got input args: ", args)
    #print("Got input level args[0]: ", args[0])

    lvl = int(args[0])
    sudoku_gen(level=lvl)
