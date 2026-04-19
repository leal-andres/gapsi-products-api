# GAPSI Products API

Prueba técnica para GAPSI. API REST para administrar el catálogo de productos — obtener un producto por id y actualizar parcialmente (solo `description` y `model`).

Stack: Java 21, Spring Boot 4.0.5, Maven, PostgreSQL 18 (Cloud SQL), Flyway, Lombok, MapStruct, springdoc, Docker.

Despliegue: Cloud Run + Cloud SQL en GCP.

## Endpoints

Base path: `/api/v1`

| Método | Path | Descripción |
|---|---|---|
| GET | `/products/{id}` | Obtener producto por id |
| PATCH | `/products/{id}` | Actualizar `description` y/o `model` |

Los códigos de error del `ErrorResponse` están definidos en el OpenAPI: `INVALID_REQUEST`, `VALIDATION_ERROR`, `READ_ONLY_FIELD`, `PRODUCT_NOT_FOUND`, `INTERNAL_ERROR`.

## Documentación interactiva

- OpenAPI spec: [docs/openapi.yaml](docs/openapi.yaml)
- Swagger UI (local): http://localhost:8080/swagger-ui.html
- Swagger UI (Cloud Run): https://gapsi-products-api-1001139871465.us-central1.run.app/swagger-ui.html
- Colección Postman: [docs/postman/GAPSI Products API.postman_collection.json](docs/postman/GAPSI%20Products%20API.postman_collection.json)

## Variables de entorno

| Variable | Descripción | Ejemplo |
|---|---|---|
| `DB_URL` | JDBC URL de PostgreSQL | `jdbc:postgresql://HOST:5432/postgres` |
| `DB_USER` | Usuario BD | `postgres` |
| `DB_PASSWORD` | Password BD | `postgres` |

Flyway aplica las migraciones (`V1__create_products_table.sql`, `V2__seed_products.sql`) al arranque, con una BD vacía queda lista.

## Correr local con Maven

Necesitas Java 21 y acceso a una PostgreSQL 18.

```bash
DB_URL="jdbc:postgresql://localhost:5432/postgres" \
DB_USER=postgres \
DB_PASSWORD=postgres \
./mvnw spring-boot:run
```

## Correr con Docker

```bash
docker build -t gapsi-products-api .

docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://HOST:5432/postgres" \
  -e DB_USER=postgres \
  -e DB_PASSWORD=postgres \
  gapsi-products-api
```

## Deploy en GCP

### Cómo se conecta Cloud Run con Cloud SQL

La app corre en Cloud Run y se conecta a Cloud SQL **sin pasar por la red pública**, usando el Cloud SQL Auth Proxy integrado de Cloud Run:

1. En el deploy se pasa el flag `--add-cloudsql-instances=PROJECT:REGION:INSTANCE`. Cloud Run levanta un Auth Proxy como sidecar dentro del contenedor y expone un Unix socket en `/cloudsql/...`.
2. La app usa el `postgres-socket-factory` de Google (`com.google.cloud.sql:postgres-socket-factory`) como `socketFactory` en el JDBC URL. El driver conecta al Unix socket en lugar de a una IP.
3. El Auth Proxy autentica contra la API de Cloud SQL usando la Service Account del servicio (rol `roles/cloudsql.client`). El tráfico va cifrado por HTTPS (443) vía IAM — **no** por la red pública de la instancia.

Consecuencia: la instancia de Cloud SQL puede quedar **sin redes autorizadas** y con IP pública o privada sin importancia — solo Cloud Run con el SA correcto puede comunicarse.

### Limites

Como el repo es público y la URL de Cloud Run queda descubrible, el servicio se desplegó con:

| Config | Valor | Motivo |
|---|---|---|
| `--max-instances` | 1 | Los requests extra reciben 429 hasta que haya cupo. |
| `--concurrency` | 40 | Una instancia atiende hasta 40 requests simultáneos. |
| `--timeout` | 30s | Mata requests lentos / slow-loris. La API responde en menos de 1s. |
| `--min-instances` | 0 | $0 cuando no hay tráfico (cold start ~10s, aceptable para demo). |
| `--cpu` / `--memory` | 1 / 512Mi | Mínimo funcional. |


### Recursos desplegados

| Recurso | Nombre |
|---|---|
| Proyecto | `gaspi-proof` |
| Región | `us-central1` |
| Cloud SQL | `free-trial-gaspi-proof` (PostgreSQL 18) |
| Service Account | `gapsi-products-api@gaspi-proof.iam.gserviceaccount.com` con `roles/cloudsql.client` |
| Artifact Registry | `us-central1-docker.pkg.dev/gaspi-proof/gapsi/products-api` |
| Cloud Run | `gapsi-products-api` |
| URL | https://gapsi-products-api-1001139871465.us-central1.run.app |

Deploy del servicio:

```bash
gcloud run deploy gapsi-products-api \
  --image us-central1-docker.pkg.dev/gaspi-proof/gapsi/products-api:latest \
  --region us-central1 \
  --service-account gapsi-products-api@gaspi-proof.iam.gserviceaccount.com \
  --add-cloudsql-instances gaspi-proof:us-central1:free-trial-gaspi-proof \
  --set-env-vars="^##^DB_URL=jdbc:postgresql:///postgres?cloudSqlInstance=gaspi-proof:us-central1:free-trial-gaspi-proof&socketFactory=com.google.cloud.sql.postgres.SocketFactory##DB_USER=postgres##DB_PASSWORD=postgres" \
  --max-instances=1 --min-instances=0 --concurrency=40 --timeout=30 \
  --cpu=1 --memory=512Mi --port=8080 \
  --allow-unauthenticated
```

## Probar la API desplegada

Importar la colección de Postman (`docs/postman/GAPSI Products API.postman_collection.json`).

