package dev.magadiflo.jpa.app.persistence.entity.pk;


import dev.magadiflo.jpa.app.model.enums.TelephoneType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Setter
@Getter
public class TelephonePK {
    private Long employeeId;
    private TelephoneType telephoneType;
}
