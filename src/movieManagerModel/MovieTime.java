package movieManagerModel;

import java.sql.Timestamp;

public class MovieTime {
    private String id;
    private String movieId;
    private Timestamp time;

    public MovieTime() {
    }

    public MovieTime(String id, String movieId, Timestamp time) {
        this.id = id;
        this.movieId = movieId;
        this.time = time;
    }
    
    public String toString() {
        if (time == null) return "";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(time);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
