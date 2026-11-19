
import java.sql.*;
import java.util.*;

public class TransactionFailureTest {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/assignment5";

        try (Connection con =
                     DriverManager.getConnection(url, "root", "root")) {

            try (Statement st = con.createStatement()) {
                st.executeUpdate("DELETE FROM bus_pass");
            }

            List<String[]> passes = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {

                String amount = (i == 5) ? "INVALID" : "4500";

                passes.add(new String[]{
                        "P" + i,
                        "19231100" + i,
                        "Student" + i,
                        "12",
                        amount
                });
            }

            try {
                PassLoader.loadAll(con, passes);
            } catch (Exception e) {
                System.out.println("Insert Failed: " + e.getMessage());
            }

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT COUNT(*) FROM bus_pass")) {

                if (rs.next()) {
                    System.out.println("Remaining Rows: " + rs.getInt(1));
                }
            }
        }
    }
}
