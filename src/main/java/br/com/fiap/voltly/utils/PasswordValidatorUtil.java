package br.com.fiap.voltly.utils;

import java.util.regex.Pattern;

public final class PasswordValidatorUtil {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$");

    private PasswordValidatorUtil() { }

    public static boolean isValid(String rawPassword) {
        return rawPassword != null && PASSWORD_PATTERN.matcher(rawPassword).matches();
    }

    public static void assertValid(String rawPassword) {
        if (!isValid(rawPassword)) {
            throw new br.com.fiap.voltly.exception.ValidationException(
                    "Password must be ≥8 chars, contain upper & lower case, a digit and a special char");
        }
    }
}
