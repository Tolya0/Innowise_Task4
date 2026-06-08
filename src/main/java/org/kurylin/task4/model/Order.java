package org.kurylin.task4.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {

    public enum Status {
        PENDING, CONFIRMED, CANCELLED
    }

    private int id;
    private int userId;
    private int productId;
    private int quantity;
    private BigDecimal totalPrice;
    private Status status;
    private LocalDateTime createdAt;


    private String username;
    private String productName;

    public Order() {}

    public Order(int id, int userId, int productId, int quantity, BigDecimal totalPrice, Status status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    @Override
    public String toString() {
        return "Order{id=" + id + ", userId=" + userId + ", productId=" + productId
                + ", quantity=" + quantity + ", totalPrice=" + totalPrice + ", status=" + status + "}";
    }
}
