package com.github.finacial.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte hashByte : hashBytes) {
                hexStringBuilder.append(String.format("%02x", hashByte));
            }

            return hexStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
