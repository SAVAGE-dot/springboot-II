# CiberBus

Proyecto del curso **5484 Lenguaje de Programación II** (cuarto ciclo). Sistema de pasajes en **Spring Boot 3**, **Thymeleaf** y **MySQL**.

- Repo: https://github.com/SAVAGE-dot/springboot-II
- Delegada: Kelly del Pilar Yaipen Sevillano
- Docente: Yan Carlos Bocanegra Pinchi

## Estado de la primera instancia (P1)

Listo en `main`:

- Proyecto Maven / Spring Boot (sin Servlets ni JSP)
- Entidades JPA y repositorios de `bd_reserva_buses`
- Capa de servicio por frente (el controller no llama al repository)
- Security abierto (`permitAll`) para que P2 lo reemplace
- Página de inicio en Thymeleaf

Pendiente por frente: login (P2), CRUD maestros (P3), búsqueda y asientos (P4), reserva (P5), UI completa (P6).

## Arquitectura y patrón

**Arquitectura: en capas.**  
**Patrón: MVC + Service + Repository.**

```
Navegador
    │
    ▼
controller     presentación (Thymeleaf). Recibe HTTP, llama al service, no al repository.
    │
    ▼
service        negocio. Una interfaz por frente. Validaciones y transacciones.
    │
    ▼
repository     Spring Data JPA. Acceso a MySQL.
    │
    ▼
entity         tablas de bd_reserva_buses
```

| Capa | Paquete | Qué va ahí |
|---|---|---|
| Presentación | `com.ciberbus.controller` | Controllers y rutas web |
| Negocio | `com.ciberbus.service` / `service.impl` | Interfaces + implementaciones |
| Persistencia | `com.ciberbus.repository` | `JpaRepository` |
| Modelo | `com.ciberbus.entity` | Entidades JPA |
| Infraestructura | `com.ciberbus.config` | Security y configuración |
| Errores | `com.ciberbus.exception` | `NegocioException` y handler global |

Regla: **controller → service → repository**. Nunca `controller → repository`.

Servicios por frente:

| Interfaz | Frente | Integrante |
|---|---|---|
| `UsuarioService` | Seguridad | Cristian Arellano (P2) |
| `CatalogoService` | Maestros | Antonio Ospina (P3) |
| `ViajeService` | Viajes / asientos | Kelly Yaipen (P4) |
| `ReservaService` | Reserva / autogestión | Brando Sandoval (P5) |
| templates Thymeleaf | UI | Luis Paredes (P6) |

P5 consume `ViajeService.buscar()` y `listarAsientos()`. No use `ViajeAsientoRepository` directo.

## Cómo arrancar

1. Clonar (ver comandos abajo).
2. En MySQL Workbench: `sql/00_bd_reserva_buses.sql`. Si falta, `sql/01_ampliar_usuario.sql`.
3. Usuario/clave en `src/main/resources/application.yml` (por defecto `root` / `1234`).
4. `.\mvnw.cmd spring-boot:run`
5. http://localhost:8080

## Git — Ernesto (primera vez, ya hecho en el repo)

```
git init
git add .
git commit -m "Initial Spring Boot layered architecture for CiberBus."
git branch -M main
git remote add origin https://github.com/SAVAGE-dot/springboot-II.git
git push -u origin main
```

## Git — compañeros (hacer una sola vez)

```
git clone https://github.com/SAVAGE-dot/springboot-II.git
cd springboot-II
```

### Cómo crear su rama (una sola vez)

Una **rama** es una copia de trabajo paralela a `main`. Cada integrante crea la suya para no pisar el código de los demás. El nombre de la rama es el de su frente (tabla de abajo). El ejemplo usa `feature/p2-security`; cada uno cambia esa parte por **su** rama.

```
git checkout main
git pull origin main
git checkout -b feature/p2-security
```

Qué hace cada comando:

1. `git checkout main` — se posiciona en la rama estable del equipo.
2. `git pull origin main` — descarga lo último que ya está en GitHub, para no partir de un `main` viejo.
3. `git checkout -b feature/p2-security` — **crea** la rama nueva (`-b`) y entra en ella. A partir de aquí, todos los commits quedan en esa rama, no en `main`.

Si ya la crearon y solo quieren seguir trabajando otro día:

```
git checkout feature/p2-security
```

No vuelvan a usar `-b` si la rama ya existe (Git dirá que ya está creada).

Nombre de rama según el frente:

| Integrante | Rama |
|---|---|
| Cristian Arellano | `feature/p2-security` |
| Antonio Ospina | `feature/p3-maestros` |
| Kelly Yaipen | `feature/p4-viajes` |
| Brando Sandoval | `feature/p5-reserva` |
| Luis Paredes | `feature/p6-ui` |

Cada vez que terminen un avance:

```
git add .
git commit -m "Describe el cambio en inglés o español, una frase."
git push -u origin feature/p2-security
```

En GitHub: **Pull request** hacia `main`. No suban a `main` directo.

Si `main` avanzó mientras trabajaban:

```
git checkout main
git pull origin main
git checkout feature/p2-security
git merge main
```

No copiar Servlets, JSP ni `MySQLConexion` a este proyecto.
