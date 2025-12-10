# /// script
# requires-python = ">=3.13"
# dependencies = [
#     "z3-solver>=4.15.4.0",
# ]
# ///

import re
from z3 import *

pattern = r"\] ([^{]+) \{([^}]+)\}"

total = 0

with open('src/test/resources/day10.txt') as file:
    for line in file:
        match = re.search(pattern, line.rstrip())
        buttons = [[int(n) for n in re.findall(r'\d+', s)] for s in match.group(1).split(' ')]
        joltages = [int(n) for n in match.group(2).split(',')]
        print(buttons, joltages)
        
        o = Optimize()
        
        variables = []
        for i in range(len(buttons)):
            b = Int(f'b{i}')
            variables.append(b)
            o.add(b >= 0)
        
        for i, joltage in enumerate(joltages):
            summands = []
            for j, button in enumerate(buttons):
                if i in button:
                    summands.append(variables[j])
            o.add(Sum(summands) == joltage)
        
        result = o.minimize(Sum(variables))
        
        if o.check() == sat:
            print(result.value())
            total += result.value().as_long()

print(total)
