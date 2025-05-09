
package movieManagerData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService {  

   String URL = environmentVariable.getEnv("DB_URL");
    String USER = environmentVariable.getEnv("DB_USER");
    String PASSWORD = environmentVariable.getEnv("DB_PASSWORD");
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