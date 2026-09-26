# Gimnasio API

API REST para la gestión de reservas de clases de un gimnasio, construida con Java 21 y Spring Boot.

**Desplegada:** [gimnasio-api-production-19a6.up.railway.app](https://gimnasio-api-production-19a6.up.railway.app/swagger-ui.html)

Para probarla: regístrate en `POST /api/auth/registro`, copia el token de la respuesta, pulsa **Authorize** en Swagger y pégalo.

---

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.1 |
| Persistencia | Spring Data JPA · Hibernate |
| Base de datos | MySQL 8 |
| Validación | Bean Validation (Jakarta) |
| Seguridad | Spring Security · JWT (JJWT) |
| Documentación | OpenAPI / Swagger (springdoc) |
| Tests | JUnit 5 · Mockito · AssertJ · H2 |
| Contenedores | Docker · Docker Compose |
| CI | GitHub Actions |
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

## Autenticación

Todos los endpoints salvo `/api/auth/**` y la documentación requieren un token JWT en la cabecera:

```
Authorization: Bearer <token>
```

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/auth/registro` | Alta de usuario. Devuelve el token |
| `POST` | `/api/auth/login` | Autenticación. Devuelve el token |

Dos roles: `USER` consulta clases y gestiona sus reservas; `ADMIN` administra salas, monitores, socios y consulta todas las reservas.

El administrador inicial se crea al arrancar si se definen las variables `ADMIN_EMAIL` y `ADMIN_PASSWORD`.

---

## Documentación interactiva

Con la aplicación en marcha:

```
http://localhost:8080/swagger-ui.html
```

Incluye el botón **Authorize** para pegar el token y probar los endpoints protegidos desde el navegador.

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

**Credenciales fuera del código.** Ni la contraseña de la base de datos ni la clave de firma de los tokens aparecen en el repositorio: se leen de las variables de entorno `DB_PASSWORD` y `JWT_SECRET`.

**Autenticación sin estado.** La sesión se configura como `STATELESS`: el servidor no guarda nada entre peticiones y la identidad viaja en el token, firmado con HMAC. Eso permite escalar a varias instancias sin sesiones compartidas.

**Contraseñas con BCrypt.** Se almacena el hash, nunca la contraseña. BCrypt incorpora una sal distinta por usuario y es deliberadamente lento, lo que encarece los ataques por fuerza bruta.

**Errores de autenticación en el mismo formato que el resto.** Un `AuthenticationEntryPoint` y un `AccessDeniedHandler` propios devuelven 401 y 403 con la misma estructura JSON que el resto de la API, en lugar de la página de error por defecto de Spring Security.

---

## Limitaciones conocidas

**Condición de carrera en el control de aforo.** Entre el recuento de plazas ocupadas y el guardado de la reserva existe una ventana en la que dos peticiones simultáneas podrían superar el aforo. Se resolvería con bloqueo optimista (`@Version`) o con una restricción a nivel de base de datos.

**`ddl-auto=update`.** Válido en desarrollo, inaceptable en producción: da permiso a Hibernate para alterar el esquema real. La versión desplegada usará `validate` junto con migraciones versionadas.

---

## Puesta en marcha con Docker

La forma recomendada: levanta la base de datos y la aplicación con un solo comando.

```bash
cp .env.example .env     # y edita los valores
docker compose up --build
```

---

## Puesta en marcha manual

**Requisitos:** JDK 21 y MySQL 8.

```sql
CREATE DATABASE gimnasio;
```

```bash
export DB_PASSWORD=tu_contraseña
export JWT_SECRET=una-clave-de-al-menos-32-caracteres
export ADMIN_EMAIL=admin@gimnasio.com
export ADMIN_PASSWORD=tu_contraseña_de_admin

./mvnw spring-boot:run
```

### Primer uso

```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{"email":"socio@mail.com","password":"micontrasena123"}'
```

La respuesta incluye el token. A partir de ahí:

```bash
curl http://localhost:8080/api/clases \
  -H "Authorization: Bearer <token>"
```

---

## Tests

```bash
./mvnw test
```

11 tests. Los de servicio usan mocks y no necesitan base de datos; el de contexto levanta la aplicación contra H2 en memoria. GitHub Actions los ejecuta en cada push a `main`.

---

## Pendiente

- [ ] Despliegue en un proveedor cloud
- [ ] Bloqueo optimista para la condición de carrera del aforo
- [ ] Migraciones versionadas (Flyway) para sustituir `ddl-auto`
- [ ] Tests de controlador con MockMvc
