package Controller;

import Model.Login;
import Model.PasswordBasedEncryption;

import javax.crypto.SecretKey;

/**
 *
 * @author jaredb
 */
public class LoginController {

    private final Login login;

    private final byte[] salt = {
        (byte) 0x9B, (byte) 0x6B, (byte) 0x61, (byte) 0x50,
        (byte) 0xD0, (byte) 0x04, (byte) 0x5A, (byte) 0x36
    };

    private SecretKey key;

    public LoginController() {
        login = new Login();
    }

    public boolean isFirstLogin() {
        return login.isFirstLogin();
    }

    public boolean authenticate(String username, String password) {
        return login.authenticate(username, password);
    }

    public boolean authenticatePin(String pin) {
        return login.authenticatePin(pin);
    }

    public void setPasswordBasedKey(String password) {
        key = PasswordBasedEncryption.deriveKey(password, salt);
    }

    public SecretKey getPasswordBasedKey() {
        return key;
    }
}
