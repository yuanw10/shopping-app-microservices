package dev.yuanw.inventoryservice.service;

import dev.yuanw.inventoryservice.dto.InventoryResponse;
import dev.yuanw.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryResponses(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(inventory -> InventoryResponse.builder()
                                    .skuCode(inventory.getSkuCode())
                                    .isInStock(inventory.getQuantity() > 0)
                                    .build()
                    ).toList();
    }
}
