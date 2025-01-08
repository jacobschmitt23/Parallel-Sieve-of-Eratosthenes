package Threaded_Prime_Numbers;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class DistributedSolver {

    // Synchronization occurs naturally as they all just write false to the array so overwriting false to false won't cahnge anything
    private boolean[] primes;

    /**
     * Menu display for the distributed solver Sieve of Eratosthenes that allows input of the upper bound for the prime number calculations
     * This is scalabale in the sense that multiple microservices are dispatched to the available processors
     * 
     * @param scanner - allows for reading from the command prompt to take in the upper bound of the range from the user
     */
    public void menu(Scanner scanner) {
        
        // Initial menu display
        System.out.println("\nWelcome to the Distributed Solver Sieve of Eratosthenes Solver");
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
     * Solves the Sieve of Eratosthenes in a distributed solver manner
     * 
     * @param num - the upper bound of the range of primes you are trying to find. This number is inclusive in the number of primes calculated
     * @return - the number of primes in the inclusive range from 1 to num
     */
    private int solve(int num) {
        // Initialize primes array
        primes = new boolean[num-1];
        IntStream.range(0,num-1)
                 .forEach(i -> primes[i] = true);

        // Determine aspects of the microservices and create them
        int numServices = Runtime.getRuntime().availableProcessors() + 1;
        int rangeOfServices = (num-1) / numServices;
        IntStream.range(0,numServices).parallel().forEach(i -> {
            int startRange = i * rangeOfServices + 1;
            int endRange = (i == numServices-1) ? num : startRange+rangeOfServices;
            serviceRange(startRange, endRange);
        });

        // Count the number of primes
        return (int)IntStream.range(0,num-1)
                             .filter(i -> primes[i])
                             .count();
    }

    /**
     * Makes the request to the microservice to calculate primes of specified range
     * 
     * @param startRange - starting value of the range of primes to send to the microservice
     * @param endRange - ending value of the range of primes to send to the microservice
     */
    private void serviceRange(int startRange, int endRange) {
        // Make the url for the microservice
        String url = "http://localhost:5000/" + startRange + "/" + endRange;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            // Set the timeout protocol
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Ensures a successful connection and gets response
            if (connection.getResponseCode() == 200) {
                Scanner responseScanner = new Scanner(connection.getInputStream());
                String response = responseScanner.useDelimiter("\\A").next();
                responseScanner.close();

                // Filters the response and updates the prime array
                List<Integer> primeNums = new ArrayList<>();
                String[] nums = response.split(",");
                for (String num: nums) {
                    primeNums.add(Integer.parseInt(num.trim()));
                }
                for (int primeNum: primeNums) {
                    if (primeNum >= 2) {
                        primes[primeNum-2] = true;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error establishing a connection");
        }
    }

}
