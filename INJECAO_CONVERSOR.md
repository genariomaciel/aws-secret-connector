# Guia de Injeção de Conversor Padrão

## Visão Geral

A partir da versão 1.0.0, o `SecretManagerConnector` suporta **injeção de conversor padrão** no construtor. Isso permite que você defina um conversor que será utilizado em todas as chamadas sem precisar especificá-lo cada vez.

## Construtores Disponíveis

### 1. Construtor sem Argumentos (região padrão, sem conversor)
```java
SecretManagerConnector connector = new SecretManagerConnector();
// Região: us-east-1
// Conversor padrão: null (usa String)
```

### 2. Construtor com Região (sem conversor)
```java
SecretManagerConnector connector = new SecretManagerConnector("sa-east-1");
// Região: sa-east-1
// Conversor padrão: null (usa String)
```

### 3. Construtor com Região e Conversor (NOVO)
```java
SecretManagerConnector connector = new SecretManagerConnector(
    "sa-east-1",
    SecretConverters.asInteger()
);
// Região: sa-east-1
// Conversor padrão: Integer
```

## Usando o Conversor Padrão Injetado

### Método `getSecretWithDefaultConverter()`

```java
public <T> T getSecretWithDefaultConverter(String secretName)
```

Recupera um secret utilizando o conversor padrão injetado no construtor.

### Exemplo Básico

```java
// Injetar conversor Integer
SecretManagerConnector connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asInteger()
);

try {
    // Usa o conversor Integer injetado
    Integer port = connector.getSecretWithDefaultConverter("database/port");
    System.out.println("Porta: " + port);
    
} finally {
    connector.close();
}
```

## Exemplos Práticos

### Exemplo 1: Conversor Integer Injetado

```java
SecretManagerConnector connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asInteger()
);

try {
    Integer timeout = connector.getSecretWithDefaultConverter("app/timeout");
    System.out.println("Timeout: " + timeout + "ms");
    
} finally {
    connector.close();
}
```

### Exemplo 2: Conversor Array Injetado

```java
SecretManagerConnector connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asArray(",")
);

try {
    String[] servidores = connector.getSecretWithDefaultConverter("infra/servidores");
    for (String servidor : servidores) {
        System.out.println(servidor);
    }
    
} finally {
    connector.close();
}
```

### Exemplo 3: Conversor Boolean Injetado

```java
SecretManagerConnector connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asBoolean()
);

try {
    Boolean prodMode = connector.getSecretWithDefaultConverter("app/prod-mode");
    if (prodMode) {
        System.out.println("Modo produção ativo");
    }
    
} finally {
    connector.close();
}
```

### Exemplo 4: Conversor Customizado Injetado

```java
// Conversor que retorna só a primeira parte (antes de ":")
SecretConverter<String> usernameExtractor = (secret) -> secret.split(":")[0];

SecretManagerConnector connector = new SecretManagerConnector(
    "us-east-1",
    usernameExtractor
);

try {
    String username = connector.getSecretWithDefaultConverter("database/credentials");
    System.out.println("Usuário: " + username);
    
} finally {
    connector.close();
}
```

### Exemplo 5: Combinar Conversor Padrão com Específico

```java
SecretManagerConnector connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asInteger()  // Padrão
);

try {
    // Usa o conversor padrão (Integer)
    Integer port = connector.getSecretWithDefaultConverter("database/port");
    
    // Usa um conversor específico (Boolean), sobrescrevendo o padrão
    Boolean ssl = connector.getSecret("database/ssl", SecretConverters.asBoolean());
    
    System.out.println("Porta: " + port);
    System.out.println("SSL: " + ssl);
    
} finally {
    connector.close();
}
```

## Verificar o Conversor Injetado

### Método `getDefaultConverter()`

```java
public SecretConverter<?> getDefaultConverter()
```

Retorna o conversor padrão injetado, ou `null` se nenhum foi injetado.

### Exemplo

```java
SecretManagerConnector connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asInteger()
);

SecretConverter<?> defaultConv = connector.getDefaultConverter();

if (defaultConv != null) {
    System.out.println("Conversor: " + defaultConv.getClass().getSimpleName());
} else {
    System.out.println("Nenhum conversor padrão");
}

connector.close();
```

## Comparação: Com vs Sem Injeção

### Sem Injeção (forma original)

```java
SecretManagerConnector connector = new SecretManagerConnector("us-east-1");

Integer port1 = connector.getSecret("db/port", SecretConverters.asInteger());
Integer port2 = connector.getSecret("app/port", SecretConverters.asInteger());
Integer port3 = connector.getSecret("cache/port", SecretConverters.asInteger());

// Precisa passar o conversor em cada chamada
```

### Com Injeção (novo)

```java
SecretManagerConnector connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asInteger()
);

Integer port1 = connector.getSecretWithDefaultConverter("db/port");
Integer port2 = connector.getSecretWithDefaultConverter("app/port");
Integer port3 = connector.getSecretWithDefaultConverter("cache/port");

// Conversor é reutilizado automaticamente
```

