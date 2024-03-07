package dev.magadiflo.jpa.app.service;

import dev.magadiflo.jpa.app.model.projection.BasicEmployeeInformationProjection;

import java.util.List;

public interface EmployeeService {
    List<BasicEmployeeInformationProjection> findEmployeesBasicInformation();
}
