
import java.io.*;
import java.nio.file.*;

public class BufferBenchmark {

    public static long unbuffered(String file) throws IOException {

        long lines = 0;

        try (FileInputStream fis = new FileInputStream(file)) {

            int ch;

            while ((ch = fis.read()) != -1) {
                if (ch == '\n') lines++;
            }
        }

        return lines;
    }

    public static long buffered(String file) throws IOException {

        long lines = 0;

        try (BufferedInputStream bis =
                     new BufferedInputStream(new FileInputStream(file))) {

            int ch;

            while ((ch = bis.read()) != -1) {
                if (ch == '\n') lines++;
            }
        }

        return lines;
    }

    public static void main(String[] args) throws IOException {

        int[] sizes = {1, 10, 50};

        for (int size : sizes) {

            String filename = size + "MB.txt";

            try (BufferedWriter bw =
                         Files.newBufferedWriter(Path.of(filename))) {

                for (int i = 0; i < size * 100000; i++) {
                    bw.write("192311001,Present,Attendance Record");
                    bw.newLine();
                }
            }

            long start = System.nanoTime();
            long u = unbuffered(filename);
            long end = System.nanoTime();

            double unbufferedTime = (end - start) / 1_000_000.0;

            start = System.nanoTime();
            long b = buffered(filename);
            end = System.nanoTime();

            double bufferedTime = (end - start) / 1_000_000.0;

            System.out.println("\nFile: " + filename);
            System.out.println("Unbuffered Lines: " + u);
            System.out.println("Unbuffered Time: " + unbufferedTime + " ms");
            System.out.println("Buffered Lines: " + b);
            System.out.println("Buffered Time: " + bufferedTime + " ms");
        }
    }
}
