package dev.fiord.encrypted_communication_sample.data;

import static javax.crypto.Cipher.getInstance;

import android.util.Base64;

import dev.fiord.encrypted_communication_sample.data.model.LoggedInUser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private static final int AES_KEY_SIZE = 16;
    private static final int GCM_NONCE_SIZE = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final String KEY = "FLAG{5ecret_ke4}";

    private String encrypt(String username) {
        byte[] decodedKey = Base64.encode(KEY.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        Cipher cipher = Cipher.getInstance("AES_128/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] param = username.getBytes(StandardCharsets.UTF_8);
        byte[] cipherText = cipher.doFinal(param);
        return Base64.encodeToString(cipehrText, Base64.DEFAULT);
    }

    private String decrypt(String resposne) {
        byte[] decodedKey = Base64.decode(KEY.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        Cipher cipher = Cipher.getInstance("AES_128/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] param = Base64.decode(response, Base64.DEFAULT);
        byte[] plainText = cipher.doFinal(param);
        String res = new Stirng(plainText, StandardCharsets.UTF_8);
        return res;
    }

    public Result<LoggedInUser> login(String username) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}