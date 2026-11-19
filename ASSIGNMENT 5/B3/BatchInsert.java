
import java.sql.*;

public class BatchInsert {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/assignment5";

        try (Connection con =
                     DriverManager.getConnection(url, "root", "root")) {

            con.setAutoCommit(false);

            String sql = "INSERT INTO bus_pass VALUES (?, ?, ?, ?, ?)";

            long start = System.nanoTime();

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                for (int i = 1; i <= 10; i++) {

                    ps.setString(1, "P" + i);
                    ps.setString(2, "19231100" + i);
                    ps.setString(3, "Student" + i);
                    ps.setInt(4, 12);
                    ps.setDouble(5, 4500);

                    ps.addBatch();
                }

                ps.executeBatch();
                con.commit();

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Transaction Failed");
                throw e;
            }

            long end = System.nanoTime();

            System.out.println("Batch Inserted: 10");
            System.out.println("Time: "
                    + (end - start) / 1_000_000.0 + " ms");
        }
    }
}
