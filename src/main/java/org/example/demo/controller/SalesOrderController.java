package org.example.demo.controller;

import org.example.demo.entity.OrderItem;
import org.example.demo.entity.SalesOrder;
import org.example.demo.service.SalesOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sales-orders")
public class SalesOrderController {
    private final SalesOrderService salesOrderService;

    public SalesOrderController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody SalesOrder salesOrder) {
        String createdOrder = salesOrderService.createOrder(salesOrder);
        return ResponseEntity.ok("Order with ID " + salesOrder.getOrderId() + " created successfully.");
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<String> addItemToOrder(@PathVariable Long orderId, @RequestBody OrderItem orderItem) {
        String updatedOrder = salesOrderService.addItemToOrder(orderId, orderItem);
        System.out.println(orderItem.getItemId());
        return ResponseEntity.ok("Item added successfully to the sales order with ID "+orderId);
    }

    @DeleteMapping("/{orderId}/{itemId}")
    public ResponseEntity<String> deleteItemWithoutQuantity(
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {
        String updatedOrder = salesOrderService.deleteItemFromOrder(orderId, itemId, null);
        return ResponseEntity.ok("Item with ID " + itemId + " removed from sales order with ID " + orderId);
    }

    @DeleteMapping("/{orderId}/{itemId}/{quantityToRemove}")
    public ResponseEntity<String> deleteItemWithQuantity(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @PathVariable Integer quantityToRemove
    ) {
        String updatedOrder = salesOrderService.deleteItemFromOrder(orderId, itemId, quantityToRemove);
        return ResponseEntity.ok("Quantity " + quantityToRemove + " of item with ID " + itemId + " removed from sales order with ID " + orderId);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<String> applyDiscount(
            @PathVariable Long orderId,
            @RequestBody Map<String, Double> requestBody
    ) {
        Double discount = requestBody.get("discount");
        if (discount == null || discount < 0) {
            return ResponseEntity.badRequest().body("Invalid discount value provided.");
        }
        salesOrderService.applyDiscount(orderId, discount);
        return ResponseEntity.ok("Discount of " + discount + "% applied successfully to order ID " + orderId + "!");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<SalesOrder> getSalesOrder(@PathVariable Long orderId) {
        SalesOrder salesOrder = salesOrderService.getSalesOrder(orderId);
        return ResponseEntity.ok(salesOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteSalesOrder(@PathVariable Long orderId) {
        salesOrderService.deleteSalesOrder(orderId);
        return ResponseEntity.ok("Sales order with ID " + orderId + " deleted successfully!");
    }
}
