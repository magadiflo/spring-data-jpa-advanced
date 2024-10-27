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
    # Para mostrar el SQL generado
    org.hibernate.SQL: DEBUG
    # Para mostrar el valor de los parámetros que pasamos a una consulta
    org.hibernate.orm.jdbc.bind: TRACE
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
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
````

````java
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
````

## Listando empleados con interfaz de proyección cerrada

La interfaz de proyección cerrada contendrá únicamente la representación de 4 columnas:

````java
public interface BasicEmployeeInformationProjection {
    String getFirstName();

    String getLastName();

    String getEmail();

    String getPhoneNumber();
}
````

Creamos la consulta personalizada usando la anotación `@Query`. Es importante definir a cada columna de la consulta
un alias. El `alias` de cada columna debe tener el mismo nombre que le definimos a su correspondiente propiedad de
la entidad que representa. Por ejemplo, como estamos consultando a la tabla `employees`, significa que debemos tener
una entidad llamada `Employee`, ahora, si nos fijamos en la propiedad de la tabla `employees` de la base de datos,
vemos el campo `first_name`, que corresponde a la propiedad `firstName` de la entidad java, por consiguiente, el
alias que le definamos a la columna `first_name` deberá ser `firstName`.

````java
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
````

Creamos nuestra capa de servicio para la entidad `Employee`, desde donde haremos uso del repositorio
`EmployeeRepository`:

````java
public interface EmployeeService {
    List<BasicEmployeeInformationProjection> findEmployeesBasicInformation();
}
````

````java

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
}
````

Finalmente, en la capa web definimos nuestro controlador:

````java

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/employees")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    @GetMapping(path = "/basic-information-list")
    public ResponseEntity<List<BasicEmployeeInformationProjection>> findEmployeesBasicInformation() {
        return ResponseEntity.ok(this.employeeService.findEmployeesBasicInformation());
    }
}
````

### Resultados obtenidos

Observamos que obtenemos la lista completa de la consulta realizada:

````bash
$ curl -v http://localhost:8080/api/v1/employees/basic-information-list | jq

>
< HTTP/1.1 200
[
  {
    "email": "almagro@gmail.com",
    "phoneNumber": "963258969",
    "lastName": "Almagro",
    "firstName": "Martín"
  },
  {
    "email": "lucia@gmail.com",
    "phoneNumber": "985478969",
    "lastName": "Campos",
    "firstName": "Lucía"
  },
  {...},
  {...},
  {...},
  {...},
  {
    "email": "ciro@gmail.com",
    "phoneNumber": "943851697",
    "lastName": "Alegría",
    "firstName": "Judith"
  }
]
````

En log de la consola, observamos nuestra consulta `SQL` que se ha ejecutado:

````bash
2024-03-07T11:15:02.496-05:00 DEBUG 4188 --- [spring-data-jpa-advanced] [nio-8080-exec-2] org.hibernate.SQL                        : 
    SELECT
        e.first_name AS firstName,
        e.last_name AS lastName,
        e.email AS email,
        e.phone_number AS phoneNumber 
    FROM
        employees AS e 
````

## Listando empleados con proyección y paginación

Dentro de la interfaz `EmployeeRepository` crearemos un método personalizado usando la anotación `Query`, donde nos
devolverá un `Page` de nuestra proyección `BasicEmployeeInformationProjection`. Este método recibirá un `Pageable`
donde se definirán algunos parámetros de paginación como el `pageNumber`, `pageSize`:

````java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    /* other method */

    @Query(value = """
            SELECT e.first_name AS firstName,
                    e.last_name AS lastName,
                    e.email AS email,
                    e.phone_number AS phoneNumber
            FROM employees AS e
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM employees
                    """,
            nativeQuery = true)
    Page<BasicEmployeeInformationProjection> findEmployeesBasicInformationPage(Pageable pageable);
}
````

Nuestra interfaz anterior sigue extendiendo de `JpaRepository`, pero para este caso en particular, necesitamos crear
nuestro método personalizado de consulta que recibirá un `Pageable` y retornará un `Page`.

Si observamos la anotación `Query` en el atributo `value` definimos nuestra consulta con las columnas que queremos
recuperar y que están acorde a los métodos definidos en nuestra proyección `BasicEmployeeInformationProjection`.
Además, observamos una consulta adicional en el atributo `countQuery`.

El `countQuery`, define una consulta de recuento especial que se utilizará para `consultas de paginación` para buscar
el número total de elementos de una página. **Si no hay ninguno configurado, derivaremos la consulta de recuento de la
consulta original o de la consulta countProjection(), si corresponde.** En nuestro caso, si no hubiéramos definido la
consulta en el atributo `countQuery`, automáticamente jpa habría generado la siguiente consulta para dicho atributo:

