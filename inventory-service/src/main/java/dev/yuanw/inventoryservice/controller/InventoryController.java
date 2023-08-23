package dev.yuanw.inventoryservice.controller;

import dev.yuanw.inventoryservice.dto.InventoryResponse;
import dev.yuanw.inventoryservice.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Slf4j
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean allIsInStock(@RequestParam("sku_code") List<String> skuCodes) {
        List<InventoryResponse> inventoryResponses = inventoryService.getInventoryResponses(skuCodes);
        log.info("Sku codes received: {}", skuCodes);
        log.info("Inventory responses size: {}", inventoryResponses.size());
        return inventoryResponses.size() == skuCodes.size()
                && inventoryResponses.stream().allMatch(InventoryResponse::isInStock);
    }
}
