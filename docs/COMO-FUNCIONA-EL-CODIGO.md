# Cómo funciona el código de CiberBus

Guía para leer el proyecto **línea por línea**. Está pensada para el video y para que cada integrante sepa qué hace su parte.

Si solo quieres arrancar la app, usa [GUIA-DEL-PROYECTO.md](GUIA-DEL-PROYECTO.md).

---

## 1. El `pom.xml` (qué librerías usamos y por qué esa versión)

El `pom.xml` es la **lista de compras** de Maven. Sin este archivo, Java no sabe de dónde sacar Spring, Thymeleaf ni el conector de MySQL.

### 1.1 El padre: Spring Boot 3.5.5

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.5</version>
</parent>
```

**Qué es:** un POM padre que ya trae versiones coordinadas de decenas de librerías.

**Por qué 3.5.5:** es Spring Boot **3**. La versión 3 usa **Jakarta** (`jakarta.persistence.Entity`) y no el `javax` viejo. Encaja con **Java 17**. No hace falta poner a mano la versión de Hibernate, Thymeleaf o Security: el padre las alinea.

**Por qué no Spring Boot 2:** el curso pidió Spring Boot moderno, sin Servlets ni JSP. Boot 2 es el modelo anterior.

### 1.2 Java 17

```xml
<java.version>17</java.version>
```

**Por qué 17:** es la versión LTS que pide Spring Boot 3. En la PC a veces Maven corre con JDK 18; eso sirve, porque 18 entiende código 17. No bajar a Java 8/11: Boot 3 no arranca.

### 1.3 Cada dependencia (starter)

Un **starter** es un paquete que junta varias librerías. No ponemos 20 jars: ponemos un starter.

| Starter / jar | Para qué en CiberBus |
|---|---|
| `spring-boot-starter-web` | El servidor embebido (Tomcat) + MVC. Sin esto no hay `http://localhost:8080`. |
| `spring-boot-starter-thymeleaf` | Las pantallas HTML en `templates/`. Reemplaza a los JSP. |
| `spring-boot-starter-data-jpa` | Entidades + repositorios. Hibernate habla con MySQL. |
| `spring-boot-starter-security` | Login, roles ADMIN/CLIENTE, CSRF, BCrypt. |
| `spring-boot-starter-validation` | Validaciones de datos (por si un formulario llega incompleto). |
| `mysql-connector-j` (`runtime`) | El driver JDBC. Solo se necesita al correr, no al compilar. |
| `spring-boot-starter-test` | Pruebas. En este proyecto hay un test mínimo. |
| `spring-security-test` | Apoyo para probar seguridad. |

**Por qué no hay versión en cada starter:** las hereda el padre `3.5.5`. Si alguien pone una versión a mano, se pelean las librerías.

**Por qué no hay Tomcat en el POM:** `starter-web` ya lo trae. Por eso el comando es `.\mvnw.cmd spring-boot:run` y no “desplegar un WAR”.

**Plugin:** `spring-boot-maven-plugin` arma el jar ejecutable y permite `spring-boot:run`.

---

## 2. Las carpetas (quién vive dónde)

```
com.ciberbus
 ├── CiberbusApplication.java     arranque + usuarios de prueba
 ├── controller                   URLs y HTML
 ├── service                      interfaces (el contrato)
 ├── service.impl                 el código real de las reglas
 ├── repository                   acceso a MySQL
 ├── entity                       una clase = una tabla
 ├── config                       SecurityConfig y PasswordConfig
 ├── security                     puente login ↔ base de datos
 └── exception                    errores de negocio
```

Regla del curso, hay que decirla en el video:

**controller → service → repository**

El controller **nunca** llama al repository. Si Cristian (seguridad) o Brando (reserva) necesitan un usuario o un asiento, pasan por el service.

---

## 3. ¿Qué es un controller?

Un **controller** es una clase Java que **escucha al navegador**.

Cuando escribes `http://localhost:8080/inicio` o envías un formulario, Spring busca un método anotado que coincida con esa URL.

Ejemplo real: `HomeController`.

```java
@Controller
public class HomeController {

    private final CatalogoService catalogoService;
    private final ViajeService viajeService;

    public HomeController(CatalogoService catalogoService, ViajeService viajeService) {
        this.catalogoService = catalogoService;
        this.viajeService = viajeService;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(..., Model model) {
        model.addAttribute("origenes", viajeService.listarOrigenesDisponibles());
        return "inicio";   // → templates/inicio.html
    }
}
```

### Qué significa cada pieza

