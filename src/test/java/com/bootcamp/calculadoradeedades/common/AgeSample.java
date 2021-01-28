package com.bootcamp.calculadoradeedades.common;

import com.bootcamp.calculadoradeedades.model.DateDTO;
import net.minidev.json.JSONValue;

import java.time.LocalDate;

public class AgeSample {
    public static AgeSample SAMPLE1 = new AgeSample(22, 10, 1991, 29, LocalDate.of(2021, 1, 28));
    public static AgeSample SAMPLE2 = new AgeSample(17, 8, 2000, 6, LocalDate.of(2007, 5, 4));
    public static AgeSample SAMPLE3 = new AgeSample(26, 4, 1909, 75, LocalDate.of(1984, 10, 12));
    public static AgeSample SAMPLE4 = new AgeSample(29, 2, 2048, 68, LocalDate.of(2116, 7, 20));

    public int day;
    public int month;
    public int year;
    public int age;
    public LocalDate now;

    public AgeSample(int day, int month, int year, int age, LocalDate now) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.age = age;
        this.now = now;
    }

    public static AgeSample[] getSamplesArray() {
        return new AgeSample[]{SAMPLE1, SAMPLE2, SAMPLE3, SAMPLE4};
    }

    public String getDateDTOasString() {
        return JSONValue.toJSONString(new DateDTO(day, month, year));
    }
}