````sql
select
    count(1) 
FROM
    employees AS e
````

En nuestro caso, sí estamos definiendo explícitamente la consulta en el atributo `countQuery`:

````sql
SELECT COUNT(*)
FROM employees
````

En el `EmployeeService` definimos un nuevo método, el cual lo implementaremos en el `EmployeeServiceImpl`:

````java
public interface EmployeeService {
    /* other method */
    Page<BasicEmployeeInformationProjection> findEmployeesBasicInformationPagination(Pageable pageable);
}
````

````java

@RequiredArgsConstructor
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    /* other method */

    @Override
    public Page<BasicEmployeeInformationProjection> findEmployeesBasicInformationPagination(Pageable pageable) {
        return this.employeeRepository.findEmployeesBasicInformationPage(pageable);
    }
}
````

Finalmente, creamos un endpoint en nuestro controlador para llamar al método del servicio anterior:

````java

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/employees")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    /* method */

    @GetMapping(path = "/basic-information-pagination")
    public ResponseEntity<Page<BasicEmployeeInformationProjection>> findEmployeesBasicInformationPagination(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return ResponseEntity.ok(this.employeeService.findEmployeesBasicInformationPagination(pageable));
    }
}
````

### Resultados obtenidos

````bash
$ curl -v http://localhost:8080/api/v1/employees/basic-information-pagination | jq

>
< HTTP/1.1 200
<
{
  "content": [
    {
      "phoneNumber": "963258969",
      "email": "almagro@gmail.com",
      "firstName": "Martín",
      "lastName": "Almagro"
    },
    {...},
    {...},
    {...},
    {
      "phoneNumber": "953689596",
      "email": "mgonzales@gmail.com",
      "firstName": "Liz",
      "lastName": "Gonzales"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": {
      "sorted": false,
      "empty": true,
      "unsorted": true
    },
    "offset": 0,
    "unpaged": false,
    "paged": true
  },
  "last": false,
  "totalPages": 3,
  "totalElements": 11,
  "size": 5,
  "number": 0,
  "sort": {
    "sorted": false,
    "empty": true,
    "unsorted": true
  },
  "numberOfElements": 5,
  "first": true,
  "empty": false
}
````

````bash
2024-03-07T12:59:24.665-05:00 DEBUG 10488 --- [spring-data-jpa-advanced] [nio-8080-exec-1] org.hibernate.SQL                        : 
    SELECT
        e.first_name AS firstName,
        e.last_name AS lastName,
        e.email AS email,
        e.phone_number AS phoneNumber 
    FROM
        employees AS e 
    limit
        ? 
2024-03-07T12:59:24.727-05:00 DEBUG 10488 --- [spring-data-jpa-advanced] [nio-8080-exec-1] org.hibernate.SQL                        : 
    SELECT
        COUNT(*) 
    FROM
        employees
````

---

# Claves primarias compuestas

---

En la medida en que utilizamos bases de datos relacionales, tenemos que considerar los campos clave que actúan como
identificadores de la tabla. Además de las claves primarias habituales, la clave compuesta desempeña un papel
específico, ya que varios candidatos generan colectivamente el identificador.

En `JPA (Java Persistence API)`, hay dos formas de especificar claves compuestas de entidad: `@IdClass` y `@EmbeddedId`.

## [Claves primarias compuestas con @IdClass](https://www.oscarblancarteblog.com/2016/11/02/llaves-compuestas-idclass/)

En este apartado explicaremos cómo manejamos la relación de clave compuesta en la aplicación Java Spring Boot
utilizando la anotación `@Idclass`.

La utilización de `@IdClass` es una de las dos opciones para definir `claves primarias compuestas`, y esta consiste en
crear una clase adicional únicamente con los campos que corresponden a la clave primaria.

Veamos un caso concreto, normalmente un empleado puede tener más de un teléfono, entonces, podríamos crear una tabla
donde la `clave primaria` sea el `ID del empleado` y el `tipo de teléfono`, de esta forma nos aseguramos de tener solo
un tipo de teléfono por empleado.

Empecemos creando una clase de enumeración para tener bien definido los tipos de teléfonos que manejará nuestra entidad.

````java
public enum TelephoneType {
    MOBILE, HOUSE, WORK, FAX, OTHER
}
````

