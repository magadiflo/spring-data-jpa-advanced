package dev.magadiflo.jpa.app.persistence.entity;

import dev.magadiflo.jpa.app.persistence.entity.pk.OrderItemPK;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "order_items")
public class OrderItem {

    @EmbeddedId
    private OrderItemPK id;

    private Integer quantity;
    private Double price;
}
