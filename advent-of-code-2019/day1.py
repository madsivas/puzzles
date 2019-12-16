import math


def calculate_fuel_required(mass):
    print("got module mass: %s" % mass)
    fuel_required = math.floor(mass/3) - 2
    return fuel_required


def read_module_masses(filename):
    with open(filename) as f:
        mm_list = f.readlines()
    # you may also want to remove whitespace characters like `\n` at the end of each line
    mm_list = [x.strip() for x in mm_list]
    return mm_list


if __name__ == "__main__":

    #  expect a file in the current dir
    param_file = "advent-of-code-d1-puzzle1-input.txt"
    module_masses = read_module_masses(param_file)

    fuel_total = 0
    for m in module_masses:
        fr = calculate_fuel_required(mass=float(m))
        print("Fuel required for module of mass %s is %s" % (m, fr))
        fuel_total += fr

    print("Total fuel required: %s" % fuel_total)
