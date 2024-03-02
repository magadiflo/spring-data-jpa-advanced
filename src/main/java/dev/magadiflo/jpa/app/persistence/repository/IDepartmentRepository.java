package dev.magadiflo.jpa.app.persistence.repository;

import dev.magadiflo.jpa.app.persistence.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDepartmentRepository extends JpaRepository<Department, Long> {
}
