package com.leicam.secretconnector;

/**
 * Exceção personalizada para erros relacionados ao AWS Secrets Manager.
 * Encapsula exceções do Secrets Manager e fornece contexto adicional.
 */
public class SecretManagerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor com mensagem de erro.
     *
     * @param message a mensagem de erro
     */
    public SecretManagerException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem de erro e causa.
     *
     * @param message a mensagem de erro
     * @param cause a exceção que causou este erro
     */
    public SecretManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor com a causa da exceção.
     *
     * @param cause a exceção que causou este erro
     */
    public SecretManagerException(Throwable cause) {
        super(cause);
    }
}
