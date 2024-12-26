package org.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sales_order")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "final_amount")
    private Double finalAmount;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void updateTotalAndFinalAmounts() {
        this.totalAmount = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        this.finalAmount = totalAmount - (discount != null ? discount : 0.0);
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Double getDiscount() {
        return discount;
    }
    public void setDiscount(Double discount) {
        this.discount = discount;
    }
    public Double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public Double getFinalAmount() {
        return finalAmount;
    }
    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderId() {
        return String.valueOf(orderId);
    }
}
