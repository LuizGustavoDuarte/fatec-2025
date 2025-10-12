# Fatec 2025 — Projeto (Backend + Frontend)

Aplicação full‑stack demonstrativa para a apresentação da semana de tecnologia na Fatec.

Resumo:
- Backend: Spring Boot 3.x (Java 21) — API REST para upload/consulta de imagens, armazenamento em MinIO, metadados em PostgreSQL, fila RabbitMQ.
- Frontend: Next.js 14 (React + Tailwind) — UI para upload/visualização.
- Docker + docker‑compose para orquestração local (Postgres, RabbitMQ, MinIO, backend, frontend).

## Requisitos

- Java 21
- Maven
- Node.js (compatível com Next.js 14) e npm
- MinIO
- RabbitMQ
- PostgreSQL

## Estrutura do repositório
- `src/main/java/...` — backend (Spring Boot)
- `fatec-front/` — frontend (Next.js)
- `Dockerfile` — build da imagem do backend
- `compose.yaml` — serviços Para desenvolvimento local com Docker

## Variáveis de ambiente (principais)
As variáveis podem ser definidas em um `.env` usado pelo docker-compose ou exportadas no ambiente.

Backend (Spring):
- POSTGRES_HOST / POSTGRES_DB / POSTGRES_USER / POSTGRES_PASSWORD
- RABBITMQ_HOST / RABBITMQ_PORT / RABBITMQ_DEFAULT_USER  / RABBITMQ_DEFAULT_PASS / RABBITMQ_QUEUE_NAME / RABBITMQ_EXCHANGE_NAME / RABBITMQ_ROUTING_KEY
- MINIO_HOST / MINIO_ROOT_USER / MINIO_ROOT_PASSWORD  / MINIO_BUCKET_NAME
- CORS_ALLOWED_ORIGINS (ex.: http://localhost:3000)
- HIBERNATE_DDL_AUTO / HIBERNATE_SHOW_SQL / HIBERNATE_FORMAT_SQL

Frontend:
- NEXT_PUBLIC_API_URL (URL do backend)

## Executar com Docker / docker-compose (recomendado)
1. Crie `.env` com as variáveis usadas em `compose.yaml`.
2. Build + subir:
   - PowerShell:
     docker-compose -f compose.yaml up --build
   ou (Docker Desktop):
     docker compose -f compose.yaml up --build

Serviços expostos:
- Backend: http://localhost:8080
- Frontend: http://localhost:3000
- Postgres: 5432
- RabbitMQ management: http://localhost:15672
- MinIO: 9000 (console http://localhost:9001)

## Dockerfile (notas)
- Stages multi-stage: build com Maven, runtime com JRE mais enxuto.
- Runtime usa imagem Debian‑based para compatibilidade de libs nativas.
- Recomendações:
  - Definir `JAVA_OPTS` para limitar memória em container.

Para parar:
docker-compose -f compose.yaml down

## Endpoints principais (ImageController)

Base: `/api/image`

- POST `/api/image/upload`
  - Descrição: Recebe um `ImageUploadDTO` com campo `imageBase64` contendo a imagem em base64, decodifica e envia para processamento/upload.
  - Parâmetros:
    - Body: JSON { "imageBase64": "<base64>" }
  - Retorno: `200 OK` com `imageID` (string) ou `500` em erro interno.

- GET `/api/image/get/{imageID}`
  - Descrição: Retorna a imagem original (codificada em base64).
  - Parâmetros: Path `imageID` (UUID)
  - Retorno: `200 OK` com base64 da imagem, `404` se não encontrada.

- GET `/api/image/get/{imageID}/processes`
  - Descrição: Retorna lista de imagens processadas relacionadas ao `imageID` (cada item em base64).
  - Parâmetros: Path `imageID` (UUID)
  - Retorno: `200 OK` com `List<String>` (base64) ou `500` em erro.