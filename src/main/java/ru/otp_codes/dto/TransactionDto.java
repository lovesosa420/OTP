package ru.otp_codes.dto;

public class TransactionDto {

    private int amount;
    private String purchase;

    public TransactionDto() {
    }

    public TransactionDto(int amount, String purchase) {
        this.amount = amount;
        this.purchase = purchase;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPurchase() {
        return purchase;
    }

    public void setPurchase(String purchase) {
        this.purchase = purchase;
    }
}
