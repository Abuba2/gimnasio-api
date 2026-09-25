# Gimnasio API

API REST para la gestión de reservas de clases de un gimnasio, construida con Java 21 y Spring Boot.

---

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.1 |
| Persistencia | Spring Data JPA · Hibernate |
| Base de datos | MySQL 8 |
| Validación | Bean Validation (Jakarta) |
| Build | Maven |
| Utilidades | Lombok |

---

## Modelo de datos

```
MONITOR ──1:N──> CLASE <──1:N── SALA
                   │
                  1:N
                   ↓
SOCIO ───1:N───> RESERVA
```

`Socio` y `Clase` mantienen una relación N:M que resuelve la tabla `reserva`. No es una simple tabla puente: guarda datos propios (fecha de la reserva y estado), así que es una entidad de pleno derecho.

---

## Reglas de negocio

Una reserva solo se acepta si pasa las cuatro comprobaciones:

| # | Regla | Respuesta si falla |
|---|---|---|
| 1 | El socio debe estar activo | `409 Conflict` |
| 2 | La clase no puede haber pasado | `409 Conflict` |
| 3 | El socio no puede tener ya una reserva confirmada en esa clase | `409 Conflict` |
| 4 | No se puede superar el aforo de la clase | `409 Conflict` |

Las reglas 3 y 4 solo cuentan las reservas en estado `CONFIRMADA`. Una reserva cancelada libera la plaza y permite al socio volver a apuntarse.

Las comprobaciones se ordenan de la más barata a la más cara: primero los campos ya cargados en memoria, y solo después las consultas a la base de datos.

---

## Endpoints

### Salas

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/salas` | Crear sala |
| `GET` | `/api/salas` | Listar |
| `GET` | `/api/salas/{id}` | Buscar por id |

### Monitores

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/monitores` | Crear monitor |
| `GET` | `/api/monitores` | Listar. Admite `?especialidad=` |
| `GET` | `/api/monitores/{id}` | Buscar por id |

### Socios

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/socios` | Alta de socio |
| `GET` | `/api/socios` | Listar. Admite `?activo=true|false` |
| `GET` | `/api/socios/{id}` | Buscar por id |
| `PATCH` | `/api/socios/{id}/baja` | Baja lógica |

### Clases

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/clases` | Programar clase |
| `GET` | `/api/clases` | Listado resumido |
| `GET` | `/api/clases/detalle` | Listado con sala y monitor |

### Reservas

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/reservas` | Reservar plaza |
| `GET` | `/api/reservas` | Listar con socio y clase |
| `PATCH` | `/api/reservas/{id}/cancelar` | Cancelar |

---

## Decisiones de diseño

**Arquitectura en capas estricta.** `Controller → Service → Repository`. El controlador no contiene reglas de negocio ni bloques `try/catch`; el repositorio no toma decisiones. Cada capa habla solo con la de abajo.

**DTOs de entrada y salida.** Las entidades nunca se exponen por HTTP. Evita que el cliente pueda imponer campos que controla el servidor (como el `id`, que convertiría un `POST` en un `UPDATE` encubierto) y desacopla el esquema de la base de datos del contrato de la API.

**Manejo de errores centralizado.** Un único `@RestControllerAdvice` traduce cada excepción a su código HTTP. Las excepciones de negocio heredan de `RuntimeException`: las capas intermedias no pueden resolverlas, así que obligarlas a declararlas solo añadiría ruido.

**Carga perezosa por defecto.** Todas las relaciones `@ManyToOne` declaran `fetch = LAZY` de forma explícita, porque el valor por defecto de JPA es `EAGER`. Donde se sabe que la relación se va a usar, se emplea `JOIN FETCH` para evitar el problema N+1: en el listado de clases con detalle, eso reduce de 7 consultas a 1.

**`open-in-view` desactivado.** La conversión de entidad a DTO se hace dentro de la capa de servicio, con la transacción abierta. Es más trabajo, pero ninguna consulta se lanza fuera de la capa que debe controlarlas.

**Baja lógica en lugar de borrado.** Socios y reservas se marcan como inactivos o cancelados, no se eliminan. Borrar un socio dejaría sus reservas históricas apuntando al vacío.

**Estados como `enum` con `EnumType.STRING`.** Nunca `ORDINAL`: guarda la posición en el enum, así que insertar un valor nuevo en medio cambiaría el significado de todas las filas existentes.

**Credenciales fuera del código.** La contraseña de la base de datos se lee de la variable de entorno `DB_PASSWORD`.

---

## Limitaciones conocidas

**Condición de carrera en el control de aforo.** Entre el recuento de plazas ocupadas y el guardado de la reserva existe una ventana en la que dos peticiones simultáneas podrían superar el aforo. Se resolvería con bloqueo optimista (`@Version`) o con una restricción a nivel de base de datos.

**`ddl-auto=update`.** Válido en desarrollo, inaceptable en producción: da permiso a Hibernate para alterar el esquema real. La versión desplegada usará `validate` junto con migraciones versionadas.

---

## Puesta en marcha

**Requisitos:** JDK 21, Maven, MySQL 8.

```sql
CREATE DATABASE gimnasio;
```

```bash
export DB_PASSWORD=tu_contraseña      # Windows: set DB_PASSWORD=tu_contraseña
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

```bash
curl -X POST http://localhost:8080/api/salas \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Sala de spinning","aforo":20}'
```

---

## Pendiente

- [ ] Tests unitarios y de integración (JUnit 5 + Mockito)
- [ ] Autenticación con Spring Security y JWT
- [ ] Documentación con OpenAPI / Swagger
- [ ] Contenedorización con Docker
- [ ] Integración continua con GitHub Actions
- [ ] Despliegue
