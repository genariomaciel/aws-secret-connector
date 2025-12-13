package com.leicam.secretconnector;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leicam.secretconnector.converter.impl.SecretConverters;
import com.leicam.secretconnector.models.DatabaseCredentials;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) {
        
        String region = "us-east-1";
        String accessKey = "local-stack-id";
        String secretKey = "local-stack-secret";
        String endpoint = "http://localhost:4566";

        Application app = new Application();

        String secretString = app.getSecretToString(region, endpoint, accessKey, secretKey).orElse("Secret não encontrada");
        logger.info("Secret como String: {}", secretString);

        Optional<DatabaseCredentials> dbCredentials = app.getSecretDatabaseCredentials(region, endpoint, accessKey, secretKey);
        if (dbCredentials.isPresent()) {
            logger.info("Database Credentials: {}", dbCredentials.get());
        } else {
            logger.debug("Database Credentials não encontradas");
        }   

    }


    public Optional<String> getSecretToString(String region, String endpoint, String accessKey, String secretKey) {

        SecretManagerConnector connector = new SecretManagerConnector(SecretConverters.asString(), createForLocalStack(region, endpoint, accessKey, secretKey));
        Optional<String> secretValue = null;
        try {
            // Obtém a secret (substitua 'my-secret' pelo ARN ou Name da secret)
            String secretName = "product_database_credentials";
            secretValue = Optional.of(connector.get(secretName));
            
            logger.debug("Secret obtida com sucesso: {}", secretValue.get());
            
        } catch (SecretManagerException e) {
            secretValue = Optional.empty();
            logger.error("Erro ao obter a secret: {}", e.getMessage());
        } finally {
            connector.close();
        }
        return secretValue;
    }

    public Optional<DatabaseCredentials> getSecretDatabaseCredentials(String region, String endpoint, String accessKey, String secretKey) {

        SecretManagerConnector connector = new SecretManagerConnector(SecretConverters.asString(), createForLocalStack(region, endpoint, accessKey, secretKey));
        Optional<DatabaseCredentials> databaseCredentials = null;

        try {
            // Obtém a secret (substitua 'my-secret' pelo ARN ou Name da secret)
            String secretName = "product_database_credentials";
            databaseCredentials = Optional.of(connector.get(secretName, SecretConverters.asObject(DatabaseCredentials.class)));
            
            logger.debug("Secret obtida com sucesso: {}", databaseCredentials);
            
        } catch (SecretManagerException e) {
            databaseCredentials = Optional.empty();
            logger.error("Erro ao obter a secret: {}", e.getMessage());
        } finally {
            connector.close();
        }
        return databaseCredentials;
    }

    public SecretsManagerClient createForLocalStack(String region, String endpoint, String accessKey, String secretKey) {
        return SecretsManagerClient.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create(endpoint)) // ex: http://localhost:4566
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
    }
}
