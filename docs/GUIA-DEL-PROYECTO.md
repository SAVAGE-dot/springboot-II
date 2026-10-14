# Guía del proyecto CiberBus (desde lo más básico)

Este documento explica el sistema **como si nunca hubieras visto Spring Boot**.
Sirve para entender, instalar, usar y defender el proyecto en la sustentación.

---

## 1. ¿Qué es CiberBus?

CiberBus es un **sistema web de reserva de pasajes interprovinciales**.

En palabras simples:

1. Un **administrador** carga ciudades, buses, conductores, rutas y viajes.
2. Un **cliente** (o un invitado) busca un viaje, elige asientos (máximo 5) y confirma la reserva.
3. El sistema guarda la reserva en MySQL y marca esos asientos como ocupados.

No es una app de celular. Se usa desde el navegador: `http://localhost:8080`.

Curso: **5484 Lenguaje de Programación II** (4.º ciclo).
Docente: Yan Carlos Bocanegra Pinchi.
Delegada: Kelly del Pilar Yaipen Sevillano.
Repositorio: https://github.com/SAVAGE-dot/springboot-II

---

## 2. Equipo y qué cubre cada uno

El trabajo se dividió en 6 frentes. En el código, cada frente tiene sus clases.

| Frente | Integrante | Qué hace en el sistema | Dónde está en el código |
|---|---|---|---|
| P1 Arquitectura | Ernesto Silva | Armó el proyecto, las capas y las tablas JPA | `entity`, `repository`, `config`, esta guía |
| P2 Seguridad | Cristian Arellano | Login, registro, roles ADMIN/CLIENTE, clave encriptada | `SecurityConfig`, `UsuarioService`, `RegistroController` |
| P3 Maestros | Antonio Ospina | CRUD de ciudad, turno, bus, conductor y ruta | `CatalogoService`, `AdminMaestroController` |
| P4 Viajes | Kelly Yaipen | Buscar viaje, mapa de asientos, crear viajes | `ViajeService`, `ViajeController`, `AdminViajeController` |
| P5 Reserva | Brando Sandoval | Checkout, consultar por código, cancelar, comprobante | `ReservaService`, `ReservaController` |
| P6 Frontend | Luis Paredes | Encabezado, CSS y pantallas Thymeleaf | `templates/`, `static/css/ciberbus.css` |

---

## 3. Palabras que hay que entender (glosario)

| Palabra | Significado simple |
|---|---|
| **Spring Boot** | El “motor” de Java que levanta el servidor web solo. No usamos Tomcat a mano ni JSP. |
| **Maven** | Herramienta que descarga librerías y compila. El comando es `mvnw`. |
| **MySQL** | La base de datos. Ahí viven las tablas (ciudad, viaje, reserva…). |
| **JPA / Hibernate** | Traduce una clase Java (`Viaje`) a una tabla MySQL (`viaje`). |
| **Entidad** | Clase Java que representa una tabla. Ejemplo: `Ciudad.java` = tabla `ciudad`. |
| **Repository** | Interfaz que pregunta o guarda datos. Spring escribe el SQL por nosotros. |
| **Service** | Capa de reglas: “no más de 5 asientos”, “origen ≠ destino”. |
| **Controller** | Recibe lo que el navegador pide (`/login`, `/admin/ciudades`) y elige qué HTML mostrar. |
| **Thymeleaf** | Los HTML de `templates/`. Pueden mostrar datos con `th:text`. **No son JSP.** |
| **CSRF** | Token de seguridad en los formularios. Thymeleaf lo pone solo si usas `th:action`. |
| **BCrypt** | Forma de guardar la clave. En la BD no se ve `admin123`, se ve un hash. |
| **Rol** | `ADMIN` entra al panel. `CLIENTE` reserva. Invitado reserva sin login. |
| **Estado 1 / 0** | 1 = activo o asiento libre. 0 = inactivo u ocupado. |
| **Invitado (GUEST)** | Usuario especial para comprar sin iniciar sesión. |

---

## 4. Cómo está armado el proyecto (arquitectura)

Usamos **arquitectura en capas** y el patrón **MVC + Service + Repository**.

```
Navegador (tú)
    │  pide /inicio o envía un formulario
    ▼
CONTROLLER   (presentación)
    │  no habla con la base de datos
    ▼
SERVICE      (negocio / reglas)
    │  sí puede validar y llamar a varios repositorios
    ▼
REPOSITORY   (Spring Data JPA)
    │
    ▼
MYSQL        bd_reserva_buses
```

Regla del curso (importante para la rúbrica):

**controller → service → repository**

Nunca un controller usa un repository directo.

Paquetes:

