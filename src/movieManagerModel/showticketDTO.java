package movieManagerModel;

import java.sql.Timestamp;
import java.util.List;

public class showticketDTO {
    private String userName;
    private String movieName;
    private Timestamp movieTime;
    private List<Seat> seats;

    public showticketDTO() {
    }

    public showticketDTO	(String userName, String movieName, Timestamp movieTime, List<Seat> seats) {
        this.userName = userName;
        this.movieName = movieName;
        this.movieTime = movieTime;
        this.seats = seats;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
 
    public Timestamp getMovieTime() {
        return movieTime;
    }

    public void setMovieTime(Timestamp movieTime) {
        this.movieTime = movieTime;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
