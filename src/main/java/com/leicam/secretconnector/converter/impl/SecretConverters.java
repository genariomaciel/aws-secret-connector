package com.leicam.secretconnector.converter.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leicam.secretconnector.converter.SecretConverter;

/**
 * Classe utilitária que fornece conversores pré-configurados para tipos comuns.
 * Facilita a conversão de secrets em diferentes formatos.
 */
public class SecretConverters {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Conversor que retorna a string como está (sem conversão).
     *
     * @return um conversor que retorna a string original
     */
    public static SecretConverter<String> asString() {
        return secretValue -> secretValue;
    }

    /**
     * Conversor que faz parsing de JSON para um mapa.
     *
     * @param <T> o tipo do objeto JSON
     * @param clazz a classe do objeto a ser convertido
     * @return um conversor que transforma JSON em um objeto
     */
    public static <T> SecretConverter<T> asObject(Class<T> clazz) {
        return secretValue -> objectMapper.readValue(secretValue, clazz);
    }

    /**
     * Conversor que retorna um inteiro.
     *
     * @return um conversor que converte string para Integer
     */
    public static SecretConverter<Integer> asInteger() {
        return Integer::parseInt;
    }

    /**
     * Conversor que retorna um long.
     *
     * @return um conversor que converte string para Long
     */
    public static SecretConverter<Long> asLong() {
        return Long::parseLong;
    }

    /**
     * Conversor que retorna um boolean.
     *
     * @return um conversor que converte string para Boolean
     */
    public static SecretConverter<Boolean> asBoolean() {
        return Boolean::parseBoolean;
    }

    /**
     * Conversor que retorna um double.
     *
     * @return um conversor que converte string para Double
     */
    public static SecretConverter<Double> asDouble() {
        return Double::parseDouble;
    }

    /**
     * Conversor que divide uma string por delimitador e retorna um array.
     *
     * @param delimiter o delimitador para dividir a string
     * @return um conversor que retorna um array de strings
     */
    public static SecretConverter<String[]> asArray(String delimiter) {
        return secretValue -> secretValue.split(delimiter);
    }

    /**
     * Conversor customizado para lógica específica do usuário.
     *
     * @param <T> o tipo de retorno
     * @param converter a função de conversão
     * @return um conversor customizado
     */
    public static <T> SecretConverter<T> custom(SecretConverter<T> converter) {
        return converter;
    }
}
