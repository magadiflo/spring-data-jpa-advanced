package dev.magadiflo.jpa.app.persistence.repository;

import dev.magadiflo.jpa.app.persistence.entity.EmployeeDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDetailRepository extends JpaRepository<EmployeeDetail, Long> {
}
