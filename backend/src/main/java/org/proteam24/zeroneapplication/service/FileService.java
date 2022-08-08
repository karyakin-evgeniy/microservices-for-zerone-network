package org.proteam24.zeroneapplication.service;

import org.proteam24.zeroneapplication.entity.FileEntity;

public interface FileService {

    FileEntity getFile(Long id);
    void deletedFile(Long id);

}
