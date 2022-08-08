package org.proteam24.zeroneapplication.conroller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.proteam24.zeroneapplication.dto.AuthenticationRequestDto;
import org.proteam24.zeroneapplication.integration.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.core.IsNot.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@DisplayName("Тест Авторизация через контроллер")
class AuthenticationControllerTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Авторизация")
    void testLogin() throws Exception {

        var objectMapper = new ObjectMapper();

        var requestDto = new AuthenticationRequestDto();
        requestDto.setEmail("akrafit@gmail.com");
        requestDto.setPassword("WQa6J5do");
        mvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(emptyOrNullString())));
    }

}