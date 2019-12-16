import sys

op_code_add = 1
op_code_multiply = 2
op_code_exit = 99


def read_int_codes(filename):
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

    print(operation, operand_1_index, operand_2_index, output_index)

    idx = output_index
    if operation == op_code_add:
        int_code_seq[output_index] = do_add(int_code_seq[operand_1_index], int_code_seq[operand_2_index])
        print("Output index: %s, output value: %s" % (idx, int_code_seq[output_index]))
    elif operation == op_code_multiply:
        int_code_seq[output_index] = do_multiply(int_code_seq[operand_1_index], int_code_seq[operand_2_index])
        print("Output index: %s, output value: %s" % (idx, int_code_seq[output_index]))
    elif operation == op_code_exit:
        return False
    else:
        print("Invalid or unknown operation %s! Terminating and going up in flames!" % operation)
        sys.exit(2)

    return True


def int_code_computer(int_code_seq):
    result = None
    instructions = [int_code_seq[x:x+4] for x in range(0, len(int_code_seq), 4)]
    print("# instructions to execute: %s" % len(instructions))
    for instr in instructions:
        indicator = execute(instr, int_code_seq)  # both args to execute function change each iteration
        if indicator is False:
            break;
    return result


if __name__ == "__main__":

    '''
     See https://adventofcode.com/2019/day/2 for specs of this day's puzzle
    '''

    #  expect a file in the current dir
    param_file = "advent-of-code-d2-input.txt"
    int_codes = read_int_codes(param_file)

    print("Int codes before computing: %s" % int_codes)
    int_code_computer(int_codes)

    print("Int codes after computing: %s" % int_codes)

    print("Day 2 result, int code at position 0 = %s" % int_codes[0])
