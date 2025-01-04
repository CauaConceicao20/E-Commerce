<h1>eCommerce Application 🛒</h1>

<p>Bem-vindo à aplicação <strong>eCommerce</strong>! Este projeto está em desenvolvimento e visa proporcionar uma solução completa para gerenciamento de vendas, produtos e usuários, com funcionalidades avançadas como relatórios de vendas, autenticação segura e integração com APIs de pagamento.</p>

<h2>🧩 Funcionalidades Implementadas</h2>

<h3>👤 Gerenciamento de Usuários</h3>
<ul>
    <li>Cadastro de usuários com roles (papéis) diferenciadas.</li>
    <li>Criptografia de senhas.</li>
    <li>Recuperação de senha com envio de e-mail contendo um token com validade de 10 minutos.</li>
</ul>

<h3>📦 Produtos</h3>
<ul>
    <li>CRUD completo para produtos.</li>
    <li>Carrinho de compras:
        <ul>
            <li>Adicionar e remover produtos.</li>
        </ul>
    </li>
</ul>

<h3>💰 Vendas</h3>
<ul>
    <li>CRUD completo para vendas.</li>
    <li>Geração de relatórios de vendas por:
        <ul>
            <li>Dia.</li>
            <li>Semana.</li>
            <li>Mês.</li>
        </ul>
    </li>
</ul>

<h3>🔒 Segurança</h3>
<ul>
    <li>Spring Security com geração de tokens JWT.</li>
    <li>Controle de acesso baseado em roles de usuários.</li>
    <li>Limitação de acesso a endpoints conforme a role.</li>
</ul>

<h3>🌐 API</h3>
<ul>
    <li>Versionamento de API.</li>
    <li>Implementação inicial de HATEOAS (ainda não aplicado a todos os endpoints).</li>
</ul>

<h3>⚡ Performance</h3>
<ul>
    <li>Uso de cache para otimizar acessos frequentes.</li>
</ul>

<h2>🚧 Funcionalidades em Desenvolvimento</h2>

<h3>1. Integração com API de pagamento via Pix</h3>

<ul>
    <li>Utilizando Feign para comunicação.</li>
    <li>Kafka para eventos de pagamento e notificações.</li>
</ul>

<h4>🔗 Detalhes da API de Pagamento Pix</h4>
<p>Link: https://github.com/CauaConceicao20/Payment-System.git</p>

<h3>2. API Gateway</h3>
<ul>
    <li>Gerenciamento centralizado de autenticação e comunicação entre serviços.</li>
</ul>

<h3>3. Expansão do HATEOAS</h3>
<ul>
    <li>Aplicar a todos os endpoints.</li>
</ul>

<h3>4. Documentação com Swagger</h3>
<ul>
    <li>Finalizar a cobertura de todos os endpoints.</li>
</ul>

<h3>5. Testes Unitários</h3>
<ul>
    <li>Implementação para garantir qualidade e robustez.</li>
</ul>

<h2>🛠️ Tecnologias Utilizadas</h2>
<ul>
    <li><strong>Java 17</strong></li>
    <li><strong>Spring Boot</strong>:
        <ul>
            <li>Spring Security</li>
            <li>Spring Data JPA</li>
            <li>Spring Web</li>
            <li>Spring HATEOAS</li>
        </ul>
    </li>
    <li><strong>Feign</strong> e <strong>Kafka</strong> (para comunicação e eventos).</li>
    <li><strong>Swagger</strong> (documentação).</li>
    <li><strong>Hibernate</strong> (persistência de dados).</li>
    <li><strong>MySQL</strong> (banco de dados relacional).</li>
    <li><strong>Redis</strong> (cache).</li>
    <li><strong>JWT</strong> (autenticação).</li>
    <li><strong>JavaMailSender</strong> (envio de e-mails).</li>
</ul>

<h2>🔧 Configuração do Projeto</h2>
<ol>
    <li>Clone o repositório:
        <pre><code>git clone https://github.com/seu-usuario/sua-repositorio.git</code></pre>
    </li>
    <li>Configure o arquivo <code>application.properties</code> com as credenciais necessárias:
        <ul>
            <li>Banco de dados.</li>
            <li>Servidor de e-mail.</li>
            <li>Serviços de cache e mensageria.</li>
        </ul>
    </li>
    <li>Execute o projeto:
        <pre><code>./mvnw spring-boot:run</code></pre>
    </li>
</ol>

<h2>📈 Próximos Passos</h2>
<ul>
    <li>Finalizar a comunicação com a API de pagamento.</li>
    <li>Implementar autenticação completa no API Gateway.</li>
    <li>Expandir HATEOAS e concluir a documentação Swagger.</li>
    <li>Adicionar cobertura total de testes unitários e de integração.</li>
</ul>
