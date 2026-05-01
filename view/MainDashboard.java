package view;

import dao.TaskDAO;
import model.Task;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainDashboard extends JFrame {
    private JPanel taskListPanel;
    private TaskDAO taskDAO;

    public MainDashboard() {
        taskDAO = new TaskDAO();
        setTitle("MY TO-DO LIST");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 242, 245));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(63, 81, 181));
        headerPanel.setPreferredSize(new Dimension(900, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("MY TASKS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Buttons Panel (Logout and Add Task)
        JPanel topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        topButtonsPanel.setBackground(new Color(63, 81, 181));

        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setBackground(new Color(255, 255, 255, 40)); // Semi-transparent white
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setPreferredSize(new Dimension(100, 45));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame();
                dispose();
            }
        });

        JButton addBtn = new JButton("+ NEW TASK");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.setBackground(new Color(255, 64, 129));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setPreferredSize(new Dimension(150, 45));
        addBtn.addActionListener(e -> {
            TaskForm form = new TaskForm();
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    refreshData();
                }
            });
            form.setVisible(true);
        });

        topButtonsPanel.add(logoutBtn);
        topButtonsPanel.add(addBtn);
        headerPanel.add(topButtonsPanel, BorderLayout.EAST);

        // Content Scroll Pane
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(new Color(240, 242, 245));
        taskListPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JScrollPane scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        taskListPanel.removeAll();
        List<Task> tasks = taskDAO.getAllTasks();

        if (tasks.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your list is empty! Enjoy your free time.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0));
            taskListPanel.add(emptyLabel);
        } else {
            for (Task t : tasks) {
                taskListPanel.add(createTaskCard(t));
                taskListPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    private JPanel createTaskCard(Task t) {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(840, 100));
        card.setPreferredSize(new Dimension(840, 100));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, getPriorityColor(t.getPriority())),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Checkbox for Completion
        JCheckBox check = new JCheckBox();
        check.setSelected(t.isCompleted());
        check.setBackground(Color.WHITE);
        check.addActionListener(e -> {
            taskDAO.toggleComplete(t.getId(), check.isSelected());
            refreshData();
        });
        card.add(check, BorderLayout.WEST);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(t.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        if (t.isCompleted()) {
            titleLabel.setForeground(Color.LIGHT_GRAY);
        }
        
        JLabel descLabel = new JLabel(t.getDescription());
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(Color.GRAY);

        infoPanel.add(titleLabel);
        infoPanel.add(descLabel);

        // Actions Panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        actionsPanel.setBackground(Color.WHITE);

        JButton editBtn = new JButton("EDIT");
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        editBtn.addActionListener(e -> {
            TaskForm form = new TaskForm(t);
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    refreshData();
                }
            });
            form.setVisible(true);
        });

        JButton deleteBtn = new JButton("DELETE");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteBtn.setForeground(Color.RED);
        deleteBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Delete task?") == JOptionPane.YES_OPTION) {
                taskDAO.deleteTask(t.getId());
                refreshData();
            }
        });

        actionsPanel.add(editBtn);
        actionsPanel.add(deleteBtn);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionsPanel, BorderLayout.EAST);

        return card;
    }

    private Color getPriorityColor(String priority) {
        if (priority == null) return Color.GRAY;
        switch (priority.toUpperCase()) {
            case "HIGH": return Color.RED;
            case "MEDIUM": return Color.ORANGE;
            case "LOW": return Color.GREEN;
            default: return Color.GRAY;
        }
    }
}