
import java.nio.file.Files;
import java.nio.file.Path;

public class PassSearchBenchmark {

    public static void main(String[] args) throws Exception {

        int[] sizes = {1000, 10000, 50000, 100000};

        for (int size : sizes) {

            Path file = Files.createTempFile("benchmark", ".csv");

            StringBuilder data = new StringBuilder();

            for (int i = 1; i <= size; i++) {
                data.append("P").append(i)
                    .append(",192311001,Student")
                    .append(i).append(",12,4500.0\n");
            }

            Files.writeString(file, data);

            PassStore store = new PassStore(file);

            long start = System.nanoTime();

            store.find("P" + size);

            long end = System.nanoTime();

            System.out.println("Records: " + size);
            System.out.println("Search Time: "
                    + (end - start) / 1_000_000.0 + " ms");

            Files.delete(file);
        }
    }
}
