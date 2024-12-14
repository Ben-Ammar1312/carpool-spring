package gle.carpoolspring.dto;

public class PaymentRequest {
    private long amount; // Amount in the smallest currency unit (e.g., cents)
    private String currency;

    // Getters and Setters

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    // Optionally, add validation annotations
}