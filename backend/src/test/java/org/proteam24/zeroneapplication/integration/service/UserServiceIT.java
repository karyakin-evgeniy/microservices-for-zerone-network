package org.proteam24.zeroneapplication.integration.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.proteam24.zeroneapplication.dto.UserReadDto;
import org.proteam24.zeroneapplication.integration.IntegrationTestBase;
import org.proteam24.zeroneapplication.service.PlainUserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RequiredArgsConstructor
@DisplayName("Тест UserServiceIT")
public class UserServiceIT extends IntegrationTestBase {

    private final PlainUserService userService;

    @Test
    @DisplayName("Тест мягкое удаление")
    void softDelete() {
        userService.delete(1L);
        List<UserReadDto> users = userService.findAll();

        assertThat(users).hasSize(4);
    }
}
