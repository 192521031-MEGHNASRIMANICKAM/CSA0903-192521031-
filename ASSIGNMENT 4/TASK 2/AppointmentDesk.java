import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class AppointmentDesk extends JFrame {
    private final JTextField patientId = new JTextField(12);
    private final JComboBox<String> department = new JComboBox<>(
            new String[]{"Cardiology", "Neurology", "Orthopedics"});
    private final JComboBox<String> doctor = new JComboBox<>();
    private final JComboBox<String> slot = new JComboBox<>(
            new String[]{"09:00 AM", "10:00 AM", "11:00 AM", "02:00 PM"});
    private final JLabel status = new JLabel("Enter details and book an appointment.");
    private final Map<String, java.util.List<String>> doctors = new HashMap<>();
    private boolean bookingInProgress = false;

    public AppointmentDesk() {
        super("Hospital Outpatient Appointment Desk");
        doctors.put("Cardiology", Arrays.asList("Dr. Kumar", "Dr. Priya"));
        doctors.put("Neurology", Arrays.asList("Dr. Ravi", "Dr. Meena"));
        doctors.put("Orthopedics", Arrays.asList("Dr. Arun", "Dr. Divya"));

        setLayout(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Patient ID:")); form.add(patientId);
        form.add(new JLabel("Department:")); form.add(department);
        form.add(new JLabel("Doctor:")); form.add(doctor);
        form.add(new JLabel("Time slot:")); form.add(slot);
        JButton book = new JButton("Book Appointment");
        form.add(new JLabel("")); form.add(book);
        add(form, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        department.addActionListener(e -> refreshDoctors());
        book.addActionListener(e -> bookAppointment());
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                if (bookingInProgress) {
                    int choice = JOptionPane.showConfirmDialog(AppointmentDesk.this,
                            "A booking is in progress. Close anyway?", "Confirm",
                            JOptionPane.YES_NO_OPTION);
                    if (choice != JOptionPane.YES_OPTION) return;
                }
                dispose();
            }
        });
        refreshDoctors();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void refreshDoctors() {
        String selected = (String) department.getSelectedItem();
        doctor.removeAllItems();
        for (String name : doctors.getOrDefault(selected, Collections.emptyList())) {
            doctor.addItem(name);
        }
    }

    private void bookAppointment() {
        if (patientId.getText().trim().isEmpty()) {
            status.setText("Error: Patient ID is required.");
            return;
        }
        if (doctor.getSelectedItem() == null || slot.getSelectedItem() == null) {
            status.setText("Error: Select a doctor and time slot.");
            return;
        }
        bookingInProgress = true;
        try {
            String message = "Appointment booked for Patient " + patientId.getText().trim()
                    + " with " + doctor.getSelectedItem() + " (" + department.getSelectedItem()
                    + ") at " + slot.getSelectedItem();
            status.setText(message);
            JOptionPane.showMessageDialog(this, message, "Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);
        } finally {
            bookingInProgress = false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppointmentDesk().setVisible(true));
    }
}
