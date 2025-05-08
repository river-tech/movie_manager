
package movieManagerData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService {  

    private static final String URL = "jdbc:mysql://mysql-2322d47c-ahmobile17022005-692f.l.aivencloud.com:11057/javaMovie?ssl-mode=REQUIRED";
    private static final String USER = "avnadmin";
    
    private static Connection connection;
    static {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Kết nối thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("✅ Kết nối thành!");
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}