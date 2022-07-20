package org.proteam24.reports.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.proteam24.reports.entity.ReportEntity;
import org.proteam24.reports.repository.ReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;

    public ResponseEntity<ReportEntity> putReport(ReportEntity reportEntity, Principal principal) {
        reportRepository.save(reportEntity);
        log.info("In putReport was save {} report", reportEntity);
        return new ResponseEntity<>(reportEntity, HttpStatus.OK);
    }

    public ResponseEntity<List<ReportEntity>> getAllReports() {
        List<ReportEntity> reports = new ArrayList<>();
        reportRepository.findAll().forEach(reports::add);
        log.info("In getAllReports - {} reports was found", reports.size());

        return new ResponseEntity<>(reports, HttpStatus.OK);
    }
}
