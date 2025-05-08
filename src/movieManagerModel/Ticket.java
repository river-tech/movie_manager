package movieManagerModel;

public class Ticket {
    private String id;
    private String userId;
    private String movieTimeId;

    public Ticket() {
    }

    public Ticket(String id, String userId, String movieTimeId) {
        this.id = id;
        this.userId = userId;
        this.movieTimeId = movieTimeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMovieTimeId() {
        return movieTimeId;
    }

    public void setMovieTimeId(String movieTimeId) {
        this.movieTimeId = movieTimeId;
    }
}
