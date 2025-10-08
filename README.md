# Fatec 2025 — Projeto (Backend + Frontend)

Este repositório contém uma aplicação full-stack de demonstração desenvolvida para apresentação na semana de tecnologia. O projeto tem um backend em Spring Boot (Java 21) e um frontend em Next.js (React + TailwindCSS).

## Visão geral

- Backend: Spring Boot 3.x (Java 21)
  - Expõe API REST em `/api/image` para upload, consulta e consulta de imagens processadas.
  - Armazena imagens em MinIO e metadados em banco (JPA).
  - Envia mensagens para RabbitMQ para processamento assíncrono.
  - Regras de CORS configuráveis por variável de ambiente.

- Frontend: Next.js 14 (React 18)
  - Interface para envio/visualização de imagens.

## Conteúdo do repositório

- `src/main/java/tech/liax/fatec_2025` — código-fonte do backend
  - `Config/` — configurações (CORS, RabbitMQ)
  - `Controllers/` — controladores REST (ex.: `ImageController`)
  - `Services/` — lógica de negócio, integração MinIO e RabbitMQ
  - `Entities/`, `Repositories/`, `DTOs/`, `Utils/`, `Exceptions/`
- `fatec-front/` — frontend Next.js

## Requisitos

- Java 21
- Maven
- Node.js (compatível com Next.js 14) e npm
- MinIO
- RabbitMQ
- PostgreSQL

## Variáveis de ambiente (Backend)

O backend usa propriedades Spring que devem ser definidas em `application.properties` ou como variáveis de ambiente. As chaves usadas no código são:

- MinIO
  - `SPRING_MINIO_HOST` -> `spring.minio.host` (ex.: http://localhost:9000)
  - `SPRING_MINIO_ACCESSKEY` -> `spring.minio.accessKey`
  - `SPRING_MINIO_SECRETKEY` -> `spring.minio.secretKey`
  - `SPRING_MINIO_BUCKETNAME` -> `spring.minio.bucketName`

- RabbitMQ
  - `SPRING_RABBITMQ_QUEUE_NAME` -> `spring.rabbitmq.queueName`
  - `SPRING_RABBITMQ_EXCHANGE_NAME` -> `spring.rabbitmq.exchangeName`
  - `SPRING_RABBITMQ_ROUTING_KEY` -> `spring.rabbitmq.routingKey`

- CORS
  - `SPRING_CORS_ALLOWED_ORIGINS` -> `spring.cors.allowed-origins` (array de origens permitidas)
    - Exemplo para properties: `spring.cors.allowed-origins=http://localhost:3000`
    - Exemplo por variáveis de ambiente: definir `SPRING_CORS_ALLOWED_ORIGINS` com valores separados por vírgula pode ser necessário dependendo do launcher.

- Banco de dados (Spring Data JPA)
  - Configure as propriedades Spring padrão para datasource (ex.: `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`, `spring.jpa.hibernate.ddl-auto`, etc.). O projeto usa PostgreSQL como dependência em tempo de execução.

## Endpoints principais (ImageController)

Base: `/api/image`

- POST `/api/image/upload/{processCode}`
  - Descrição: Recebe um `ImageUploadDTO` com campo `imageBase64` contendo a imagem em base64, decodifica e envia para processamento/upload.
  - Parâmetros:
    - Path: `processCode` (int) — código do processo (mapa em `ProcessCodeEnum` no código)
    - Body: JSON { "imageBase64": "<base64>" }
  - Retorno: `200 OK` com `imageID` (string) ou `400` se código inválido, `500` em erro interno.

- GET `/api/image/get/{imageID}`
  - Descrição: Retorna a imagem original (codificada em base64).
  - Parâmetros: Path `imageID` (UUID)
  - Retorno: `200 OK` com base64 da imagem, `404` se não encontrada.

- GET `/api/image/get/{imageID}/processes`
  - Descrição: Retorna lista de imagens processadas relacionadas ao `imageID` (cada item em base64).
  - Parâmetros: Path `imageID` (UUID)
  - Retorno: `200 OK` com `List<String>` (base64) ou `500` em erro.

## Como executar (local)

Backend (Windows PowerShell):

1. Configure as variáveis de ambiente necessárias (exemplo mínimo usando MinIO local, RabbitMQ local e PostgreSQL):

```powershell
$env:POSTGRES_HOST =
$env:POSTGRES_DB =
$env:POSTGRES_USER =
$env:POSTGRES_PASSWORD =
$env:SPRING_JPA_HIBERNATE_DDL_AUTO =

$env:RABBITMQ_HOST =
$env:RABBITMQ_PORT =
$env:RABBITMQ_DEFAULT_USER =
$env:RABBITMQ_DEFAULT_PASS =
$env:IMAGE_PROCESSING_QUEUE =
$env:RABBITMQ_EXCHANGE_NAME =
$env:RABBITMQ_ROUTING_KEY =

$env:MINIO_HOST =
$env:MINIO_ROOT_USER =
$env:MINIO_ROOT_PASSWORD =
$env:MINIO_BUCKET_NAME =

$env:CORS_ALLOWED_ORIGINS =
$env:NEXT_PUBLIC_API_URL =
$env:HIBERNATE_DDL_AUTO =
$env:HIBERNATE_SHOW_SQL =
$env:HIBERNATE_FORMAT_SQL =
```

2. Rodar com Maven (usando wrapper incluso):

```powershell
.\
\mvnw.cmd spring-boot:run
```

ou usando maven instalado:

```powershell
mvn spring-boot:run
```

Front-end (Next.js):

1. Entre na pasta do frontend e instale dependências:

```powershell
cd fatec-front
npm install
```

2. Rodar em modo de desenvolvimento:

```powershell
npm run dev
```

O frontend estará disponível em `http://localhost:3000` por padrão.

## Notas de implementação

- O `ImageUploaderService` usa MinIO para armazenar objetos. Se o bucket não existir, ele tenta criar durante a inicialização.
- O `ImageController` aceita imagens em base64 (campo `imageBase64`) e usa utilitários em `Utils/ImageUtil.java` para conversão.
- CORS: o aplicativo lança uma exceção na inicialização se `spring.cors.allowed-origins` não estiver configurado — assegure-se de definir a variável.

## Estrutura de pastas (resumida)

- `src/main/java` — backend Java/Spring Boot
- `src/main/resources` — recursos do backend (imagens, application.properties se criado)
- `fatec-front/` — frontend Next.js


---
