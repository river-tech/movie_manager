package movieManagerUI;

import javax.swing.*;

import movieManagerData.MovieService;
import movieManagerData.MovieTimeService;
import movieManagerData.SeatTicketService;
import movieManagerModel.CreateTicketDTO;
import movieManagerModel.Movie;
import movieManagerModel.MovieTime;
import movieManagerModel.Seat;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Booking extends JFrame {

    private static final Font UI_FONT = new Font("Tahoma", Font.PLAIN, 14);
    MovieService service = new MovieService();
    MovieTimeService timeService = new MovieTimeService();
    SeatTicketService seatService = new SeatTicketService();
    
    static {
        UIManager.put("Label.font",     UI_FONT);
        UIManager.put("TextField.font", UI_FONT);
        UIManager.put("ComboBox.font",  UI_FONT); 
        UIManager.put("Button.font",    UI_FONT);
    }

    private final JComboBox<Movie> filmCbo = new JComboBox<>();
    private final JComboBox<MovieTime> timeCbo = new JComboBox<>();
    private final JTextField emailTxt  = new JTextField(20);
    private final JTextField phoneTxt  = new JTextField(20);
    private final JTextField nameTxt   = new JTextField(20);
    private final JButton    submitBtn = new JButton("Gửi");
    private final JButton    adminBtn  = new JButton("Tiếp tục với admin");

    private final JPanel seatPanel = new JPanel(new GridLayout(5, 6, 10, 10));
    private final List<JToggleButton> seatButtons = new ArrayList<>();

   
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

         // tạo 30 ghế
        container.add(seatPanel);
//        reloadTimes();

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        btnRow.add(submitBtn);
        btnRow.add(adminBtn);
        container.add(btnRow);
 
        add(container);
 
        List<Movie> movieList = service.showAllMovie();

        DefaultComboBoxModel<Movie> model = new DefaultComboBoxModel<>();
        for (Movie m : movieList) {
            model.addElement(m);
        }
        filmCbo.setModel(model); 
        filmCbo.setSelectedIndex(1);
        reloadTimes();
        filmCbo.addActionListener(e -> reloadTimes());
        timeCbo.addActionListener(e -> {
            MovieTime selected = (MovieTime) timeCbo.getSelectedItem();
            if (selected != null) {
                int movieTimeId = Integer.parseInt(selected.getId());
                createSeats(movieTimeId);
                
            } 
        });     

     
        submitBtn.addActionListener(e -> {	
            if (validateInput()) {
                List<String> selectedSeats = getSelectedSeats();
                if (selectedSeats.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Bạn chưa chọn ghế!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String name  = nameTxt.getText().trim();
                String phone = phoneTxt.getText().trim();
                String email = emailTxt.getText().trim();
                Movie selectedMovie = (Movie) filmCbo.getSelectedItem();
                MovieTime selectedTime = (MovieTime) timeCbo.getSelectedItem();

                String message = String.format("""
                        ❓ Xác nhận đặt vé:
                        
                        👤 Tên: %s
                        📞 SĐT: %s
                        📧 Email: %s
                        🎬 Phim: %s
                        🕒 Suất chiếu: %s
                        🎟 Ghế: %s
                        
                        Bạn có chắc chắn muốn đặt vé?
                        """, name, phone, email.isEmpty() ? "(Không có)" : email,
                             selectedMovie.getName(),
                             selectedTime.getTime().toString(), // hoặc định dạng nếu cần
                             String.join(", ", selectedSeats));

                int confirm = JOptionPane.showConfirmDialog(this, message, "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // TODO: Lưu vé vào DB ở đây
                	CreateTicketDTO dto = new CreateTicketDTO();
                	dto.setName(name);
                	dto.setPhone(phone);
                	dto.setEmail(email);
                	dto.setMovieTimeId(selectedTime.getId());
                	dto.setSeatIds(selectedSeats);
                	seatService.createNewTicket(dto);

                    JOptionPane.showMessageDialog(this,
                            "🎉 Đặt vé thành công!\nGhế: " + String.join(", ", selectedSeats),
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    clearInput();
                }
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

    public void createSeats(Integer movieTimeId) {
        seatButtons.clear();
        seatPanel.removeAll();

        List<Seat> allSeats = new ArrayList<>();

        // Tạo 30 ghế: 5 hàng (A-E), 6 cột (1-6)
        for (int i = 0; i < 5; i++) {
            for (int j = 1; j <= 6; j++) {
                Seat seat = new Seat();
                seat.setRow(i);         // A = 0, B = 1, ...
                seat.setCol(j);         // 1, 2, ..., 6
                seat.setId((i * 6 + j) + ""); // giả định id là số thứ tự
                allSeats.add(seat);
            }
        }

        // Danh sách ghế đã đặt
        List<Seat> reservedSeats = new ArrayList<>();
        if (movieTimeId != null && movieTimeId != -1) {
            reservedSeats = seatService.SeatReserved(movieTimeId); // ⬅ bạn cần triển khai hàm này
        }

        for (Seat seat : allSeats) {
        	String seatName = String.valueOf((char) ('A' + seat.getRow())) + seat.getCol();
            JToggleButton seatBtn = new JToggleButton(seatName);
            seatBtn.setPreferredSize(new Dimension(50, 50));
            seatBtn.setFont(UI_FONT);
            seatBtn.setFocusPainted(false);

            // Kiểm tra xem có bị đặt rồi không
            boolean isReserved = reservedSeats.stream()
                .anyMatch(rs -> rs.getRow() == seat.getRow() && rs.getCol() == seat.getCol());

            seatBtn.setEnabled(!isReserved);
            seatBtn.putClientProperty("seatId", seat.getId());

            if (isReserved) {
                seatBtn.setBackground(Color.LIGHT_GRAY);
            }

            seatButtons.add(seatBtn);
            seatPanel.add(seatBtn);
        }

        seatPanel.revalidate();
        seatPanel.repaint();
    }




    private List<String> getSelectedSeats() {
        List<String> selectedSeatIds = new ArrayList<>();
        for (JToggleButton btn : seatButtons) {
            if (btn.isSelected()) {
                String seatId = (String) btn.getClientProperty("seatId");
                if (seatId != null) {
                    selectedSeatIds.add(seatId);
                }
            }
        }
        return selectedSeatIds;
    }
 

    private void reloadTimes() {
        Movie selectedMovie = (Movie) filmCbo.getSelectedItem();
        if (selectedMovie == null) return;

        int movieId = Integer.parseInt(selectedMovie.getId());
        List<MovieTime> times = timeService.showAllMovieTimeByMovie(movieId);

        DefaultComboBoxModel<MovieTime> timeModel = new DefaultComboBoxModel<>();
        for (MovieTime mt : times) {
            timeModel.addElement(mt);
        }

        timeCbo.setModel(timeModel);

        if (timeModel.getSize() > 0) {
            timeCbo.setSelectedIndex(0);

            // 🆕 Gọi createSeats với suất chiếu đầu tiên
            MovieTime firstTime = (MovieTime) timeModel.getElementAt(0);
            createSeats(Integer.parseInt(firstTime.getId()));
            
        } else {
            createSeats(null);
            JOptionPane.showMessageDialog(this, "⛔ Phim này hiện không có suất chiếu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
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
