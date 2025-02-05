# eCommerce Application 🏎️

Bem-vindo à aplicação eCommerce! Este projeto está em desenvolvimento e visa proporcionar uma solução completa para gerenciamento de vendas, produtos e usuários, com funcionalidades avançadas como relatórios de vendas, autenticação segura e integração com APIs de pagamento.

## 🧩 Funcionalidades Principais

### 👤 Gerenciamento de Usuários

- **Cadastro de Usuários**: Permite o registro de novos usuários, com roles (papéis) diferenciadas, como Admin e Cliente.
- **Criptografia de Senhas**: Senhas de usuários são criptografadas para garantir a segurança.
- **Recuperação de Senha**: Envio de e-mail com token de recuperação de senha válido por 10 minutos.

### 🛆 Produtos

- **CRUD Completo de Produtos**: Gerenciamento completo de produtos no sistema.
- **Carrinho de Compras**: Adicione ou remova produtos do carrinho de compras.

### 💰 Vendas

- **CRUD Completo de Vendas**: Gerencie as vendas, registrando todas as transações no sistema.
- **Relatórios de Vendas**: Geração de relatórios de vendas por:
  - **Dia**
  - **Semana**
  - **Mês**

### 🛍️ Compra e Confirmação de Pagamento

- **Compra de Produtos**: Após adicionar os itens no carrinho, o usuário pode realizar a compra e proceder para o pagamento.
- **Integração com API de Pagamento**: Comunicação com uma API de pagamento externa para validar e processar transações.
- **Notificação por E-mail**: Após a confirmação do pagamento, o usuário recebe um e-mail com a confirmação da compra e detalhes do pedido. 
- **Fluxo de Compra**: Garantia de que o pagamento foi realizado antes de finalizar a venda, notificando o cliente automaticamente.

### 🔒 Segurança

- **Autenticação JWT**: Proteção dos endpoints da API com tokens JWT.
- **Controle de Acesso Baseado em Roles**: Cada usuário tem permissões específicas baseadas em seu papel (Admin, Cliente).
- **Comunicação Segura**: Uso de chaves assimétricas e JWT para a comunicação entre microsserviços.

### 🌐 API

- **Versionamento de API**: A API foi projetada para ser facilmente escalável e versionada.
- **HATEOAS (em progresso)**: Implementação parcial de HATEOAS para navegação de recursos na API.

### ⚡ Performance

- **Cache de Produtos**: Uso de Redis para otimizar a recuperação de produtos e aumentar a performance.

## 🛠️ Tecnologias Utilizadas

- **Java 19**
- **Spring Boot**:
  - Spring Security
  - Spring Data JPA
  - Spring Web
  - Spring HATEOAS
- **Feign e Kafka** (para comunicação e eventos).
- **Swagger** (documentação).
- **Hibernate** (persistência de dados).
- **MySQL** (banco de dados relacional).
- **Redis** (cache).
- **JWT** (autenticação).
- **JavaMailSender** (envio de e-mails).

## 🚧 Configuração do Projeto

### 1️⃣ Clonar o Repositório:


git clone https://github.com/CauaConceicao20/E-Commerce.git

### 2️⃣ Configurar o arquivo `application.properties`:

#### Banco de Dados:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=senha
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```

#### Configuração do Kafka (Producer e Consumer):

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=ecommerce-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer

```

#### Configuração do Feign (Comunicação com API de Pagamento):

```properties
payment.api.url=http://localhost:8082
feign.client.config.default.connectTimeout=5000
feign.client.config.default.readTimeout=5000
```

#### Configuração do Envio de E-mails:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 3️⃣ Executar o projeto:

```sh
./mvnw spring-boot:run
```

## 📈 Próximos Passos

- Expandir HATEOAS e concluir a documentação Swagger.
- Adicionar cobertura total de testes unitários e de integração.
- Melhorar a escalabilidade da arquitetura com balanceamento de carga.

---

Se precisar de mais detalhes ou melhorias, sinta-se à vontade para contribuir! 🚀

