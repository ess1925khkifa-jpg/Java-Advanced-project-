package dao;

import model.Task;
import util.DBConnection;
import java.sql.*;
import java.util.*;

public class TaskDAO {

    public TaskDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS tasks (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "title VARCHAR(100)," +
                     "description TEXT," +
                     "priority VARCHAR(20)," +
                     "is_completed BOOLEAN DEFAULT FALSE" +
                     ")";
        try (Connection c = DBConnection.getConnection()) {
            if (c != null) {
                try (Statement st = c.createStatement()) {
                    st.execute(sql);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTask(Task t) {
        String sql = "INSERT INTO tasks(title, description, priority, is_completed) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection()) {
            if (c == null) return;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, t.getTitle());
                ps.setString(2, t.getDescription());
                ps.setString(3, t.getPriority());
                ps.setBoolean(4, t.isCompleted());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY is_completed ASC, id DESC";
        try (Connection c = DBConnection.getConnection()) {
            if (c == null) return list;
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("priority"),
                        rs.getBoolean("is_completed")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateTask(Task t) {
        String sql = "UPDATE tasks SET title=?, description=?, priority=?, is_completed=? WHERE id=?";
        try (Connection c = DBConnection.getConnection()) {
            if (c == null) return false;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, t.getTitle());
                ps.setString(2, t.getDescription());
                ps.setString(3, t.getPriority());
                ps.setBoolean(4, t.isCompleted());
                ps.setInt(5, t.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection c = DBConnection.getConnection()) {
            if (c == null) return false;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleComplete(int id, boolean status) {
        String sql = "UPDATE tasks SET is_completed=? WHERE id=?";
        try (Connection c = DBConnection.getConnection()) {
            if (c == null) return false;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setBoolean(1, status);
                ps.setInt(2, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
