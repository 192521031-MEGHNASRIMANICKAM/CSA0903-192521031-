import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class FeeService {
    public String pay(String regNo, String amountText) {
        if (regNo == null || regNo.trim().isEmpty()) return "Enter registration number.";
        double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (Exception ex) {
            return "Enter a valid amount.";
        }
        if (amount <= 0) return "Amount must be greater than zero.";
        return String.format("Payment successful: Rs. %.2f", amount);
    }

    public String receipt(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) return "Enter registration number first.";
        return "Receipt for registration number: " + regNo.trim();
    }
}

public class FeeWindow extends JFrame {
    private final JTextField regNo = new JTextField(15);
    private final JTextField amount = new JTextField(15);
    private final JLabel status = new JLabel("Enter fee details.");
    private final FeeService service = new FeeService();

    public FeeWindow() {
        super("Student Fee Payment");
        setLayout(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.add(new JLabel("Registration No:"));
        form.add(regNo);
        form.add(new JLabel("Amount:"));
        form.add(amount);

        JButton pay = new JButton("Pay");
        JButton clear = new JButton("Clear");
        JButton receipt = new JButton("Receipt");
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(pay);
        buttons.add(clear);
        buttons.add(receipt);

        add(form, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        pay.addActionListener(e -> status.setText(service.pay(regNo.getText(), amount.getText())));
        clear.addActionListener(e -> {
            regNo.setText("");
            amount.setText("");
            status.setText("Fields cleared.");
        });
        receipt.addActionListener(e -> status.setText(service.receipt(regNo.getText())));

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(FeeWindow.this, "Close the window?",
                        "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FeeWindow().setVisible(true));
    }
}
