
import java.sql.*;

public class PreparedStatementInsert {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/assignment5";

        try (Connection con =
                     DriverManager.getConnection(url, "root", "root")) {

            String sql = "INSERT INTO bus_pass VALUES (?, ?, ?, ?, ?)";

            long start = System.nanoTime();

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                for (int i = 1; i <= 10; i++) {

                    ps.setString(1, "P" + i);
                    ps.setString(2, "19231100" + i);
                    ps.setString(3, "Student" + i);
                    ps.setInt(4, 12);
                    ps.setDouble(5, 4500);

                    ps.executeUpdate();
                }
            }

            long end = System.nanoTime();

            System.out.println("PreparedStatement Inserted: 10");
            System.out.println("Time: "
                    + (end - start) / 1_000_000.0 + " ms");
        }
    }
}
