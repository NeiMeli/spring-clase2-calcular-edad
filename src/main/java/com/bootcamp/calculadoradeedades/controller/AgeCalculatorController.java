package com.bootcamp.calculadoradeedades.controller;

import com.bootcamp.calculadoradeedades.model.AgeDTO;
import com.bootcamp.calculadoradeedades.model.DateDTO;
import com.bootcamp.calculadoradeedades.service.AgeCalculatorException;
import com.bootcamp.calculadoradeedades.service.AgeCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;


@RestController
public class AgeCalculatorController {
    @Autowired
    AgeCalculatorService service;

    @GetMapping("/{day}/{month}/{year}")
    @ResponseBody
    public ResponseEntity<AgeDTO> calculateAge(@PathVariable Integer day, @PathVariable Integer month, @PathVariable Integer year) {
        return calculate(day, month, year);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<AgeDTO> calculateAge(@RequestBody DateDTO dateDTO) {
        return calculate(dateDTO.getDay(), dateDTO.getMonth(), dateDTO.getYear());
    }

    private ResponseEntity<AgeDTO> calculate(Integer day, Integer month, Integer year) {
        AgeDTO response;
        try {
            int age = service.calulateAge(day, month, year);
            response = new AgeDTO(String.format("%s/%s/%s", day, month, year), age);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            HttpStatus status;
            if (e instanceof AgeCalculatorException) {
                status = HttpStatus.BAD_REQUEST;
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMismatchException(MethodArgumentTypeMismatchException ex) {
        String expectedArgName = ex.getName();
        String expectedArgType = Objects.requireNonNull(ex.getRequiredType()).getSimpleName();
        String actualValueType = Objects.requireNonNull(ex.getValue()).getClass().getSimpleName();
        return new ResponseEntity<>(buildArgumentTypeMismatchResponse(expectedArgName, expectedArgType, actualValueType), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    public static String buildArgumentTypeMismatchResponse(String expectedArgName, String expectedArgType, Object actualValue) {
        String actualValueType = actualValue.getClass().getSimpleName();
        return String.format("El parametro '%s' deberia ser de tipo '%s' y es de tipo '%s'", expectedArgName, expectedArgType, actualValueType);
    }
}
