package com.bootcamp.calculadoradeedades;

import com.bootcamp.calculadoradeedades.common.AgeSample;
import com.bootcamp.calculadoradeedades.controller.AgeCalculatorController;
import com.bootcamp.calculadoradeedades.model.AgeDTO;
import com.bootcamp.calculadoradeedades.service.AgeCalculatorError;
import com.bootcamp.calculadoradeedades.service.AgeCalculatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AgeCalculatorIntegrationTest {
    private static final String GET_PATH = "/{day}/{month}/{year}";
    private static final String POST_PATH = "/";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AgeCalculatorService service;

    @BeforeEach // solo quiero mockear la fecha
    void beforeEach() throws Exception {
        when(service.calulateAge(anyInt(), anyInt(), anyInt())).thenCallRealMethod();
    }

    @Test
    void testGetHappy() throws Exception {
        AgeSample[] samplesArray = AgeSample.getSamplesArray();
        for (AgeSample sample : samplesArray) {
            when(service.getNow()).thenReturn(sample.now);
            MvcResult mvcResult = mockMvc.perform(get(GET_PATH, sample.day, sample.month, sample.year))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andReturn();
            AgeDTO ageDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AgeDTO.class);
            assertEquals(sample.age, ageDTO.getAge());
            assertEquals(String.format("%s/%s/%s",sample.day, sample.month, sample.year), ageDTO.getDate());
        }
    }

    @Test
    void testPostHappy() throws Exception {
        AgeSample[] samplesArray = AgeSample.getSamplesArray();
        for (AgeSample sample : samplesArray) {
            when(service.getNow()).thenReturn(sample.now);
            final String dateDTOjson = sample.getDateDTOasString();
            MvcResult mvcResult = mockMvc.perform(post(POST_PATH)
                    .contentType(MediaType.APPLICATION_JSON).content(dateDTOjson))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andReturn();
            AgeDTO ageDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AgeDTO.class);
            assertEquals(sample.age, ageDTO.getAge());
            assertEquals(String.format("%s/%s/%s",sample.day, sample.month, sample.year), ageDTO.getDate());
        }
    }
    
    @Test
    void testBadRequests() throws Exception {
        DateInputsAsserter badRequestAsserter = (day, month, year) ->
        {
            MvcResult mvcResult = mockMvc.perform(get(GET_PATH, day, month, year))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andReturn();
            assertThat(mvcResult.getResolvedException()).isInstanceOf(ResponseStatusException.class).hasMessageContaining(AgeCalculatorError.INVALID_DATE.getValue());
        };

        // negative day
        badRequestAsserter.doAssert(-5, 10, 1999);
        // negative month
        badRequestAsserter.doAssert(5, -10, 1999);
        // negative year
        badRequestAsserter.doAssert(5, 10, -1999);

        // day surpasses max
        badRequestAsserter.doAssert(32, 10, 1999);
        // month surpasses max
        badRequestAsserter.doAssert(5, 13, 1999);

        // day zero
        badRequestAsserter.doAssert(0, 10, 1999);
        // month zero
        badRequestAsserter.doAssert(32, 0, 1999);
        // year zero
        badRequestAsserter.doAssert(32, 10, 0);
    }

    @FunctionalInterface
    private interface DateInputsAsserter {
        void doAssert(int day, int month, int year) throws Exception;
    }

    @Test
    void testArgumentTypeMismatch() throws Exception {
        mockMvc.perform(get(GET_PATH, "day", "9", "1991"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().string(containsString(AgeCalculatorController.buildArgumentTypeMismatchResponse("day", Integer.class.getSimpleName(), "day"))));
        mockMvc.perform(get(GET_PATH, "12", "month", "1991"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().string(containsString(AgeCalculatorController.buildArgumentTypeMismatchResponse("month", Integer.class.getSimpleName(), "month"))));
        mockMvc.perform(get(GET_PATH, "12", "9", "year"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().string(containsString(AgeCalculatorController.buildArgumentTypeMismatchResponse("year", Integer.class.getSimpleName(), "year"))));
    }

    @Test
    void testInvalidDateDTO() throws Exception {
        final String invalidDateDTO = "{\n" +
                "    \"day\": 22a,\n" +
                "    \"month\":\"10b\",\n" +
                "    \"year\":1991c\n" +
                "}";
        mockMvc.perform(post(POST_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(invalidDateDTO))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().string(containsString("JSON parse error")));
    }
}
