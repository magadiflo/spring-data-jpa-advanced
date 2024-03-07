package dev.magadiflo.jpa.app.service.impl;

import dev.magadiflo.jpa.app.model.projection.BasicEmployeeInformationProjection;
import dev.magadiflo.jpa.app.persistence.repository.EmployeeRepository;
import dev.magadiflo.jpa.app.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BasicEmployeeInformationProjection> findEmployeesBasicInformation() {
        return this.employeeRepository.findEmployeesBasicInformation();
    }

    @Override
    public Page<BasicEmployeeInformationProjection> findEmployeesBasicInformationPagination(Pageable pageable) {
        return this.employeeRepository.findEmployeesBasicInformationPage(pageable);
    }
}
