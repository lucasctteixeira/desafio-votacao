# Sistema de Votação - Cooperativa

API REST para gerenciar sessões de votação em assembleias de cooperativas.
Permite cadastrar pautas, abrir sessões de votação por tempo determinado,
receber votos de associados e contabilizar o resultado.

## Tecnologias

- Java 21
- Spring Boot 3.3.5
- Spring Data JPA
- H2 Database (em arquivo, para persistência)
- Flyway (versionamento do schema)
- Bean Validation (Hibernate Validator)
- JUnit 5, Mockito e AssertJ (testes)
- Swagger / OpenAPI (documentação da API)
- Maven

## Como executar

Pré-requisitos: Java 21 instalado.

Na raiz do projeto, execute:

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080` usando um banco H2 em arquivo,
que persiste os dados entre reinicializações.

### Documentação interativa (Swagger)

Com a aplicação rodando, acesse:

http://localhost:8080/swagger-ui.html


## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/pautas` | Cadastra uma nova pauta |
| POST | `/pautas/{pautaId}/sessoes` | Abre uma sessão de votação (duração opcional, padrão 1 min) |
| POST | `/pautas/{pautaId}/votos` | Registra um voto (SIM ou NAO) |
| GET | `/pautas/{pautaId}/resultado` | Contabiliza e retorna o resultado |

### Exemplos

Criar pauta:
```json
POST /pautas
{
  "titulo": "Reforma do estatuto",
  "descricao": "Votacao sobre mudancas no estatuto"
}
```

Abrir sessão (5 minutos):
```json
POST /pautas/1/sessoes
{
  "duracaoMinutos": 5
}
```

Registrar voto:
```json
POST /pautas/1/votos
{
  "associadoId": "assoc-123",
  "cpf": "52998224725",
  "opcao": "SIM"
}
```

## Decisões técnicas

**Arquitetura em camadas.** Separei em controller, service, repository e model.
Optei por um monólito em camadas em vez de microsserviços por causa do escopo
enxuto — microsserviços seriam over-engineering aqui. A separação mantém o
código testável e organizado.

**Persistência com H2 em arquivo.** Usei H2 em arquivo para persistir os dados
sem exigir dependências externas — o avaliador roda a aplicação sem instalar
banco, e os dados sobrevivem ao restart. O schema é gerenciado pelo Flyway
(migrations versionadas), com `ddl-auto=validate`.

**Voto único e concorrência.** A regra de "um voto por associado por pauta" é
protegida em duas camadas: validações no service (associadoId e CPF) para dar
erros amigáveis, e constraints UNIQUE no banco — (pauta_id, associado_id) e
(pauta_id, cpf) — que garantem a integridade mesmo sob requisições concorrentes.
Validar também por CPF evita que a mesma pessoa vote com identificadores
diferentes.

**Uso de Instant (UTC).** Os timestamps usam Instant, que representa um momento
absoluto em UTC. Isso evita ambiguidade de fuso horário numa aplicação que roda
na nuvem; a conversão para o horário local é responsabilidade do cliente.

**Contabilização no banco.** Os votos são contados via COUNT no banco, sem
carregar os registros em memória — o que escala para grandes volumes.

**Tratamento de erros centralizado.** Uso @RestControllerAdvice para converter
exceções de domínio em respostas HTTP padronizadas: 404 (não encontrado),
409 (conflito de regra de negócio) e 400 (validação de entrada).

**Validação de CPF (bônus).** O formato do CPF é validado com @CPF (dígitos
verificadores). O CPF é persistido junto ao voto para garantir a
unicidade por pauta (impedindo que a mesma pessoa vote com identificadores
diferentes).

## Testes

O projeto tem cobertura em três níveis:

- **Unitários** dos services (com Mockito), cobrindo as regras de negócio
- **De controller** isolados (@WebMvcTest), cobrindo a camada web e validações
- **De integração** (@SpringBootTest), cobrindo o fluxo completo

Para rodar os testes:

```bash
./mvnw test
```

## Decisões sobre dependências e evolução

**Banco H2 (sem Docker).** Optei por não usar Docker/PostgreSQL para não
introduzir dependências externas na execução — o enunciado pede cuidado com
dependências externas, e com o H2 em arquivo o avaliador roda a aplicação
diretamente, sem precisar instalar ou subir nada. Os dados persistem entre
reinicializações. Em produção, a configuração por profiles permitiria apontar
para um PostgreSQL sem alterar o código.

**Versionamento de API.** As rotas poderiam ser versionadas por URI
(ex: `/api/v1/pautas`) para permitir evoluir a API sem quebrar clientes
existentes.

**Escalabilidade.** Para volumes muito altos de votos, o registro poderia ser
processado de forma assíncrona através de mensageria (ex: Kafka): a aplicação
recebe o voto, responde rapidamente e grava de forma assíncrona, no ritmo que o
banco suporta. Combinado com índices nas colunas de busca e réplicas de leitura
para a contabilização, isso permitiria escalar para grandes volumes sem
sobrecarregar o banco.