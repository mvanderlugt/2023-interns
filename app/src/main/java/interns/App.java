/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package interns;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws LifecycleException, SQLException {
        ConnectionManager connectionManager = new ConnectionManager();
        App app = new App(connectionManager);
        List<String> tables = app.getTables();
        if(!tables.contains("suppliers")) {
            app.createTables();
        }

        SupplierRepository repository = new SupplierRepository(connectionManager);

        Tomcat tomcat = new Tomcat();

        Context context = tomcat.addContext("", getContextPath());
        tomcat.addServlet("", "Embedded", new SupplierServlet(repository));
        context.addServletMappingDecoded("/*", "Embedded");

        Connector connector = new Connector();
        connector.setPort(getPort());
        tomcat.setConnector(connector);

        tomcat.start();
        tomcat.getServer().await();
    }

    private static Integer getPort() {
        String port = System.getenv("SERVER_PORT");
        if (port != null) {
            return Integer.valueOf(port);
        } else {
            return 8080;
        }
    }

    private static String getContextPath() {
        return new File("").getAbsolutePath();
    }

    private final SupplierRepository repository;
    private final ConnectionManager connectionManager;

    public App(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.repository = new SupplierRepository(connectionManager);
    }

    public String getGreeting() {
        return "Hello World!";
    }

    public List<String> getTables() throws SQLException {
        return getTables("public");
    }

    public List<String> getTables(String schema) throws SQLException {
        List<String> tables = new ArrayList<>();
        try {
            String sql = "SELECT table_name " +
                    "FROM information_schema.tables " +
                    "WHERE table_schema = '" + schema + "'";
            Connection connection = connectionManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        tables.add(resultSet.getString(1));
                    }
                }
            }
        } finally {
            connectionManager.close();
        }
        return tables;
    }

    public void createTables() throws SQLException {
        try {
            String sql = "CREATE TABLE suppliers (" +
                    "sup_id SERIAL PRIMARY KEY, " +
                    "sup_name VARCHAR(40) NOT NULL, " +
                    "sup_street VARCHAR(40) NOT NULL, " +
                    "sup_city VARCHAR(20) NOT NULL, " +
                    "sup_state CHAR(2) NOT NULL, " +
                    "sup_zip CHAR(5))";
            Connection connection = connectionManager.getConnection();

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
        } finally {
            connectionManager.close();
        }
    }

    public boolean createSupplier(Supplier supplier) throws SQLException {
        return repository.createSupplier(supplier);
    }

    public List<Supplier> getSuppliers() throws SQLException {
        return repository.getSuppliers();
    }
}
