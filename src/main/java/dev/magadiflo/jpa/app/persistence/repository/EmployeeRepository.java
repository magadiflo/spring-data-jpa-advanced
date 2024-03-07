package dev.magadiflo.jpa.app.persistence.repository;

import dev.magadiflo.jpa.app.model.projection.BasicEmployeeInformationProjection;
import dev.magadiflo.jpa.app.persistence.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query(value = """
            SELECT e.first_name AS firstName,
                    e.last_name AS lastName,
                    e.email AS email,
                    e.phone_number AS phoneNumber
            FROM employees AS e
            """, nativeQuery = true)
    List<BasicEmployeeInformationProjection> findEmployeesBasicInformation();
}
