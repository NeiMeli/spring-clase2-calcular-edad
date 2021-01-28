package com.bootcamp.calculadoradeedades.service;

public class AgeCalculatorException extends Exception {
    public AgeCalculatorException(AgeCalculatorErrorInterface e) {
        super(e.getValue());
    }
}
