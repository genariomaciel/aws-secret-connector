package com.leicam.secretconnector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.leicam.secretconnector.converter.SecretConverter;
import com.leicam.secretconnector.converter.impl.SecretConverters;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import com.leicam.secretconnector.Secret;

/**
 * Testes para injeção de conversor padrão no construtor.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Injeção de Conversor")
public class DefaultConverterInjectionTest {

    @Mock
    private SecretsManagerClient mockClient;

    @Nested
    @DisplayName("Testes de Criação com Diferentes Conversores")
    class CreationWithConvertersTests {

        @Test
        @DisplayName("Deve criar conector com conversor padrão Integer")
        public void testConnectorWithIntegerConverter() {
            SecretConverter<Integer> intConverter = SecretConverters.asInteger();
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", intConverter, mockClient);
            
            assertNotNull(connector.getConverter());
            assertEquals(intConverter, connector.getConverter());
        }

        @Test
        @DisplayName("Deve criar conector com conversor padrão customizado")
        public void testConnectorWithCustomConverter() {
            SecretConverter<String[]> arrayConverter = SecretConverters.asArray(",");
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", arrayConverter, mockClient);
            
            assertNotNull(connector.getConverter());
            assertEquals(arrayConverter, connector.getConverter());
        }

        @Test
        @DisplayName("Deve aceitar conversor Boolean como padrão")
        public void testConnectorWithBooleanConverter() {
            SecretConverter<Boolean> boolConverter = SecretConverters.asBoolean();
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", boolConverter, mockClient);
            
            assertNotNull(connector.getConverter());
            assertEquals(boolConverter, connector.getConverter());
        }

        @Test
        @DisplayName("Deve aceitar conversor Double como padrão")
        public void testConnectorWithDoubleConverter() {
            SecretConverter<Double> doubleConverter = SecretConverters.asDouble();
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", doubleConverter, mockClient);
            
            assertNotNull(connector.getConverter());
            assertEquals(doubleConverter, connector.getConverter());
        }

        @Test
        @DisplayName("Deve aceitar conversor Long como padrão")
        public void testConnectorWithLongConverter() {
            SecretConverter<Long> longConverter = SecretConverters.asLong();
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", longConverter, mockClient);
            
            assertNotNull(connector.getConverter());
            assertEquals(longConverter, connector.getConverter());
        }

        @Test
        @DisplayName("Deve aceitar conversor String como padrão")
        public void testConnectorWithStringConverter() {
            SecretConverter<String> stringConverter = SecretConverters.asString();
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", stringConverter, mockClient);
            
            assertNotNull(connector.getConverter());
            assertEquals(stringConverter, connector.getConverter());
        }

        @Test
        @DisplayName("Deve aceitar conversor lambda como padrão")
        public void testConnectorWithLambdaConverter() {
            SecretConverter<Integer> lambdaConverter = (secret) -> Integer.parseInt(secret) * 2;
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", lambdaConverter, mockClient);
            
            assertNotNull(connector.getConverter());
            assertEquals(lambdaConverter, connector.getConverter());
        }

        @Test
        @DisplayName("Deve aceitar conversor para objeto JSON como padrão")
        public void testConnectorWithJsonObjectConverter() {
            SecretConverter<Secret> jsonConverter = SecretConverters.asObject(Secret.class);
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", jsonConverter, mockClient);
            
            assertNotNull(connector.getConverter());
            assertEquals(jsonConverter, connector.getConverter());
        }

        @Test
        @DisplayName("Deve aceitar conversor Array como padrão")
        public void testConnectorWithArrayConverter() {
            SecretConverter<String[]> arrayConverter = SecretConverters.asArray("|");
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", arrayConverter, mockClient);
            
            assertNotNull(connector.getConverter());
            assertEquals(arrayConverter, connector.getConverter());
        }
    }

    @Nested
    @DisplayName("Testes de Persistência do Conversor")
    class ConverterPersistenceTests {

        @Test
        @DisplayName("Deve manter conversor após múltiplas chamadas")
        public void testConverterPersistence() {
            SecretConverter<Integer> intConverter = SecretConverters.asInteger();
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", intConverter, mockClient);
            
            // Primeira chamada
            SecretConverter<?> conv1 = connector.getConverter();
            
            // Segunda chamada
            SecretConverter<?> conv2 = connector.getConverter();
            
            // Terceira chamada
            SecretConverter<?> conv3 = connector.getConverter();
            
            assertEquals(conv1, conv2);
            assertEquals(conv2, conv3);
        }

        @Test
        @DisplayName("Deve usar conversor padrão injetado")
        public void testUsingInjectedDefaultConverter() {
            SecretConverter<Integer> intConverter = SecretConverters.asInteger();
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", intConverter, mockClient);
            
            assertNotNull(connector.getConverter());
            assertSame(intConverter, connector.getConverter());
        }

        @Test
        @DisplayName("Deve retornar conversor mesmo após close")
        public void testConverterAccessAfterClose() {
            SecretConverter<Integer> intConverter = SecretConverters.asInteger();
            SecretManagerConnector connector = new SecretManagerConnector("us-east-1", intConverter, mockClient);
            
            connector.close();
            
            // Conversor ainda deve estar acessível
            assertNotNull(connector.getConverter());
            assertEquals(intConverter, connector.getConverter());
        }
    }

    @Nested
    @DisplayName("Testes de Múltiplas Regiões")
    class MultipleRegionsTests {

        @Test
        @DisplayName("Deve manter conversor em diferentes regiões")
        public void testConverterInMultipleRegions() {
            SecretConverter<Integer> intConverter = SecretConverters.asInteger();
            
            SecretManagerConnector connectorUS = new SecretManagerConnector("us-east-1", intConverter, mockClient);
            SecretManagerConnector connectorSA = new SecretManagerConnector("sa-east-1", intConverter, mockClient);
            SecretManagerConnector connectorEU = new SecretManagerConnector("eu-west-1", intConverter, mockClient);
            
            assertEquals("us-east-1", connectorUS.getRegion());
            assertEquals("sa-east-1", connectorSA.getRegion());
            assertEquals("eu-west-1", connectorEU.getRegion());
            
            assertEquals(intConverter, connectorUS.getConverter());
            assertEquals(intConverter, connectorSA.getConverter());
            assertEquals(intConverter, connectorEU.getConverter());
        }

        @Test
        @DisplayName("Deve suportar conversores diferentes em regiões diferentes")
        public void testDifferentConvertersInMultipleRegions() {
            SecretConverter<Integer> intConverter = SecretConverters.asInteger();
            SecretConverter<String> stringConverter = SecretConverters.asString();
            
            SecretManagerConnector connectorInt = new SecretManagerConnector("us-east-1", intConverter, mockClient);
            SecretManagerConnector connectorString = new SecretManagerConnector("eu-west-1", stringConverter, mockClient);
            
            assertEquals(intConverter, connectorInt.getConverter());
            assertEquals(stringConverter, connectorString.getConverter());
        }
    }
}
