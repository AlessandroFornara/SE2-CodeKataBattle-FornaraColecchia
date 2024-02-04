import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PalindromeChecker {
    public static void main(String[] args) {
        if (args.length > 0) {
            String fileName = args[0];

            try {
                File inputFile = new File(fileName);
                Scanner scanner = new Scanner(inputFile);

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    System.out.println(line + ": " + (isPalindrome(line) ? "true" : "false"));
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                System.err.println("File non trovato: " + e.getMessage());
            }
        }
    }

    public static boolean isPalindrome(String text) {
        text = text.replaceAll("[\\W]", "").toLowerCase();

        int i = 0;
        int j = text.length() - 1;

        while (i < j) {
            if (text.charAt(i) != text.charAt(j)) {
                return false;
            }
            i++;
            j--;
        }

        return true;
    }
}
