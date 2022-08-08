package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> { }
