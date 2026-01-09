
import java.util.Scanner;

public class TypingSpeedTest {

    public static void main(String[] args) {
        String testText = "The quick brown fox jumps over the lazy dog.";
        Scanner scanner = new Scanner(System.in);

        System.out.println("Typing Speed Test");
        System.out.println("Type the following:");
        System.out.println(testText);
        System.out.println("\nPress Enter when you are ready...");
        scanner.nextLine();

        long startTime = System.currentTimeMillis();
        String inputText = scanner.nextLine();
        long endTime = System.currentTimeMillis();

        if (inputText.equals(testText)) {
            double timeTaken = (endTime - startTime) / 1000.0;
            int wordCount = testText.split(" ").length;
            double wpm = (wordCount / timeTaken) * 60;

            System.out.printf("Time: %.2f seconds%n", timeTaken);
            System.out.printf("Speed: %.2f WPM%n", wpm);
        } else {
            System.out.println("Input did not match the test text.");
        }

        scanner.close();
    }
}
