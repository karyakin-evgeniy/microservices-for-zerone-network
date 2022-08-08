package org.proteam24.zeroneapplication.service;

import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    BaseResponseDto store(String userName, MultipartFile file, String type) throws IOException;

    void deletedById(Long id) throws IOException;

    void deletedByURL(String url) throws IOException;
}
