package dev.yuanw.inventoryservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_inventories")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String skuCode;
    private Integer quantity;
}
