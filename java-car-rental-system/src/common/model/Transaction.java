package common.model;

import java.time.LocalDateTime;

public abstract class Transaction {
    protected String transactionId;
    protected LocalDateTime createdAt;

    // Contructor
    protected Transaction() {
    }

    // Contructor
    protected Transaction(String transactionId, LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.createdAt = createdAt;
    }

    // Getters
    public String getTransactionId(){ 
        return transactionId; 
    }

    public LocalDateTime getCreatedAt(){ 
        return createdAt; 
    }

    // Setters
    public void setTransactionId(String transactionId){ 
        this.transactionId = transactionId; 
    }

    public void setCreatedAt(LocalDateTime createdAt){ 
        this.createdAt = createdAt; 
    }
}
