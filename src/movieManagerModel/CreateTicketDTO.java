package movieManagerModel;

import java.util.List;

public class CreateTicketDTO {
    private String name;
    private String phone;
    private String email;
    private String movieTimeId;
    private List<String> seatIds;

    public CreateTicketDTO() {
    }

    public CreateTicketDTO(String name, String phone, String email, String movieTimeId, List<String> seatIds) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.movieTimeId = movieTimeId;
        this.seatIds = seatIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMovieTimeId() {
        return movieTimeId;
    }

    public void setMovieTimeId(String movieTimeId) {
        this.movieTimeId = movieTimeId;
    }

    public List<String> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<String> seatIds) {
        this.seatIds = seatIds;
    }
}
