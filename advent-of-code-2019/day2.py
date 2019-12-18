import sys
from timeit import default_timer as timer


op_code_add = 1
op_code_multiply = 2
op_code_exit = 99


def initialize_computer_memory(filename):
    intcode_list = list()
    with open(filename) as f:
        lines = f.readlines()
        for line in lines:
            intcode_list = line.split(",")

    intcode_list = [int(x) for x in intcode_list]
    return intcode_list


def do_add(op1, op2):
    return op1 + op2


def do_multiply(op1, op2):
    return op1 * op2


def execute(instruction, int_code_seq):
    # modifies the contents of int_code_seq
    operation = instruction[0]
    operand_1_index = instruction[1] if len(instruction) > 1 else None
    operand_2_index = instruction[2] if len(instruction) > 2 else None
    output_index = instruction[3] if len(instruction) > 3 else None

    # print(operation, operand_1_index, operand_2_index, output_index)

    idx = output_index
    if operation == op_code_add:
        int_code_seq[output_index] = do_add(int_code_seq[operand_1_index], int_code_seq[operand_2_index])
    elif operation == op_code_multiply:
        int_code_seq[output_index] = do_multiply(int_code_seq[operand_1_index], int_code_seq[operand_2_index])
    elif operation == op_code_exit:
        return False
    else:
        print("Invalid or unknown operation %s! Terminating and going up in flames!" % operation)
        sys.exit(2)

    # print("Output index: %s, output value: %s" % (idx, int_code_seq[output_index]))
    return True


def int_code_computer(int_code_seq):
    result = None
    #  instructions = [int_code_seq[x:x+4] for x in range(0, len(int_code_seq), 4)]
    num_int_codes = len(int_code_seq)
    num_instructions = int(num_int_codes / 4)
    # print("# instructions to execute: %s" % num_instructions)
    idx = 0
    while idx < num_int_codes:
        instr = int_code_seq[idx:idx+4]
        if idx < num_int_codes - 4:
            idx += 4
        else:
            idx += num_int_codes - idx  # the last few int codes - may not be a multiple of 4

        indicator = execute(instr, int_code_seq)  # both args to execute function change each iteration
        if indicator is False:
            break
    return result


def noun_verb_pairs(n):
    """Produce pairs of indexes in range(n)"""
    for i in range(n-1, -1, -1):
        for j in range(0, n):
            yield i, j


if __name__ == "__main__":

    '''
     See https://adventofcode.com/2019/day/2 for specs of this day's puzzle
    '''

    #  expect a file in the current dir
    param_file = "advent-of-code-d2-input.txt"
    original_state = initialize_computer_memory(param_file)

    # print("Int codes length: %s" % len(int_codes))

    # print("Int codes before computing: %s" % int_codes)
    start = timer()

    # puzzle day2 part 2: keep trying pairs of noun and verb until the computer returns a result of 19690720
    magic_value = 19690720

    i = 0
    magic_noun = None
    magic_verb = None

    # for noun in range(99, -1, -1):  # range count down from 99 to 0 (inclusive), step -1
        # for verb in range(0, 100):
    for noun, verb in noun_verb_pairs(100):
        start_iter = timer()
        i += 1
        int_codes = original_state.copy()
        int_codes[1] = noun  # pre_condition_value_reset_at_idx_1 = 12
        int_codes[2] = verb  # pre_condition_value_reset_at_idx_2 = 2
        int_code_computer(int_codes)
        if int_codes[0] == magic_value:
            magic_noun = noun
            magic_verb = verb
            break
        iter_time = round(timer() - start_iter, 1)
        print("%s secs for iteration %s: noun = %s, verb = %s" % (iter_time, i, noun, verb))

    # print("Int codes after computing: %s" % int_codes)
    elapsed = round(timer() - start, 1)

    # print("Day 2 result, int code at position 0 = %s" % int_codes[0])
    print("Day 2 part 2: magic noun = %s, magic verb = %s" % (magic_noun, magic_verb))
    print("Day 2 result: 100 * noun + verb = %s" % (100 * magic_noun + magic_verb))
    print("Total elapsed time (secs): %s" % elapsed)  # Time in seconds, e.g. 5.38091952400282
