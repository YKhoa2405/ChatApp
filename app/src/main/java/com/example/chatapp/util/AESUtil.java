package com.example.chatapp.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class AESUtil {

    private static final String ALGORITHM = "AES";

    // Tạo khóa AES
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256); // 128, 192, 256-bit AES
        return keyGenerator.generateKey();
    }

    // Mã hóa tin nhắn
    public static String encrypt(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    // Giải mã tin nhắn
    public static String decrypt(String encryptedText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // Chuyển đổi SecretKey thành chuỗi Base64
    public static String keyToBase64(SecretKey key) {
        byte[] encodedKey = key.getEncoded();
        return Base64.encodeToString(encodedKey, Base64.DEFAULT);
    }

    // Chuyển đổi chuỗi Base64 thành SecretKey
    public static SecretKey base64ToKey(String base64Key) {
        byte[] decodedKey = Base64.decode(base64Key, Base64.DEFAULT);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }
}