| Pieza | Qué hace |
|---|---|
| `@Controller` | “Esta clase atiende páginas web”. Spring la descubre al arrancar. |
| El constructor | Spring **inyecta** los services. No usamos `new CatalogoServiceImpl()`. Eso se llama **inyección de dependencias**. |
| `Model` | Bolsa de datos hacia el HTML. `model.addAttribute("origenes", lista)` se usa en Thymeleaf como `${origenes}`. |
| `return "inicio"` | No es una URL. Es el **nombre del archivo** `templates/inicio.html` (sin `.html`). |

### Controllers del proyecto (para el video)

| Clase | Frente | URLs que atiende |
|---|---|---|
| `HomeController` | P1 / P4 | `/` y `/inicio` |
| `LoginController` | P2 | `/login` |
| `RegistroController` | P2 | `/registro` |
| `AdminMaestrosController` | P3 | `/admin/ciudades`, turnos, buses, conductores, rutas |
| `AdminViajeController` | P4 | `/admin/viajes` |
| `AdminController` | P3 / P6 | `/admin` (panel) y `/admin/reservas` |
| `ViajeController` | P4 | `/viajes/buscar`, asientos, resumen |
| `ReservaController` | P5 | `/reserva/**` (checkout, consultar, cancelar) |
| `UsuarioModelAdvice` | P6 | no es una URL: pone el usuario en **todas** las pantallas para el menú |

---

## 4. ¿Qué es `@GetMapping`? (y las otras anotaciones HTTP)

HTTP tiene verbos. Los dos que usamos:

- **GET** = “enséñame la página” (el usuario abre un link o pega la URL).
- **POST** = “guarda esto” (el usuario envía un formulario).

### `@GetMapping`

```java
@GetMapping({"/", "/inicio"})
public String inicio(...) { ... }
```

Significa: si el navegador pide **GET** `/` o **GET** `/inicio`, ejecuta este método.

Otro ejemplo P4:

```java
@GetMapping("/viajes/buscar")
```

La barra de direcciones queda: `http://localhost:8080/viajes/buscar?rutaId=1&fecha=2026-10-20`.

### `@PostMapping`

```java
@PostMapping("/registro")
public String registrar(...) { ... }
```

Solo se dispara cuando el formulario hace `method="post"`. Así la clave no viaja en la URL.

### `@RequestMapping`

Se pone en la **clase** para no repetir el prefijo:

```java
@Controller
@RequestMapping("/reserva")
public class ReservaController { ... }
```

Un método `@GetMapping("/consultar")` queda en `/reserva/consultar`.

### `@RequestParam` y `@PathVariable`

- `@RequestParam` = dato que viene de un `?nombre=valor` o de un `<input name="nombre">`.
- `@PathVariable` = dato que va **en la ruta**, ejemplo `/reserva/detalle/CB-D0E930CA` → `{codigo}`.

### Tabla rápida para el video

| Anotación | Ejemplo en CiberBus | Cuándo |
|---|---|---|
| `@GetMapping` | `/inicio`, `/viajes/buscar` | Ver |
| `@PostMapping` | `/login`, `/reserva/procesar` | Guardar |
| `@RequestMapping` | `/admin`, `/reserva` | Prefijo de la clase |
| `@RequestParam` | `origenId`, `clave` | Campo de formulario o query |
| `@PathVariable` | `idViaje`, `codigo` | Parte de la URL |

---

## 5. ¿Qué es un service?

El **service** es donde viven las **reglas**. El controller no debe decir “máximo 5 asientos” ni “origen ≠ destino”: eso lo dice el service.

Hay **dos archivos** por frente:

1. **Interfaz** (`ViajeService.java`) = la lista de métodos que prometemos.
2. **Implementación** (`ViajeServiceImpl.java`) = el código de verdad.

¿Por qué dos? Para que P5 (Brando) use `ViajeService` sin copiar el SQL de Kelly. Es el **contrato entre frentes**.

```java
@Service
@Transactional
public class ViajeServiceImpl implements ViajeService {
    private final ViajeRepository viajeRepository;
    ...
}
```

| Anotación | Qué hace |
|---|---|
| `@Service` | Spring crea un único objeto de esta clase y lo inyecta en los controllers. |
| `implements ViajeService` | Esta clase **cumple** el contrato de la interfaz. |

Servicios del proyecto:

| Interfaz | Implementación | Frente |
|---|---|---|
| `UsuarioService` | `UsuarioServiceImpl` | P2 |
| `CatalogoService` | `CatalogoServiceImpl` | P3 |
| `ViajeService` | `ViajeServiceImpl` | P4 |
| `ReservaService` | `ReservaServiceImpl` | P5 |

Ejemplo de regla (P5): en `crearReserva` se valida que no haya más de 5 asientos, que estén libres y que haya un pasajero por asiento. Recién después se hace `save`.

---

## 6. ¿Qué es `@Override`?

