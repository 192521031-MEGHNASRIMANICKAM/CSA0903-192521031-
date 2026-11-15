import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EventRegistrationApp extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/campus_events";
    private static final String USER = "root";
    private static final String PASSWORD = "YOUR_PASSWORD";

    private final JTextField studentId = new JTextField(12);
    private final JTextField studentName = new JTextField(12);
    private final JComboBox<String> event = new JComboBox<>(
            new String[]{"Cultural Fest", "Tech Symposium", "Sports Day"});
    private final JComboBox<String> filter = new JComboBox<>(
            new String[]{"All", "Cultural Fest", "Tech Symposium", "Sports Day"});
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Student ID", "Student Name", "Event", "Registered At"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private int offset = 0;
    private static final int PAGE_SIZE = 25;

    public EventRegistrationApp() {
        super("Campus Event Registration");
        setLayout(new BorderLayout(8, 8));

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshItem = new JMenuItem("Refresh");
        JMenuItem exitItem = new JMenuItem("Exit");
        refreshItem.addActionListener(e -> loadPage(0));
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(refreshItem); fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel registration = new JPanel(new FlowLayout(FlowLayout.LEFT));
        registration.add(new JLabel("Student ID:")); registration.add(studentId);
        registration.add(new JLabel("Name:")); registration.add(studentName);
        registration.add(new JLabel("Event:")); registration.add(event);
        JButton register = new JButton("Register");
        registration.add(register);

        JPanel filtering = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtering.add(new JLabel("Filter event:")); filtering.add(filter);
        JButton applyFilter = new JButton("Apply");
        JButton export = new JButton("Export CSV");
        JButton previous = new JButton("Previous");
        JButton next = new JButton("Next");
        filtering.add(applyFilter); filtering.add(previous); filtering.add(next); filtering.add(export);

        JPanel north = new JPanel(new BorderLayout());
        north.add(registration, BorderLayout.NORTH);
        north.add(filtering, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        register.addActionListener(e -> registerStudent());
        applyFilter.addActionListener(e -> loadPage(0));
        previous.addActionListener(e -> loadPage(Math.max(0, offset - PAGE_SIZE)));
        next.addActionListener(e -> loadPage(offset + PAGE_SIZE));
        export.addActionListener(e -> exportCsv());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);
        loadPage(0);
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void registerStudent() {
        String sid = studentId.getText().trim();
        String name = studentName.getText().trim();
        String eventName = (String) event.getSelectedItem();
        if (sid.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID and name are required.");
            return;
        }

        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() {
                String sql = "INSERT INTO registrations(student_id, student_name, event_name) VALUES(?,?,?)";
                try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, sid);
                    ps.setString(2, name);
                    ps.setString(3, eventName);
                    ps.executeUpdate();
                    return "Registration successful.";
                } catch (SQLIntegrityConstraintViolationException ex) {
                    return "Duplicate: this student is already registered for this event.";
                } catch (SQLException ex) {
                    return "Database error: " + ex.getMessage();
                }
            }
            @Override protected void done() {
                try {
                    String message = get();
                    JOptionPane.showMessageDialog(EventRegistrationApp.this, message);
                    if (message.startsWith("Registration successful")) loadPage(0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(EventRegistrationApp.this, ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadPage(int newOffset) {
        offset = newOffset;
        String selected = (String) filter.getSelectedItem();
        new SwingWorker<Vector<Object[]>, Void>() {
            @Override protected Vector<Object[]> doInBackground() {
                Vector<Object[]> rows = new Vector<>();
                boolean all = "All".equals(selected);
                String sql = all
                    ? "SELECT id, student_id, student_name, event_name, registered_at FROM registrations ORDER BY id LIMIT ? OFFSET ?"
                    : "SELECT id, student_id, student_name, event_name, registered_at FROM registrations WHERE event_name=? ORDER BY id LIMIT ? OFFSET ?";
                try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
                    int p = 1;
                    if (!all) ps.setString(p++, selected);
                    ps.setInt(p++, PAGE_SIZE);
                    ps.setInt(p, offset);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            rows.add(new Object[]{rs.getInt("id"), rs.getString("student_id"),
                                rs.getString("student_name"), rs.getString("event_name"),
                                rs.getTimestamp("registered_at")});
                        }
                    }
                } catch (SQLException ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            EventRegistrationApp.this, "Database error: " + ex.getMessage()));
                }
                return rows;
            }
            @Override protected void done() {
                try {
                    model.setRowCount(0);
                    for (Object[] row : get()) model.addRow(row);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(EventRegistrationApp.this, ex.getMessage());
                }
            }
        }.execute();
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("registrations.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (java.io.PrintWriter out = new java.io.PrintWriter(chooser.getSelectedFile())) {
            for (int c = 0; c < model.getColumnCount(); c++) {
                if (c > 0) out.print(",");
                out.print(model.getColumnName(c));
            }
            out.println();
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    if (c > 0) out.print(",");
                    String value = String.valueOf(model.getValueAt(r, c)).replace("\"", "\"\"");
                    out.print("\"" + value + "\"");
                }
                out.println();
            }
            JOptionPane.showMessageDialog(this, "Current page exported.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EventRegistrationApp().setVisible(true));
    }
}
