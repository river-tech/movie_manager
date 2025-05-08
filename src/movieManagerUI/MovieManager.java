package movieManagerUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import movieManagerData.MovieService;
import movieManagerData.MovieTimeService;
import movieManagerData.SeatTicketService;
import movieManagerModel.Movie;
import movieManagerModel.MovieTime;
import movieManagerModel.Seat;
import movieManagerModel.showticketDTO;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MovieManager extends JFrame {

    private final JComboBox<Movie> filmCbo = new JComboBox<>();
    private final JComboBox<MovieTime> timeCbo = new JComboBox<>();
    private final JPanel seatPanel = new JPanel(new GridLayout(5, 6, 5, 5));
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final List<JButton> seatButtons = new ArrayList<>(); // danh sách ghế
    MovieService service = new MovieService();
    MovieTimeService timeService = new MovieTimeService();
    SeatTicketService seatService = new SeatTicketService();

    public MovieManager() {
        setTitle("MovieManager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.add(new JLabel("Chọn phim"));
        topPanel.add(filmCbo);
        topPanel.add(new JLabel("Chọn suất chiếu"));
        topPanel.add(timeCbo);
        add(topPanel, BorderLayout.NORTH);

        // Center UI: Ghế + Bảng
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
 
        // Ghế
        JPanel leftPanel = new JPanel(new BorderLayout());
        seatPanel.setPreferredSize(new Dimension(240, 200));
//        createSeatButtons();
        leftPanel.add(seatPanel, BorderLayout.CENTER);
        centerPanel.add(leftPanel);

        // Bảng người đặt
        String[] colNames = {"STT", "Người đặt", "Ghế"}; 
        tableModel = new DefaultTableModel(colNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER); 

        // Bottom: Nút
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        JButton addBtn = new JButton("Thêm mới");
        JButton editBtn = new JButton("Sửa");
        JButton delBtn = new JButton("Xóa");
        JButton backBtn = new JButton("Trở về");

        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn); 
        bottomPanel.add(delBtn);
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // dữ liệu mẫu
        List<Movie> movieList = service.showAllMovie();
       
        DefaultComboBoxModel<Movie> model = new DefaultComboBoxModel<>();
        for (Movie m : movieList) {
            model.addElement(m);
        }
        filmCbo.setModel(model); 
        filmCbo.addActionListener(e -> reloadTimes());
        timeCbo.addActionListener(e -> {
            MovieTime selectedTime = (MovieTime) timeCbo.getSelectedItem();
            if (selectedTime != null) {
                createSeats(Integer.parseInt(selectedTime.getId()));
            }
            tableModel.setRowCount(0);
            List<showticketDTO> ticketLists = seatService.getAllTicketByMovieTime(Integer.parseInt(selectedTime.getId()));
            int index = 1;
            for (showticketDTO dto : ticketLists) {
                String seatNames = dto.getSeats().stream()
                        .map(seat -> String.valueOf((char) ('A' + seat.getRow())) + seat.getCol())
                        .reduce((s1, s2) -> s1 + ", " + s2)
                        .orElse("");

                // 🔍 Log kiểm tra dữ liệu từng vé 
                System.out.println("🧾 Vé #" + index);
                System.out.println("👤 Người đặt: " + dto.getUserName());
                System.out.println("🎬 Phim: " + dto.getMovieName());
                System.out.println("🕒 Giờ chiếu: " + dto.getMovieTime());
                System.out.println("💺 Ghế: " + seatNames);
                System.out.println("---------------------------");

                tableModel.addRow(new Object[]{
                    index++,
                    dto.getUserName(),
                    seatNames
                });
            }

            
        });

        reloadTimes();
        
        addBtn.addActionListener(e -> {
			this.dispose();
			new NewMovie(null).setVisible(true);
		});
        editBtn.addActionListener(e -> {
			Movie selectedFilm = (Movie) filmCbo.getSelectedItem();
			if (selectedFilm.getId() != null) {
				this.dispose();
				new NewMovie(selectedFilm.getId()).setVisible(true);
			} else {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn phim để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			} 
		});
        delBtn.addActionListener(e -> {
            Movie selectedFilm = (Movie) filmCbo.getSelectedItem();
            if (selectedFilm == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phim để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa phim \"" + selectedFilm.getName() + "\"?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = service.deleteMovie(Integer.parseInt(selectedFilm.getId()));
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "✅ Xóa phim thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Không thể xóa phim!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

                // ✅ Reload movie list
                List<Movie> updatedMovies = service.showAllMovie();
                DefaultComboBoxModel<Movie> newModel = new DefaultComboBoxModel<>();
                for (Movie m : updatedMovies) {
                    newModel.addElement(m);
                }
                filmCbo.setModel(newModel);

                // Gọi lại reloadTimes nếu có phim mới được chọn
                if (newModel.getSize() > 0) {
                    filmCbo.setSelectedIndex(0); 
                    reloadTimes();
                } else {
                    timeCbo.setModel(new DefaultComboBoxModel<>());
                    seatPanel.removeAll();
                    seatPanel.repaint();
                    ((DefaultTableModel) table.getModel()).setRowCount(0);
                }
            }
        });

        backBtn.addActionListener(e -> {
            this.dispose();
            new Booking().setVisible(true);
        });
    }

    public void createSeats(Integer movieTimeId) {
        seatButtons.clear();
        seatPanel.removeAll();

        List<Seat> allSeats = new ArrayList<>();

        // Tạo 30 ghế: 5 hàng (A-E), 6 cột (1-6)
        for (int i = 0; i < 5; i++) {
            for (int j = 1; j <= 6; j++) {
                Seat seat = new Seat();
                seat.setRow(i); // A = 0, B = 1, ...
                seat.setCol(j); // 1, 2, ..., 6
                seat.setId((i * 6 + j) + "");
                allSeats.add(seat);
            }
        }

        // Lấy danh sách ghế đã đặt
        List<Seat> reservedSeats = movieTimeId != null && movieTimeId != -1
                ? seatService.SeatReserved(movieTimeId)
                : new ArrayList<>();

        for (Seat seat : allSeats) {
            String seatName = String.valueOf((char) ('A' + seat.getRow())) + seat.getCol();
            JButton seatBtn = new JButton(seatName);
            seatBtn.setPreferredSize(new Dimension(50, 50)); 
            seatBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
            seatBtn.setEnabled(false); // không cho bấm

            boolean isReserved = reservedSeats.stream()
                    .anyMatch(rs -> rs.getRow() == seat.getRow() && rs.getCol() == seat.getCol());

            seatBtn.setBackground(isReserved ? Color.LIGHT_GRAY : Color.white);
            seatBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            seatPanel.add(seatBtn);
        }

        seatPanel.revalidate();
        seatPanel.repaint();
    }


    private void reloadTimes() {
        Movie selectedFilm = (Movie) filmCbo.getSelectedItem();
        if (selectedFilm == null) return;
      
        
        int movieId = Integer.parseInt(selectedFilm.getId());
        List<MovieTime> times = timeService.showAllMovieTimeByMovie(movieId);
        
    

        DefaultComboBoxModel<MovieTime> timeModel = new DefaultComboBoxModel<>();
        for (MovieTime mt : times) {
            timeModel.addElement(mt);
        }
        timeCbo.setModel(timeModel);
        if (timeModel.getSize() > 0) {
            timeCbo.setSelectedIndex(0);
      
            MovieTime selectedTime = (MovieTime) timeCbo.getSelectedItem();
            if (selectedTime != null) {	
                createSeats(Integer.parseInt(selectedTime.getId()));
            }
        } else {
            createSeats(null); 
            JOptionPane.showMessageDialog(this, "⛔ Phim này hiện không có suất chiếu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MovieManager movieManager = new MovieManager();
			movieManager.setVisible(true);
		});
	}
}
