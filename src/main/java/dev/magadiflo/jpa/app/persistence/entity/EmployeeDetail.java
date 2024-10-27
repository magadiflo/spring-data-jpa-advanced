package dev.magadiflo.jpa.app.persistence.entity;

import dev.magadiflo.jpa.app.model.enums.ContractType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.time.LocalDate;

@ToString
@Setter
@Getter
@Entity
@Immutable
@Subselect("""
        SELECT e.id AS employee_id, e.first_name, e.last_name, e.email, e.phone_number AS phone_employee, e.salary, e.hire_date, e.contract_type,
                d.code, d.name, d.phone_number AS phone_department
        FROM employees AS e
            INNER JOIN departments AS d ON(e.department_id = d.id)
        """)
public class EmployeeDetail {
    @Id
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneEmployee;
    private Double salary;
    private LocalDate hireDate;
    @Enumerated(EnumType.STRING)
    private ContractType contractType;
    private String code;
    private String name;
    private String phoneDepartment;
}
