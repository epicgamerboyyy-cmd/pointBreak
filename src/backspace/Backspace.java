package backspace;

import java.util.Scanner;

/**
 * PROG5121 PoE Parts 1 and 2.
 * Registers a user, logs them in, then runs the QuickChat menu.
 */
public class Backspace {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login user = new Login();

        System.out.println("===== USER ACCOUNT REGISTRATION =====");

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

        System.out.println("\n--- Registration Output ---");
        System.out.println(user.registerUser());

        // Stop here if any of the three details were rejected.
        if (!user.checkUserName() || !user.checkPasswordComplexity() || !user.checkCellPhoneNumber()) {
            System.out.println("\nRegistration incomplete. Please correct the details and try again.");
            scanner.close();
            return;
        }

        System.out.println("\n===== USER LOGIN =====");

        System.out.print("Enter Username: ");
        String enteredUsername = scanner.nextLine();

        System.out.print("Enter Password: ");
        String enteredPassword = scanner.nextLine();

        boolean isLoggedIn = user.loginUser(enteredUsername, enteredPassword);

        System.out.println("\n--- Login Output ---");
        System.out.println(user.returnLoginStatus(isLoggedIn));

        // Messages are only available once the login succeeded.
        if (isLoggedIn) {
            Message.setCurrentUser(user.getCellPhoneNumber());
            runQuickChat(scanner);
        }

        scanner.close();
    }

    /**
     * The QuickChat menu. Keeps looping until the user picks quit.
     */
    private static void runQuickChat(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat.");

        // The user decides up front how many messages they may enter.
        int messageLimit = readNumber(scanner, "How many messages would you like to send? ");
        int messagesEntered = 0;

        boolean running = true;
        while (running) {
            System.out.println("\n----- MENU -----");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (messagesEntered >= messageLimit) {
                        System.out.println("You have already entered all " + messageLimit + " of your messages.");
                    } else if (sendMessage(scanner)) {
                        // Only counts if the details were accepted, so a
                        // rejected message does not waste one of the slots.
                        messagesEntered++;
                    }
                    break;

                case "2":
                    System.out.println("Coming Soon.");
                    break;

                case "3":
                    running = false;
                    break;

                case "4":
                    storedMessagesMenu(scanner);
                    break;

                default:
                    System.out.println("Please choose 1, 2, 3 or 4.");
            }
        }

        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
    }

    /**
     * Walks the user through creating and sending one message.
     * Returns true if the message passed the checks.
     */
    private static boolean sendMessage(Scanner scanner) {
        System.out.print("\nEnter the recipient's cell number (e.g. +27718693002): ");
        String recipient = scanner.nextLine();

        System.out.print("Enter your message: ");
        String text = scanner.nextLine();

        Message message = new Message(recipient, text);

        // Check the recipient and the message length before going any further.
        String recipientResult = message.checkRecipientCell();
        System.out.println(recipientResult);
        if (!recipientResult.equals("Cell phone number successfully captured.")) {
            return false;
        }

        String lengthResult = message.checkMessageLength();
        System.out.println(lengthResult);
        if (!lengthResult.equals("Message ready to send.")) {
            return false;
        }

        System.out.println(message.createMessageID());

        System.out.println("\nWhat would you like to do with this message?");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        System.out.print("Choose an option: ");

        int action = readNumber(scanner, "");
        System.out.println(message.SentMessage(action));

        // Show the full details once the message has been sent.
        if (action == 1) {
            System.out.println("\n--- Message Details ---");
            System.out.println(message.printMessageDetails());
        }

        return true;
    }

    /**
     * The Stored Messages menu from Part 3.
     */
    private static void storedMessagesMenu(Scanner scanner) {
        // Pull the saved messages back out of the JSON file first, so the
        // array holds everything from previous runs too.
        System.out.println("\n" + Message.readStoredMessages());

        boolean open = true;
        while (open) {
            System.out.println("\n--- STORED MESSAGES ---");
            System.out.println("a) Sender and recipient of all stored messages");
            System.out.println("b) Longest message");
            System.out.println("c) Search by message ID");
            System.out.println("d) Search by recipient");
            System.out.println("e) Delete a message using its hash");
            System.out.println("f) Full report");
            System.out.println("g) Back to the main menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "a":
                    System.out.println("\n" + Message.displaySenderAndRecipient());
                    break;

                case "b":
                    System.out.println("\nLongest message: " + Message.displayLongestMessage());
                    break;

                case "c":
                    System.out.print("Enter the message ID: ");
                    String id = scanner.nextLine().trim();
                    Message found = Message.findByMessageID(id);

                    if (found == null) {
                        System.out.println("No message found with that ID.");
                    } else {
                        System.out.println("Recipient: " + found.getRecipient());
                        System.out.println("Message: " + found.getMessage());
                    }
                    break;

                case "d":
                    System.out.print("Enter the recipient's cell number: ");
                    System.out.println("\n" + Message.searchByRecipient(scanner.nextLine().trim()));
                    break;

                case "e":
                    System.out.print("Enter the message hash: ");
                    System.out.println(Message.deleteByMessageHash(scanner.nextLine().trim()));
                    break;

                case "f":
                    System.out.println("\n" + Message.displayReport());
                    break;

                case "g":
                    open = false;
                    break;

                default:
                    System.out.println("Please choose a letter from a to g.");
            }
        }
    }

    /**
     * Reads a whole number, and asks again if the user types something else.
     */
    private static int readNumber(Scanner scanner, String prompt) {
        while (true) {
            if (!prompt.isEmpty()) {
                System.out.print(prompt);
            }
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("That is not a number, please try again: ");
            }
        }
    }
}
