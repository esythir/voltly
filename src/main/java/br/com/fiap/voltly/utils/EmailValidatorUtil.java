package br.com.fiap.voltly.utils;

import java.util.regex.Pattern;

public final class EmailValidatorUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?=.{1,254}$)(?=.{1,64}@)[A-Za-z0-9.!#$%&'*+/=?^`{|}~-]+" +
                    "@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?" +
                    "(?:.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$"
    );

    private EmailValidatorUtil() { }

    public static boolean isValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static void assertValid(String email) {
        if (!isValid(email)) {
            throw new br.com.fiap.voltly.exception.ValidationException("Invalid e‑mail format");
        }
    }
}
