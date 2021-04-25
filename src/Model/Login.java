package Model;

import Controller.FileController;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jaredb
 */
public class Login {
    
    private final Map<String, String> userPass;
    private final String pin;
    
    private final FileController fileCtl;
    
    private final Argon2 argon2;
    
    public Login() {
        argon2 = Argon2Factory.create(Argon2Types.ARGON2id);
        userPass = new HashMap<>();
        fileCtl = new FileController();
        
        if (!isFirstLogin()) {
            userPass.put("username", fileCtl.getCredentials().replaceAll(" ", "").split(":")[0]);
            userPass.put("password", fileCtl.getCredentials().replaceAll(" ", "").split(":")[1]);
            if (fileCtl.isPinSaved()) {
                pin = fileCtl.getPin();
            } else {
                pin = "";
            }
        } else {
            userPass.put("username", "");
            userPass.put("password", "");
            pin = "";
        }
    }
    
    public boolean authenticate(String username, String password) {
        return argon2.verify(this.userPass.get("username"), username) && argon2.verify(this.userPass.get("password"), password);
    }
    
    public boolean authenticatePin(String pin) {
        return argon2.verify(this.pin, pin);
    }
    
    public final boolean isFirstLogin() {
        return fileCtl.isFirstLogin();
    }
    
}
