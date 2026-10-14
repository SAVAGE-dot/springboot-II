# CiberBus

Sistema de reserva de pasajes interprovinciales. Curso **5484 Lenguaje de Programación II** (4.º ciclo).

- Spring Boot 3 + Thymeleaf + Spring Security + Spring Data JPA + MySQL
- Repo: https://github.com/SAVAGE-dot/springboot-II
- Delegada: Kelly del Pilar Yaipen Sevillano
- Docente: Yan Carlos Bocanegra Pinchi

**Guía completa (paso a paso, desde lo básico):** [docs/GUIA-DEL-PROYECTO.md](docs/GUIA-DEL-PROYECTO.md)

Diseño de BD (sección 7.3): [docs/diseno-bd-jpa.md](docs/diseno-bd-jpa.md)

## Qué hace el sistema

1. El **admin** registra ciudades, turnos, buses, conductores, rutas y viajes.
2. Al crear un viaje se generan los asientos del bus.
3. El **cliente o invitado** busca origen / destino / fecha, elige hasta 5 asientos y confirma la reserva.
4. El pago es simulado. Queda un código de reserva y los asientos pasan a ocupados.
5. Se puede **consultar** la reserva por código y **cancelarla** (los asientos se liberan).
6. Un cliente nuevo se **registra** con su DNI (`/registro`).

## Equipo

| Frente | Integrante | Código principal |
|---|---|---|
| P1 Arquitectura | Ernesto Silva | entidades, repos, capas |
| P2 Seguridad | Cristian Arellano | login, roles, BCrypt |
| P3 Maestros | Antonio Ospina | `/admin` ciudad, turno, bus, conductor, ruta |
| P4 Viajes | Kelly Yaipen | búsqueda, asientos, alta de viajes |
| P5 Reserva | Brando Sandoval | checkout y comprobante |
| P6 UI | Luis Paredes | CSS y plantillas Thymeleaf |

## Cómo arrancar

1. MySQL: ejecutar `sql/00_bd_reserva_buses.sql` (y los scripts de datos si los necesitan).
2. Si tu clave no es `1234`, crea `src/main/resources/application-local.yml` (no se sube a Git) con tu password.
3. `.\mvnw.cmd spring-boot:run`
4. http://localhost:8080

Usuarios de prueba (se crean al arrancar si no existen):

- Admin: `12345678` / `admin123` → `/admin`
- Cliente: `87654321` / `cliente123`

## Arquitectura

**En capas. Patrón MVC + Service + Repository.**

```
controller → service → repository → MySQL
```

El controller **nunca** llama al repository.

No hay JSP ni Servlets. Las pantallas están en `src/main/resources/templates/`.

## Git (compañeros)

```
git clone https://github.com/SAVAGE-dot/springboot-II.git
cd springboot-II
git checkout main
git pull origin main
```

Ramas por frente (si siguen trabajando aparte): `feature/p2-security`, `feature/p3-maestros`, `feature/p4-viajes`, `feature/p5-reserva`, `feature/p6-ui`.
Pull request hacia `main`. No suban `application-local.yml` ni claves.
