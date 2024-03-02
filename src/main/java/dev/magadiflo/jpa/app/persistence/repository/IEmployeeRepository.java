package dev.magadiflo.jpa.app.persistence.repository;

import dev.magadiflo.jpa.app.persistence.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEmployeeRepository extends JpaRepository<Employee, Long> {
}
