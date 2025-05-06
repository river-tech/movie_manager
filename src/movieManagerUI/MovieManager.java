package movieManagerUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MovieManager extends JFrame {

    private final JComboBox<String> filmCbo = new JComboBox<>();
    private final JComboBox<String> timeCbo = new JComboBox<>();
    private final JPanel seatPanel = new JPanel(new GridLayout(5, 6, 5, 5));
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final List<JButton> seatButtons = new ArrayList<>(); // danh sách ghế
   

    // demo data
    private final Map<String, String[]> filmTimes = Map.of(
            "Avengers",     new String[]{"09:00", "13:00", "19:00"},
            "The Matrix",   new String[]{"10:00", "15:00", "20:00"}
    );

    // demo trạng thái ghế và danh sách người đặt
    private final Set<String> bookedSeats = new HashSet<>(Set.of("1-2", "2-3", "4-5"));
    private final List<String[]> bookedList = List.of(
            new String[]{"1", "Nguyễn Văn A", "1-2"},
            new String[]{"2", "Trần Thị B", "2-3"},
            new String[]{"3", "Lê C", "4-5"}
    );

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
        createSeatButtons();
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
        filmCbo.setModel(new DefaultComboBoxModel<>(filmTimes.keySet().toArray(String[]::new)));
        filmCbo.addActionListener(e -> reloadTimes());
        reloadTimes();
        
        addBtn.addActionListener(e -> {
			this.dispose();
			new NewMovie(null).setVisible(true);
		});
        editBtn.addActionListener(e -> {
			String selectedFilm = (String) filmCbo.getSelectedItem();
			if (selectedFilm != null) {
				this.dispose();
				new NewMovie(selectedFilm).setVisible(true);
			} else {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn phim để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		});
        backBtn.addActionListener(e -> {
            this.dispose();
            new Booking().setVisible(true);
        });
    }

    private void createSeatButtons() {
        seatButtons.clear();
        seatPanel.removeAll();

        for (int row = 1; row <= 5; row++) {
            for (int col = 1; col <= 6; col++) {
                String seatId = row + "-" + col;
                JButton btn = new JButton(seatId);
                btn.setEnabled(false); 
                seatButtons.add(btn);
                seatPanel.add(btn);
            }
        }
    }
    
    

    
    private void reloadTimes() {
        String film = (String) filmCbo.getSelectedItem();
        timeCbo.setModel(new DefaultComboBoxModel<>(filmTimes.getOrDefault(film, new String[]{})));
        timeCbo.setSelectedIndex(0);
       
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MovieManager().setVisible(true));
    }
}
