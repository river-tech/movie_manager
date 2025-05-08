package movieManagerModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ShowMovieDTO {

    private String id;
    private String name;
    private List<Timestamp> movieTimes;

    /* ──────────────────── Constructors ──────────────────── */

    public ShowMovieDTO() {
        this.movieTimes = new ArrayList<>();
    }

    public ShowMovieDTO(String id, String name, List<Timestamp> movieTimes) {
        this.id = id;
        this.name = name;
        this.movieTimes = movieTimes;
    }

    /* ───────────────────── Getters & Setters ───────────────────── */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Timestamp> getMovieTimes() {
        return movieTimes;
    }

    public void setMovieTimes(List<Timestamp> movieTimes) {
        this.movieTimes = movieTimes;
    }

  
    public void addMovieTime(Timestamp ts) {
        this.movieTimes.add(ts);
    }

 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShowMovieDTO)) return false;
        ShowMovieDTO that = (ShowMovieDTO) o;
        return Objects.equals(id, that.id);
    }

 
    public int hashCode() {
        return Objects.hash(id);
    }
}
