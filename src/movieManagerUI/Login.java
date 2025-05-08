package movieManagerUI;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {

    private static final Font UI_FONT = new Font("Tahoma", Font.PLAIN, 14);

    static {
        UIManager.put("Label.font",     UI_FONT);
        UIManager.put("TextField.font", UI_FONT);
        UIManager.put("PasswordField.font", UI_FONT);
        UIManager.put("Button.font",    UI_FONT);
    }

    private final JTextField usernameTxt = new JTextField(20);
    private final JPasswordField passwordTxt = new JPasswordField(20);
    private final JButton loginBtn = new JButton("Đăng nhập");
    
    private final String validUsername = "admin";
    private final String validPassword = "admin";
    
    public Login() {
        setTitle("Đăng nhập hệ thống");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 250);
        setLocationRelativeTo(null);

        GridBagLayout layout = new GridBagLayout();
        layout.columnWeights = new double[]{0, 1};
        setLayout(layout);

        add(new JLabel("Tài khoản:"), gbc(0, 0));
        add(usernameTxt,              gbcFill(1, 0));

        add(new JLabel("Mật khẩu:"),  gbc(0, 1));
        add(passwordTxt,              gbcFill(1, 1));

        GridBagConstraints btnGbc = gbc(0, 2);
        btnGbc.gridwidth = 2;
        btnGbc.anchor = GridBagConstraints.CENTER;
        add(loginBtn, btnGbc);

        loginBtn.addActionListener(e -> {
           String username = new String(usernameTxt.getText());
           String password = new String(passwordTxt.getPassword());
           if (username.equals(validUsername) && password.equals(validPassword)) {
			   JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
			   // Open the main application window here
			   this.dispose();
			   new MovieManager().setVisible(true);
			   
		   } else {
			   JOptionPane.showMessageDialog(this, "Tài khoản hoặc mật khẩu không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		   }
        });
    }

    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.insets = new Insets(12, 12, 12, 12);
        c.anchor = GridBagConstraints.WEST;
        return c;
    }

    private GridBagConstraints gbcFill(int x, int y) {
        GridBagConstraints c = gbc(x, y);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        return c;
    }


}
