
import java.sql.*;

public class SQLInjectionTest {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/assignment5";
        String user = "root";
        String password = "root";

        try (Connection con = DriverManager.getConnection(url, user, password)) {

            String id = "admin";
            String pwd = "' OR '1'='1";

            String unsafeSQL = "SELECT * FROM users WHERE id='"
                    + id + "' AND pwd='" + pwd + "'";

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(unsafeSQL)) {

                System.out.println("Unsafe Login: "
                        + (rs.next() ? "Accepted" : "Rejected"));
            }

            String safeSQL =
                    "SELECT * FROM users WHERE id=? AND pwd=?";

            try (PreparedStatement ps = con.prepareStatement(safeSQL)) {

                ps.setString(1, id);
                ps.setString(2, pwd);

                try (ResultSet rs = ps.executeQuery()) {

                    System.out.println("Safe Login: "
                            + (rs.next() ? "Accepted" : "Rejected"));
                }
            }
        }
    }
}
