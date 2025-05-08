package movieManagerModel;

public class TicketSeat {
    private String id;
    private String ticketId;
    private String seatId;

    public TicketSeat() {
    }

    public TicketSeat(String id, String ticketId, String seatId) {
        this.id = id;
        this.ticketId = ticketId; 
        this.seatId = seatId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }
}
