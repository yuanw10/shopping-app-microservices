package dev.yuanw.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yuanw.productservice.dto.ProductRequest;
import dev.yuanw.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@DynamicPropertySource
	public static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongo.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	public void shouldCreateProduct() {
		long productCount = productRepository.count();

		ProductRequest productRequest = ProductRequest.builder()
				.name("iPhone 14")
				.description("New iPhone 14!")
				.price(BigDecimal.valueOf(1200))
				.build();
		try {
			mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(productRequest)))
					.andExpect(status().isCreated());
		} catch (Exception e) {
			System.out.println("Product creation failed");
		}

        Assertions.assertEquals(productCount + 1, productRepository.findAll().size());

	}

	@Test
	public void shouldGetProducts() {

	}

}
