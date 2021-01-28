package com.bootcamp.calculadoradeedades.service;

import com.bootcamp.calculadoradeedades.common.AgeSample;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
class AgeCalculatorServiceTest {

    @MockBean
    AgeCalculatorService service;

    @Test
    public void testHappy() throws Exception {
        AgeSample [] samples = AgeSample.getSamplesArray();
        for (AgeSample sample : samples) {
            when(service.getNow()).thenReturn(sample.now);
            when(service.calulateAge(sample.day, sample.month, sample.year)).thenCallRealMethod();
            assertEquals(sample.age, service.calulateAge(sample.day, sample.month, sample.year));
        }
    }

    @Test
    public void testFails() throws Exception {
        when(service.calulateAge(anyInt(), anyInt(), anyInt())).thenCallRealMethod();
        // negative day
        assertFails(-5, 10, 1999);
        // negative month
        assertFails(5, -10, 1999);
        // negative year
        assertFails(5, 10, -1999);

        // day surpasses max
        assertFails(32, 10, 1999);
        // month surpasses max
        assertFails(5, 13, 1999);

        // day zero
        assertFails(0, 10, 1999);
        // month zero
        assertFails(32, 0, 1999);
        // year zero
        assertFails(32, 10, 0);
    }

    private void assertFails(int day, int month, int year) {
        assertThatExceptionOfType(AgeCalculatorException.class).isThrownBy(() -> service.calulateAge(day, month, year));
    }
}