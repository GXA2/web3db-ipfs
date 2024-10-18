package exchange;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Block {
    private byte[] data;  // The data stored in this block
    private String hash;  // SHA-256 hash representing this block

    public Block(byte[] data) {
        this.data = data;
        this.hash = generateHash(data);
    }

    // Generates a SHA-256 hash for the data
    private String generateHash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(data);
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Utility method to convert bytes to a hex string
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public byte[] getData() {
        return data;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "Block{" +
                "hash='" + hash + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    public static void main(String[] args) {
        // Example block with a string converted to bytes
        Block block = new Block("This is some test data".getBytes(StandardCharsets.UTF_8));
        System.out.println("Block created with hash: " + block.getHash());
    }
}

