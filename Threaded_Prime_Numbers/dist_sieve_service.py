from flask import Flask, jsonify
import math
import sys

app = Flask(__name__)

@app.route('/<int:start>/<int:end>')
def find_primes(start, end):
    primes = [True] * (end-start+1)
    MAX_SQRT = int(math.sqrt(sys.maxsize))
    for i in range(start, end):
        multiple = i + 2
        if primes[i-start]:
            starting_multiple = multiple + multiple
            if multiple <= MAX_SQRT:
                starting_multiple = multiple * multiple
            for j in range(starting_multiple, end+1, multiple):
                primes[j-start] = False
    prime_nums = ""
    for i, is_prime in enumerate(primes):
        if is_prime:
            prime_nums += i+start + ","
    return prime_nums.rstrip(',')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
