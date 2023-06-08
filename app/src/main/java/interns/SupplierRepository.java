package interns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierRepository {
    private final ConnectionManager connectionManager;

    public SupplierRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public boolean createSupplier(Supplier supplier) throws SQLException {
        try {
            String sql = "INSERT INTO suppliers (sup_name, sup_street, sup_city, sup_state, sup_zip) " +
                    "VALUES (?, ?, ?, ?, ?)";
            Connection connection = connectionManager.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, supplier.getName());
                statement.setString(2, supplier.getStreet());
                statement.setString(3, supplier.getCity());
                statement.setString(4, supplier.getState());
                statement.setString(5, supplier.getZip());
                int rowsUpdated = statement.executeUpdate();
                return rowsUpdated == 1;
            }
        } finally {
            connectionManager.close();
        }
    }

    public List<Supplier> getSuppliers() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        try {
            String sql = "SELECT sup_id, sup_name, sup_street, sup_city, sup_state, sup_zip " +
                    "FROM suppliers ";
            Connection connection = connectionManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        suppliers.add(new Supplier(
                                resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4),
                                resultSet.getString(5),
                                resultSet.getString(6)
                        ));
                    }
                }
            }
        } finally {
            connectionManager.close();
        }
        return suppliers;
    }
}
