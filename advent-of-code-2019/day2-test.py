import unittest
from day2 import int_code_computer


class Day2Test(unittest.TestCase):
    def test_computer_1(self):
        int_code_input = [1, 0, 0, 0, 99]
        changed_int_code = [2, 0, 0, 0, 99]
        int_code_computer(int_code_input)
        self.assertEqual(int_code_input, changed_int_code)

    def test_computer_2(self):
        int_code_input = [2, 3, 0, 3, 99]
        changed_int_code = [2, 3, 0, 6, 99]
        int_code_computer(int_code_input)
        self.assertEqual(int_code_input, changed_int_code)

    def test_computer_3(self):
        int_code_input = [2, 4, 4, 5, 99, 0]
        changed_int_code = [2, 4, 4, 5, 99, 9801]
        int_code_computer(int_code_input)
        self.assertEqual(int_code_input, changed_int_code)

    def test_computer_4(self):
        int_code_input = [1, 1, 1, 4, 99, 5, 6, 0, 99]
        changed_int_code = [30, 1, 1, 4, 2, 5, 6, 0, 99]
        int_code_computer(int_code_input)
        self.assertEqual(int_code_input, changed_int_code)
