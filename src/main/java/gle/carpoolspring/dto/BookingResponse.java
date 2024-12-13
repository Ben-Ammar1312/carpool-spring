package gle.carpoolspring.dto;

public class BookingResponse {
    private String message;

    public BookingResponse(String message) {
        this.message = message;
    }

    // Getter and setter

    public String getMessage() {
        return message;
    }
}