`@Override` es una anotación de **Java** (no de Spring).

Significa: “este método **ya existe** en la interfaz (o en la clase padre). Yo lo estoy **reescribiendo**”.

```java
@Override
public List<Viaje> listar() {
    return viajeRepository.listarConDetalle();
}
```

`listar()` está declarado en `ViajeService`. `ViajeServiceImpl` está obligado a implementarlo.

**Para qué sirve:** si te equivocas en el nombre (`listarr` con dos r), el compilador avisa. Sin `@Override` Java creería que es un método nuevo y el error aparecería más tarde.

En el video: ábrelo en `ViajeServiceImpl` y en `UsuarioDetailsService.loadUserByUsername` (ahí sobreescribe un método de Spring Security).

---

## 7. ¿Qué es `@Transactional`?

Una **transacción** es: “o se guarda **todo**, o no se guarda **nada**”.

Piensa en una reserva:

1. Se inserta la fila en `reserva`.
2. Se insertan filas en `detallereserva`.
3. Los asientos pasan a ocupados (`estado = 0`).

Si el paso 3 falla y no hay transacción, te quedaría una reserva a medias. Con `@Transactional`, MySQL **deshace** los pasos 1 y 2.

```java
@Service
@Transactional
public class ViajeServiceImpl implements ViajeService { ... }
```

Toda la clase corre dentro de una transacción.

```java
@Override
@Transactional(readOnly = true)
public List<Viaje> listar() { ... }
```

`readOnly = true` = “esto **solo lee**”. Hibernate no intenta hacer `UPDATE`. Es más claro y un poco más eficiente. Se usa en `listar`, `buscar`, `listarAsientos`.

Los métodos que **escriben** (`guardar`, `guardarConAsientos`, `crearReserva`, `cancelar`) dejan el `@Transactional` de la clase (lectura y escritura).

---

## 8. ¿Para qué es un repository?

El **repository** es la puerta a MySQL. **No escribimos SQL a mano** para un `findById` o un `save`.

```java
public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {
    List<Ciudad> findByEstado(Integer estado);
}
```

| Pieza | Significado |
|---|---|
| `interface` | No hay cuerpo. Spring crea la implementación al arrancar. |
| `JpaRepository<Ciudad, Integer>` | Trabaja con la entidad `Ciudad` y la PK es `Integer` (`IdCiudad`). |
| Métodos gratis | `findById`, `findAll`, `save`, `delete`. No se declaran. |
| `findByEstado` | Spring lee el nombre y genera `WHERE Estado = ?`. |

Cuando la consulta es más difícil, sí escribimos **JPQL** (parecido a SQL, pero con nombres de clases):

```java
@Query("select r from Ruta r join fetch r.ciudadPartida ...")
List<Ruta> listarConCiudades();
```

`join fetch` trae la ciudad en la misma consulta. Lo usamos porque en `application.yml` está `open-in-view: false`: después de que el service termina, Hibernate **cierra** la sesión. Si el HTML pide `ruta.ciudadPartida.ciudad` y no se hizo fetch, explota un `LazyInitializationException`.

El controller **no** usa el repository. El service sí.

---

## 9. Carpeta `entity`: una clase = una tabla

Ejemplo: `Ciudad.java` ↔ tabla `ciudad`.

```java
@Entity
@Table(name = "ciudad")
public class Ciudad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdCiudad")
    private Integer idCiudad;
    ...
}
```

| Anotación | Qué hace |
|---|---|
| `@Entity` | Hibernate la persiste. |
| `@Table(name = "ciudad")` | Nombre real en MySQL. |
| `@Id` | Clave primaria. |
| `@GeneratedValue(IDENTITY)` | MySQL asigna el número (`AUTO_INCREMENT`). |
| `@Column(name = "IdCiudad")` | El nombre de la columna **tal cual** en Workbench. |
| `@ManyToOne` + `@JoinColumn` | FK. Ejemplo: `Viaje` tiene una `Ruta`. |

Los nombres de columna no se “camelCasean” porque en `application.yml` usamos:

`PhysicalNamingStrategyStandardImpl`

Así `IdCiudad` se queda `IdCiudad`, igual que el script `sql/00_bd_reserva_buses.sql`.

Hay **11 entidades**, una por tabla. Detalle en [diseno-bd-jpa.md](diseno-bd-jpa.md).

---

## 10. Carpeta `security` (P2)

No confundir:

| Archivo | Carpeta | Rol |
|---|---|---|
| `SecurityConfig.java` | `config` | Quién entra a cada URL, login y logout |
| `PasswordConfig.java` | `config` | Crea el `BCryptPasswordEncoder` |
| `UsuarioDetailsService.java` | **`security`** | Traduce el DNI del formulario a un usuario de Spring |

