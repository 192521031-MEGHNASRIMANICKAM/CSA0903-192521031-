
import java.sql.*;

public class ResultSetMetaDataTest {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/assignment5";

        try (Connection con =
                     DriverManager.getConnection(url, "root", "root");
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM bus_pass")) {

            ResultSetMetaData md = rs.getMetaData();

            int columns = md.getColumnCount();

            System.out.println("Table Columns:");

            for (int i = 1; i <= columns; i++) {

                System.out.println(
                        md.getColumnName(i) + " - "
                        + md.getColumnTypeName(i));
            }

            System.out.println("\nTable Records:");

            while (rs.next()) {
                System.out.println(
                        rs.getString("pass_id") + " "
                        + rs.getString("reg_no") + " "
                        + rs.getString("name") + " "
                        + rs.getInt("route_no") + " "
                        + rs.getDouble("amount"));
            }
        }
    }
}
