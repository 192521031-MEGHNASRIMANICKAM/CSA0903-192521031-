
import java.sql.*;

public class DatabaseMetadataTest {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/assignment5";

        try (Connection con =
                     DriverManager.getConnection(url, "root", "root")) {

            DatabaseMetaData md = con.getMetaData();

            System.out.println("Database Product: "
                    + md.getDatabaseProductName());

            System.out.println("Database Version: "
                    + md.getDatabaseProductVersion());

            System.out.println("Driver Name: "
                    + md.getDriverName());

            System.out.println("Driver Version: "
                    + md.getDriverVersion());

            System.out.println("\nAvailable Tables:");

            try (ResultSet rs = md.getTables(
                    null, null, "%", new String[]{"TABLE"})) {

                while (rs.next()) {
                    System.out.println(rs.getString("TABLE_NAME"));
                }
            }
        }
    }
}