## Casos de Uso

### 1. Aplicação que sempre trabalha com Integer

```java
SecretManagerConnector connector = new SecretManagerConnector(
    "sa-east-1",
    SecretConverters.asInteger()
);

// Todos os secrets dessa aplicação são números
Integer dbPort = connector.getSecretWithDefaultConverter("db/port");
Integer cachePort = connector.getSecretWithDefaultConverter("cache/port");
Integer apiPort = connector.getSecretWithDefaultConverter("api/port");
```

### 2. Configuração com conversor Array

```java
SecretManagerConnector connector = new SecretManagerConnector(
    "sa-east-1",
    SecretConverters.asArray(";")  // Delimiter diferente
);

// Todos os secrets são listas separadas por ";"
String[] hosts = connector.getSecretWithDefaultConverter("db/replicas");
String[] nodes = connector.getSecretWithDefaultConverter("cluster/nodes");
String[] endpoints = connector.getSecretWithDefaultConverter("api/endpoints");
```

### 3. Conversor JSON padrão

```java
SecretManagerConnector connector = new SecretManagerConnector(
    "sa-east-1",
    SecretConverters.asJsonObject(DatabaseCredentials.class)
);

// Todos os secrets são objetos JSON do tipo DatabaseCredentials
DatabaseCredentials mainDb = connector.getSecretWithDefaultConverter("databases/main");
DatabaseCredentials replicaDb = connector.getSecretWithDefaultConverter("databases/replica");
```

## Comportamento com Conversor Null

Quando o conversor padrão é `null`:

```java
SecretManagerConnector connector = new SecretManagerConnector("us-east-1", null);
// ou
SecretManagerConnector connector = new SecretManagerConnector("us-east-1");

String secret = connector.getSecretWithDefaultConverter("api/key");
// Retorna uma String normalmente
```

## Thread Safety

- ✅ Os construtores são thread-safe
- ✅ O conversor injetado é imutável
- ✅ Múltiplas threads podem usar o mesmo conector

```java
SecretManagerConnector connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asInteger()
);

// Thread 1
new Thread(() -> {
    Integer port = connector.getSecretWithDefaultConverter("db/port");
}).start();

// Thread 2
new Thread(() -> {
    Integer timeout = connector.getSecretWithDefaultConverter("app/timeout");
}).start();
```

## Logging

O logging automático inclui informações sobre o conversor injetado:

```
INFO  SecretManagerConnector inicializado com região: us-east-1 com conversor padrão: SecretConverters
DEBUG Convertendo secret 'db/port' para tipo genérico
INFO  Secret 'db/port' convertido com sucesso para tipo Integer
```

## Boas Práticas

✅ **DO's**
- Injetar um conversor padrão se a maioria dos secrets for do mesmo tipo
- Usar conversores pré-configurados quando possível
- Documentar qual conversor foi injetado
- Usar `getSecretWithDefaultConverter()` para secrets que usam o conversor padrão
- Usar `getSecret(name, converter)` para exceções

❌ **DON'Ts**
- Não injetar um conversor muito genérico (ex: lambda complexa)
- Não mudar de conversor durante a execução (crie um novo conector)
- Não ignorar se o conversor injetado é null
- Não reutilizar conector com conversor após close()

## Integração com Spring Boot

```java
@Configuration
public class SecretManagerConfig {
    
    @Bean
    public SecretManagerConnector secretManagerConnector() {
        return new SecretManagerConnector(
            "sa-east-1",
            SecretConverters.asJsonObject(AppConfig.class)
        );
    }
}
```

## Exemplo Completo

```java
import com.leicam.secretconnector.*;

public class Application {
    public static void main(String[] args) {
        // Criar conector com conversor Integer injetado
        SecretManagerConnector connector = new SecretManagerConnector(
            "sa-east-1",
            SecretConverters.asInteger()
        );
        
        try {
            // Verificar conversor injetado
            if (connector.getDefaultConverter() != null) {
                System.out.println("Conversor padrão: " + 
                    connector.getDefaultConverter().getClass().getSimpleName());
            }
            
            // Usar o conversor padrão
            Integer dbPort = connector.getSecretWithDefaultConverter("database/port");
            Integer cachePort = connector.getSecretWithDefaultConverter("cache/port");
            Integer apiPort = connector.getSecretWithDefaultConverter("api/port");
            
            System.out.println("DB Port: " + dbPort);
            System.out.println("Cache Port: " + cachePort);
            System.out.println("API Port: " + apiPort);
            
        } finally {
            connector.close();
        }
    }
}
```

## Changelog

### Versão 1.0.0
- ✅ Adicionado novo construtor com injeção de conversor
- ✅ Adicionado método `getSecretWithDefaultConverter()`
- ✅ Adicionado método `getDefaultConverter()`
- ✅ Suporte a múltiplos tipos de conversores injetáveis
- ✅ Logging automático do conversor injetado
