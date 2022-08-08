package org.proteam24.zeroneapplication.service.impl;

import lombok.RequiredArgsConstructor;
import org.proteam24.zeroneapplication.entity.FileEntity;
import org.proteam24.zeroneapplication.repository.FileRepository;
import org.proteam24.zeroneapplication.service.FileService;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public FileEntity getFile(Long id) {

        return fileRepository.getById(id);
    }

    @Override
    public void deletedFile(Long id) {
        fileRepository.deleteById(id);
    }
}
