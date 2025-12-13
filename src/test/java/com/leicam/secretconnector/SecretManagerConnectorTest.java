package com.leicam.secretconnector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
import com.leicam.secretconnector.converter.SecretConverter;
import com.leicam.secretconnector.converter.impl.SecretConverters;

/**
 * Testes unitários para a classe SecretManagerConnector.
 * Todos os testes utilizam mocks para evitar requisições reais à AWS.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do SecretManagerConnector")
public class SecretManagerConnectorTest {

    @Mock
    private SecretsManagerClient mockClient;

    private SecretManagerConnector connector;

    @BeforeEach
    public void setUp() {
        connector = new SecretManagerConnector("us-east-1", SecretConverters.asString(), mockClient);
    }

    @Test
    @DisplayName("Deve criar instância do conector com sucesso")
    public void testConnectorCreation() {
        assertNotNull(connector);
        assertNotNull(connector.getSecretsManagerClient());
        assertEquals("us-east-1", connector.getRegion());
    }

    @Test
    @DisplayName("Deve retornar a região correta")
    public void testGetRegion() {
        assertEquals("us-east-1", connector.getRegion());
    }

    @Test
    @DisplayName("Deve retornar o conversor padrão injetado")
    public void testGetConverter() {
        assertNotNull(connector.getConverter());
        assertInstanceOf(SecretConverter.class, connector.getConverter());
    }

    @Test
    @DisplayName("Deve retornar o cliente corretamente")
    public void testGetSecretsManagerClient() {
        assertEquals(mockClient, connector.getSecretsManagerClient());
    }

    @Test
    @DisplayName("Deve recuperar secret como String com sucesso")
    public void testGetSecretAsString() {
        String secretName = "test-secret";
        String secretContent = "secret-value-123";
        
        GetSecretValueResponse response = GetSecretValueResponse.builder()
                .secretString(secretContent)
                .build();
        
        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(response);
        
        String result = connector.get(secretName);
        
        assertEquals(secretContent, result);
        verify(mockClient).getSecretValue(any(GetSecretValueRequest.class));
    }

    @Test
    @DisplayName("Deve recuperar secret binário com sucesso")
    public void testGetSecretAsBinary() {
        String secretName = "test-binary-secret";
        byte[] binaryContent = "binary-secret-content".getBytes();
        
        GetSecretValueResponse response = GetSecretValueResponse.builder()
                .secretBinary(software.amazon.awssdk.core.SdkBytes.fromByteArray(binaryContent))
                .build();
        
        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(response);
        
        String result = connector.get(secretName);
        
        assertEquals(new String(binaryContent), result);
        verify(mockClient).getSecretValue(any(GetSecretValueRequest.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao recuperar secret inexistente")
    public void testGetSecretNotFound() {
        String secretName = "nonexistent-secret";
        
        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenThrow(SecretsManagerException.builder()
                        .message("Secrets Manager secret not found")
                        .build());
        
        SecretManagerException exception = assertThrows(
                SecretManagerException.class,
                () -> connector.get(secretName)
        );
        
        assertTrue(exception.getMessage().contains("Falha ao recuperar o secret"));
    }

    @Test
    @DisplayName("Deve converter secret usando conversor específico")
    public void testGetSecretWithConverter() {
        String secretName = "numeric-secret";
        String secretContent = "12345";
        
        GetSecretValueResponse response = GetSecretValueResponse.builder()
                .secretString(secretContent)
                .build();
        
        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(response);
        
        Integer result = connector.get(secretName, SecretConverters.asInteger());
        
        assertEquals(12345, result);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar conversão de tipo")
    public void testGetSecretWithConverterError() {
        String secretName = "invalid-numeric-secret";
        String secretContent = "not-a-number";
        
        GetSecretValueResponse response = GetSecretValueResponse.builder()
                .secretString(secretContent)
                .build();
        
        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(response);
        
        SecretManagerException exception = assertThrows(
                SecretManagerException.class,
                () -> connector.get(secretName, SecretConverters.asInteger())
        );
        
        assertTrue(exception.getMessage().contains("Falha ao converter o secret"));
    }

    @Test
    @DisplayName("Deve verificar existência de secret existente")
    public void testSecretExists() {
        String secretName = "existing-secret";
        
        GetSecretValueResponse response = GetSecretValueResponse.builder()
                .secretString("content")
                .build();
        
        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(response);
        
        boolean exists = connector.secretExists(secretName);
        
        assertTrue(exists);
    }

    @Test
    @DisplayName("Deve verificar que secret inexistente não existe")
    public void testSecretNotExists() {
        String secretName = "nonexistent-secret";
        
        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenThrow(SecretsManagerException.builder()
                        .message("Secret not found")
                        .build());
        
        boolean exists = connector.secretExists(secretName);
        
        assertFalse(exists);
    }

    @Test
    @DisplayName("Deve converter JSON para objeto customizado")
    public void testGetSecretAsJsonObject() {
        String secretName = "json-secret";
        String secretContent = "{\"clientId\": \"my-client\", \"clientSecret\": \"my-secret\"}";
        
        GetSecretValueResponse response = GetSecretValueResponse.builder()
                .secretString(secretContent)
                .build();
        
        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(response);
        
        Secret result = connector.get(secretName, SecretConverters.asObject(Secret.class));
        
        assertNotNull(result);
        assertEquals("my-client", result.getClientId());
        assertEquals("my-secret", result.getClientSecret());
    }

    @Test
    @DisplayName("Deve converter para array usando delimitador")
    public void testGetSecretAsArray() {
        String secretName = "array-secret";
        String secretContent = "value1,value2,value3,value4";
        
        GetSecretValueResponse response = GetSecretValueResponse.builder()
                .secretString(secretContent)
                .build();
        
        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(response);
        
        String[] result = connector.get(secretName, SecretConverters.asArray(","));
        
        assertNotNull(result);
        assertEquals(4, result.length);
        assertEquals("value1", result[0]);
        assertEquals("value4", result[3]);
    }

    @Test
    @DisplayName("Deve fechar conexão sem erros")
    public void testCloseConnection() {
        connector.close();
        verify(mockClient).close();
    }
}

