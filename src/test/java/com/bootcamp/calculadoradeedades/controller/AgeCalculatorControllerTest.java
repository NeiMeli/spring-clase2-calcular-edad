package com.bootcamp.calculadoradeedades.controller;

import com.bootcamp.calculadoradeedades.model.AgeDTO;
import com.bootcamp.calculadoradeedades.service.AgeCalculatorError;
import com.bootcamp.calculadoradeedades.service.AgeCalculatorException;
import com.bootcamp.calculadoradeedades.service.AgeCalculatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AgeCalculatorControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AgeCalculatorService service;

    private static final String PATH = "/{day}/{month}/{year}";
    private static final MockHttpServletRequestBuilder requestBuilder = get(PATH, 1, 2, 3);

    @Test
    public void testHappy() throws Exception {
        final int result = 1;
        when(service.calulateAge(anyInt(), anyInt(), anyInt())).thenReturn(result);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();
        AgeDTO ageDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AgeDTO.class);
        assertEquals(1, ageDTO.getAge());
        assertEquals("1/2/3", ageDTO.getDate());
    }

    @Test
    public void testBadRequest() throws Exception {
        when(service.calulateAge(anyInt(), anyInt(), anyInt())).thenThrow(new AgeCalculatorException(AgeCalculatorError.INVALID_DATE));
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(ResponseStatusException.class).hasMessageContaining(AgeCalculatorError.INVALID_DATE.getValue());
    }

    @Test
    public void testInternalServerError() throws Exception {
        when(service.calulateAge(anyInt(), anyInt(), anyInt())).thenThrow(new RuntimeException(AgeCalculatorError.INVALID_DATE.getValue()));
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value())).andReturn();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(RuntimeException.class).hasMessageContaining(AgeCalculatorError.INVALID_DATE.getValue());
    }
}