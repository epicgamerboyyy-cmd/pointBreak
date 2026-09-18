package backspace;

import java.util.Scanner;

/**
 * PROG5121 PoE Part 1.
 * Asks the user for their details, registers them and then logs them in.
 */
public class Backspace {

    public static void main(String[] args) {
        // Scanner reads whatever the user types into the console.
        Scanner scanner = new Scanner(System.in);
        // One Login object holds this user's details for the whole program.
        Login user = new Login();

        System.out.println("===== USER ACCOUNT REGISTRATION =====");

        // nextLine() waits for the user to type and press Enter, then the
        // setter stores that text inside the Login object.
        System.out.print("Enter First Name: ");
        user.setFirstName(scanner.nextLine());

        System.out.print("Enter Last Name: ");
        user.setLastName(scanner.nextLine());

        System.out.print("Enter Username: ");
        user.setUsername(scanner.nextLine());

        System.out.print("Enter Password: ");
        user.setPassword(scanner.nextLine());

        System.out.print("Enter Cell Phone Number (e.g. +27838968976): ");
        user.setCellPhoneNumber(scanner.nextLine());

        // Login does the checking and hands back the message to display.
        System.out.println("\n--- Registration Output ---");
        System.out.println(user.registerUser());

        // Only move on to the login section if all three details were valid.
        if (user.checkUserName() && user.checkPasswordComplexity() && user.checkCellPhoneNumber()) {

            System.out.println("\n===== USER LOGIN =====");

            System.out.print("Enter Username: ");
            String enteredUsername = scanner.nextLine();

            System.out.print("Enter Password: ");
            String enteredPassword = scanner.nextLine();

            // true if the details match what was registered, otherwise false.
            boolean isLoggedIn = user.loginUser(enteredUsername, enteredPassword);

            System.out.println("\n--- Login Output ---");
            System.out.println(user.returnLoginStatus(isLoggedIn));

        } else {
            System.out.println("\nRegistration incomplete. Please correct the details and try again.");
        }

        // Close the Scanner now that we are finished reading input.
        scanner.close();
    }
}
