package com.leicam.secretconnector.models;

import java.io.Serializable;

/**
 * Classe que representa um valor de secret recuperado do AWS Secrets Manager.
 * Encapsula o nome do secret e seu valor.
 */
public class SecretValue implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String secretName;
    private final String secretContent;

    /**
     * Construtor que inicializa um SecretValue.
     *
     * @param secretName o nome ou ARN do secret
     * @param secretContent o conteúdo/valor do secret
     */
    public SecretValue(String secretName, String secretContent) {
        this.secretName = secretName;
        this.secretContent = secretContent;
    }

    /**
     * Retorna o nome ou ARN do secret.
     *
     * @return o nome do secret
     */
    public String getSecretName() {
        return secretName;
    }

    /**
     * Retorna o conteúdo/valor do secret.
     *
     * @return o valor do secret
     */
    public String getSecretContent() {
        return secretContent;
    }

    /**
     * Verifica se o secret está vazio.
     *
     * @return true se o conteúdo do secret está vazio, false caso contrário
     */
    public boolean isEmpty() {
        return secretContent == null || secretContent.isEmpty();
    }

    @Override
    public String toString() {
        return "SecretValue{" +
                "secretName='" + secretName + '\'' +
                ", contentLength=" + (secretContent != null ? secretContent.length() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecretValue that = (SecretValue) o;

        if (secretName != null ? !secretName.equals(that.secretName) : that.secretName != null)
            return false;
        return secretContent != null ? secretContent.equals(that.secretContent) : that.secretContent == null;
    }

    @Override
    public int hashCode() {
        int result = secretName != null ? secretName.hashCode() : 0;
        result = 31 * result + (secretContent != null ? secretContent.hashCode() : 0);
        return result;
    }
}
