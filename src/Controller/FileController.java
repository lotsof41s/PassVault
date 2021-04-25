package Controller;

import Model.PasswordBasedEncryption;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

/**
 *
 * @author jaredb
 */
public class FileController {

    private final File savedCredentials;
    private final File loginCredentials;
    private final File savedPin;

    private final Argon2 argon2;
    private final int PARALLELISM;

    public FileController() {

        argon2 = Argon2Factory.create(Argon2Types.ARGON2id);
        PARALLELISM = 2 * Runtime.getRuntime().availableProcessors();

        savedCredentials = new File("saved_credentials.txt");
        if (!savedCredentials.exists()) {
            try (BufferedWriter credentialWriter = new BufferedWriter(new FileWriter(savedCredentials))) {
                credentialWriter.write("username/email, password, website");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        loginCredentials = new File("login_credentials.txt");
        savedPin = new File("login_pin.txt");
    }

    public void saveLoginCredentials(String username, String password, SecretKey key) {
        try (BufferedWriter loginWriter = new BufferedWriter(new FileWriter(loginCredentials))) {
            loginWriter.write("username, passowrd");
            loginWriter.newLine();
            loginWriter.write(argon2.hash(4, 1024 * 1024, PARALLELISM, username) + ":" + argon2.hash(4, 1024 * 1024, PARALLELISM, password));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void saveCredentials(String username, String password, String website, SecretKey key) {
        try (BufferedWriter credentialWriter = new BufferedWriter(new FileWriter(savedCredentials, true))) {
            credentialWriter.write("\n" + PasswordBasedEncryption.encrypt(username, key)
                    + ", " + PasswordBasedEncryption.encrypt(password, key) + ", "
                    + PasswordBasedEncryption.encrypt(website, key));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<String> getTableValues(SecretKey key) {
        List<String> values = new ArrayList<>();

        try (BufferedReader credentialReader = new BufferedReader(new FileReader(savedCredentials))) {
            int lineNum = 0;
            // consume first line
            credentialReader.readLine();

            String line;

            while ((line = credentialReader.readLine()) != null) {
                values.add(PasswordBasedEncryption.decrypt(line.split(",")[0], key, lineNum++) + ", "
                        + PasswordBasedEncryption.decrypt(line.split(",")[1].replace(" ", ""), key, lineNum++) + ", "
                        + PasswordBasedEncryption.decrypt(line.split(",")[2].replace(" ", ""), key, lineNum++));
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return values;
    }

    public boolean isFirstLogin() {
        return !loginCredentials.exists();
    }

    public String getCredentials() {

        try (BufferedReader loginReader = new BufferedReader(new FileReader(loginCredentials))) {
            String last = "";
            String line;

            while ((line = loginReader.readLine()) != null) {
                last = line;
            }

            return last;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return null;

    }

    public boolean isLoginSaved() {
        return loginCredentials.exists();
    }

    public boolean isPinSaved() {
        return savedPin.exists();
    }

    public void savePin(String pin) {
        try (BufferedWriter pinWriter = new BufferedWriter(new FileWriter(savedPin))) {
            pinWriter.write("pin");
            pinWriter.newLine();
            pinWriter.write(argon2.hash(4, 1024 * 1024, PARALLELISM, pin));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String getPin() {
        try (BufferedReader pinReader = new BufferedReader(new FileReader(savedPin))) {
            String last = "";
            String line;

            while ((line = pinReader.readLine()) != null) {
                last = line;
            }

            return last;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

}
