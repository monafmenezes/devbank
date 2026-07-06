# Devbank

API REST simplificada de banco digital: transferência de fundos entre contas e consulta de movimentações, com notificação assíncrona via Kafka.

## Stack

- Java 17 + Spring Boot 4.1
- Spring Data JPA + PostgreSQL
- Flyway (migrations)
- Spring Kafka
- springdoc-openapi (Swagger)
- MapStruct + Lombok
- JUnit 5 / Mockito / AssertJ

## Como rodar

Pré-requisitos: Docker, Docker Compose e JDK 17.

1. Suba a infraestrutura (Postgres + Kafka + Kafka UI):

   ```bash
   docker compose up -d
   ```

2. Suba a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

3. Acesse:
   - API: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - Kafka UI: `http://localhost:8081`

O banco já sobe com 4 clientes e uma conta para cada um (migrations `V1` a `V5`), então dá para testar uma transferência direto:

```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{"originAccountId": 1, "destinationAccountId": 2, "amount": 100.00}'
```

## Testes

```bash
./mvnw test
```

Inclui testes unitários das regras de negócio (`TransferServiceImpl`, `AccountServiceImpl`) e um teste de integração (`TransferConcurrencyTest`) que dispara 50 transferências concorrentes na mesma conta contra o Postgres real e confere que o saldo final bate — precisa da infraestrutura do `docker compose` de pé.

## Fluxo de trabalho

O projeto segue o modelo **Gitflow**: features são desenvolvidas em branches `feature/<nome>` a partir da `develop` e integradas via Pull Request. Os commits seguem o padrão de **Conventional Commits** (`feat:`, `fix:`, `test:`, `docs:`, etc.), facilitando o entendimento do histórico e a geração de changelog.

## Endpoints principais

| Método | Rota                        | Descrição                              |
|--------|-----------------------------|-----------------------------------------|
| POST   | `/api/accounts`              | Cria uma conta com saldo inicial        |
| GET    | `/api/accounts/{id}`         | Consulta uma conta                      |
| GET    | `/api/accounts/{id}/transfers` | Consulta as movimentações da conta    |
| POST   | `/api/transfers`              | Executa uma transferência entre contas |

## Decisões de design e arquitetura

**Modelo de dados.** `Client` (id, name) é separado de `Account` (id, accountNumber, balance, client), permitindo um cliente ter mais de uma conta. `Transfer` referencia conta de origem e destino, não clientes diretamente — quem transfere é uma conta, não uma pessoa.

**Camadas.** Controller → Service → Repository, com DTOs de request/response (não expõe entidades JPA na API) e MapStruct fazendo a conversão entidade → DTO. Exceções de negócio (`AccountNotFoundException`, `InsufficientBalanceException`, `InvalidTransferException`, `ClientNotFoundException`) são tratadas centralmente em `ApiExceptionHandler`, cada uma mapeada para o status HTTP correto (404, 422, 400).

**Concorrência na transferência.** `TransferServiceImpl` usa lock pessimista (`SELECT ... FOR UPDATE`, via `AccountRepository.findByIdForUpdate`) nas duas contas envolvidas, sempre travando primeiro a conta de menor id — isso evita deadlock quando duas transferências concorrentes envolvem as mesmas duas contas em sentidos opostos (A→B e B→A ao mesmo tempo). O teste `TransferConcurrencyTest` comprova que 50 transferências simultâneas na mesma conta não perdem atualização de saldo.

**Notificação via Kafka.** A transferência publica um evento interno do Spring (`NotificationsReadyEvent`) dentro da própria transação; um listener (`TransferNotificationListener`) só encaminha esse evento para o Kafka depois que a transação é confirmada (`@TransactionalEventListener(phase = AFTER_COMMIT)`). Isso evita notificar o cliente sobre uma transferência que acabou sendo revertida. Um `@KafkaListener` (`NotificationConsumer`) simula o envio da notificação (hoje só loga; seria o ponto de integração com e-mail/push).

**Migrations.** Schema versionado via Flyway (`V1` a `V5`), incluindo o seed de clientes e contas de teste — não há criação de schema via `ddl-auto` (está em `validate`).
