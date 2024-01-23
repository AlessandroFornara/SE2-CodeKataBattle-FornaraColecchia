package ingsw2.codekatabattle.Utils;

import java.security.SecureRandom;

public class KeywordGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final int LENGTH = 8;

    /**
     * This method generates a random keyword containing capital and non-capital letters, numbers and symbols.
     * @return a String containing the keyword
     */
    public static String generateKeyword(){
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
