package org.proteam24.zeroneapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.service.StorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Файлы", description = "Работа с файлами (фото)")
public class StorageController {

    private final StorageService storageService;

    @PostMapping(value = "/storage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузка изображения", description = "Загрузка изображение в клаудинари")
    public Object upload(
            Principal principal,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String image) throws IOException {
        if (principal != null) {
            return storageService.store(principal.getName(), file, image);
        }
        return null;
    }

    @DeleteMapping(value = "/storage/{id}")
    @Operation(summary = "Удаление изображения", description = "Удаление изображения в клаудинари")
    public BaseResponseDto deleted(@PathVariable("id") Long id) throws IOException {
        storageService.deletedById(id);
        return new BaseResponseDto();
    }
}
