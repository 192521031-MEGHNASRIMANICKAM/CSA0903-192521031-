
import java.io.FileInputStream;
import java.io.IOException;

public class UnbufferedLineCounter {

    public static long countLines(String filename) throws IOException {

        long lines = 0;

        try (FileInputStream fis = new FileInputStream(filename)) {

            int ch;

            while ((ch = fis.read()) != -1) {
                if (ch == '\n') {
                    lines++;
                }
            }
        }

        return lines;
    }

    public static void main(String[] args) throws IOException {

        String filename = "attendance.txt";

        long start = System.nanoTime();

        long count = countLines(filename);

        long end = System.nanoTime();

        System.out.println("Total Lines: " + count);
        System.out.println("Time Taken: " + (end - start) / 1_000_000.0 + " ms");
    }
}
