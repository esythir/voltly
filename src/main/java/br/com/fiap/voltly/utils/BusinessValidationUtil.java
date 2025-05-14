package br.com.fiap.voltly.utils;

import br.com.fiap.voltly.exception.InvalidParameterException;
import org.springframework.util.StringUtils;

public final class BusinessValidationUtil {

    private BusinessValidationUtil() { }

    public static void assertNotBlank(String value, String name) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidParameterException(name, "must not be blank");
        }
    }

    public static void assertNotNull(Object value, String name) {
        if (value == null) {
            throw new InvalidParameterException(name, "must not be null");
        }
    }
}
