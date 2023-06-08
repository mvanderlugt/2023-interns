package interns;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private final String url;
    private final String username;
    private final String password;

    private Connection connection;

    public ConnectionManager() {
        this("jdbc:postgresql://localhost:5432/postgres", "postgres", System.getenv("POSTGRES_PASSWORD"));
    }

    public ConnectionManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        if(password != null && password.trim().length() > 0) {
            this.password = password;
        } else {
            this.password = "secret";
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            } finally {
                this.connection = null;
            }
        }
    }
}
