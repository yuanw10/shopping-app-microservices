package dev.yuanw.inventoryservice;

import dev.yuanw.inventoryservice.model.Inventory;
import dev.yuanw.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> {
			updateInsert("iphone_14", 150, inventoryRepository);
			updateInsert("iphone_14_plus", 120, inventoryRepository);
			updateInsert("macbook_air", 50, inventoryRepository);
			updateInsert("iphone_13", 0, inventoryRepository);
		};
	}

	private void updateInsert (String skuCode, Integer quantity, InventoryRepository inventoryRepository) {
		Optional<Inventory> optionalInventory = inventoryRepository.findBySkuCode(skuCode);
		optionalInventory.ifPresentOrElse(
				inventory -> {
					inventory.setQuantity(quantity);
					inventoryRepository.save(inventory);
				},
				() -> {
					inventoryRepository.save(Inventory.builder()
							.skuCode(skuCode)
							.quantity(quantity)
							.build());
				}
		);
	}
}
