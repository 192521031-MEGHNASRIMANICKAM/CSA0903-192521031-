
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class ResultProcessor {

    // Stores the result of processing one file
    static class Result {

        String file;
        boolean success;
        int mark;
        String message;

        Result(String file, boolean success,
               int mark, String message) {

            this.file = file;
            this.success = success;
            this.mark = mark;
            this.message = message;
        }
    }

    // Callable task for processing a file
    static class ProcessFile implements Callable<Result> {

        Path path;

        ProcessFile(Path path) {
            this.path = path;
        }

        public Result call() {

            try {

                String data = Files.readString(path).trim();

                int mark = Integer.parseInt(data);

                if (mark < 0 || mark > 100) {
                    throw new IllegalArgumentException(
                        "Invalid marks");
                }

                return new Result(
                    path.getFileName().toString(),
                    true, mark, "Processed successfully");

            } catch (Exception e) {

                return new Result(
                    path.getFileName().toString(),
                    false, 0, e.getMessage());
            }
        }
    }

    public static void main(String[] args)
            throws Exception {

        Path directory = Paths.get("scores");

        Files.createDirectories(directory);

        // Create sample student files
        Files.writeString(
            directory.resolve("student1.txt"), "85");

        Files.writeString(
            directory.resolve("student2.txt"), "72");

        Files.writeString(
            directory.resolve("student3.txt"), "not-a-number");

        // Input files
        List<Path> files = Arrays.asList(
            directory.resolve("student1.txt"),
            directory.resolve("student2.txt"),
            directory.resolve("student3.txt"),
            directory.resolve("missing.txt")
        );

        // Test different thread pool sizes
        int[] poolSizes = {1, 2, 4, 8};

        for (int poolSize : poolSizes) {

            long start = System.nanoTime();

            ExecutorService executor =
                Executors.newFixedThreadPool(poolSize);

            List<Future<Result>> futures =
                new ArrayList<>();

            ConcurrentLinkedQueue<Result> results =
                new ConcurrentLinkedQueue<>();

            // Submit all file-processing tasks
            for (Path file : files) {

                futures.add(
                    executor.submit(new ProcessFile(file)));
            }

            // Collect results
            for (Future<Result> future : futures) {

                try {
                    results.add(future.get());

                } catch (ExecutionException e) {

                    System.out.println(
                        "Unexpected task error: "
                        + e.getCause());
                }
            }

            executor.shutdown();

            executor.awaitTermination(
                1, TimeUnit.MINUTES);

            long end = System.nanoTime();

            // Calculate success and failure counts
            int success = 0;
            int failure = 0;

            System.out.println(
                "\nThread Pool Size: " + poolSize);

            for (Result r : results) {

                if (r.success) {

                    success++;

                    System.out.println(
                        r.file + " : SUCCESS - Marks: "
                        + r.mark);

                } else {

                    failure++;

                    System.out.println(
                        r.file + " : FAILED - "
                        + r.message);
                }
            }

            System.out.println(
                "Successful files: " + success);

            System.out.println(
                "Failed files: " + failure);

            System.out.println(
                "Execution time: "
                + (end - start) / 1000000
                + " ms");
        }
    }
}