### Por qué existe `security/UsuarioDetailsService`

Spring Security no conoce nuestra tabla `usuario`. Él espera un `UserDetailsService` con el método:

```java
@Override
public UserDetails loadUserByUsername(String nroDocumento)
```

En CiberBus el “username” **no es un correo**: es el **número de documento**. Por eso el formulario de login tiene `name="nroDocumento"`.

Qué hace el método:

1. Busca el usuario con `UsuarioService` (no con el repository, para respetar las capas).
2. Arma el rol: `ADMIN` → `ROLE_ADMIN` (Spring exige el prefijo `ROLE_` para `hasRole("ADMIN")`).
3. Si `estado = 0`, marca la cuenta como `disabled`.
4. Devuelve usuario + **clave ya encriptada** (BCrypt). Security compara el texto que escribió el usuario con el hash.

### `SecurityConfig` en una frase

- Público: `/`, `/inicio`, `/login`, `/registro`, `/viajes/**`, `/reserva/**`, CSS.
- Solo ADMIN: `/admin/**`.
- Login POST a `/login` con campos `nroDocumento` y `clave`.
- Si entra un ADMIN, lo manda a `/admin`; si es CLIENTE, a `/inicio`.
- CSRF activo: los formularios deben usar `th:action` para que Thymeleaf ponga el token.

### `PasswordConfig`

Está **separado** de `SecurityConfig` para no crear un ciclo:

`SecurityConfig` → `UsuarioDetailsService` → `UsuarioService` → `PasswordEncoder` → (si estuviera en SecurityConfig, se muerde la cola).

---

## 11. `config` y `exception`

- `PasswordConfig`: bean BCrypt.
- `SecurityConfig`: reglas de acceso.
- `NegocioException`: “El asiento ya está ocupado”. Es un error **de negocio**, no un bug.
- `GlobalExceptionHandler`: si salta esa excepción, muestra `error.html` en vez de una página blanca de Tomcat.

---

## 12. Thymeleaf (P6) en 30 segundos

Los HTML viven en `src/main/resources/templates/`.

| Expresión | Qué hace |
|---|---|
| `th:text="${viaje.codigoViaje}"` | Pone el código en el HTML |
| `th:each="c : ${ciudades}"` | Repite una fila por ciudad |
| `th:href="@{/login}"` | Link interno (respeta el contexto) |
| `th:action="@{/login}"` | Formulario POST **con CSRF** |
| `th:if="${usuario != null}"` | Muestra el menú de sesión |
| `th:replace="~{fragments/layout :: header}"` | Encabezado repetido |

`static/css/ciberbus.css` se sirve en `/css/ciberbus.css` (público en Security).

---

## 13. Recorrido de una reserva (para dibujarlo en la pizarra)

1. GET `/inicio` → `HomeController` → `ViajeService.listarOrigenesDisponibles()`.
2. GET `/viajes/buscar` → `ViajeController` → lista de viajes.
3. GET `/viajes/{id}/asientos` → mapa. JavaScript elige hasta 5. **Aún no guarda nada.**
4. POST `/viajes/resumen` → el **servidor** vuelve a validar (nunca confiar solo en el navegador).
5. POST `/reserva/checkout` → formulario de pasajeros.
6. POST `/reserva/procesar` → `ReservaService.crearReserva`:
   - usuario logueado o GUEST
   - máximo 5 asientos libres
   - inserta `reserva` + `detallereserva`
   - asientos `estado = 0`
7. GET `/reserva/detalle/{codigo}` → comprobante.
8. POST `/reserva/cancelar` → asientos vuelven a `1`.

Todo eso es **una transacción** en el paso 6.

---

## 14. Qué mostrar de cada frente (defensa / video)

| Frente | Archivo estrella | Frase de una línea |
|---|---|---|
| P1 | `entity/`, `pom.xml`, capas | “Armé el esqueleto: Maven, JPA y la regla controller-service-repository.” |
| P2 | `SecurityConfig`, `UsuarioDetailsService` | “El DNI es el usuario; la clave va con BCrypt; `/admin` solo ADMIN.” |
| P3 | `AdminMaestrosController`, `CatalogoServiceImpl` | “CRUD de maestros con baja lógica si no está en uso.” |
| P4 | `ViajeController`, `guardarConAsientos` | “Busco por ruta y fecha, y al crear el viaje genero los asientos.” |
| P5 | `ReservaServiceImpl.crearReserva` | “Confirmo, ocupo asientos y puedo consultar o cancelar por código.” |
| P6 | `templates/fragments/layout.html` | “Una sola cabecera y el CSS azul/oro en todas las pantallas.” |
