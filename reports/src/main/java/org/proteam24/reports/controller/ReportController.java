package org.proteam24.reports.controller;


import lombok.RequiredArgsConstructor;
import org.proteam24.reports.entity.ReportEntity;
import org.proteam24.reports.repository.ReportRepository;
import org.proteam24.reports.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;


    @PutMapping("/")
    public ResponseEntity putReport(@RequestBody ReportEntity reportEntity, Principal principal) {
        return reportService.putReport(reportEntity,principal);
    }

    @GetMapping("/")
    public ResponseEntity getAllReports() {
        return reportService.getAllReports();
    }

}
