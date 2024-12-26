package org.example.demo.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.demo.entity.OrderItem;
import org.example.demo.entity.SalesOrder;
import org.example.demo.repository.OrderItemRepository;
import org.example.demo.repository.SalesOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalesOrderService {
    private final SalesOrderRepository salesOrderRepository;
    private final OrderItemRepository orderItemRepository;

    public SalesOrderService(SalesOrderRepository salesOrderRepository, OrderItemRepository orderItemRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public String createOrder(SalesOrder order){

            salesOrderRepository.save(order);
            updateTotalAndFinalAmount(order);
            for (OrderItem item : order.getOrderItems()) {
                item.setSalesOrder(order);
                orderItemRepository.save(item);
            }
            return "SalesOrder created successfully with ID: " + order.getOrderId();

    }

    public String addItemToOrder(Long orderId, OrderItem newItem) {
        try {
            SalesOrder salesOrder = salesOrderRepository.findById(orderId)
                    .orElse(null);
            if (salesOrder == null) {
                throw new EntityNotFoundException();
            }

            Optional<OrderItem> existingItem = salesOrder.getOrderItems().stream()
                    .filter(item -> item.getDescription().equals(newItem.getDescription()))
                    .findFirst();
            if (existingItem.isPresent()) {
                OrderItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
            } else {
                newItem.setSalesOrder(salesOrder);
                salesOrder.getOrderItems().add(newItem);
            }

            salesOrder.updateTotalAndFinalAmounts();
            salesOrderRepository.save(salesOrder);
            return "Item added to SalesOrder successfully.";
        } catch (Exception e) {
            return "Error occurred while adding the item: " + e.getMessage();
        }
    }

    public String deleteItemFromOrder(Long orderId, Long itemId, Integer quantityToRemove) {
        try {
            SalesOrder order = salesOrderRepository.findById(orderId)
                    .orElse(null);
            if (order == null) {
                throw new EntityNotFoundException();
            }

            List<OrderItem> items = order.getOrderItems();
            OrderItem itemToDelete = items.stream()
                    .filter(item -> item.getItemId().equals(itemId))
                    .findFirst()
                    .orElse(null);

            if (itemToDelete == null) {
                throw new EntityNotFoundException();
            }

            if (quantityToRemove == null || quantityToRemove <= 0) {
                quantityToRemove = 1;
            }
            if (itemToDelete.getQuantity() > quantityToRemove) {
                itemToDelete.setQuantity(itemToDelete.getQuantity() - quantityToRemove);
                itemToDelete.setTotalPrice(itemToDelete.getPrice() * itemToDelete.getQuantity());
            } else {
                items.remove(itemToDelete);
                orderItemRepository.delete(itemToDelete);
            }

            for (OrderItem item : order.getOrderItems()) {
                item.setSalesOrder(order);
                orderItemRepository.save(item);
            }
            updateTotalAndFinalAmount(order);
            salesOrderRepository.save(order);
            return "Item with ID " + itemId + " successfully removed from SalesOrder " + orderId + ".";
        } catch (Exception e) {
            return "Error occurred while deleting the item: " + e.getMessage();
        }
    }

    public String applyDiscount(Long orderId, Double discount) {
        try {
            SalesOrder salesOrder = salesOrderRepository.findById(orderId)
                    .orElse(null);
            if (salesOrder == null) {
                throw new EntityNotFoundException();
            }

            salesOrder.setDiscount(discount);
            double totalAmount = salesOrder.getTotalAmount();
            double finalAmount = totalAmount - (totalAmount * (discount / 100));
            salesOrder.setFinalAmount(finalAmount);
            salesOrderRepository.save(salesOrder);

            return "Discount applied successfully to SalesOrder with ID: " + orderId;
        } catch (Exception e) {
            return "Error occurred while applying discount: " + e.getMessage();
        }
    }

    public SalesOrder getSalesOrder(Long orderId) {

            SalesOrder salesOrder = salesOrderRepository.findById(orderId)
                    .orElse(null);
            if (salesOrder == null) {
                throw new EntityNotFoundException();
            }
            return salesOrder;

    }


    public String deleteSalesOrder(Long orderId) {
        try {
            SalesOrder salesOrder = salesOrderRepository.findById(orderId)
                    .orElse(null);
            if (salesOrder == null) {
                throw new EntityNotFoundException();
            }
            salesOrderRepository.deleteById(orderId);
            return "SalesOrder with ID " + orderId + " deleted successfully.";
        } catch (Exception e) {
            return "Error occurred while deleting the SalesOrder: " + e.getMessage();
        }
    }

    private void updateTotalAndFinalAmount(SalesOrder order) {
        double totalAmount = 0;
        for (OrderItem item : order.getOrderItems()) {
            totalAmount += item.getTotalPrice();
        }
        order.setTotalAmount(totalAmount);
        double discount = order.getDiscount();
        double finalAmount = totalAmount - (totalAmount * (discount / 100));
        order.setFinalAmount(finalAmount);
    }
}
