# Spring Data JPA - Avanzado

- Tutorial que implementa **Criteria API:**
  [spring-data-jpa-criteria-queries](https://github.com/magadiflo/spring-data-jpa-criteria-queries.git)
- Tutorial que habla netamente de **Specifications:**
  [spring-data-jpa-specifications]( https://github.com/magadiflo/spring-data-jpa-specifications.git)
- Tutorial que habla netamente de **Projections:**
  [spring-data-jpa-projections]( https://github.com/magadiflo/spring-data-jpa-projections.git)
- Tutorial donde se usa **Specifications** y **Projections**:
  [spring-boot-web-crud](https://github.com/magadiflo/spring-boot-web-crud.git)

## Dependencias

````xml
<!--Spring Boot 3.2.3-->
<!--Java 21-->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-jpamodelgen</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
````

## Propiedades

````yml
server:
  port: 8080
  error:
    include-message: always

spring:
  application:
    name: spring-data-jpa-advanced

  datasource:
    url: jdbc:mysql://localhost:3306/db_spring_data_jpa
    username: admin
    password: magadiflo

  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    org.hibernate.SQL: DEBUG
````

## Entidades

````java

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
````

````java

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private String phoneNumber;
}
````

### Tablas generadas

![tablas](./assets/01.tables.png)

## Modelos

````java
public enum ContractType {
    INDEFINIDO, PLAZO_FIJO, POR_OBRA, TEMPORAL
}
````

## Repositorios

````java
public interface IEmployeeRepository extends JpaRepository<Employee, Long> {
}
````

````java
public interface IDepartmentRepository extends JpaRepository<Department, Long> {
}
````
