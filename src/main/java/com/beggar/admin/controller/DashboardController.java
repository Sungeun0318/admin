package com.beggar.admin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;

@Controller
public class DashboardController {

    private final WebClient webClient;

    @Value("${api.external-server.url}")
    private String apiServerUrl;

    public DashboardController(WebClient backendWebClient) {
        this.webClient = backendWebClient;
    }

    /**
     * 1-1. 루트 경로 접근 시 대시보드로 강제 리다이렉트
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/admin";
    }

    /**
     * 1-2. 메인 대시보드 - 전체 운영 지표 멀티 WebClient 연동
     */
    @GetMapping("/admin")
    public String dashboard(Model model) {
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin")
                .toUriString();

        Map response = webClient.get().uri(uri).retrieve().bodyToMono(Map.class).block();
        Map<String, Object> data = response == null ? null : (Map<String, Object>) response.get("data");

        model.addAttribute("stats", data == null ? null : data.get("stats"));
        model.addAttribute("recentUsers", data == null ? null : data.get("recentUsers"));
        model.addAttribute("recentRooms", data == null ? null : data.get("recentRooms"));
        model.addAttribute("recentPosts", data == null ? null : data.get("recentPosts"));

        // 타임리프 레이아웃 고정 메타 데이터 바인딩
        model.addAttribute("pageTitle", "대시보드");
        model.addAttribute("pageDescription", "서비스 운영 지표를 한눈에 확인합니다.");
        model.addAttribute("activeMenu", "dashboard");

        return "dashboard";
    }
}
