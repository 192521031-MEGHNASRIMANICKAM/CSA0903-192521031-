
import java.sql.*;

public class LargeDataBenchmark {

    static final String URL =
            "jdbc:mysql://localhost:3306/assignment5";

    static final String USER = "root";
    static final String PASSWORD = "root";

    static void clear(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("DELETE FROM bus_pass");
        }
    }

    static void statementInsert(Connection con, int size)
            throws SQLException {

        try (Statement st = con.createStatement()) {

            for (int i = 1; i <= size; i++) {

                String sql = "INSERT INTO bus_pass VALUES (" +
                        "'S" + i + "'," +
                        "'19231100" + i + "'," +
                        "'Student" + i + "',12,4500)";

                st.executeUpdate(sql);
            }
        }
    }

    static void preparedInsert(Connection con, int size)
            throws SQLException {

        String sql = "INSERT INTO bus_pass VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 1; i <= size; i++) {

                ps.setString(1, "P" + i);
                ps.setString(2, "19231100" + i);
                ps.setString(3, "Student" + i);
                ps.setInt(4, 12);
                ps.setDouble(5, 4500);

                ps.executeUpdate();
            }
        }
    }

    static void batchInsert(Connection con, int size)
            throws SQLException {

        con.setAutoCommit(false);

        String sql = "INSERT INTO bus_pass VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 1; i <= size; i++) {

                ps.setString(1, "B" + i);
                ps.setString(2, "19231100" + i);
                ps.setString(3, "Student" + i);
                ps.setInt(4, 12);
                ps.setDouble(5, 4500);

                ps.addBatch();

                if (i % 1000 == 0) {
                    ps.executeBatch();
                }
            }

            ps.executeBatch();
            con.commit();

        } catch (SQLException e) {
            con.rollback();
            throw e;

        } finally {
            con.setAutoCommit(true);
        }
    }

    public static void main(String[] args) throws Exception {

        int[] sizes = {1000, 10000, 100000};

        try (Connection con =
                     DriverManager.getConnection(URL, USER, PASSWORD)) {

            for (int size : sizes) {

                System.out.println("\nRecords: " + size);

                clear(con);
                long start = System.nanoTime();
                statementInsert(con, size);
                long end = System.nanoTime();

                System.out.println("Statement: "
                        + (end - start) / 1_000_000.0 + " ms");

                clear(con);
                start = System.nanoTime();
                preparedInsert(con, size);
                end = System.nanoTime();

                System.out.println("PreparedStatement: "
                        + (end - start) / 1_000_000.0 + " ms");

                clear(con);
                start = System.nanoTime();
                batchInsert(con, size);
                end = System.nanoTime();

                System.out.println("Batch: "
                        + (end - start) / 1_000_000.0 + " ms");
            }
        }
    }
}
