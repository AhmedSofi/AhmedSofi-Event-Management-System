package com.ems.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification.
 * Uses SHA-256 with salt for secure password storage.
 */
public class PasswordHasher {
    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * Hashes a password with a randomly generated salt.
     * @param password The password to hash
     * @return A string containing the salt and hash separated by a colon
     */
    public static String hashPassword(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Create MessageDigest instance
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);

            // Add salt to digest
            md.update(salt);

            // Get the hash's bytes
            byte[] hashedBytes = md.digest(password.getBytes());

            // Combine salt and hash
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);

            return saltBase64 + ":" + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    /**
     * Verifies a password against a stored hash.
     * @param password The password to verify
     * @param storedHash The stored hash (salt:hash)
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split the stored hash into salt and hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHashBytes = Base64.getDecoder().decode(parts[1]);

            // Create MessageDigest instance
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);

            // Add salt to digest
            md.update(salt);

            // Get the hash's bytes
            byte[] hashedBytes = md.digest(password.getBytes());

            // Compare the hashes
            return MessageDigest.isEqual(storedHashBytes, hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to verify password", e);
        }
    }
}
