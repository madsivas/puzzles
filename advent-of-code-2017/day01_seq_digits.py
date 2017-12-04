# coding=UTF-8
import sys

if __name__ == "__main__":
    input_num_str = sys.argv[1]
    print "Hello from the other side! You entered: ", input_num_str
    relevant_nums = []
    prev_dig = input_num_str[len(input_num_str) - 1]
    for dig in input_num_str:
        # print "Previous digit: ", prev_dig, " Digit: ", dig
        if dig == prev_dig:
            relevant_nums.append(dig)
        prev_dig = dig

    print "Digits matching previous in sequence: ", relevant_nums

    sum_relevant_digits = sum(int(digit) for digit in relevant_nums)

    print "Sum of digits matching previous in sequence: ", sum_relevant_digits
