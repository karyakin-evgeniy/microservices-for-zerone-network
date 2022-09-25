package org.proteam24.zeroneapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.proteam24.zeroneapplication.dto.SupportReadDto;
import org.proteam24.zeroneapplication.dto.SupportWriteDto;
import org.proteam24.zeroneapplication.service.SupportService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Поддержка", description = "Запросы в техническую поддержку")
public class SupportRestController {

    private final SupportService supportService;

    @PostMapping("/support")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Запрос в поддержку", description = "Создание запроса в поддержку")
    public SupportReadDto create(@RequestBody SupportWriteDto support) {
        return supportService.create(support);
    }
}