Veamos cómo quedaría la clase `PK` con los atributos que formarán parte de nuestra `clave primaria compuesta`. Algo
que podría resaltar aquí es que el atributo `telephoneType` podríamos haberlo definido como un `String`, pero para
acotar los tipos de teléfono a manejar y no escribir cualquier cadena, es que lo he definido del tipo `enum`.

````java

@EqualsAndHashCode
@Setter
@Getter
public class TelephonePK {
    private Long employeeId;
    private TelephoneType telephoneType;
}
````

Definimos la clase `TelephonePK`, quien tiene como atributos el `employeeId` y el `telephoneType`.
Observemos que esta clase no requiere de ningún tipo de anotación especial, pero `sí es requerido` sobrescribir los
métodos `hashCode & equals`, en nuestro caso eso lo hacemos con la anotación de lombok `@EqualsAndHashCode`. Además,
aprovechamos el poder de lombok para generar los `@Getter` y `@Setter`. Es decir, si no hubiéramos usado esas
anotaciones de lombok, simplemente habríamos escrito mano el código que representan. Más allá de eso, esta clase
no requiere ninguna anotación especial.

La siguiente clase que definimos es la entidad `Telephone`, quien representa un teléfono de empleado.

````java

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@IdClass(TelephonePK.class)
@Entity
@Table(name = "telephones")
public class Telephone {
    @Id
    private Long employeeId;
    @Id
    @Enumerated(EnumType.STRING)
    private TelephoneType telephoneType;

    private String number;
}
````

Observemos que esta clase cuenta con los atributos `employeeId` y `telephoneType` anotados con `@Id`. Además, tiene la
anotación `@IdClass` a nivel de clase y como valor tiene la clase `TelephonePK.class`.

Un dato importante es que tanto la definición de los atributos en la clase ID (`TelephonePK`) como en la clase
de entidad (`Telephone`) deben de coincidir a la perfección en caso contrario provocara un error en tiempo de ejecución.

Según la documentación dice:

> `@IdClass`, especifica una `clase de clave principal compuesta` que se asigna a varios campos o propiedades de la
> entidad. Los nombres de los campos o propiedades de la clase de clave principal y los campos o propiedades de clave
> principal de la entidad deben corresponderse y sus tipos deben ser los mismos.

Tras aplicar estos últimos cambios, ejecutamos la aplicación y vemos en el log, la creación de la tabla `telephones`
y como `clave primaria compuesta` la combinación de `(employee_id, telephone_type)`.

````bash
2024-10-27T00:17:18.848-05:00 DEBUG 17732 --- [spring-data-jpa-advanced] [           main] org.hibernate.SQL                        : 
    create table telephones (
        employee_id bigint not null,
        number varchar(255),
        telephone_type enum ('MOBILE','HOUSE','WORK','FAX','OTHER') not null,
        primary key (employee_id, telephone_type)
    ) engine=InnoDB
````

Observamos en la tabla que nuestra clave primaria compuesta se ha creado correctamente.

![02.png](assets/02.png)

## [Claves primarias compuestas con @EmbeddedId](https://www.oscarblancarteblog.com/2016/11/08/embeber-llave-primaria-embeddedid/)

Otra manera de definir `claves primarías compuestas` es con la anotación `@EmbeddedId`, quien anota a una clase
como `ID`. A diferencia de `@IdClass`, este método no requiere definir los atributos de la clave primaria en la entidad,
sino que solo hace falta agregar como atributo la clase que contiene todos los campos.

Una diferencia que tiene este método con respecto al `@IdClass`, es qué es necesario que la clase `ID` esté
anotada a nivel de clase con la anotación `@Embeddable`. Esto le dice a `JPA` que esta clase se puede embeber
dentro de otra.

Creamos la clase `OrderItemPK` que contendrá las claves primarias compuestas `orderId` y `productId`.

````java

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@Embeddable
public class OrderItemPK {
    private Long orderId;
    private Long productId;
}
````

Observemos primero que nada que se le agregó la anotación `@Embeddable` a nivel de clase y los métodos equals y hashCode
con la anotación de lombok `@EqualsAndHashCode`.

Ahora creamos la entidad `OrderItem` que incluirá la clase `OrderItemPK` como identificador.

````java

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "order_items")
public class OrderItem {

    @EmbeddedId
    private OrderItemPK id;

    private Integer quantity;
    private Double price;
}
````

Observemos que como identificador usamos la clase `OrderItemPK` a quien lo anotamos con `@EmbeddedId`.

La anotación `@EmbeddedId` provocará que los campos definidos en la clase `OrderItemPK` sean considerados como
si fueran parte de la clase `OrderItem`.

