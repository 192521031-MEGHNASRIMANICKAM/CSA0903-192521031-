
import java.sql.*;
import java.util.*;

public class PassLoaderTest {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/assignment5";

        try (Connection con =
                     DriverManager.getConnection(url, "root", "root")) {

            int[] sizes = {1000, 10000, 100000};

            for (int size : sizes) {

                try (Statement st = con.createStatement()) {
                    st.executeUpdate("DELETE FROM bus_pass");
                }

                List<String[]> passes = new ArrayList<>();

                for (int i = 1; i <= size; i++) {

                    passes.add(new String[]{
                            "P" + i,
                            "19231100" + i,
                            "Student" + i,
                            "12",
                            "4500"
                    });
                }

                long start = System.nanoTime();

                int count = PassLoader.loadAll(con, passes);

                long end = System.nanoTime();

                System.out.println("Records Loaded: " + count);
                System.out.println("Time Taken: "
                        + (end - start) / 1_000_000.0 + " ms");
            }
        }
    }
}
