package org.proteam24.zeroneapplication.service.impl;

import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.config.StorageConfig;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.entity.FileEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.repository.UserRepository;
import org.proteam24.zeroneapplication.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final UserRepository userRepository;
    private final StorageConfig storageConfig;
    private final FileServiceImpl fileService;

    public BaseResponseDto store(String userName, MultipartFile file, String type) throws IOException {
        String filename = file.getOriginalFilename();
        Map params = ObjectUtils.asMap("transformation", new Transformation().width(236).height(236).crop("scale"));
        if (filename == null) {
            log.info("Не определен файл для записи");
        } else {
            Map uploadResult = storageConfig.cloudinaryBean().uploader().upload(file.getBytes(), params);
            if (type.equals("IMAGE")) {
                UserEntity userEntity = userRepository.findByEmail(userName);
                userEntity.setPhoto(uploadResult.get("secure_url").toString());
                userRepository.save(userEntity);
                return new BaseResponseDto<Map>(Map.of("id", 1, "url", uploadResult.get("url")), "");
            } else if (type.equals("POSTIMAGE")) {
                uploadResult.get("url");
                return new BaseResponseDto<Map>(Map.of("id", 1, "url", uploadResult.get("url")), "");
            }
        }
        return null;
    }

    @Override
    public void deletedById(Long id) throws IOException {
        FileEntity file = fileService.getFile(id);
        String path = file.getPath();
        deletedByURL(path);
        fileService.deletedFile(id);
        String publicId = path.substring(path.lastIndexOf("/") + 1, path.length() - 4);
        storageConfig.cloudinaryBean().uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    @Override
    public void deletedByURL(String url) throws IOException {
        String publicUrl = url.substring(url.lastIndexOf("/") + 1, url.length() - 4);
        storageConfig.cloudinaryBean().uploader().destroy(publicUrl, Map.of());
    }
}