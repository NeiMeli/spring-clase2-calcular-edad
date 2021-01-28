package com.bootcamp.calculadoradeedades.service;

public enum AgeCalculatorError implements AgeCalculatorErrorInterface {
    INVALID_DATE("Fecha invalida");
    private final String value;

    AgeCalculatorError(String s) {
        this.value = s;
    }
    @Override
    public String getValue() {
        return value;
    }
}