Cabe mencionar que tanto `@IdClass` como `@EmbeddedId` darán el mismo resultado en tiempo de ejecución, por lo que
la única diferencia es solo a nivel del código. Recordemos que con `@IdClass` es necesario definir los campos que serán
la clave primaria tanto en la entidad como en las clases Id, en cambio, con `@EmbeddedId` solo será necesario embeber
la clave como una propiedad más de la clase. Particularmente yo prefiero trabajar con `@EmbeddedId` para evitar
repetir atributos.

Ahora, si ejecutamos la aplicación veremos el siguiente resultado en consola.

````bash
2024-10-27T01:11:28.953-05:00 DEBUG 11236 --- [spring-data-jpa-advanced] [           main] org.hibernate.SQL                        : 
    create table order_items (
        price float(53),
        quantity integer,
        order_id bigint not null,
        product_id bigint not null,
        primary key (order_id, product_id)
    ) engine=InnoDB
````

Observamos que se nos ha creado correctamente la clave primaria compuesta `(order_id, product_id)`. Ahora, si revisamos
la tabla en la base de datos obtendremos gráficamente el mismo resultado.

![03.png](assets/03.png)

---

# Anotación @Subselect en Hibernate

Fuentes:

- [Baeldung: Hibernate-subselect](https://www.baeldung.com/hibernate-subselect)
- [Medium: Exposing Subset/View of the database with a JPA Repository](https://medium.com/@jonathan.turnock/exposing-subset-view-of-the-database-with-a-jpa-repository-over-rest-5b9d6e07344b)

---

`@Subselect` nos permite mapear una `entidad inmutable` a la `consulta SQL`. Así que vamos a desenrollar un poco esta
explicación, empezando por lo que significa el mapeo de entidades a consultas SQL.

## Mapeo a consulta SQL

Normalmente, cuando creamos nuestras entidades en Hibernate, las anotamos con `@Entity`. Esta anotación indica que se
trata de una entidad y que debe administrarse mediante un contexto de persistencia. Opcionalmente, también podemos
proporcionar la anotación `@Table` para indicar a qué tabla Hibernate debe mapear exactamente esta entidad. Entonces,
de forma predeterminada, cada vez que creamos una entidad en Hibernate, asume que una entidad se asigna directamente
a una tabla en particular. En la mayoría de los casos, eso es exactamente lo que queremos, pero no siempre.

A veces, nuestra entidad no se asigna directamente a una tabla particular en la base de datos, sino que es el resultado
de la ejecución de una consulta SQL. Por ejemplo, podríamos tener una entidad `EmployeeDetail`, donde cada instancia de
esta entidad es una fila en un `ResultSet` de una ejecución de consulta `SQL (o vista SQL)`.

Lo importante es que es posible que no haya ninguna tabla de `EmployeeDetail` dedicada en la base de datos. Así que eso
es lo que significa mapear una entidad en una consulta SQL:
`obtenemos entidades de una consulta SQL de subselección, no de una tabla`. Esta consulta puede seleccionar de
cualquier tabla y realizar cualquier lógica dentro de ella: a Hibernate no le importa.

## Inmutabilidad

Por lo tanto, es posible que tengamos una entidad que no esté asignada a una tabla en particular. Como consecuencia
directa, no está claro cómo realizar las instrucciones `INSERT/UPDATE`. Simplemente, no hay una tabla en la base de
datos que haga referencia a `EmployeeDetail` en la que podamos insertar registros.

De hecho, Hibernate no tiene ni idea de qué tipo de SQL ejecutamos para recuperar los datos. Por lo tanto, Hibernate no
puede realizar ninguna operación de escritura en dicha entidad, `se convierte en de solo lectura`. Lo complicado
aquí es que todavía podemos pedirle a Hibernate que inserte esta entidad, pero fallará, ya que es imposible
(al menos según ANSI SQL) emitir un `INSERT` en la `subselección`.

## Ejemplo de uso

Ahora, una vez que entendemos lo que hace la anotación `@Subselect`, tratemos de ensuciarnos las manos e intentemos
usarla. Aquí, tenemos nuestra entidad `EmployeeDetails` que estará mapeada a la consulta SQL definida en la anotación
`@Subselect`.

Es importante tener en cuenta que a esta entidad que usa la anotación `@Subselect` debemos agregarle la anotación
`@Immutable`. `Hibernate`, por lo tanto, deshabilitará cualquier seguimiento de cheques sucios para nuestra entidad
para evitar declaraciones `UPDATE` accidentales.

````java

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
````

- `@Subselect`, asigna una entidad inmutable y de solo lectura a una expresión de selección `SQL` determinada. Esta es
  una alternativa a definir una `vista` de base de datos y asignar la entidad a la vista mediante la anotación `@Table`.


- `@Immutable`, marca una entidad, colección o atributo de una entidad como inmutable. La ausencia de esta anotación
  significa que el elemento es mutable. Los cambios realizados en la memoria al estado de una entidad inmutable nunca se
  sincronizan con la base de datos. Los cambios se ignoran y no se lanza ninguna excepción. No es necesario comprobar si
  una entidad inmutable es correcta, por lo que Hibernate no necesita mantener una instantánea de su estado.

Ahora que tenemos nuestra entidad JPA asignada a una consulta SQL (o vista), podemos crear una interaz `JpaRepository`
estándar, luego implementar sus métodos en el servicio para finalmente en el controlador exponer un endpoint que
el listado de estas entidades.

````java
public interface EmployeeDetailRepository extends JpaRepository<EmployeeDetail, Long> {
}
````

Procedemos a crear un método en el servicio de `EmployeeService` aprovechando que ya lo teníamos construído.

````java
public interface EmployeeService {
    List<EmployeeDetail> findAllEmployeeDetails();
}
````

Implementamos el método agregado a la interfaz de servicio.

````java

@RequiredArgsConstructor
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeDetailRepository employeeDetailRepository;

    @Override
    public List<EmployeeDetail> findAllEmployeeDetails() {
        return this.employeeDetailRepository.findAll();
    }
}
````

Finalmente, en el controlador `EmployeeController` exponemos un endpoint que nos mostraré la lista de employeeDetails.

````java

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/employees")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    @GetMapping(path = "/details")
    public ResponseEntity<List<EmployeeDetail>> findAllEmployeeDetails() {
        return ResponseEntity.ok(this.employeeService.findAllEmployeeDetails());
    }
}
````

