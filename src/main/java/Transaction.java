package com.jpmc.midascore;

public class Transaction {
    private Long sender;
    private Long recipient;
    private Double amount;

    // Required no-arg constructor for Jackson
    public Transaction() {}

    public Transaction(Long sender, Long recipient, Double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    // getters & setters
    public Long getSender() { return sender; }
    public void setSender(Long sender) { this.sender = sender; }

    public Long getRecipient() { return recipient; }
    public void setRecipient(Long recipient) { this.recipient = recipient; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    @Override
    public String toString() {
        return "Transaction{" +
                "sender=" + sender +
                ", recipient=" + recipient +
                ", amount=" + amount +
                '}';
    }
}