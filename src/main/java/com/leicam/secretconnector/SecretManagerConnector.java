package com.leicam.secretconnector;

import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leicam.secretconnector.config.SecretManagerClientConfig;
import com.leicam.secretconnector.converter.SecretConverter;
import com.leicam.secretconnector.converter.impl.SecretConverters;

/**
 * Classe responsável pela conexão e recuperação de secrets no AWS Secrets Manager.
 * Fornece uma interface simples para obter valores de secrets armazenados na AWS.
 */
public class SecretManagerConnector {

    private static final Logger logger = LoggerFactory.getLogger(SecretManagerConnector.class);
    private final SecretsManagerClient secretsManagerClient;
    private final SecretConverter<?> converter;

    /**
     * Construtor padrão que inicializa o conector com configurações pré-definidas.
     * 
     * <p>Configurações utilizadas:
     * <ul>
     *   <li>Região: us-east-1 (padrão da AWS)</li>
     *   <li>Conversor: String (valor bruto do secret)</li>
     *   <li>Cliente: criado automaticamente com credenciais do ambiente</li>
     * </ul>
     * 
     * <p>O cliente é criado automaticamente e a região é inferida a partir das:
     * credenciais do sistema, variáveis de ambiente AWS, arquivo de configuração AWS CLI, 
     * ou de acordo com as preferências do sistema operacional.
     * 
     * <p>Use este construtor para casos simples onde você deseja recuperar secrets como strings.
     * Para casos mais complexos com regiões diferentes, conversores customizados ou 
     * configurações avançadas do cliente, use o construtor principal 
     * {@link #SecretManagerConnector(String, SecretConverter, SecretsManagerClient)}.
     * 
     * @see #SecretManagerConnector(String, SecretConverter, SecretsManagerClient)
     * @see SecretConverters
     */
    public SecretManagerConnector() {
        this(SecretConverters.asString(), SecretManagerClientConfig.create());
    }

    /**
     * Construtor de conveniência que inicializa o cliente com uma região e conversor padrão.
     * O cliente SecretsManagerClient é criado automaticamente com as credenciais do ambiente.
     * 
     * <p>Use este construtor quando você quer especificar uma região e conversor padrão,
     * mas não precisa de configurações customizadas do cliente AWS.
     *
     * @param region a região AWS a ser utilizada (ex: us-east-1, sa-east-1)
     * @param profileName nome do profile a ser usado pela classe {@link ProfileFile#defaultProfileFile()}
     * 
     * @see #SecretManagerConnector(String, SecretConverter, SecretsManagerClient)
     * @see SecretConverters
     */
    public SecretManagerConnector(String region, String profileName) {
        this(SecretConverters.asString(), SecretManagerClientConfig.create(region, profileName));
    }


    /**
     * Construtor que inicializa o cliente do Secrets Manager com uma região, conversor padrão e cliente customizado.
     * Este é o construtor principal que permite máxima flexibilidade na configuração do conector.
     * O conversor padrão será utilizado em chamadas de getSecret sem especificar um conversor explícito.
     * 
     * <p>O cliente SecretsManagerClient pode ser configurado com credenciais personalizadas, 
     * timeout, retry policy e outras configurações avançadas antes de ser passado a este construtor.
     *
     * @param converter o conversor padrão a ser utilizado para transformar valores de secrets 
     *                  em objetos Java (ex: SecretConverters.asString(), SecretConverters.asJson(Classe.class)).
     *                  Não pode ser nulo.
     * @param secretsManagerClient cliente AWS SecretsManagerClient configurado e pronto para uso.
     *                              A responsabilidade de criar e manter este cliente é do chamador.
     *                              Não pode ser nulo.
     * 
     * @throws IllegalArgumentException se algum dos parâmetros for nulo
     */
    public SecretManagerConnector(SecretConverter<?> converter, SecretsManagerClient secretsManagerClient) {
        this.converter = converter;
        this.secretsManagerClient = secretsManagerClient;

        logger.info("SecretManagerConnector inicializado com conversor padrão: {}", 
            converter.getClass().getSimpleName()
        );
    }

    /**
     * Recupera o valor de um secret e converte para o tipo especificado usando um conversor genérico.
     * 
     * @param <T> o tipo de objeto a ser retornado
     * @param secretName o nome ou ARN do secret a ser recuperado
     * @param converter a função conversora que transforma a string em um objeto do tipo T
     * @return o valor do secret convertido para o tipo T
     * @throws SecretManagerException se ocorrer erro ao recuperar ou converter o secret
     */
    public <T> T get(String secretName, SecretConverter<T> converter) {
        try {
            String secretValue = get(secretName);
            logger.debug("Convertendo secret '{}' para tipo genérico", secretName);

            T convertedValue = converter.convert(secretValue);
            logger.info("Secret '{}' convertido com sucesso para tipo {}", secretName, 
                    convertedValue.getClass().getSimpleName());
            return convertedValue;
        } catch (Exception e) {
            logger.error("Erro ao converter secret '{}': {}", secretName, e.getMessage());
            throw new SecretManagerException("Falha ao converter o secret: " + secretName, e);
        }
    }

    /**
     * Recupera o valor de um secret armazenado no AWS Secrets Manager.
     *
     * @param secretName o nome ou ARN do secret a ser recuperado
     * @return o valor do secret em formato String
     * @throws SecretNotFoundException se o secret não for encontrado
     * @throws SecretManagerException se ocorrer erro ao recuperar o secret
     */
    public String get(String secretName) {
        try {
            logger.debug("Recuperando secret: {}", secretName);
            
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

            GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
            
            String secretValue;
            if (response.secretString() != null) {
                secretValue = response.secretString();
            } else {
                secretValue = new String(response.secretBinary().asByteArray());
            }
            logger.debug("Secret recuperado \n'{}'", response.toString());
            
            return secretValue;
            
        } catch (SecretsManagerException e) {
            logger.error("Erro ao recuperar secret '{}': {}", secretName, e.getMessage());
            throw new SecretManagerException("Falha ao recuperar o secret: " + secretName, e);
        }
    }

    /**
     * Verifica se um secret existe no AWS Secrets Manager.
     *
     * @param secretName o nome ou ARN do secret
     * @return true se o secret existe, false caso contrário
     */
    public boolean secretExists(String secretName) {
        try {
            logger.debug("Verificando existência do secret: {}", secretName);
            
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

            secretsManagerClient.getSecretValue(request);
            return true;
            
        } catch (SecretsManagerException e) {
            logger.debug("Secret '{}' não encontrado", secretName);
            return false;
        }
    }

    /**
     * Fecha a conexão com o cliente do Secrets Manager.
     * Deve ser chamado quando o objeto não for mais necessário.
     */
    public void close() {
        if (secretsManagerClient != null) {
            secretsManagerClient.close();
            logger.debug("Conexão com Secrets Manager fechada");
        }
    }

    /**
     * Retorna o cliente do Secrets Manager (para uso avançado).
     *
     * @return a instância do SecretsManagerClient
     */
    public SecretsManagerClient getSecretsManagerClient() {
        return secretsManagerClient;
    }

    /**
     * Retorna o conversor padrão injetado no construtor.
     *
     * @return o conversor padrão, ou null se nenhum foi definido
     */
    public SecretConverter<?> getConverter() {
        return converter;
    }

}
