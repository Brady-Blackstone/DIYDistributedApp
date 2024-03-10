import random

rand = [round(random.random(), 10) for _ in range(5000)]

with open("rand.dat", "w") as file:
    for num in rand:
        file.write(f"{num}\n")