Si realizamos una petición al entpoint anterior, veremos que se muestra nuestra lista de `EmployeeDetail` con todos
los atributos que definimos en la entidad.

````bash
$ curl -v http://localhost:8080/api/v1/employees/details | jq
>
< HTTP/1.1 200
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Sun, 27 Oct 2024 22:47:02 GMT
<
[
  {
    "employeeId": 1,
    "firstName": "Martín",
    "lastName": "Almagro",
    "email": "almagro@gmail.com",
    "phoneEmployee": "963258969",
    "salary": 5000,
    "hireDate": "2015-03-01",
    "contractType": "INDEFINIDO",
    "code": "D01",
    "name": "Sistemas",
    "phoneDepartment": "332636"
  },
  {...},
  {
    "employeeId": 11,
    "firstName": "Judith",
    "lastName": "Alegría",
    "email": "ciro@gmail.com",
    "phoneEmployee": "943851697",
    "salary": 5455,
    "hireDate": "2015-03-29",
    "contractType": "INDEFINIDO",
    "code": "D05",
    "name": "Soporte",
    "phoneDepartment": "321478"
  }
]
````

Si revisamos el log que se muestra en el ide, veremos que la consulta ejecutada es la siguiente.

````bash
2024-10-27T17:47:02.527-05:00 DEBUG 11988 --- [spring-data-jpa-advanced] [nio-8080-exec-2] org.hibernate.SQL                        : 
    select
        ed1_0.employee_id,
        ed1_0.code,
        ed1_0.contract_type,
        ed1_0.email,
        ed1_0.first_name,
        ed1_0.hire_date,
        ed1_0.last_name,
        ed1_0.name,
        ed1_0.phone_department,
        ed1_0.phone_employee,
        ed1_0.salary 
    from
        ( SELECT
            e.id AS employee_id,
            e.first_name,
            e.last_name,
            e.email,
            e.phone_number AS phone_employee,
            e.salary,
            e.hire_date,
            e.contract_type,
            d.code,
            d.name,
            d.phone_number AS phone_department 
        FROM
            employees AS e     
        INNER JOIN
            departments AS d 
                ON(e.department_id = d.id)  ) ed1_0
````

Si observamos la consulta generada, hibernate tiende a envolver la consulta definida en `@Subselect` dentro de otra
`subconsulta` al generar el SQL final. Esto es un comportamiento esperado y común cuando trabajas con `@Subselect`, ya
que Hibernate trata la entidad como una `vista` de solo lectura y, al realizar consultas, suele aplicar optimizaciones
o adaptaciones para su propio uso interno.

La `subconsulta` adicional no afecta la funcionalidad ni el rendimiento, a menos que trabajes con conjuntos de datos
extremadamente grandes. `Hibernate` lo hace para tener más control sobre los resultados y el mapeo de los datos a los
objetos en memoria.