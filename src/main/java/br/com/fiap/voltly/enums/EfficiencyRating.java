package br.com.fiap.voltly.enums;

public enum EfficiencyRating {
    GOOD,
    AVERAGE,
    POOR;

    public static EfficiencyRating classify(double consumption, double dailyLimitKwh) {
        if (consumption <= dailyLimitKwh * 0.8) {
            return GOOD;
        } else if (consumption <= dailyLimitKwh) {
            return AVERAGE;
        } else {
            return POOR;
        }
    }
}
