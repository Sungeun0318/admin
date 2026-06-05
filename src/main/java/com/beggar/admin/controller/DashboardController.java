package com.beggar.admin.controller;

import com.beggar.admin.dto.DashboardStats;
import com.beggar.admin.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/admin";
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        DashboardStats stats = dashboardService.getStats();

        model.addAttribute("pageTitle", "대시보드");
        model.addAttribute("pageDescription", "서비스 운영 지표를 한눈에 확인해.");
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("stats", stats);
        model.addAttribute("recentUsers", dashboardService.getRecentUsers());
        model.addAttribute("recentRooms", dashboardService.getRecentRooms());
        model.addAttribute("recentPosts", dashboardService.getRecentPosts());
        return "dashboard";
    }
}
