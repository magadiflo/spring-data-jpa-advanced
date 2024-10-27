package dev.magadiflo.jpa.app.persistence.entity.pk;

import jakarta.persistence.Embeddable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@Embeddable
public class OrderItemPK {
    private Long orderId;
    private Long productId;
}
