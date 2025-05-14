package br.com.fiap.voltly.utils;

import br.com.fiap.voltly.exception.InvalidParameterException;

import java.time.LocalDate;
import java.time.YearMonth;

public final class ParameterValidatorUtil {

    private ParameterValidatorUtil() { }

    public static void assertPositive(double value, String name) {
        if (value <= 0) {
            throw new InvalidParameterException(name, "must be positive");
        }
    }

    public static void assertInRange(double value, double min, double max, String name) {
        if (value < min || value > max) {
            throw new InvalidParameterException(name, "must be between " + min + " and " + max);
        }
    }

    public static void assertNonNegative(int value, String name) {
        if (value < 0) {
            throw new InvalidParameterException(name, "must be non-negative");
        }
    }

    public static void assertDateNotFuture(LocalDate date, String name) {
        if (date.isAfter(LocalDate.now())) {
            throw new InvalidParameterException(name, "cannot be in the future");
        }
    }

    public static void assertYearMonthNotFuture(YearMonth ym, String name) {
        if (ym.isAfter(YearMonth.now())) {
            throw new InvalidParameterException(name, "cannot be in the future");
        }
    }
}
