
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class PassStoreTest {

    public static void main(String[] args) throws Exception {

        Path file = Files.createTempFile("testpasses", ".csv");

        PassStore store = new PassStore(file);

        store.add("P101", "192311001", "Arun", 12, 4500);
        store.add("P102", "192311002", "Divya", 7, 3800);

        System.out.println("Existing Pass:");
        System.out.println(Arrays.toString(store.find("P101")));

        System.out.println("Missing Pass:");
        System.out.println(Arrays.toString(store.find("P999")));

        Files.writeString(file, "P103,INVALID\n",
                StandardOpenOption.APPEND);

        System.out.println("Damaged Pass:");
        System.out.println(Arrays.toString(store.find("P103")));

        Files.delete(file);
    }
}
