# AWS Secret Connector

Uma biblioteca Java para conectar ao **AWS Secrets Manager** e recuperar secrets de forma simples e eficiente.

## Características

- ✅ Integração com AWS SDK for Java 2.x
- ✅ Suporte a múltiplas regiões AWS
- ✅ Interface simples para recuperação de secrets
- ✅ Tratamento robusto de exceções
- ✅ Logging com SLF4J
- ✅ Testes unitários com JUnit 5

## Requisitos

- Java 11 ou superior
- Maven 3.6+
- Credenciais AWS configuradas (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
- Permissões para acessar AWS Secrets Manager

## Instalação

### 1. Clonar o repositório

```bash
cd c:\dev\source\projetos\aws

git clone git@github.com:genariomaciel/secret-connector.git

cd secret-connector
```

### 2. Compilar o projeto

```bash
mvn clean install
```

### 3. Usar em seu projeto

Adicione a dependência no seu `pom.xml`:

```xml
<dependency>
    <groupId>com.aws</groupId>
    <artifactId>secret-connector</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Uso

### Exemplo Básico

```java
import com.aws.secret.SecretManagerConnector;

public class Main {
    public static void main(String[] args) {
        // Criar conector com região padrão
        SecretManagerConnector connector = new SecretManagerConnector();
        
        try {
            // Recuperar secret
            String secret = connector.getSecret("meu-secret");
            System.out.println("Secret recuperado: " + secret);
            
        } finally {
            connector.close();
        }
    }
}
```

### Exemplo com Região Específica

```java
import com.aws.secret.SecretManagerConnector;

public class Main {
    public static void main(String[] args) {
        // Criar conector com região específica
        SecretManagerConnector connector = new SecretManagerConnector("sa-east-1");
        
        try {
            String secret = connector.getSecret("banco-dados/producao");
            System.out.println("Credencial recuperada: " + secret);
            
        } finally {
            connector.close();
        }
    }
}
```

### Exemplo com Objeto SecretValue

```java
import com.aws.secret.SecretManagerConnector;
import com.aws.secret.SecretValue;

public class Main {
    public static void main(String[] args) {
        SecretManagerConnector connector = new SecretManagerConnector();
        
        try {
            // Recuperar como objeto
            SecretValue secretValue = connector.getSecretAsObject("api/chave");
            
            System.out.println("Nome do secret: " + secretValue.getSecretName());
            System.out.println("Conteúdo: " + secretValue.getSecretContent());
            System.out.println("Está vazio? " + secretValue.isEmpty());
            
        } finally {
            connector.close();
        }
    }
}
```

### Verificar Existência de um Secret

```java
import com.aws.secret.SecretManagerConnector;

public class Main {
    public static void main(String[] args) {
        SecretManagerConnector connector = new SecretManagerConnector();
        
        try {
            if (connector.secretExists("meu-secret")) {
                System.out.println("Secret existe!");
            } else {
                System.out.println("Secret não encontrado");
            }
            
        } finally {
            connector.close();
        }
    }
}
```

## API Principal

### SecretManagerConnector

#### Construtores

- `SecretManagerConnector()` - Inicializa com região padrão (us-east-1)
- `SecretManagerConnector(String region)` - Inicializa com região específica

#### Métodos

| Método | Descrição |
|--------|-----------|
| `getSecret(String secretName)` | Recupera o valor do secret como String |
| `getSecretAsObject(String secretName)` | Recupera o secret como objeto SecretValue |
| `secretExists(String secretName)` | Verifica se o secret existe |
| `getRegion()` | Retorna a região configurada |
| `getSecretsManagerClient()` | Retorna o cliente do Secrets Manager (uso avançado) |
| `close()` | Fecha a conexão com o Secrets Manager |

### SecretValue

Encapsula o nome e o valor de um secret.

| Método | Descrição |
|--------|-----------|
| `getSecretName()` | Retorna o nome do secret |
| `getSecretContent()` | Retorna o conteúdo do secret |
| `isEmpty()` | Verifica se o secret está vazio |

### Exceções

- `SecretManagerException` - Exceção personalizada para erros relacionados ao Secrets Manager

## Configuração de Credenciais AWS

### Opção 1: Variáveis de Ambiente

```bash
export AWS_ACCESS_KEY_ID=sua_chave_aqui
export AWS_SECRET_ACCESS_KEY=sua_chave_secreta_aqui
export AWS_REGION=us-east-1
```

### Opção 2: Arquivo de Configuração (~/.aws/credentials)

```ini
[default]
aws_access_key_id = sua_chave_aqui
aws_secret_access_key = sua_chave_secreta_aqui

[profile-name]
aws_access_key_id = sua_chave_aqui
aws_secret_access_key = sua_chave_secreta_aqui
```

### Opção 3: IAM Role (EC2)

Se executando em uma instância EC2, use uma IAM Role com permissão para acessar o Secrets Manager.

## Permissões IAM Necessárias

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "secretsmanager:GetSecretValue",
                "secretsmanager:ListSecrets",
                "secretsmanager:DescribeSecret"
            ],
            "Resource": "arn:aws:secretsmanager:*:*:secret:*"
        }
    ]
}
```

## Estrutura do Projeto

```
secret-connector/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/aws/secret/
│   │   │       ├── SecretManagerConnector.java
│   │   │       ├── SecretValue.java
│   │   │       └── SecretManagerException.java
│   │   └── resources/
│   └── test/
│       └── java/
│           └── com/aws/secret/
│               └── SecretManagerConnectorTest.java
├── pom.xml
└── README.md
```

## Testes

Executar testes unitários:

```bash
mvn test
```

Executar com cobertura:

```bash
mvn clean test jacoco:report
```

## Dependências

- **AWS SDK for Java 2.x** - Versão 2.20.0
- **SLF4J** - Versão 1.7.36
- **JUnit 5** - Versão 5.9.0

## Licença

Este projeto é fornecido como está, para fins educacionais e de desenvolvimento.

## Suporte

Para relatar problemas ou solicitar recursos, abra uma issue no repositório.


## Atualizar o projeto no code
mvn eclipse:eclipse -DdownloadSources -DdownloadJavadocs 2>&1 | tail -20