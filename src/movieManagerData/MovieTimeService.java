package movieManagerData;

import movieManagerModel.MovieTime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieTimeService {
	public List<MovieTime> showAllMovieTimeByMovie(int movieId) { 

	    List<MovieTime> movieTimes = new ArrayList<>();
	    String sql = "SELECT id, movieId, time FROM MovieTime WHERE movieId = ? ORDER BY time";

	    try (Connection conn = DatabaseService.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, movieId);               
 
	        try (ResultSet rs = stmt.executeQuery()) { 
	            while (rs.next()) {
	                MovieTime mt = new MovieTime();
	                mt.setId(rs.getString("id"));
	                mt.setMovieId(rs.getString("movieId")); 
	                mt.setTime(rs.getTimestamp("time"));   
	                movieTimes.add(mt);
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return movieTimes;                         
	}
	
	   public static void main(String[] args) {
		   MovieTimeService service = new MovieTimeService();
		   List<MovieTime> cac = service.showAllMovieTimeByMovie(4);
		   
		   for (MovieTime m : cac) {
	            System.out.println(m.getId() + " - " + m.getMovieId() + "-" +m.getTime());
	        }
	    }
}
