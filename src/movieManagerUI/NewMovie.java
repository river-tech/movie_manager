package movieManagerUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import movieManagerData.MovieService;
import movieManagerModel.Movie;
import movieManagerModel.ShowMovieDTO;

import java.sql.Timestamp;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewMovie extends JFrame {

    private final JTextField filmNameField = new JTextField(20);
    private final JSpinner timeSpinner;
    private final JButton addTimeBtn = new JButton("Thêm");
    private final JButton saveBtn = new JButton("Cập nhật");
    private final JButton cancelBtn = new JButton("Hủy");

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final boolean isEditMode;
    private final String editingFilmId;

    private final List<String> showtimes = new ArrayList<>();
    private final MovieService movieService = new MovieService();

    public NewMovie() {
        this(null);
    }

    public NewMovie(String filmIdToEdit) {
        this.isEditMode = (filmIdToEdit != null);
        this.editingFilmId = filmIdToEdit;

        setTitle(isEditMode ? "Sửa phim" : "Tạo phim mới");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin phim"));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameRow.add(new JLabel("Tên phim"));
        nameRow.add(filmNameField); 
        formPanel.add(nameRow);

        JPanel timeRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeRow.add(new JLabel("Ngày giờ chiếu"));

        timeSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(timeSpinner, "dd/MM/yyyy HH:mm");
        timeSpinner.setEditor(dateEditor);
        timeSpinner.setPreferredSize(new Dimension(160, 25));
        timeRow.add(timeSpinner);
        timeRow.add(addTimeBtn);
        formPanel.add(timeRow);

        add(formPanel, BorderLayout.NORTH);

        String[] columns = {"StartTime", "Remove"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };

        table.getColumn("Remove").setCellRenderer(new ButtonRenderer());
        table.getColumn("Remove").setCellEditor(new ButtonEditor(new JCheckBox(), tableModel, showtimes));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(550, 250));
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        if (isEditMode) {
            ShowMovieDTO dto = movieService.ShowMovieById(Integer.parseInt(editingFilmId));
            if (dto != null) {
                filmNameField.setText(dto.getName());
                // Cho phép sửa tên phim
                filmNameField.setEditable(true);

                showtimes.clear();
                tableModel.setRowCount(0);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (Timestamp ts : dto.getMovieTimes()) {
                    String formatted = formatter.format(ts);
                    showtimes.add(formatted);
                    tableModel.addRow(new Object[]{formatted, "Xóa"});
                }
            }
        }

        addTimeBtn.addActionListener(e -> {
            Date date = (Date) timeSpinner.getValue();
            String formatted = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
            if (!showtimes.contains(formatted)) {
                showtimes.add(formatted);
                tableModel.addRow(new Object[]{formatted, "Xóa"});
                JOptionPane.showMessageDialog(this, "Đã thêm suất chiếu: " + formatted);
            } else {
                JOptionPane.showMessageDialog(this, "Suất chiếu này đã tồn tại!");
            }
        });

        cancelBtn.addActionListener(e -> {
            this.dispose();
            new MovieManager().setVisible(true);
        });

        saveBtn.addActionListener(e -> {
            String name = filmNameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên phim.");
                return;
            }
            if (showtimes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Thêm ít nhất 1 giờ chiếu.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn " + (isEditMode ? "cập nhật" : "tạo") + " phim này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (isEditMode) {
                    updateMovie(name);
                } else {
                    saveNewMovie(name);
                }
                new MovieManager().setVisible(true);
                dispose();
            }
        });
    }

    private void saveNewMovie(String name) {
        List<Timestamp> timestamps = new ArrayList<>();
        for (String timeStr : showtimes) {
            try {
                Timestamp ts = Timestamp.valueOf(
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(timeStr)
                    )
                );
                timestamps.add(ts);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean success = movieService.createNewMovie(name, timestamps);
        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Đã thêm phim: " + name);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi thêm phim!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMovie(String name) {
        List<Timestamp> timestamps = new ArrayList<>();
        for (String timeStr : showtimes) {
            try {
                Timestamp ts = Timestamp.valueOf(
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(timeStr)
                    )
                );
                timestamps.add(ts);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int movieId = Integer.parseInt(editingFilmId);
        boolean success = movieService.editMovie(movieId, name, timestamps);
        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Đã cập nhật phim: " + name);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi cập nhật phim!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setText("Xóa");
            setFont(new Font("Tahoma", Font.PLAIN, 11));
            setMargin(new Insets(2, 4, 2, 4));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private static class ButtonEditor extends DefaultCellEditor {
        private final JButton button = new JButton("Xóa");
        private final DefaultTableModel model;
        private final List<String> showtimes;
        private int row;

        public ButtonEditor(JCheckBox checkBox, DefaultTableModel model, List<String> showtimes) {
            super(checkBox);
            this.model = model;
            this.showtimes = showtimes;
            button.setFont(new Font("Tahoma", Font.PLAIN, 11));
            button.setMargin(new Insets(2, 4, 2, 4));
            button.addActionListener(e -> {
                if (row >= 0 && row < model.getRowCount()) {
                    String removedTime = (String) model.getValueAt(row, 0);
                    showtimes.remove(removedTime);
                    model.removeRow(row);
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }
    }
}
