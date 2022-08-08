package org.proteam24.zeroneapplication.service;

import lombok.RequiredArgsConstructor;
import org.proteam24.zeroneapplication.dto.SupportReadDto;
import org.proteam24.zeroneapplication.dto.SupportResponseDto;
import org.proteam24.zeroneapplication.dto.SupportWriteDto;
import org.proteam24.zeroneapplication.mapper.SupportReadMapper;
import org.proteam24.zeroneapplication.mapper.SupportResponseMapper;
import org.proteam24.zeroneapplication.mapper.SupportWriteMapper;
import org.proteam24.zeroneapplication.repository.SupportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupportService {

    private final SupportRepository supportRepository;
    private final SupportWriteMapper supportWriteMapper;
    private final SupportResponseMapper supportResponseMapper;
    private final SupportReadMapper supportReadMapper;

    public List<SupportReadDto> findAll() {
        return supportRepository.findAll().stream()
                .map(supportReadMapper::map)
                .collect(Collectors.toList());
    }

    public Optional<SupportReadDto> findById(Long id) {
        return supportRepository.findById(id)
                .map(supportReadMapper::map);
    }

    @Transactional
    public SupportResponseDto create(SupportWriteDto supportDto) {
        return Optional.of(supportDto)
                .map(supportWriteMapper::map)
                .map(supportRepository::save)
                .map(supportResponseMapper::map)
                .orElseThrow();
    }
}
