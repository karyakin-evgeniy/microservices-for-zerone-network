package org.proteam24.zeroneapplication.security.jwt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = JwtTokenProvider.class)
@ExtendWith(SpringExtension.class)
@DisplayName("Тест JwtTokenProvider")
class JwtTokenProviderTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;


    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private HttpServletRequest request;

    @Test
    @DisplayName("Создание токена")
    void createToken() {
        String token = jwtTokenProvider.createToken("user@user.ru");
        Assertions.assertTrue(token.length() > 0);
        System.out.println(token);
    }

    @Test
    @DisplayName("Получаем пользователя по его токену")
    void testGetUsername() {
        String email = "akrafit@gmail.com";
        String token = jwtTokenProvider.createToken(email);
        String userName = jwtTokenProvider.getUsername(token);
        Assertions.assertEquals(email, userName);
    }

    @Test
    @DisplayName("Корректируем токен")
    void testResolveToken() {
        String bearer = "Bearer_777";
        when(request.getHeader("Authorization")).thenReturn(bearer);
        String text = jwtTokenProvider.resolveToken(request);
        Assertions.assertEquals("777", text);

    }

    @Test
    @DisplayName("Валидация токена")
    void testValidateToken() {
        String email = "akrafit@gmail.com";
        String token = jwtTokenProvider.createToken(email);
        boolean valid = jwtTokenProvider.validateToken(token);
        Assertions.assertTrue(valid);
    }

    @Test
    @DisplayName("Отлов ошибки при валидации токена")
    void testValidateTokenException(){
        String token = "ABS";
        Exception thrown = Assertions.assertThrows(Exception.class, () -> jwtTokenProvider.validateToken(token));
        Assertions.assertEquals("JWT token is expired or invalid", thrown.getMessage());

    }
}