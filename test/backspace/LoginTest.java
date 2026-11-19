package backspace;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for Login, using the test data given in the brief.
 */
public class LoginTest {

    // Builds a user whose details are all valid. Each test below changes only
    // the one detail it is testing, which saves repeating this five times.
    private Login validUser() {
        return new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
    }

    // ---------- assertEquals: checks the exact message ----------

    @Test
    public void testUsernameCorrectlyFormattedMessage() {
        // "kyl_1" has an underscore and is 5 characters, so it passes.
        assertEquals("Username successfully captured.", validUser().returnUsernameMessage());
    }

    @Test
    public void testUsernameIncorrectlyFormattedMessage() {
        Login user = validUser();
        // Too long and no underscore, so the error message comes back instead.
        user.setUsername("kyle!!!!!!!");
        assertEquals("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.",
                user.returnUsernameMessage());
    }

    @Test
    public void testPasswordMeetsComplexityMessage() {
        // "Ch&&sec@ke99!" has a capital, digits, symbols and is long enough.
        assertEquals("Password successfully captured.", validUser().returnPasswordMessage());
    }

    @Test
    public void testPasswordDoesNotMeetComplexityMessage() {
        Login user = validUser();
        // "password" has no capital, no number and no special character.
        user.setPassword("password");
        assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                user.returnPasswordMessage());
    }

    @Test
    public void testCellPhoneCorrectlyFormattedMessage() {
        // "+27838968976" starts with the country code.
        assertEquals("Cell number successfully captured.", validUser().returnCellPhoneMessage());
    }

    @Test
    public void testCellPhoneIncorrectlyFormattedMessage() {
        Login user = validUser();
        // "08966553" has no + and no country code.
        user.setCellPhoneNumber("08966553");
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.",
                user.returnCellPhoneMessage());
    }

    @Test
    public void testLoginSuccessfulMessage() {
        Login user = validUser();
        // The right details, so the welcome message should use the user's name.
        boolean loggedIn = user.loginUser("kyl_1", "Ch&&sec@ke99!");
        assertEquals("Welcome Kyle, Smith it is great to see you again.", user.returnLoginStatus(loggedIn));
    }

    @Test
    public void testLoginFailedMessage() {
        Login user = validUser();
        // Wrong password, so the failure message should come back.
        boolean loggedIn = user.loginUser("kyl_1", "wrongPassword");
        assertEquals("Username or password incorrect, please try again.", user.returnLoginStatus(loggedIn));
    }

    // ---------- assertTrue and assertFalse: checks true or false ----------

    @Test
    public void testLoginSuccessful() {
        // Correct username and password, so loginUser gives true.
        assertTrue(validUser().loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailed() {
        // Wrong password, so loginUser gives false.
        assertFalse(validUser().loginUser("kyl_1", "wrongPassword"));
    }

    @Test
    public void testUsernameCorrectlyFormatted() {
        assertTrue(validUser().checkUserName());
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        Login user = validUser();
        user.setUsername("kyle!!!!!!!");
        assertFalse(user.checkUserName());
    }

    @Test
    public void testPasswordMeetsComplexity() {
        assertTrue(validUser().checkPasswordComplexity());
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {
        Login user = validUser();
        user.setPassword("password");
        assertFalse(user.checkPasswordComplexity());
    }

    @Test
    public void testCellPhoneCorrectlyFormatted() {
        assertTrue(validUser().checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {
        Login user = validUser();
        user.setCellPhoneNumber("08966553");
        assertFalse(user.checkCellPhoneNumber());
    }
}
