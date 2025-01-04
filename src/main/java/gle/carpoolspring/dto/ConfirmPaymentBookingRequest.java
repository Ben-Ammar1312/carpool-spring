// ConfirmPaymentBookingRequest.java
package gle.carpoolspring.dto;

public class ConfirmPaymentBookingRequest {
    private int reservationId;
    private int rideId;
    private String paymentIntentId;

    public int getReservationId() {
        return reservationId;
    }
    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getRideId() {
        return rideId;
    }
    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }
    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }
}