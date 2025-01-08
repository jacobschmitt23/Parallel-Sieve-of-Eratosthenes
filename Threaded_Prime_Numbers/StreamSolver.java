package Threaded_Prime_Numbers;

import java.util.Scanner;
import java.util.stream.IntStream;

public class StreamSolver {
    
    // @GuardedBy("primes")
    private boolean[] primes;
    private int MAX_SQRT = (int)Math.sqrt(Integer.MAX_VALUE);

    /**
     * Menu display for the stream solver Sieve of Eratosthenes that allows input of the upper bound for the prime number calculations
     * 
     * @param scanner - allows for reading from the command prompt to take in the upper bound of the range from the user
     */
    public void menu(Scanner scanner) {
        
        // Initial menu display
        System.out.println("\nWelcome to the Stream Solver Sieve of Eratosthenes Solver");
        System.out.println("Please enter a number and we will return the amount of prime numbers between 1 and that number (inclusive). Enter 0 to return to the main menu.");

        while (true) {
            int num;

            // Ensure proper input and if not change the number for error messages
            try {
                num = Integer.parseInt(scanner.nextLine());
                if (num < 0) {
                    num = -1;
                }
            } catch (NumberFormatException e) {
                num = -1;
            }

            // Based off input return to first main menu, print an error, or perform calculation
            if (num == 0) {
                return;
            } else if (num == -1) {
                System.out.println("Invalid input, try again");
            } else {
                long startTime = System.nanoTime();
                int numPrimes = solve(num);
                long endTime = System.nanoTime();
                System.out.println("There are " + numPrimes + " prime numbers between 1 and " + num);
                System.out.println("It took " + ((endTime - startTime) / 100000.0) + " milliseconds to calculate");
            }
        }
    }

    /**
     * Solves the Sieve of Eratosthenes in a stream solver manner
     * 
     * @param num - the upper bound of the range of primes you are trying to find. This number is inclusive in the number of primes calculated
     * @return - the number of primes in the inclusive range from 1 to num
     */
    private int solve(int num) {
        // Initialize primes array
        primes = new boolean[num-1];
        IntStream.range(0, num-1)
                 .parallel()
                 .forEach(i -> primes[i]=true);
        
        // Determine the primes
        IntStream.range(0,num-1).parallel().forEach(i -> {
            int multiple = i+2;
            if (primes[i]) {
                // Ensure no integer overflow
                int startingMultiple = multiple+multiple;
                if (multiple <= MAX_SQRT) {
                    startingMultiple = multiple*multiple;
                }
                // Critical region where shared data is manipulated
                for (int j = startingMultiple; j-2 < primes.length; j += multiple) {
                    primes[j-2] = false;
                }
            }
        });

        // Return the count of the primes
        return (int)IntStream.range(0, num-1)
                             .parallel()
                             .filter(i -> primes[i])
                             .count();

    }

}
