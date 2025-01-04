package gle.carpoolspring.dto;

public class BookingRequest {
    private int annonceId;
    private double pickupLat;
    private double pickupLng;
    private boolean onRoute;
    private String paymentMethod;

    // Getters and setters

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getAnnonceId() {
        return annonceId;
    }

    public void setAnnonceId(int annonceId) {
        this.annonceId = annonceId;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public boolean isOnRoute() {
        return onRoute;
    }

    public void setOnRoute(boolean onRoute) {
        this.onRoute = onRoute;
    }
}
