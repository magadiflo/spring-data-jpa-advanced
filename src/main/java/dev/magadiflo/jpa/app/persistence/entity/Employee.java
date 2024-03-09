package dev.magadiflo.jpa.app.persistence.entity;

import dev.magadiflo.jpa.app.model.enums.ContractType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate hireDate;
    private Double salary;
    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
