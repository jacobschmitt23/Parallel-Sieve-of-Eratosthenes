package Threaded_Prime_Numbers;

import java.util.Scanner;

/**
 * Main class of the project which controls which menu is displayed
 * 
 * @author Jacob Schmitt
 */
public class WelcomeMenu {

    /**
     * Main running program of the project
     * 
     * @param args - arguments to the program call
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice != 0) {

            // Menu display options
            System.out.println("Welcome to the Sieve of Eratosthenes Solver!");
            System.out.println("Please select a menu option for how you would like the problem to be solved:");
            System.out.println("    1) Single Threaded Solver");
            System.out.println("    2) Unbounded Solver");
            System.out.println("    3) Executor Solver");
            System.out.println("    4) Stream Solver");
            System.out.println("    5) Distributed Solver");
            System.out.println("    0) Quit");

            // Ensure proper input and if not change the number for error messages
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < 0 || choice > 5) {
                    choice = -1;
                }
            } catch (NumberFormatException e) {
                choice = -1;
            }

            // Based off input transition to another menu, print an error, or end the program
            if (choice == 1) {
                SingleThreaded singleThreaded = new SingleThreaded();
                singleThreaded.menu(scanner);
            } else if (choice == 2) {
                UnboundedSolver unboundedSolver = new UnboundedSolver();
                unboundedSolver.menu(scanner);
            } else if (choice == 3) {
                ExecutorSolver executorSolver = new ExecutorSolver();
                executorSolver.menu(scanner);
            } else if (choice == 4) {
                StreamSolver streamSolver = new StreamSolver();
                streamSolver.menu(scanner);
            } else if (choice == 5) {
                DistributedSolver distributedSolver = new DistributedSolver();
                distributedSolver.menu(scanner);
            } else if (choice == -1) {
                System.out.println("Invalid input, try again\n");
            }
        }
        scanner.close();

    }

}
