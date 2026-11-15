import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LibrarySearchScreen extends JFrame {
    private final JTextField searchField = new JTextField(20);
    private final JComboBox<String> category = new JComboBox<>(
            new String[]{"All", "Book", "Journal", "Magazine"});
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Title", "Category", "Availability"}, 0);
    private final JTable results = new JTable(model);
    private final JLabel status = new JLabel("Ready.");

    public LibrarySearchScreen() {
        super("Campus Library Search");
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("Campus Library Search", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(category);
        JButton search = new JButton("Search");
        searchPanel.add(search);
        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(results), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        JPanel actions = new JPanel(new FlowLayout());
        JButton prev = new JButton("Prev");
        JButton next = new JButton("Next");
        JButton reserve = new JButton("Reserve");
        actions.add(prev); actions.add(next); actions.add(reserve);
        bottom.add(actions, BorderLayout.NORTH);
        bottom.add(status, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        search.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());
        prev.addActionListener(e -> status.setText("Previous page selected."));
        next.addActionListener(e -> status.setText("Next page selected."));
        reserve.addActionListener(e -> status.setText(
                results.getSelectedRow() >= 0 ? "Reservation request recorded." : "Select a result first."));

        model.addRow(new Object[]{"B101", "Introduction to Java", "Book", "Available"});
        model.addRow(new Object[]{"J205", "Science Review", "Journal", "Issued"});
        model.addRow(new Object[]{"M310", "Campus Monthly", "Magazine", "Available"});

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(650, 420);
        setLocationRelativeTo(null);
    }

    private void doSearch() {
        String query = searchField.getText().trim().toLowerCase();
        String selectedCategory = (String) category.getSelectedItem();
        model.setRowCount(0);
        Object[][] data = {
            {"B101", "Introduction to Java", "Book", "Available"},
            {"J205", "Science Review", "Journal", "Issued"},
            {"M310", "Campus Monthly", "Magazine", "Available"}
        };
        int found = 0;
        for (Object[] row : data) {
            boolean textMatch = query.isEmpty() || row[1].toString().toLowerCase().contains(query);
            boolean categoryMatch = "All".equals(selectedCategory) || row[2].equals(selectedCategory);
            if (textMatch && categoryMatch) {
                model.addRow(row);
                found++;
            }
        }
        status.setText(found + " result(s) found.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearchScreen().setVisible(true));
    }
}
