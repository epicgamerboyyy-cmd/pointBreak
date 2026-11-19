package backspace;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * PROG5121 PoE Parts 2 and 3.
 * Holds one QuickChat message, the checks that go with it, and the arrays
 * that keep track of every message the app has handled.
 */
public class Message {

    // Details of this one message.
    private String messageID;
    private int messageNumber;
    private String sender;
    private String recipient;
    private String message;
    private String flag;

    // Shared across every Message. static means one copy for the whole class,
    // not one copy per message, so the arrays and the total keep building up.
    private static int totalSent = 0;
    private static String currentUser = "Unknown";

    private static final ArrayList<Message> sentMessages = new ArrayList<>();
    private static final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static final ArrayList<Message> storedMessages = new ArrayList<>();
    private static final ArrayList<String> messageHashes = new ArrayList<>();
    private static final ArrayList<String> messageIDs = new ArrayList<>();

    /**
     * Normal constructor. Generates a random ID and works out which number
     * message this is.
     */
    public Message(String recipient, String message) {
        this.messageID = generateMessageID();
        this.messageNumber = totalSent;
        this.sender = currentUser;
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
        this.sender = currentUser;
        this.recipient = recipient;
        this.message = message;
    }

    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getFlag() { return flag; }

    public void setSender(String sender) { this.sender = sender; }

    /**
     * Remembers who is logged in, so new messages know who sent them.
     */
    public static void setCurrentUser(String user) { currentUser = user; }

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
     * Acts on what the user chose to do with the message and files it into the
     * right array. 1 sends it, 2 disregards it, 3 stores it for later.
     */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                flag = "Sent";
                sentMessages.add(this);
                recordIDAndHash();
                totalSent++;
                return "Message successfully sent.";
            case 2:
                flag = "Disregard";
                disregardedMessages.add(this);
                recordIDAndHash();
                return "Press 0 to delete the message.";
            case 3:
                flag = "Stored";
                storedMessages.add(this);
                recordIDAndHash();
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid choice, please pick 1, 2 or 3.";
        }
    }

    /**
     * Adds this message's ID and hash to their own arrays.
     */
    private void recordIDAndHash() {
        messageIDs.add(messageID);
        messageHashes.add(createMessageHash());
    }

    // ---------- Part 3: getting at the arrays ----------

    public static ArrayList<Message> getSentMessages() { return sentMessages; }
    public static ArrayList<Message> getDisregardedMessages() { return disregardedMessages; }
    public static ArrayList<Message> getStoredMessages() { return storedMessages; }
    public static ArrayList<String> getMessageHashes() { return messageHashes; }
    public static ArrayList<String> getMessageIDs() { return messageIDs; }

    /**
     * Just the text of every sent message, which is what the array test checks.
     */
    public static ArrayList<String> getSentMessageTexts() {
        ArrayList<String> texts = new ArrayList<>();
        for (Message m : sentMessages) {
            texts.add(m.message);
        }
        return texts;
    }

    /**
     * Every message the app is holding, whatever happened to it.
     */
    private static ArrayList<Message> allMessages() {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        all.addAll(disregardedMessages);
        return all;
    }

    // ---------- Part 3: the six menu features ----------

    /**
     * (a) Who sent and who received each stored message.
     */
    public static String displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            return "There are no stored messages.";
        }

        StringBuilder out = new StringBuilder();
        for (Message m : storedMessages) {
            out.append("Sender: ").append(m.sender)
               .append(" | Recipient: ").append(m.recipient).append("\n");
        }
        return out.toString().trim();
    }

    /**
     * (b) The longest message out of everything the app is holding.
     */
    public static String displayLongestMessage() {
        String longest = "";
        for (Message m : allMessages()) {
            if (m.message.length() > longest.length()) {
                longest = m.message;
            }
        }

        if (longest.isEmpty()) {
            return "There are no messages.";
        }
        return longest;
    }

    /**
     * (c) Finds a message by its ID and hands back the text.
     */
    public static String searchByMessageID(String id) {
        Message found = findByMessageID(id);
        if (found == null) {
            return "No message found with that ID.";
        }
        return found.message;
    }

    /**
     * Returns the whole message object, so the menu can also show the
     * recipient alongside the text.
     */
    public static Message findByMessageID(String id) {
        for (Message m : allMessages()) {
            if (m.messageID.equals(id)) {
                return m;
            }
        }
        return null;
    }

    /**
     * (d) Every message sent or stored for one recipient, one per line.
     */
    public static String searchByRecipient(String recipient) {
        StringBuilder out = new StringBuilder();

        for (Message m : allMessages()) {
            if (m.recipient.equals(recipient)) {
                if (out.length() > 0) {
                    out.append("\n");
                }
                out.append(m.message);
            }
        }

        if (out.length() == 0) {
            return "No messages found for that recipient.";
        }
        return out.toString();
    }

    /**
     * (e) Removes a message using its hash, and says which one went.
     */
    public static String deleteByMessageHash(String hash) {
        ArrayList<ArrayList<Message>> lists = new ArrayList<>();
        lists.add(sentMessages);
        lists.add(storedMessages);
        lists.add(disregardedMessages);

        for (ArrayList<Message> list : lists) {
            for (int i = 0; i < list.size(); i++) {
                Message m = list.get(i);

                if (m.createMessageHash().equals(hash)) {
                    String text = m.message;
                    list.remove(i);
                    messageHashes.remove(hash);
                    messageIDs.remove(m.messageID);
                    return "Message: \"" + text + "\" successfully deleted.";
                }
            }
        }
        return "No message found with that hash.";
    }

    /**
     * (f) The full report of everything that was sent.
     */
    public static String displayReport() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent.";
        }

        StringBuilder report = new StringBuilder("--- Sent Messages Report ---\n");
        for (Message m : sentMessages) {
            report.append("Message Hash: ").append(m.createMessageHash()).append("\n")
                  .append("Recipient: ").append(m.recipient).append("\n")
                  .append("Message: ").append(m.message).append("\n\n");
        }
        return report.toString().trim();
    }

    // ---------- Printing and totals ----------

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

    // ---------- JSON ----------

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
            entry.put("sender", m.sender);
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
     * Reads the JSON file back and rebuilds the stored messages array from it.
     * This is how the array survives the program being closed and reopened.
     */
    public static String readStoredMessages() {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader("storedMessages.json")) {
            JSONArray all = (JSONArray) parser.parse(reader);
            storedMessages.clear();

            for (Object item : all) {
                JSONObject entry = (JSONObject) item;

                Message m = new Message(
                        (String) entry.get("messageID"),
                        ((Long) entry.get("messageNumber")).intValue(),
                        (String) entry.get("recipient"),
                        (String) entry.get("message"));

                m.sender = (String) entry.get("sender");
                m.flag = "Stored";
                storedMessages.add(m);
            }
            return "Loaded " + storedMessages.size() + " stored message(s) from the file.";

        } catch (Exception e) {
            return "No stored messages could be read: " + e.getMessage();
        }
    }

    /**
     * Clears every array. Only used by the tests so one test's messages don't
     * leak into the next one.
     */
    public static void resetMessages() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalSent = 0;
    }
}
