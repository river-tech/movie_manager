package movieManagerData;

import movieManagerModel.Movie;
import movieManagerModel.ShowMovieDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MovieService { 

    public List<Movie> showAllMovie() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT id, name FROM Movie"; 

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getString("id"));
                movie.setName(rs.getString("name"));
                movies.add(movie);
            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return movies; 
    }
    
    public boolean deleteMovie(int movieId) {
        String deleteTicketSeatSQL = """
            DELETE ts FROM TicketSeat ts
            JOIN Ticket t ON ts.ticketId = t.id
            JOIN MovieTime mt ON t.movieTimeId = mt.id
            WHERE mt.movieId = ?
        """;

        String deleteTicketSQL = """
            DELETE t FROM Ticket t
            JOIN MovieTime mt ON t.movieTimeId = mt.id
            WHERE mt.movieId = ?
        """;

        String deleteMovieTimeSQL = "DELETE FROM MovieTime WHERE movieId = ?";
        String deleteMovieSQL = "DELETE FROM Movie WHERE id = ?";

        try (Connection conn = DatabaseService.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            try (
                PreparedStatement stmt0 = conn.prepareStatement(deleteTicketSeatSQL);
                PreparedStatement stmt1 = conn.prepareStatement(deleteTicketSQL);
                PreparedStatement stmt2 = conn.prepareStatement(deleteMovieTimeSQL);
                PreparedStatement stmt3 = conn.prepareStatement(deleteMovieSQL)
            ) {
                stmt0.setInt(1, movieId);
                stmt0.executeUpdate();

                stmt1.setInt(1, movieId);
                stmt1.executeUpdate();

                stmt2.setInt(1, movieId);
                stmt2.executeUpdate();

                stmt3.setInt(1, movieId);
                int affected = stmt3.executeUpdate();

                conn.commit();

                if (affected > 0) {
                    System.out.println("✅ Đã xóa hoàn toàn phim và các dữ liệu liên quan.");
                    return true;
                } else {
                    System.out.println("⚠️ Không tìm thấy phim để xóa.");
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    public boolean editMovie(int movieId, String name, List<Timestamp> movieTimes) {
		String updateMovieSQL = "UPDATE Movie SET name = ? WHERE id = ?";
		String deleteMovieTimeSQL = "DELETE FROM MovieTime WHERE movieId = ?";
		String insertMovieTimeSQL = "INSERT INTO MovieTime(movieId, time) VALUES (?, ?)";

		try (Connection conn = DatabaseService.getConnection()) {
			conn.setAutoCommit(false); 

			try (PreparedStatement updateStmt = conn.prepareStatement(updateMovieSQL)) {
				updateStmt.setString(1, name);
				updateStmt.setInt(2, movieId);
				updateStmt.executeUpdate();
			}

			try (PreparedStatement deleteStmt = conn.prepareStatement(deleteMovieTimeSQL)) {
				deleteStmt.setInt(1, movieId);
				deleteStmt.executeUpdate();
			}

			try (PreparedStatement insertStmt = conn.prepareStatement(insertMovieTimeSQL)) {
				for (Timestamp ts : movieTimes) {
					insertStmt.setInt(1, movieId);
					insertStmt.setTimestamp(2, ts);
					insertStmt.addBatch();
				}
				insertStmt.executeBatch();
			}

			conn.commit(); 
		} catch (SQLException e) {
			e.printStackTrace(); 
			return false;
		}
    	return true;
    }
    
    public ShowMovieDTO ShowMovieById(int movieId) {
    	  String sqlMovie = "SELECT id, name FROM Movie WHERE id = ?";
    	    String sqlTimes = "SELECT time FROM MovieTime WHERE movieId = ? ORDER BY time";
    	    ShowMovieDTO dto = null;
    	    try(Connection conn = DatabaseService.getConnection();
    	    	PreparedStatement movieStmt = conn.prepareStatement(sqlMovie)) {
    	    	movieStmt.setInt(1, movieId);
    	    	try(ResultSet rsMovie = movieStmt.executeQuery()) {
    	    		 if (rsMovie.next()) {
    	                 dto = new ShowMovieDTO();
    	                 dto.setId(rsMovie.getString("id"));    
    	                 dto.setName(rsMovie.getString("name"));
    	                 dto.setMovieTimes(new ArrayList<>());  
    	                 try (PreparedStatement timeStmt = conn.prepareStatement(sqlTimes)) {
    	                     timeStmt.setInt(1, movieId);

    	                     try (ResultSet rsTimes = timeStmt.executeQuery()) {
    	                         while (rsTimes.next()) {
    	                             dto.addMovieTime(rsTimes.getTimestamp("time"));
    	                         }
    	                     }
    	                 }}
				} catch (Exception e) {
					  e.printStackTrace();   
				}
    	    	
			} catch (Exception e) {
			
			}
    	 return dto;
    }
    
    
    
    public boolean createNewMovie(String name, List<Timestamp> movieTimes) {

        String insertMovieSQL     = "INSERT INTO Movie(name) VALUES (?)";
        String insertMovieTimeSQL = "INSERT INTO MovieTime(movieId, time) VALUES (?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement movieStmt =  conn.prepareStatement(insertMovieSQL, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);          
            movieStmt.setString(1, name);
            int affectedRows = movieStmt.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            int movieId;
            try (ResultSet keys = movieStmt.getGeneratedKeys()) {
                if (!keys.next()) {                  
                    conn.rollback();
                    return false;
                }
                movieId = keys.getInt(1);
            }

           
            try (PreparedStatement timeStmt = conn.prepareStatement(insertMovieTimeSQL)) {
                for (Timestamp ts : movieTimes) {
                    timeStmt.setInt(1, movieId);
                    timeStmt.setTimestamp(2, ts);
                    timeStmt.addBatch();
                }
                timeStmt.executeBatch();
            }

            conn.commit();                         
            return true;

        } catch (SQLException e) {
            e.printStackTrace();                      
            return false;
        }
    }
    
    
    public static void main(String[] args) {
        MovieService service = new MovieService();
        
        List<Timestamp> movieTimes = new ArrayList<>();
        movieTimes.add(Timestamp.valueOf("2023-10-01 10:00:00"));
        movieTimes.add(Timestamp.valueOf("2023-10-01 12:00:00"));
        movieTimes.add(Timestamp.valueOf("2023-10-01 14:00:00"));
        
        
        for (Movie m : service.showAllMovie()) {
            System.out.println(m.getId() + " - " + m.getName());
        }
        service.deleteMovie(4);
//        service.editMovie(4, "Avatar 2", movieTimes);
       
        for (Movie m : service.showAllMovie()) {
            System.out.println(m.getId() + " - " + m.getName());
        }
        
//        ShowMovieDTO dto = service.ShowMovieById(4);
//        System.out.println(dto.getName()+  " - " + dto.getMovieTimes());
        
    }
}
