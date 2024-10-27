package dev.magadiflo.jpa.app.persistence.entity;

import dev.magadiflo.jpa.app.model.enums.TelephoneType;
import dev.magadiflo.jpa.app.persistence.entity.pk.TelephonePK;
import jakarta.persistence.*;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@IdClass(TelephonePK.class)
@Entity
@Table(name = "telephones")
public class Telephone {
    @Id
    private Long employeeId;
    @Id
    @Enumerated(EnumType.STRING)
    private TelephoneType telephoneType;

    private String number;
}
