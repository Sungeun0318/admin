package com.beggar.admin.controller;

import com.beggar.admin.service.AdminActionLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;

@Controller
public class AdminActionLogController {

    // 💡 로그 사후 관리 및 내부 로깅을 위해 로컬 서비스를 유지합니다.
    private final AdminActionLogService actionLogService;
    private final WebClient webClient;

    @Value("${api.external-server.url}")
    private String apiServerUrl;

    public AdminActionLogController(AdminActionLogService actionLogService, WebClient backendWebClient) {
        this.actionLogService = actionLogService;
        this.webClient = backendWebClient;
    }

    /**
     * 4-4-1. 관리자들이 수행한 모든 시스템 작업 로그 목록 조회
     */
    @GetMapping("/admin/logs")
    public String list(
            @RequestParam(defaultValue = "") String adminUsername,
            @RequestParam(defaultValue = "") String action,
            @RequestParam(defaultValue = "") String targetType,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        // 💡 외부 API 명세 규격(/admin/logs)에 맞춰 동적 URI 및 다중 파라미터 생성
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/logs")
                .queryParam("adminUsername", adminUsername)
                .queryParam("action", action)
                .queryParam("targetType", targetType)
                .queryParam("keyword", keyword)
                .queryParam("page", page)
                .toUriString();

        // WebClient 호출로 외부 서버 데이터 가로채기
        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // response -> data -> logs 구조 해제 후 뷰 모델에 매핑
        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                model.addAttribute("logs", data.get("logs"));
            } else {
                model.addAttribute("logs", null);
            }
        } else {
            model.addAttribute("logs", null);
        }

        // 기존 타임리프 검색 조건 상태 유지 및 고정 메타 데이터 바인딩
        model.addAttribute("pageTitle", "운영 로그");
        model.addAttribute("pageDescription", "관리자 변경 액션을 조회하고 감사 기록을 확인합니다.");
        model.addAttribute("activeMenu", "logs");
        model.addAttribute("adminUsername", adminUsername);
        model.addAttribute("action", action);
        model.addAttribute("targetType", targetType);
        model.addAttribute("keyword", keyword);

        return "logs/list";
    }

    /**
     * 4-4-2. 특정 관리자 액션 로그 상세 내역 확인
     */
    @GetMapping("/admin/logs/{logId}")
    public String detail(@PathVariable Long logId, Model model) {
        // 💡 외부 API 명세 규격(/admin/logs/{logId})에 맞춰 URI 빌드
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/logs/" + logId)
                .toUriString();

        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                model.addAttribute("log", data.get("log"));
            } else {
                model.addAttribute("log", null);
            }
        } else {
            model.addAttribute("log", null);
        }

        model.addAttribute("pageTitle", "운영 로그 상세");
        model.addAttribute("pageDescription", "관리자 액션의 대상과 내용을 확인합니다.");
        model.addAttribute("activeMenu", "logs");

        return "logs/detail";
    }
}