```
com.ciberbus
 ├── CiberbusApplication.java     ← arranque
 ├── controller                   ← pantallas / URLs
 ├── service                      ← interfaces (contratos)
 ├── service.impl                 ← código real de las reglas
 ├── repository                   ← acceso a tablas
 ├── entity                       ← las 11 tablas
 ├── config                       ← seguridad y password
 ├── security                     ← cómo Spring busca al usuario
 └── exception                    ← errores de negocio
```

Pantallas:

```
src/main/resources/templates/     ← HTML Thymeleaf
src/main/resources/static/css/    ← estilos
src/main/resources/application.yml ← conexión a MySQL
```

---

## 5. Las 11 tablas (qué guarda cada una)

| Tabla | Para qué sirve | Relación importante |
|---|---|---|
| `usuario` | Quien inicia sesión o compra | Rol ADMIN o CLIENTE |
| `ciudad` | Lima, Trujillo, etc. | La usa `ruta` |
| `turno` | Mañana / tarde del conductor | La usa `conductor` |
| `bus` | Placa, tipo, cantidad de asientos | La usa `viaje` |
| `conductor` | Chofer y licencia | Pertenece a un `turno` |
| `ruta` | Origen + destino + horas | Une dos `ciudad` |
| `viaje` | Un bus en una ruta un día y hora | Une ruta + bus + conductor |
| `viajeasiento` | Cada butaca de ese viaje | 1 = libre, 0 = ocupado |
| `pasajero` | Persona que viaja (puede no ser el comprador) | Va en el detalle |
| `reserva` | La compra (código, total, pago) | Pertenece a un usuario y un viaje |
| `detallereserva` | Qué asiento y qué pasajero van en esa reserva | Une reserva + asiento + pasajero |

Orden lógico para cargar datos (si la BD está vacía):

1. Ciudades
2. Turnos
3. Buses
4. Conductores (necesitan un turno)
5. Rutas (necesitan dos ciudades distintas)
6. Viajes (necesitan ruta + bus + conductor). Al crear el viaje se generan los asientos.

Si no hay viaje para esa fecha, el cliente no verá resultados. Eso es normal.

---

## 6. Instalar y correr (paso a paso)

### 6.1 Lo que debes tener instalado

- **JDK 17** (o 18). Java.
- **Maven** no es obligatorio: el proyecto trae `mvnw.cmd`.
- **MySQL** (Workbench sirve).
- Un editor (IntelliJ, Eclipse o VS Code / Cursor).

### 6.2 Crear la base de datos

1. Abre MySQL Workbench.
2. Conéctate como `root`.
3. Abre y ejecuta, en este orden:
   - `sql/00_bd_reserva_buses.sql` (crea `bd_reserva_buses` y las tablas)
   - `sql/01_datos_prueba.sql` (ciudades, buses, etc. si el script existe)
   - `sql/02_viajes_prueba_sep_nov_2026.sql` (viajes de prueba, si lo necesitan)

Si Hibernate tiene `ddl-auto: update`, también puede crear/ajustar columnas al arrancar.
Aun así, es más seguro correr el SQL primero.

### 6.3 Contraseña de MySQL

El archivo `src/main/resources/application.yml` trae de ejemplo:

- usuario: `root`
- clave: `1234`
- base: `bd_reserva_buses`
- puerto de la app: `8080`

Si tu MySQL usa **otra clave**, crea (solo en tu PC, no se sube a GitHub):

`src/main/resources/application-local.yml`

```yml
spring:
  datasource:
    username: root
    password: TU_CLAVE
```

Ese archivo está en `.gitignore`. **No lo suban.** Ahí va la clave real.

### 6.4 Arrancar

En la carpeta del proyecto:

```
.\mvnw.cmd spring-boot:run
```

Espera a ver en consola algo como:

- `HikariPool-1 - Start completed` (ya conectó a MySQL)
- `Started CiberbusApplication` (la web ya está lista)

Abre el navegador: **http://localhost:8080**

Para detener: `Ctrl + C` en la consola.

---

## 7. Usuarios de prueba

Al arrancar, la clase `CiberbusApplication` crea estos usuarios **si no existen**:

| Rol | Documento | Clave | Qué ve |
|---|---|---|---|
| ADMIN | `12345678` | `admin123` | Panel `/admin` |
| CLIENTE | `87654321` | `cliente123` | Inicio y reservas |

La clave se guarda con **BCrypt**. En la tabla `usuario` no aparece `admin123` en texto plano.

También existe (o se crea al reservar sin login) el usuario **GUEST** para invitados.

---

## 8. Cómo usar el sistema (como usuario)

### 8.1 Cliente o invitado: comprar un pasaje

Flujo oficial del proyecto (P4 + P5):

