
import java.sql.*;

public class PassCount {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/assignment5";

        try (Connection con =
                     DriverManager.getConnection(url, "root", "root");
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT COUNT(*) FROM bus_pass")) {

            if (rs.next()) {
                System.out.println("Total Bus Passes: " + rs.getInt(1));
            }
        }
    }
}
