from optparse import OptionParser

def pascal_triangle(level):
    rows = []
    if not level:
        print "No level, returning empty"
        return rows

    row = [1]
    #rows.append(row)  # first row done outside loop
    print row
    prev_row = row

    for i in range(1, level):
#        prev_row = rows[i-1]

        row = [1]  # first elem always 1
        for j in range(1, i):
            elem = prev_row[j-1] + prev_row[j]

            row.append(elem)

        row.append(1)  # last elem always 1
        print row
#        rows.append(row)  # only store rows if rows are needed in caller
        prev_row = row  # if rows are not needed, storing just prev_row is enough

    return rows


if __name__ == '__main__':
    parser = OptionParser()
    (options, args) = parser.parse_args()
    print "Got input args: ", args
    print "Got input level args[0]: ", args[0]

    lvl = int(args[0])
    rows = pascal_triangle(level=lvl)
    #print rows
