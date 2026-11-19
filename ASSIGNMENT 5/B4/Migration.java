
import java.io.*;
import java.sql.*;

public class Migration {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/assignment5";
        String user = "root";
        String password = "root";

        String fileName = "passes.csv";

        String sql = "INSERT INTO bus_pass "
                + "(pass_id, reg_no, name, route_no, amount) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con =
                     DriverManager.getConnection(url, user, password);
             BufferedReader br = new BufferedReader(
                     new FileReader(fileName));
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);

            int count = 0;
            int failed = 0;
            int batchCount = 0;
            String line;
            int lineNumber = 0;

            try {

                while ((line = br.readLine()) != null) {

                    lineNumber++;

                    String[] p = line.split(",", -1);

                    if (p.length != 5) {
                        System.out.println("Invalid Line: " + lineNumber);
                        failed++;
                        continue;
                    }

                    try {
                        int route = Integer.parseInt(p[3]);
                        double amount = Double.parseDouble(p[4]);

                        if (p[0].isEmpty() || p[1].isEmpty()
                                || p[2].isEmpty() || amount < 0) {
                            throw new IllegalArgumentException();
                        }

                        ps.setString(1, p[0]);
                        ps.setString(2, p[1]);
                        ps.setString(3, p[2]);
                        ps.setInt(4, route);
                        ps.setDouble(5, amount);

                        ps.addBatch();

                        count++;
                        batchCount++;

                        if (batchCount == 1000) {
                            ps.executeBatch();
                            batchCount = 0;
                        }

                    } catch (NumberFormatException |
                             IllegalArgumentException e) {

                        System.out.println("Invalid Record: " + lineNumber);
                        failed++;
                    }
                }

                if (batchCount > 0) {
                    ps.executeBatch();
                }

                con.commit();

                System.out.println("Migration Completed");
                System.out.println("Records Loaded: " + count);
                System.out.println("Failed Records: " + failed);

            } catch (SQLException e) {

                con.rollback();
                System.out.println("Migration Rolled Back");
                throw e;

            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
