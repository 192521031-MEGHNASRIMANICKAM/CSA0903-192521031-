import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ConverterFrameListener extends JFrame implements ActionListener {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    public ConverterFrameListener() {
        super("Converter - Frame Listener");
        setLayout(new FlowLayout());
        JButton button = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target); add(button); add(result);
        button.addActionListener(this);
        amount.addActionListener(this);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });
        pack(); setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @Override public void actionPerformed(ActionEvent e) { convert(); }

    private void convert() {
        try {
            double value = Double.parseDouble(amount.getText().trim());
            if (value < 0) throw new NumberFormatException();
            String currency = (String) target.getSelectedItem();
            double rate = currency.equals("USD") ? 0.012 : 0.011;
            String answer = String.format("%.2f %s", value * rate, currency);
            result.setText(answer);
            JOptionPane.showMessageDialog(this, answer);
        } catch (NumberFormatException ex) {
            result.setText("Invalid amount");
            JOptionPane.showMessageDialog(this, "Invalid amount");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConverterFrameListener().setVisible(true));
    }
}
