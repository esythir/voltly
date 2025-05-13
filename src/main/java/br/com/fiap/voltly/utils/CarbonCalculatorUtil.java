package br.com.fiap.voltly.utils;

public class CarbonCalculatorUtil {

    private static final double KG_CO2_PER_KWH = 0.233;

    private CarbonCalculatorUtil() { }

    public static double calculateEmission(double consumptionKwh) {
        return consumptionKwh * KG_CO2_PER_KWH;
    }
}
