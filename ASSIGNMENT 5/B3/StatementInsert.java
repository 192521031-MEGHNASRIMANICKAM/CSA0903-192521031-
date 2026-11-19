
import java.sql.*;

public class StatementInsert {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/assignment5";

        try (Connection con =
                     DriverManager.getConnection(url, "root", "root");
             Statement st = con.createStatement()) {

            long start = System.nanoTime();

            for (int i = 1; i <= 10; i++) {

                String sql = "INSERT INTO bus_pass VALUES (" +
                        "'P" + i + "'," +
                        "'19231100" + i + "'," +
                        "'Student" + i + "'," +
                        "12,4500)";

                st.executeUpdate(sql);
            }

            long end = System.nanoTime();

            System.out.println("Statement Inserted: 10");
            System.out.println("Time: "
                    + (end - start) / 1_000_000.0 + " ms");
        }
    }
}