1. Entra a http://localhost:8080 (no es obligatorio iniciar sesión).
2. Elige **origen**. La página se recarga y llena los destinos de ese origen.
3. Elige **destino** y **fecha**. Clic en **Buscar viajes**.
4. Aparece la lista. Clic en **Elegir asientos**.
5. Marca de 1 a 5 asientos libres. Los grises ya están ocupados.
6. **Ver resumen**. Revisa ruta, asientos y total.
7. **Continuar a reserva**. Llena un pasajero por asiento y el método de pago (es simulado: no cobra de verdad).
8. Confirmar. El sistema te da un **código de reserva**.
9. En **Mis reservas** ves el historial (si entraste como cliente; si eres invitado, ves las del usuario GUEST).
10. En **Consultar reserva** cualquier persona pega el código y ve el comprobante (asientos y pasajeros).
11. Si la reserva está CONFIRMADA, se puede **cancelar**: los asientos vuelven a quedar libres.

Para crear una cuenta de cliente: **Registrarse** (el DNI será el usuario del login).

Otra búsqueda alternativa (P5): `/reserva/buscar` (origen, destino y fecha en una sola pantalla).

### 8.2 Administrador: cargar maestros y viajes

1. Inicia sesión con `12345678` / `admin123`.
2. Te manda a `/admin`.
3. Entra a cada módulo y guarda:
   - Ciudades
   - Turnos
   - Buses (el número de asientos importa: así se crean las butacas del viaje)
   - Conductores
   - Rutas (origen y destino distintos)
   - Viajes (código único, por ejemplo `CB-100`)

Al **crear un viaje nuevo**, el sistema genera un asiento por cada `nroAsientos` del bus, todos en piso 1 y estado 1 (libre).

---

## 9. URLs que existen (para la demo)

| URL | Quién puede | Qué hace |
|---|---|---|
| `/` o `/inicio` | Todos | Buscar origen / destino / fecha |
| `/login` | Todos | Iniciar sesión |
| `/registro` | Todos | Crear cuenta de cliente |
| `/viajes/buscar` | Todos | Lista de viajes |
| `/viajes/{id}/asientos` | Todos | Mapa de asientos |
| `/viajes/resumen` (POST) | Todos | Resumen antes de pagar |
| `/reserva/checkout` (POST) | Todos | Datos de pasajeros |
| `/reserva/procesar` (POST) | Todos | Crea la reserva |
| `/reserva/detalle/{codigo}` | Todos | Comprobante con pasajeros |
| `/reserva/consultar` | Todos | Autogestión: buscar por código |
| `/reserva/cancelar` (POST) | Todos | Cancela y libera asientos |
| `/reserva/mis-reservas` | Todos | Lista de reservas del usuario actual o GUEST |
| `/reserva/buscar` | Todos | Búsqueda alternativa |
| `/admin` y `/admin/**` | Solo ADMIN | Maestros, viajes y listado de reservas |

Si un CLIENTE entra a `/admin`, Spring Security lo bloquea (403). Eso se demuestra en la sustentación.

---

## 10. Qué hace cada clase (para explicar el código)

### P1 — base

- `entity/*`: una clase = una tabla. Los `@Column` usan los mismos nombres de Workbench (`IdCiudad`, `NroPlaca`…) gracias a `PhysicalNamingStrategyStandardImpl`.
- `repository/*`: `extends JpaRepository<Entidad, Integer>`. Con eso ya tienes `findById` y `save`.
- `exception/NegocioException`: error de regla (“máximo 5 asientos”).
- `exception/GlobalExceptionHandler`: muestra `error.html` en vez de una página blanca.

### P2 — seguridad

- `PasswordConfig`: bean de `BCryptPasswordEncoder`.
- `UsuarioService.registrar`: encripta la clave antes de guardar.
- `UsuarioDetailsService`: Spring pregunta “¿quién es 12345678?” y el service busca por `NroDocumento`.
- `SecurityConfig`:
  - login en `/login` con campos `nroDocumento` y `clave`
  - `/admin/**` exige rol ADMIN
  - `/viajes/**` y `/reserva/**` son públicos para poder comprar como invitado
  - CSRF activo (los formularios usan `th:action`)

### P3 — maestros

- `CatalogoService` lista y guarda ciudad, turno, bus, conductor y ruta.
- `AdminMaestroController` recibe los formularios HTML y llama al service.
- No se borran filas a la fuerza (evita romper llaves foráneas). Se puede marcar estado inactivo.

### P4 — viajes

- `ViajeService.buscarDisponiblesPorRutaYFecha`: viajes activos de esa ruta y fecha.
- `listarAsientos`: butacas de un viaje.
- `validarAsientosSeleccionados`: entre 1 y 5, distintos, y estado = 1.
- `guardarConAsientos`: guarda el viaje y, si no tenía asientos, los crea.
- `ViajeController`: pantallas del cliente.
- `AdminViajeController`: alta de viajes.

