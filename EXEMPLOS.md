# Exemplo de Uso da Biblioteca AWS Secret Connector

Este arquivo contém exemplos práticos de como usar a biblioteca.

## Exemplo 1: Recuperação Simples

```java
import com.aws.secret.SecretManagerConnector;

public class ExemploSimples {
    public static void main(String[] args) {
        SecretManagerConnector connector = new SecretManagerConnector();
        
        try {
            String secret = connector.getSecret("meu-secret");
            System.out.println("Secret: " + secret);
        } finally {
            connector.close();
        }
    }
}
```

## Exemplo 2: Recuperação com Tratamento de Erros

```java
import com.aws.secret.SecretManagerConnector;
import com.aws.secret.SecretManagerException;

public class ExemploComErros {
    public static void main(String[] args) {
        SecretManagerConnector connector = new SecretManagerConnector("sa-east-1");
        
        try {
            String secret = connector.getSecret("banco-dados/producao");
            System.out.println("Credencial: " + secret);
            
        } catch (SecretManagerException e) {
            System.err.println("Erro ao recuperar secret: " + e.getMessage());
            e.printStackTrace();
        } finally {
            connector.close();
        }
    }
}
```

## Exemplo 3: Verificação de Existência

```java
import com.aws.secret.SecretManagerConnector;

public class ExemploVerificacao {
    public static void main(String[] args) {
        SecretManagerConnector connector = new SecretManagerConnector();
        
        try {
            String secretName = "api/chave-secreta";
            
            if (connector.secretExists(secretName)) {
                String secret = connector.getSecret(secretName);
                System.out.println("Secret encontrado: " + secret);
            } else {
                System.out.println("Secret não encontrado: " + secretName);
            }
            
        } finally {
            connector.close();
        }
    }
}
```

## Exemplo 4: Usando SecretValue

```java
import com.aws.secret.SecretManagerConnector;
import com.aws.secret.SecretValue;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExemploSecretValue {
    public static void main(String[] args) {
        SecretManagerConnector connector = new SecretManagerConnector();
        
        try {
            SecretValue secretValue = connector.getSecretAsObject("api/credentials");
            
            System.out.println("Nome: " + secretValue.getSecretName());
            System.out.println("Conteúdo: " + secretValue.getSecretContent());
            System.out.println("Vazio? " + secretValue.isEmpty());
            
            // Se for JSON, pode fazer parsing
            ObjectMapper mapper = new ObjectMapper();
            // Map<String, String> credentials = mapper.readValue(
            //     secretValue.getSecretContent(), 
            //     new TypeReference<Map<String, String>>() {}
            // );
            
        } finally {
            connector.close();
        }
    }
}
```

## Exemplo 5: Configuração com Múltiplas Regiões

```java
import com.aws.secret.SecretManagerConnector;

public class ExemploMultiRegiao {
    
    public static String getSecretFromRegion(String secretName, String region) {
        SecretManagerConnector connector = new SecretManagerConnector(region);
        
        try {
            return connector.getSecret(secretName);
        } finally {
            connector.close();
        }
    }
    
    public static void main(String[] args) {
        try {
            String secretUS = getSecretFromRegion("api-key", "us-east-1");
            String secretBR = getSecretFromRegion("api-key", "sa-east-1");
            
            System.out.println("Secret US: " + secretUS);
            System.out.println("Secret BR: " + secretBR);
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}
```

## Exemplo 6: Padrão Singleton (Thread-Safe)

```java
import com.aws.secret.SecretManagerConnector;

public class SecretConnectorSingleton {
    private static SecretManagerConnector instance;
    private static final Object lock = new Object();
    
    public static SecretManagerConnector getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SecretManagerConnector("sa-east-1");
                }
            }
        }
        return instance;
    }
    
    public static void shutdown() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }
}

// Uso:
// SecretManagerConnector connector = SecretConnectorSingleton.getInstance();
// String secret = connector.getSecret("meu-secret");
// ...
// SecretConnectorSingleton.shutdown(); // No final da aplicação
```

## Exemplo 7: Com Spring Boot

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aws.secret.SecretManagerConnector;

@Configuration
public class SecretManagerConfig {
    
    @Value("${aws.region:us-east-1}")
    private String region;
    
    @Bean
    public SecretManagerConnector secretManagerConnector() {
        return new SecretManagerConnector(region);
    }
}

// Service:
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecretService {
    
    @Autowired
    private SecretManagerConnector connector;
    
    public String getApiKey() {
        return connector.getSecret("api/chave");
    }
}
```

## Exemplo 8: Com try-with-resources (Java 9+)

Para versões futuras que implementem AutoCloseable:

```java
// Quando a classe SecretManagerConnector implementar AutoCloseable
// try (SecretManagerConnector connector = new SecretManagerConnector()) {
//     String secret = connector.getSecret("meu-secret");
//     System.out.println(secret);
// } // Fecha automaticamente
```
