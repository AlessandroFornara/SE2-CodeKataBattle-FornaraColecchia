import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class SortWords {
    public static void main(String[] args) {
        if (args.length > 0) {
            String fileName = args[0];
            ArrayList<String> words = new ArrayList<>();

            try {
                File file = new File(fileName);
                Scanner scanner = new Scanner(file);

                while (scanner.hasNextLine()) {
                    String word = scanner.nextLine().trim();
                    if (!word.isEmpty()) {
                        words.add(word);
                    }
                }
                scanner.close();

                Collections.sort(words);

                for (String word : words) {
                    System.out.println(word);
                }
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + e.getMessage());
            }
        }
    }
}

