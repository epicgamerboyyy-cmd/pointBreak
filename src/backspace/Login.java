package backspace;

/**
 * PROG5121 PoE Part 1.
 * Stores the user's details and checks that they are correctly formatted.
 */
public class Login {

    // The user's details. Private so that only this class can change them.
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    // Empty constructor, used when the details are entered one at a time.
    public Login() {
    }

    // Constructor that fills in all five details at once. Used by the tests.
    public Login(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    // Getters read a detail, setters change it. This is how other classes
    // reach the private fields above.
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCellPhoneNumber() { return cellPhoneNumber; }
    public void setCellPhoneNumber(String cellPhoneNumber) { this.cellPhoneNumber = cellPhoneNumber; }

    /**
     * The username must have an underscore and be five characters or fewer.
     */
    public boolean checkUserName() {
        // Nothing was typed in yet, so it cannot be valid.
        if (username == null) {
            return false;
        }
        // Both conditions have to be true, which is what && means.
        return username.contains("_") && username.length() <= 5;
    }

    /**
     * The password must be eight characters or longer and must have a capital
     * letter, a number and a special character.
     *
     * Regex "lookahead" technique adapted from:
     * Oracle, 2023. Class Pattern. [Online] Available at:
     * https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html
     * [Accessed 17 September 2026].
     */
    public boolean checkPasswordComplexity() {
        if (password == null) {
            return false;
        }
        // Each (?=...) checks the whole password for one thing without using
        // it up: a capital, then a digit, then a character that is neither a
        // letter nor a digit. The .{8,} at the end sets the minimum length.
        return password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$");
    }

    /**
     * The cell number must start with an international dialling code.
     */
    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber == null) {
            return false;
        }
        // A + sign, then 10 to 12 digits and nothing else.
        return cellPhoneNumber.matches("^\\+\\d{10,12}$");
    }

    // Says whether the username was accepted, and why not if it was rejected.
    public String returnUsernameMessage() {
        if (checkUserName()) {
            return "Username successfully captured.";
        }
        return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
    }

    // Same idea for the password.
    public String returnPasswordMessage() {
        if (checkPasswordComplexity()) {
            return "Password successfully captured.";
        }
        return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    }

    // Same idea for the cell number.
    public String returnCellPhoneMessage() {
        if (checkCellPhoneNumber()) {
            return "Cell number successfully captured.";
        }
        return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
    }

    /**
     * Builds the message shown after the user tries to register.
     */
    public String registerUser() {
        // Report the first problem found and stop there.
        if (!checkUserName()) {
            return returnUsernameMessage();
        }
        if (!checkPasswordComplexity()) {
            return returnPasswordMessage();
        }
        if (!checkCellPhoneNumber()) {
            return returnCellPhoneMessage();
        }
        // Nothing was wrong, so show all three success messages.
        return returnUsernameMessage() + "\n" + returnPasswordMessage() + "\n" + returnCellPhoneMessage();
    }

    /**
     * Compares what was typed at login against what was saved at registration.
     */
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        // No one has registered yet, so there is nothing to compare against.
        if (username == null || password == null) {
            return false;
        }
        // equals() compares the text itself, which == would not do for Strings.
        return username.equals(enteredUsername) && password.equals(enteredPassword);
    }

    /**
     * Turns the true or false from loginUser into a message for the user.
     */
    public String returnLoginStatus(boolean isLoggedIn) {
        if (isLoggedIn) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}
