package dao;

import model.User;
import util.DBConnection;
import java.sql.*;

public class UserDAO {
    
    public UserDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        // Initial creation with all columns
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "full_name VARCHAR(100)," +
                     "email VARCHAR(100)," +
                     "username VARCHAR(50) UNIQUE," +
                     "password VARCHAR(50)" +
                     ")";
        try (Connection c = DBConnection.getConnection()) {
            if (c != null) {
                try (Statement st = c.createStatement()) {
                    st.execute(sql);
                    
                    // If the table already existed but was missing columns, let's add them
                    try { st.execute("ALTER TABLE users ADD COLUMN full_name VARCHAR(100) AFTER id"); } catch (SQLException e) { /* ignore if exists */ }
                    try { st.execute("ALTER TABLE users ADD COLUMN email VARCHAR(100) AFTER full_name"); } catch (SQLException e) { /* ignore if exists */ }
                    
                    System.out.println("Users table structure verified.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying table: " + e.getMessage());
        }
    }

    /**
     * @return 1 for success, 0 for connection error, -1 for SQL error
     */
    public int addUser(User u) {
        String sql = "INSERT INTO users(full_name, email, username, password) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection()) {
            if (c == null) return 0;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, u.getFullName());
                ps.setString(2, u.getEmail());
                ps.setString(3, u.getUsername());
                ps.setString(4, u.getPassword());
                int rows = ps.executeUpdate();
                return rows > 0 ? 1 : -1;
            }
        } catch (SQLException e) {
            System.err.println("SQL ERROR in addUser: " + e.getMessage());
            return -1;
        }
    }

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection c = DBConnection.getConnection()) {
            if (c == null) return null;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL ERROR in authenticate: " + e.getMessage());
        }
        return null;
    }
}
