package dev.magadiflo.jpa.app.service;

import dev.magadiflo.jpa.app.model.projection.BasicEmployeeInformationProjection;
import dev.magadiflo.jpa.app.persistence.entity.EmployeeDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    List<BasicEmployeeInformationProjection> findEmployeesBasicInformation();
    Page<BasicEmployeeInformationProjection> findEmployeesBasicInformationPagination(Pageable pageable);

    List<EmployeeDetail> findAllEmployeeDetails();
}
