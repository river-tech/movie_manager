package movieManagerUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Booking extends JFrame {

    private static final Font UI_FONT = new Font("Tahoma", Font.PLAIN, 14);

    static {
        UIManager.put("Label.font",     UI_FONT);
        UIManager.put("TextField.font", UI_FONT);
        UIManager.put("ComboBox.font",  UI_FONT);
        UIManager.put("Button.font",    UI_FONT);
    }

    private final JComboBox<String> filmCbo = new JComboBox<>();
    private final JComboBox<String> timeCbo = new JComboBox<>();
    private final JTextField emailTxt  = new JTextField(20);
    private final JTextField phoneTxt  = new JTextField(20);
    private final JTextField nameTxt   = new JTextField(20);
    private final JButton    submitBtn = new JButton("Gửi");
    private final JButton    adminBtn  = new JButton("Tiếp tục với admin");

    private final JPanel seatPanel = new JPanel(new GridLayout(5, 6, 10, 10));
    private final List<JToggleButton> seatButtons = new ArrayList<>();

    private final Map<String, String[]> demoTimes = Map.of(
            "Avengers",     new String[]{"09:00", "13:00", "19:00"},
            "Oppenheimer",  new String[]{"10:30", "14:30", "20:30"},
            "Inside Out 2", new String[]{"08:00", "12:00", "18:00"}
    );

    public Booking() {
        setTitle("Booking");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        container.setBackground(Color.WHITE);
      
        
        container.add(row("Chọn phim:", filmCbo));
        container.add(row("Chọn suất chiếu:", timeCbo));
        container.add(row("Nhập email:", emailTxt));
        container.add(row("Nhập SĐT:", phoneTxt));
        container.add(row("Nhập tên:", nameTxt));

        container.add(Box.createVerticalStrut(20));
        
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelPanel.add(new JLabel("Chọn ghế:"));
        container.add(labelPanel);

        createSeats();  // tạo 30 ghế
        container.add(seatPanel);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        btnRow.add(submitBtn);
        btnRow.add(adminBtn);
        container.add(btnRow);
 
        add(container);

        filmCbo.setModel(new DefaultComboBoxModel<>(demoTimes.keySet().toArray(String[]::new)));
        filmCbo.addActionListener(e -> reloadTimes());
        reloadTimes();

     
        submitBtn.addActionListener(e -> {	
            if (validateInput()) {
                List<String> selectedSeats = getSelectedSeats();
                if (selectedSeats.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Bạn chưa chọn ghế!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(this,
                        "Đặt vé thành công!\nGhế: " + String.join(", ", selectedSeats),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearInput();
            }
        });

        adminBtn.addActionListener(e -> {
            this.dispose();
            new Login().setVisible(true);
        });
    }

    private JPanel row(String label, JComponent field) { // create a row with label and field
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.add(new JLabel(label));
        panel.add(field);
        return panel; 
    }

    public void createSeats() { // create 30 seats
        seatButtons.clear();
        seatPanel.removeAll();
        int rows = 5;
        int cols = 6;
        for (int i = 0; i < rows; i++) {
           for(int j = 0; j < cols; j++) {
				String seatName = String.valueOf((char) ('A' + i)) + (j + 1);
				JToggleButton seatBtn = new JToggleButton(seatName);
				seatBtn.setPreferredSize(new Dimension(50, 50));
				seatBtn.setFont(UI_FONT);
				seatBtn.setFocusPainted(false);
				seatButtons.add(seatBtn);
				seatPanel.add(seatBtn);
			}
        }
    }

    private List<String> getSelectedSeats() { // get selected seats
        List<String> selected = new ArrayList<>();
        for (JToggleButton btn : seatButtons) {
            if (btn.isSelected()) {
                selected.add(btn.getText());
            }
        }
        return selected;
    }

    private void reloadTimes() { // reload times based on selected film
        String film = (String) filmCbo.getSelectedItem();
        timeCbo.setModel(new DefaultComboBoxModel<>(demoTimes.get(film)));
        timeCbo.setSelectedIndex(0);
    }

    private boolean validateInput() { // validate input fields
        String name = nameTxt.getText().trim();
        String phone = phoneTxt.getText().trim();
        String email = emailTxt.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên và SĐT bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "SĐT phải gồm đúng 10 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!email.isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }


    private void clearInput() { // clear input fields and selected seats
        emailTxt.setText("");
        phoneTxt.setText("");
        nameTxt.setText("");
        for (JToggleButton btn : seatButtons) {
            btn.setSelected(false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Booking().setVisible(true));
    }
}
