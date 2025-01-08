package Threaded_Prime_Numbers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Executor Solver Sieve of Eratosthenes Solver
 * 
 * @author Jacob Schmitt
 */
public class ExecutorSolver {

    private boolean[] primes;
    private int numPrimes;
    private int MAX_SQRT = (int)Math.sqrt(Integer.MAX_VALUE);
    
    /**
     * Menu display for the executor solver Sieve of Eratosthenes that allows input of the upper bound for the prime number calculations
     * 
     * @param scanner - allows for reading from the command prompt to take in the upper bound of the range from the user
     */
    public void menu(Scanner scanner) {
        
        // Initial menu display
        System.out.println("\nWelcome to the Executor Solver Sieve of Eratosthenes Solver");
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
     * Solves the Sieve of Eratosthenes in an executor solver manner
     * 
     * @param num - the upper bound of the range of primes you are trying to find. This number is inclusive in the number of primes calculated
     * @return - the number of primes in the inclusive range from 1 to num
     */
    private int solve(int num) {
        numPrimes = 0;
        // Initialize the prime array
        primes = new boolean[num-1];
        for (int i = 0; i < num-1; i++) {
            primes[i] = true;
        }
        int numThreads = Runtime.getRuntime().availableProcessors()+1;
        int rangeOfThreads = (num-1) / numThreads;
        int rangeStart = 0;
        int rangeEnd = rangeOfThreads;
        // Create threadpool for JVM to handle threads
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();
        // Create threads and execute their tasks
        for (int i = 0; i < numThreads; i++) {
            Task task = new Task(rangeStart, rangeEnd);
            futures.add(executor.submit(task));
            rangeStart += rangeOfThreads;
            if (i == numThreads-2) {
                rangeEnd = num-1;
            } else {
                rangeEnd += rangeOfThreads;
            }
        }
        // Ensures all tasks are completed
        for (Future<?> future: futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        // Count the number of primes
        for (boolean isPrime : primes) {
            if (isPrime) {
                numPrimes++;
            }
        }
        return numPrimes;
    }

    /**
    * Runnable task that will be completed by the threads
    * 
    * @author Jacob Schmitt
    */
    private class Task implements Runnable {

        private int startRange;
        private int endRange;

        /**
         * Constructor for Task object 
         * 
         * @param startRange - starting range of the prime array for this thread to work on
         * @param endRange - ending range of the prime array for this thread to work on
         */
        public Task(int startRange, int endRange) {
            this.startRange = startRange;
            this.endRange = endRange;
        }

        /**
         * The specific task that each thread will be completing. This is the manipulation of the prime array to mark multiples of prime numbers.
         */
        @Override
        public void run() {
            for (int i = startRange; i < endRange; i++) {
                int multiple = i+2;
                if (primes[i]) {
                    // Ensure no integer overflow
                    int startingMultiple = multiple+multiple;
                    if (multiple <= MAX_SQRT) {
                        startingMultiple = multiple*multiple;
                    }
                    // Critical region where shared data is manipulated
                    synchronized (ExecutorSolver.this) {
                        for (int j = startingMultiple; j-2 < primes.length; j += multiple) {
                            primes[j-2] = false;
                        }
                    }
                }
            }
        }
    }

}
