package backspace;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * PROG5121 PoE Part 2.
 * Holds one QuickChat message and the checks that go with it.
 */
public class Message {

    // Details of this one message.
    private String messageID;
    private int messageNumber;
    private String recipient;
    private String message;

    // Shared across every Message, so the totals keep counting as more are
    // created. static means there is one copy for the whole class, not one
    // copy per message.
    private static int totalSent = 0;
    private static final ArrayList<Message> sentMessages = new ArrayList<>();
    private static final ArrayList<Message> storedMessages = new ArrayList<>();

    /**
     * Normal constructor. Generates a random ID and works out which number
     * message this is.
     */
    public Message(String recipient, String message) {
        this.messageID = generateMessageID();
        this.messageNumber = totalSent;
        this.recipient = recipient;
        this.message = message;
    }

    /**
     * Constructor that takes a fixed ID and number. The unit tests use this so
     * the hash comes out the same every run instead of changing with the
     * random ID.
     */
    public Message(String messageID, int messageNumber, String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
    }

    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }

    /**
     * Builds a random ten digit number as text. Each loop adds one digit.
     */
    private static String generateMessageID() {
        Random random = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(random.nextInt(10));
        }
        return id.toString();
    }

    /**
     * The message ID must not be longer than ten characters.
     */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /**
     * Confirms the ID to the user.
     */
    public String createMessageID() {
        return "Message ID generated: " + messageID;
    }

    /**
     * The recipient must start with an international code. This reuses the
     * check already written in Login rather than writing the same rule twice.
     */
    public String checkRecipientCell() {
        Login check = new Login();
        check.setCellPhoneNumber(recipient);

        if (check.checkCellPhoneNumber()) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    /**
     * The message itself may not be longer than 250 characters.
     */
    public String checkMessageLength() {
        if (message.length() <= 250) {
            return "Message ready to send.";
        }
        int over = message.length() - 250;
        return "Message exceeds 250 characters by " + over + "; please reduce the size.";
    }

    /**
     * Builds the hash: first two digits of the ID, then the message number,
     * then the first and last word joined together, all in capitals.
     */
    public String createMessageHash() {
        // trim first so a stray space at the end doesn't become an empty word.
        String[] words = message.trim().split("\\s+");

        // Strip punctuation so "tonight?" becomes "tonight".
        String first = words[0].replaceAll("[^a-zA-Z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");

        String hash = messageID.substring(0, 2) + ":" + messageNumber + ":" + first + last;
        return hash.toUpperCase();
    }

    /**
     * Acts on what the user chose to do with the message.
     * 1 sends it, 2 disregards it, 3 stores it for later.
     */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storedMessages.add(this);
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid choice, please pick 1, 2 or 3.";
        }
    }

    /**
     * The full details, in the order the brief asks for them.
     */
    public String printMessageDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + createMessageHash()
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    /**
     * Every message sent so far this run.
     */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent.";
        }

        StringBuilder all = new StringBuilder();
        for (Message m : sentMessages) {
            all.append(m.printMessageDetails()).append("\n\n");
        }
        return all.toString().trim();
    }

    /**
     * How many messages have been sent.
     */
    public static int returnTotalMessages() {
        return totalSent;
    }

    /**
     * Writes the stored messages out to a JSON file so they survive after the
     * program closes.
     */
    @SuppressWarnings("unchecked")
    public void storeMessage() {
        JSONArray all = new JSONArray();

        for (Message m : storedMessages) {
            JSONObject entry = new JSONObject();
            entry.put("messageID", m.messageID);
            entry.put("messageNumber", m.messageNumber);
            entry.put("recipient", m.recipient);
            entry.put("message", m.message);
            entry.put("messageHash", m.createMessageHash());
            all.add(entry);
        }

        try (FileWriter file = new FileWriter("storedMessages.json")) {
            file.write(all.toJSONString());
        } catch (IOException e) {
            System.out.println("Could not write the file: " + e.getMessage());
        }
    }

    /**
     * Clears the shared lists. Only used by the tests so one test's messages
     * don't leak into the next one.
     */
    public static void resetMessages() {
        sentMessages.clear();
        storedMessages.clear();
        totalSent = 0;
    }
}
