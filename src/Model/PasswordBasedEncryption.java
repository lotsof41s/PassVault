package Model;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author jaredb
 */
public class PasswordBasedEncryption {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static SecretKey deriveKey(String password, byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            return secret;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }

    public static String encrypt(String plainText, SecretKey secret) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);

            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal(plainText.getBytes("UTF-8"));

            String cipherString = bytesToHex(ciphertext);
            String ivString = bytesToHex(iv);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("cipherPlusIV.txt", true), StandardCharsets.UTF_8)) {
                writer.append(cipherString + ":" + ivString + "\n");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            return cipherString;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidParameterSpecException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    public static String decrypt(String cipherText, SecretKey secret, int lineNum) {
        byte[] iv;
        byte[] ciphertext;

        String plaintext;
        Cipher cipher;

        try (BufferedReader reader = new BufferedReader(new FileReader("cipherPlusIV.txt"))) {
            String line;

            for (int i = 0; i < lineNum; i++) {
                reader.readLine();
            }

            while ((line = reader.readLine()) != null) {
                ciphertext = hexToBytes(line.split(":")[0]);
                iv = hexToBytes(line.split(":")[1]);
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));

                plaintext = new String(cipher.doFinal(ciphertext), "UTF-8");

                return plaintext;
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

}