### P5 — reserva

- `ReservaService.crearReserva`:
  - valida asientos
  - crea o reutiliza pasajeros
  - guarda `reserva` + `detallereserva`
  - pone los asientos en estado 0
  - genera un código (ejemplo `CB-EE30058D`)
- El pago es **simulado**: solo se guarda el método (Yape, tarjeta, efectivo).

### P6 — interfaz

- `static/css/ciberbus.css`: colores azul `#1b365d` y oro `#c5a46e`.
- `templates/fragments/layout.html`: encabezado y pie que se repiten.
- `UsuarioModelAdvice`: pone el usuario logueado en todas las pantallas para el menú.

---

## 11. Flujo interno de una reserva (para el informe)

Ejemplo: invitado compra 1 asiento Lima → Trujillo.

1. `HomeController` muestra orígenes (ciudades que ya tienen ruta activa).
2. Al elegir origen, vuelve a `/` con `origenId` y carga destinos.
3. `GET /viajes/buscar?rutaId=…&fecha=…` → `ViajeService` busca en MySQL.
4. El usuario elige asientos. El JavaScript del HTML no guarda nada; solo arma la lista de IDs.
5. `POST /viajes/resumen` valida otra vez en el servidor (nunca confiar solo en el navegador).
6. `POST /reserva/checkout` pide datos de cada pasajero.
7. `POST /reserva/procesar` llama a `ReservaService.crearReserva`.
8. Si no hay sesión, se usa el usuario GUEST.
9. Los asientos pasan a ocupados. Otro cliente ya no puede tomarlos.
10. Se muestra el comprobante.

---

## 12. Cómo sacar capturas para el informe

### Captura de Workbench (tablas y FK)

1. Workbench → `bd_reserva_buses`.
2. Database → Reverse Engineer (o pestaña **EER Diagram**).
3. Aparecen las tablas y las rayas de llave foránea.
4. Recorta la imagen y pégala en la sección 7.3.

También sirve: clic derecho en una tabla → **Table Inspector** → Foreign Keys.

### Captura de Spring Boot arrancado

1. Corre `.\mvnw.cmd spring-boot:run`.
2. En la consola busca:
   - `HikariPool-1 - Starting...`
   - `HikariPool-1 - Start completed.`
   - `Started CiberbusApplication in X seconds`
3. Captura esa zona de la consola.

### Capturas de pantallas

- Inicio con el formulario
- Login
- Lista de viajes
- Mapa de asientos
- Comprobante con código
- Panel admin (ciudades / viajes)

---

## 13. Problemas frecuentes

| Qué ves | Causa más común | Qué hacer |
|---|---|---|
| `Access denied for user 'root'` | La clave de MySQL no es la de `application.yml` | Usa `application-local.yml` con tu clave |
| `Unknown database 'bd_reserva_buses'` | No corriste el SQL | Ejecuta `sql/00_bd_reserva_buses.sql` |
| Puerto 8080 ocupado | Ya hay otra app corriendo | Cierra la otra o cambia `server.port` |
| No salen destinos | No hay rutas activas para ese origen | Crea rutas en `/admin/rutas` |
| No salen viajes | No hay viaje en esa fecha o está inactivo | Crea viaje en `/admin/viajes` o usa las fechas del SQL de prueba |
| No salen asientos | El viaje se creó sin generar butacas | Crea el viaje desde el admin (ahí se generan) |
| 403 en `/admin` | Entraste como CLIENTE | Entra con `12345678` / `admin123` |
| Error al guardar bus | Placa repetida | Cambia la placa |

---

## 14. Qué NO hay en este proyecto (a propósito)

- No hay JSP ni Servlets.
- No hay PostgreSQL: es **MySQL**.
- No hay pasarela de pago real (Yape/tarjeta solo se registran).
- No hay reportes avanzados ni API REST extra.
- El código es de nivel curso: formularios, CRUD, validaciones simples.

Eso cumple la rúbrica del sistema sin meter cosas de senior.

---

## 15. Cómo explicar esto en 2 minutos

> “CiberBus es un sistema web en Spring Boot con arquitectura en capas. El administrador registra ciudades, buses, rutas y viajes. El cliente busca por origen, destino y fecha, elige hasta 5 asientos y confirma la reserva. Spring Security controla el login por DNI y el rol ADMIN. JPA guarda todo en MySQL `bd_reserva_buses`. Las pantallas son Thymeleaf, no JSP.”

Si preguntan por capas: “El controller solo recibe el HTTP, el service tiene las reglas, el repository habla con la base.”
