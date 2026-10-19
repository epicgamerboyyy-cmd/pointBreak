package backspace;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Message, using the test data given in the brief.
 */
public class MessageTest {

    // Test Case 1 from the brief. The ID is fixed here so the hash comes out
    // the same every run.
    private Message testMessageOne() {
        return new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
    }

    // Test Case 2 from the brief. This recipient has no international code.
    private Message testMessageTwo() {
        return new Message("0087654321", 1, "08575975889",
                "Hi Keegan, did you receive the payment?");
    }

    // Empties the shared lists before each test so one test's messages don't
    // affect the next.
    @BeforeEach
    public void reset() {
        Message.resetMessages();
    }

    // ---------- Message length ----------

    @Test
    public void testMessageLengthSuccess() {
        assertEquals("Message ready to send.", testMessageOne().checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        // 255 characters, so it is over by 5.
        String tooLong = "a".repeat(255);
        Message m = new Message("0012345678", 0, "+27718693002", tooLong);
        assertEquals("Message exceeds 250 characters by 5; please reduce the size.",
                m.checkMessageLength());
    }

    // ---------- Recipient cell ----------

    @Test
    public void testRecipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.",
                testMessageOne().checkRecipientCell());
    }

    @Test
    public void testRecipientIncorrectlyFormatted() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                testMessageTwo().checkRecipientCell());
    }

    // ---------- Message hash ----------

    @Test
    public void testMessageHash() {
        assertEquals("00:0:HITONIGHT", testMessageOne().createMessageHash());
    }

    @Test
    public void testMessageHashSecondMessage() {
        assertEquals("00:1:HIPAYMENT", testMessageTwo().createMessageHash());
    }

    // ---------- Message ID ----------

    @Test
    public void testMessageIDCreated() {
        assertEquals("Message ID generated: 0012345678", testMessageOne().createMessageID());
    }

    @Test
    public void testMessageIDIsTenCharacters() {
        // The auto generated ID must not be longer than ten characters.
        Message m = new Message("+27718693002", "Hi there");
        assertTrue(m.checkMessageID());
    }

    // ---------- Send, disregard, store ----------

    @Test
    public void testMessageSent() {
        assertEquals("Message successfully sent.", testMessageOne().SentMessage(1));
    }

    @Test
    public void testMessageDisregarded() {
        assertEquals("Press 0 to delete the message.", testMessageOne().SentMessage(2));
    }

    @Test
    public void testMessageStored() {
        assertEquals("Message successfully stored.", testMessageOne().SentMessage(3));
    }

    // ---------- Totals and printing ----------

    @Test
    public void testTotalMessagesSent() {
        testMessageOne().SentMessage(1);
        testMessageTwo().SentMessage(1);
        assertEquals(2, Message.returnTotalMessages());
    }

    @Test
    public void testPrintMessagesShowsSentMessage() {
        Message m = testMessageOne();
        m.SentMessage(1);
        assertTrue(Message.printMessages().contains("00:0:HITONIGHT"));
    }
}
