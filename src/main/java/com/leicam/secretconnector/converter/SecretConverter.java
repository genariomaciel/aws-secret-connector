package com.leicam.secretconnector.converter;

/**
 * Interface funcional para conversão de string (secret) para um objeto genérico.
 * Permite que o método getSecret converta o valor recuperado em qualquer tipo desejado.
 *
 * @param <T> o tipo de objeto a ser retornado pela conversão
 */
@FunctionalInterface
public interface SecretConverter<T> {

    /**
     * Converte uma string (valor do secret) para um objeto do tipo T.
     *
     * @param secretValue a string do secret recuperada
     * @return o objeto convertido
     * @throws Exception se ocorrer erro durante a conversão
     */
    T convert(String secretValue) throws Exception;
}
