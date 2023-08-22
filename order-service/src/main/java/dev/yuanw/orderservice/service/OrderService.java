package dev.yuanw.orderservice.service;

import dev.yuanw.orderservice.dto.OrderLineItemDto;
import dev.yuanw.orderservice.dto.OrderRequest;
import dev.yuanw.orderservice.model.Order;
import dev.yuanw.orderservice.model.OrderLineItem;
import dev.yuanw.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Boolean placeOrder(OrderRequest orderRequest) {
        List<OrderLineItem> orderLineItemList = orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToItemDto)
                .toList();
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItemList(orderLineItemList)
                .build();
        try {
            orderRepository.save(order);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private OrderLineItem mapToItemDto(OrderLineItemDto orderLineItemDto) {
        return OrderLineItem.builder()
                .price(orderLineItemDto.getPrice())
                .quantity(orderLineItemDto.getQuantity())
                .skuCode(orderLineItemDto.getSkuCode())
                .build();
    }

}
