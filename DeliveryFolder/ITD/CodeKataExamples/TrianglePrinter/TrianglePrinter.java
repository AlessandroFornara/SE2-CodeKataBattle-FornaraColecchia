import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TrianglePrinter {
    public static void main(String[] args) {
        if (args.length > 0) {
            String fileName = args[0];

            try {
                File file = new File(fileName);
                Scanner scanner = new Scanner(file);

                int height = scanner.nextInt();

                scanner.close();

                printIsoscelesTriangle(height);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + e.getMessage());
            }
        }
    }

    public static void printIsoscelesTriangle(int height) {
        for (int i = 0; i < height; i++) {
            for (int j = height - i; j > 1; j--) {
                System.out.print(" ");
            }

            for (int j = 0; j < 2 * i + 1; j++) {
                System.out.print("*");
            }

            if(i != height-1)
                System.out.print("\n");
        }
    }
}
