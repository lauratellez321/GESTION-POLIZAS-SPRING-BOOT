# Gestión de pólizas

API para consultar pólizas individuales y colectivas, renovar su vigencia y administrar riesgos. Está desarrollada con Spring Boot, Spring Data JPA y H2.

## Ejecutar el proyecto

Se necesita Java 21 y Maven 3.9 o una versión posterior.

```bash
mvn spring-boot:run
```

El servicio queda disponible en `http://localhost:8080`. Al iniciar se cargan dos pólizas para probar la API:

| ID | Tipo | Riesgos iniciales |
|---|---|---:|
| 1 | `INDIVIDUAL` | 1 |
| 2 | `COLECTIVA` | 2 |

La base H2 funciona en memoria. Los cambios se pierden al detener la aplicación y los datos de ejemplo se cargan de nuevo en el siguiente inicio.

## Autenticación

Todas las solicitudes deben incluir la cabecera `x-api-key`. El valor configurado para ejecutar el proyecto localmente es `123456`.

```bash
curl -H 'x-api-key: 123456' http://localhost:8080/polizas
```

Se puede establecer otro valor con la variable de entorno `API_KEY`. Si se cambia el puerto del servidor, también hay que ajustar `CORE_URL` para que las operaciones de escritura encuentren el mock del CORE. Por ejemplo, para el puerto 8081:

```bash
CORE_URL=http://localhost:8081 mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/polizas` | Lista pólizas. Admite los filtros opcionales `tipo` y `estado`. |
| `GET` | `/polizas/{id}/riesgos` | Consulta los riesgos de una póliza. |
| `POST` | `/polizas/{id}/renovar` | Renueva la póliza con el IPC recibido. |
| `POST` | `/polizas/{id}/cancelar` | Cancela la póliza y sus riesgos. |
| `POST` | `/polizas/{id}/riesgos` | Agrega un riesgo a una póliza colectiva. |
| `POST` | `/riesgos/{id}/cancelar` | Cancela un riesgo. |
| `POST` | `/core-mock/evento` | Registra en el log un intento de actualización del CORE. |

Los valores de `tipo` son `INDIVIDUAL` y `COLECTIVA`. Los estados de póliza son `VIGENTE`, `RENOVADA` y `CANCELADA`.

### Ejemplos

Listar pólizas colectivas vigentes:

```bash
curl -H 'x-api-key: 123456' \
  'http://localhost:8080/polizas?tipo=COLECTIVA&estado=VIGENTE'
```

Renovar la póliza 1 con un IPC del 5,2 %:

```bash
curl -X POST http://localhost:8080/polizas/1/renovar \
  -H 'x-api-key: 123456' \
  -H 'Content-Type: application/json' \
  -d '{"ipcPorcentaje":5.2}'
```

Agregar un riesgo a la póliza colectiva 2:

```bash
curl -X POST http://localhost:8080/polizas/2/riesgos \
  -H 'x-api-key: 123456' \
  -H 'Content-Type: application/json' \
  -d '{"descripcion":"Local 203"}'
```

## Reglas implementadas

- Una póliza individual tiene un único riesgo. El endpoint para agregar riesgos acepta solo pólizas colectivas.
- Una póliza cancelada no se puede renovar ni recibir riesgos nuevos.
- Al cancelar una póliza se cancelan todos sus riesgos.
- El IPC se envía como porcentaje entre 0 y 100. La renovación calcula el canon nuevo como `canon actual × (1 + IPC / 100)`, lo redondea a dos decimales y calcula la prima multiplicando el canon por los meses de vigencia.
- La renovación comienza el día siguiente al final de la vigencia anterior y mantiene la duración inicial.
- Antes de cada cambio, la aplicación envía un evento `ACTUALIZACION` al endpoint `/core-mock/evento`. Ese endpoint solo registra el intento en el log; no actualiza un CORE real.

## Pruebas

```bash
mvn test
```

Las pruebas cubren la clave API, el filtrado de pólizas, el cálculo de renovación, la restricción por tipo y la cancelación de riesgos al cancelar una póliza.
