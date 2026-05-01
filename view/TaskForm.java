package view;

import dao.TaskDAO;
import model.Task;
import javax.swing.*;
import java.awt.*;

public class TaskForm extends JFrame {
    private Task existingTask = null;
    private JTextField titleField;
    private JTextArea descArea;
    private JComboBox<String> priorityBox;

    public TaskForm() { this(null); }

    public TaskForm(Task task) {
        this.existingTask = task;
        setTitle(existingTask == null ? "ADD NEW TASK" : "EDIT TASK");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel header = new JLabel(existingTask == null ? "CREATE NEW TASK" : "UPDATE TASK");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        fieldsPanel.setBackground(Color.WHITE);

        titleField = new JTextField();
        titleField.setBorder(BorderFactory.createTitledBorder("Task Title"));
        
        descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createTitledBorder("Description"));

        priorityBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        priorityBox.setBorder(BorderFactory.createTitledBorder("Priority"));

        if (existingTask != null) {
            titleField.setText(existingTask.getTitle());
            descArea.setText(existingTask.getDescription());
            priorityBox.setSelectedItem(existingTask.getPriority());
        }

        fieldsPanel.add(titleField);
        fieldsPanel.add(descScroll);
        fieldsPanel.add(priorityBox);

        JButton saveBtn = new JButton(existingTask == null ? "SAVE TASK" : "UPDATE TASK");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setBackground(new Color(63, 81, 181));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(0, 45));
        saveBtn.addActionListener(e -> {
            if (titleField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title is required");
                return;
            }

            Task t = (existingTask == null) ? new Task() : existingTask;
            t.setTitle(titleField.getText());
            t.setDescription(descArea.getText());
            t.setPriority((String) priorityBox.getSelectedItem());
            if (existingTask == null) t.setCompleted(false);

            TaskDAO dao = new TaskDAO();
            if (existingTask == null) {
                dao.addTask(t);
            } else {
                dao.updateTask(t);
            }
            dispose();
        });

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        mainPanel.add(saveBtn, BorderLayout.SOUTH);

        add(mainPanel);
    }
}
