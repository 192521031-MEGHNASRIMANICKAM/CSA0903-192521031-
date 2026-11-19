
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class DamagedPassFileTest {

    public static void main(String[] args) throws Exception {

        Path file = Files.createTempFile("damaged", ".csv");

        Files.writeString(file,
                "P1001,192311001,Arun,12,4500.0\n" +
                "P1002,192311002,Divya\n" +
                "P1003,192311003,Kumar,7,ABC\n" +
                "\n" +
                "P1004,192311004,Ravi,9,3500.0\n");

        PassStore store = new PassStore(file);

        String[] ids = {"P1001", "P1002", "P1003", "P1004"};

        for (String id : ids) {
            System.out.println(id + ": "
                    + Arrays.toString(store.find(id)));
        }

        Files.delete(file);
    }
}
