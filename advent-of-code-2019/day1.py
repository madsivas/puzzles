import math


def calculate_fuel_required(mass):
    #  print("got module mass: %s" % mass)
    fuel_required = math.floor(mass/3) - 2
    return fuel_required


def read_module_masses(filename):
    with open(filename) as f:
        mm_list = f.readlines()
    # you may also want to remove whitespace characters like `\n` at the end of each line
    mm_list = [x.strip() for x in mm_list]
    return mm_list


#  part 2 of puzzle
def calculate_fuel_required_for_fuel(f_mass):
    #  print("got fuel mass: %s" % f_mass)
    cumulative_fuel_required = 0
    f_required = f_mass
    while f_required > 0:
        f_required = calculate_fuel_required(f_required)
        if f_required < 0:
            break
        cumulative_fuel_required += f_required
    return cumulative_fuel_required


if __name__ == "__main__":

    '''
     See https://adventofcode.com/2019/day/1 for specs of both parts of this day's puzzle
    '''

    #  expect a file in the current dir
    #  param_file = "advent-of-code-d1-puzzle1-input-1-line.txt"
    param_file = "advent-of-code-d1-puzzle1-input.txt"
    module_masses = read_module_masses(param_file)

    fuel_for_modules = 0
    fuel_for_fuel = 0
    fuel_for_module_incl_fuel_mass = list()
    for m in module_masses:
        fm = calculate_fuel_required(mass=float(m))
        ff = calculate_fuel_required_for_fuel(fm)
        print("Fuel required for fuel of mass %s is %s" % (fm, ff))
        fuel_for_modules += fm
        fuel_for_module_incl_fuel_mass.append(fm + ff)
        fuel_for_fuel += ff

    print("Total fuel required for modules excl. fuel mass: %s" % fuel_for_modules)
    print("Total fuel required for modules incl. fuel mass: %s" % sum(fuel_for_module_incl_fuel_mass))

#    fuel_for_fuel = calculate_fuel_required_for_fuel(fuel_for_modules)
    print("Total fuel required for fuel mass: %s" % fuel_for_fuel)

    fuel_total = fuel_for_modules + fuel_for_fuel
    print("Total fuel required = fuel for modules + fuel for fuel = %s" % fuel_total)
