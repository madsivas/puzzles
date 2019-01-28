for i in range(1, 101):
    if i % 3 == 0 and i % 5 == 0:
        print("FizzBuzz")
    elif i % 3 == 0:
        print("Fizz")
    elif i % 5 == 0:
        print("Buzz")
    else:
        print(i)

# more elegant way
for i in range(1, 101):
    msg = ""
    if i % 3 == 0:
        msg = "Fizz"
    if i % 5 == 0:
        msg += "Buzz"
    
    msg = msg or str(i)
    print(msg)