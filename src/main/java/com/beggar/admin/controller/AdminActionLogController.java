package com.beggar.admin.controller;

import com.beggar.admin.dto.AdminActionLogListItem;
import com.beggar.admin.service.AdminActionLogService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminActionLogController {

    private final AdminActionLogService actionLogService;

    public AdminActionLogController(AdminActionLogService actionLogService) {
        this.actionLogService = actionLogService;
    }

    @GetMapping("/admin/logs")
    public String list(
            @RequestParam(defaultValue = "") String adminUsername,
            @RequestParam(defaultValue = "") String action,
            @RequestParam(defaultValue = "") String targetType,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<AdminActionLogListItem> logs = actionLogService.getLogs(
                adminUsername,
                action,
                targetType,
                keyword,
                page
        );

        model.addAttribute("pageTitle", "운영 로그");
        model.addAttribute("pageDescription", "관리자 변경 액션을 조회하고 감사 기록을 확인해.");
        model.addAttribute("activeMenu", "logs");
        model.addAttribute("logs", logs);
        model.addAttribute("adminUsername", adminUsername);
        model.addAttribute("action", action);
        model.addAttribute("targetType", targetType);
        model.addAttribute("keyword", keyword);
        return "logs/list";
    }

    @GetMapping("/admin/logs/{logId}")
    public String detail(@PathVariable Long logId, Model model) {
        model.addAttribute("pageTitle", "운영 로그 상세");
        model.addAttribute("pageDescription", "관리자 액션의 대상과 내용을 확인해.");
        model.addAttribute("activeMenu", "logs");
        model.addAttribute("log", actionLogService.getLog(logId));
        return "logs/detail";
    }
}
