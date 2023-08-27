package dev.yuanw.orderservice.service;

import dev.yuanw.orderservice.dto.OrderLineItemDto;
import dev.yuanw.orderservice.dto.OrderRequest;
import dev.yuanw.orderservice.model.Order;
import dev.yuanw.orderservice.model.OrderLineItem;
import dev.yuanw.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        List<OrderLineItem> orderLineItemList = orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToItemDto)
                .toList();
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItemList(orderLineItemList)
                .build();

        List<String> skuCodes = orderLineItemList.stream().map(OrderLineItem::getSkuCode).toList();

        // call Inventory Service, place order if in stock
        Boolean allItemsInStock = webClientBuilder.build().get()
                            .uri("http://inventory-service/api/inventory",
                                    uriBuilder -> uriBuilder.queryParam("sku_code", skuCodes).build())
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .block();   // synchronous
        if (allItemsInStock) {
            orderRepository.save(order);
            log.info("All items in stock");
            kafkaTemplate.send("notificationTopic", order.getOrderNumber());
            return "Order placed successfully";
        }
        else {
            log.info("Not all items in stock");
            return "Order cannot be placed";
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
