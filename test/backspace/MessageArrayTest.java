package backspace;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * PROG5121 PoE Part 3.
 * Tests the arrays and the stored message features, using the five test
 * messages given in the brief.
 */
public class MessageArrayTest {

    // The five test messages. Fixed IDs so the hashes stay the same each run.
    private Message one;
    private Message two;
    private Message three;
    private Message four;
    private Message five;

    /**
     * Builds the five messages and files each one under its flag, exactly as
     * the brief's test data says.
     */
    @Before
    public void populateArrays() {
        Message.resetMessages();
        Message.setCurrentUser("+27838968976");

        one = new Message("1000000001", 0, "+27834557896", "Did you get the cake?");
        two = new Message("2000000002", 1, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        three = new Message("3000000003", 2, "+27834484567", "Yohoooo, I am at your gate.");
        four = new Message("0838884567", 3, "+27838884567", "It is dinner time!");
        five = new Message("5000000005", 4, "+27838884567", "Ok, I am leaving without you.");

        one.SentMessage(1);     // Sent
        two.SentMessage(3);     // Stored
        three.SentMessage(2);   // Disregard
        four.SentMessage(1);    // Sent
        five.SentMessage(3);    // Stored
    }

    // ---------- The arrays ----------

    @Test
    public void testSentMessagesArrayPopulated() {
        // Messages 1 and 4 are the two flagged as Sent.
        assertEquals(Arrays.asList("Did you get the cake?", "It is dinner time!"),
                Message.getSentMessageTexts());
    }

    @Test
    public void testDisregardedMessagesArrayPopulated() {
        assertEquals(1, Message.getDisregardedMessages().size());
        assertEquals("Yohoooo, I am at your gate.",
                Message.getDisregardedMessages().get(0).getMessage());
    }

    @Test
    public void testStoredMessagesArrayPopulated() {
        assertEquals(2, Message.getStoredMessages().size());
    }

    @Test
    public void testMessageHashArrayPopulated() {
        // One hash recorded for each of the five messages.
        assertEquals(5, Message.getMessageHashes().size());
        assertTrue(Message.getMessageHashes().contains(one.createMessageHash()));
    }

    @Test
    public void testMessageIDArrayPopulated() {
        assertEquals(5, Message.getMessageIDs().size());
        assertTrue(Message.getMessageIDs().contains("0838884567"));
    }

    // ---------- The six features ----------

    @Test
    public void testDisplayLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.displayLongestMessage());
    }

    @Test
    public void testSearchByMessageID() {
        assertEquals("It is dinner time!", Message.searchByMessageID("0838884567"));
    }

    @Test
    public void testSearchByRecipient() {
        // Messages 2, 4 and 5 all went to this number.
        String expected = "It is dinner time!"
                + "\nWhere are you? You are late! I have asked you to be on time."
                + "\nOk, I am leaving without you.";
        assertEquals(expected, Message.searchByRecipient("+27838884567"));
    }

    @Test
    public void testDeleteByMessageHash() {
        String expected = "Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.";
        assertEquals(expected, Message.deleteByMessageHash(two.createMessageHash()));
    }

    @Test
    public void testDeleteRemovesFromArray() {
        Message.deleteByMessageHash(two.createMessageHash());
        assertEquals(1, Message.getStoredMessages().size());
    }

    @Test
    public void testDisplaySenderAndRecipient() {
        String result = Message.displaySenderAndRecipient();
        assertTrue(result.contains("+27838968976"));
        assertTrue(result.contains("+27838884567"));
    }

    @Test
    public void testDisplayReport() {
        String report = Message.displayReport();
        // The report has to show the hash, the recipient and the message.
        assertTrue(report.contains(one.createMessageHash()));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("It is dinner time!"));
    }
}
