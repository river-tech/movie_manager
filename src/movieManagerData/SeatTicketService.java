package movieManagerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import movieManagerModel.CreateTicketDTO;
import movieManagerModel.MovieTime;
import movieManagerModel.Seat;
import movieManagerModel.showticketDTO;


public class SeatTicketService {
	public List<Seat> SeatAvailable(int movieTimeId) {
	    List<Seat> seats = new ArrayList<>();

	    String sql = """
	        SELECT s.id, s._row, s._col
	        FROM Seat s
	        WHERE NOT EXISTS (
	            SELECT 1
	            FROM TicketSeat ts
	            JOIN Ticket t ON ts.ticketId = t.id
	            WHERE ts.seatId = s.id AND t.movieTimeId = ?
	        ) 
	    """;

	    try (Connection conn = DatabaseService.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, movieTimeId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Seat seat = new Seat();
	                seat.setId(String.valueOf(rs.getInt("id")));
	                seat.setRow(rs.getInt("_row"));
	                seat.setCol(rs.getInt("_col"));
	                seats.add(seat);
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return seats;
	}

	
	public List<showticketDTO> getAllTicketByMovieTime(int movieTimeId){
		List<showticketDTO> showticket = new ArrayList<>();
		 String sql = """
			        SELECT t.id AS ticketId,
			               u.name AS userName,
			               m.name AS movieName,
			               mt.time AS movieTime,
			               s.id AS seatId, s._row, s._col
			        FROM Ticket t
			        JOIN User u ON t.userId = u.id
			        JOIN MovieTime mt ON t.movieTimeId = mt.id
			        JOIN Movie m ON mt.movieId = m.id
			        JOIN TicketSeat ts ON ts.ticketId = t.id
			        JOIN Seat s ON ts.seatId = s.id
			        WHERE t.movieTimeId = ?
			        ORDER BY t.id
			    """;
		  try (Connection conn = DatabaseService.getConnection();
			         PreparedStatement stmt = conn.prepareStatement(sql)) {

			        stmt.setInt(1, movieTimeId);
			        ResultSet rs = stmt.executeQuery();

			        Map<String, showticketDTO> ticketMap = new LinkedHashMap();

			        while (rs.next()) {
			            String ticketId = rs.getString("ticketId");

			            showticketDTO dto = ticketMap.get(ticketId);
			            if (dto == null) {
			                dto = new showticketDTO();
			                dto.setUserName(rs.getString("userName"));
			                dto.setMovieName(rs.getString("movieName"));
			                dto.setMovieTime(rs.getTimestamp("movieTime"));
			                dto.setSeats(new ArrayList<>());
			                ticketMap.put(ticketId, dto);
			            }

			            Seat seat = new Seat();
			            seat.setId(rs.getString("seatId"));
			            seat.setRow(rs.getInt("_row"));
			            seat.setCol(rs.getInt("_col"));

			            dto.getSeats().add(seat);
			        }

			        showticket.addAll(ticketMap.values());

			    } catch (SQLException e) {
			        e.printStackTrace();
			    }
		return showticket;
	}
	
	public boolean createNewTicket(CreateTicketDTO dto) {
	    String insertUserSQL = "INSERT INTO User (name, phone, email) VALUES (?, ?, ?)";
	    String insertTicketSQL = "INSERT INTO Ticket (userId, movieTimeId) VALUES (?, ?)";
	    String insertTicketSeatSQL = "INSERT INTO TicketSeat (ticketId, seatId) VALUES (?, ?)";

	    Connection conn = null;
	    PreparedStatement userStmt = null;
	    PreparedStatement ticketStmt = null;
	    PreparedStatement seatStmt = null;
	    ResultSet generatedKeys = null;

	    try {
	        conn = DatabaseService.getConnection();
	        conn.setAutoCommit(false);

	        // 1. Thêm người dùng mới
	        userStmt = conn.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS);
	        userStmt.setString(1, dto.getName());
	        userStmt.setString(2, dto.getPhone());
	        userStmt.setString(3, dto.getEmail());
	        userStmt.executeUpdate();

	        generatedKeys = userStmt.getGeneratedKeys();
	        if (!generatedKeys.next()) throw new SQLException("Không thể tạo user mới");
	        int userId = generatedKeys.getInt(1);
	        generatedKeys.close();

	        // 2. Tạo ticket
	        ticketStmt = conn.prepareStatement(insertTicketSQL, Statement.RETURN_GENERATED_KEYS);
	        ticketStmt.setInt(1, userId);
	        ticketStmt.setInt(2, Integer.parseInt(dto.getMovieTimeId()));
	        ticketStmt.executeUpdate();

	        generatedKeys = ticketStmt.getGeneratedKeys();
	        if (!generatedKeys.next()) throw new SQLException("Không thể tạo ticket mới");
	        int ticketId = generatedKeys.getInt(1);
	        generatedKeys.close();

	        // 3. Gán các ghế vào ticket
	        seatStmt = conn.prepareStatement(insertTicketSeatSQL);
	        for (String seatId : dto.getSeatIds()) {
	            seatStmt.setInt(1, ticketId);
	            seatStmt.setInt(2, Integer.parseInt(seatId));
	            seatStmt.addBatch();
	        }
	        seatStmt.executeBatch();

	        conn.commit();
	        return true;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        try {
	            if (conn != null) conn.rollback();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        return false;
	    } finally {
	        try {
	            if (generatedKeys != null) generatedKeys.close();
	            if (userStmt != null) userStmt.close();
	            if (ticketStmt != null) ticketStmt.close();
	            if (seatStmt != null) seatStmt.close();
	            if (conn != null) conn.setAutoCommit(true);
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	}
	
	public boolean removeTicket(int ticketId) {
	    String deleteSeatsSQL = "DELETE FROM TicketSeat WHERE ticketId = ?";
	    String deleteTicketSQL = "DELETE FROM Ticket WHERE id = ?";

	    try (Connection conn = DatabaseService.getConnection();
	         PreparedStatement seatStmt = conn.prepareStatement(deleteSeatsSQL);
	         PreparedStatement ticketStmt = conn.prepareStatement(deleteTicketSQL)) {

	        conn.setAutoCommit(false); // bắt đầu transaction

	        // Xóa các ghế của ticket
	        seatStmt.setInt(1, ticketId);
	        seatStmt.executeUpdate();

	        // Xóa ticket
	        ticketStmt.setInt(1, ticketId);
	        int rows = ticketStmt.executeUpdate();

	        conn.commit();
	        return rows > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        try {
	            DatabaseService.getConnection().rollback();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        return false;
	    } finally {
	        try {
	            DatabaseService.getConnection().setAutoCommit(true);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
	public List<Seat> SeatReserved(int movieTimeId) {
	    List<Seat> seats = new ArrayList<>();
	    String sql = """
	        SELECT s.id, s._row, s._col
	        FROM Seat s
	        JOIN TicketSeat ts ON s.id = ts.seatId
	        JOIN Ticket t ON ts.ticketId = t.id
	        WHERE t.movieTimeId = ?
	    """;

	    try (Connection conn = DatabaseService.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, movieTimeId);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            Seat seat = new Seat();
	            seat.setId(String.valueOf(rs.getInt("id")));
	            seat.setRow(rs.getInt("_row"));
	            seat.setCol(rs.getInt("_col"));
	            seats.add(seat);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return seats;
	}



	
	public static void main(String[] args) {
	    SeatTicketService service = new SeatTicketService();
	   
//	    CreateTicketDTO dto = new CreateTicketDTO();
//	    dto.setName("Nguyễn Văn A");
//	    dto.setPhone("0912345678");
//	    dto.setEmail("vana@example.com");
//	    dto.setMovieTimeId("6"); // ID suất chiếu có thật trong DB
//
//	    // Danh sách ghế đã chọn (ID ghế phải tồn tại trong bảng Seat)
//	    dto.setSeatIds(List.of("1", "2", "3","4")); // ví dụ chọn 3 ghế
//
//	    boolean result = service.createNewTicket(dto);
//	    if (result) {
//	        System.out.println("✅ Đặt vé thành công!");
//	    } else {
//	        System.out.println("❌ Đặt vé thất bại.");
//	    } 
//	    List<Seat> seats = service.SeatAvailable(6);
	    List<showticketDTO> tickets = service.getAllTicketByMovieTime(6);
	   
	   
	    for(showticketDTO s : tickets) {
	    	
	    	System.out.println("ves" + s.getUserName() );
	    };
	}

}
