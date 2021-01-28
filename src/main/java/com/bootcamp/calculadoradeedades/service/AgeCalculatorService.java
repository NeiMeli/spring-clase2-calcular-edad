package com.bootcamp.calculadoradeedades.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class AgeCalculatorService {
    public int calulateAge(Integer day, Integer month, Integer year) throws AgeCalculatorException {
        final LocalDate birthDate;
        try {
            validatePossitive(day, month, year);
            birthDate = LocalDate.of(year, month, day);
        } catch (final Exception e) {
            throw new AgeCalculatorException(AgeCalculatorError.INVALID_DATE);
        }
        LocalDate now = getNow();
        Period period = Period.between(birthDate, now);
        return period.getYears();
    }

    private void validatePossitive(Integer day, Integer month, Integer year) {
        if (day <= 0 || month <= 0 || year <= 0) throw new RuntimeException();
    }

    // Lo saco afuera para mockearlo en test
    public LocalDate getNow() {
        return LocalDate.now();
    }
}
