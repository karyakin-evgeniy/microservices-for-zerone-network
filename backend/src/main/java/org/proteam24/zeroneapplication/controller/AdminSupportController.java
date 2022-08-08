package org.proteam24.zeroneapplication.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.dto.SupportReadDto;
import org.proteam24.zeroneapplication.service.SupportService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/support")
@RequiredArgsConstructor
public class AdminSupportController {

    private final SupportService supportService;

    @GetMapping
    public String findAll(Model model) {
        List<SupportReadDto> tickets = supportService.findAll();
        model.addAttribute("tickets", tickets);
        return "admin/tickets";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") Long id, Model model) {
        return supportService.findById(id)
                .map(ticket -> {
                    model.addAttribute("ticket", ticket);
                    return "admin/ticket";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
