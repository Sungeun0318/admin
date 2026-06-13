package com.beggar.admin.controller;

import com.beggar.admin.service.DashboardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;

@Controller
public class DashboardController {

    // 💡 내부 통계 및 로그 사후 관리를 위해 로컬 서비스(DashboardService) 유지
    private final DashboardService dashboardService;
    private final WebClient webClient;

    @Value("${api.external-server.url}")
    private String apiServerUrl;

    public DashboardController(DashboardService dashboardService, WebClient backendWebClient) {
        this.dashboardService = dashboardService;
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

        // 📊 1. 전체 요약 통계 (전체 유저 수, 오늘 생성된 방 수, 영수증 수 등) 외부 호출
        String statsUri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/dashboard/stats")
                .toUriString();

        Map statsResponse = webClient.get().uri(statsUri).retrieve().bodyToMono(Map.class).block();
        if (statsResponse != null && statsResponse.get("data") != null) {
            model.addAttribute("stats", ((Map<String, Object>) statsResponse.get("data")).get("stats"));
        } else {
            model.addAttribute("stats", null);
        }

        // 👤 2. 최근 가입 유저 리스트 외부 호출 (/admin/users 의 최신순 혹은 전용 규격)
        String usersUri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/users")
                .queryParam("page", 0) // 첫 페이지에서 최신 데이터 추출 목적
                .toUriString();

        Map usersResponse = webClient.get().uri(usersUri).retrieve().bodyToMono(Map.class).block();
        if (usersResponse != null && usersResponse.get("data") != null) {
            model.addAttribute("recentUsers", ((Map<String, Object>) usersResponse.get("data")).get("users"));
        } else {
            model.addAttribute("recentUsers", null);
        }

        // 🏠 3. 최근 생성된 방 리스트 외부 호출
        String roomsUri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/rooms")
                .queryParam("page", 0)
                .toUriString();

        Map roomsResponse = webClient.get().uri(roomsUri).retrieve().bodyToMono(Map.class).block();
        if (roomsResponse != null && roomsResponse.get("data") != null) {
            model.addAttribute("recentRooms", ((Map<String, Object>) roomsResponse.get("data")).get("rooms"));
        } else {
            model.addAttribute("recentRooms", null);
        }

        // 📝 4. 최근 작성된 커뮤니티 게시글 리스트 외부 호출
        String postsUri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/posts")
                .queryParam("page", 0)
                .toUriString();

        Map postsResponse = webClient.get().uri(postsUri).retrieve().bodyToMono(Map.class).block();
        if (postsResponse != null && postsResponse.get("data") != null) {
            model.addAttribute("recentPosts", ((Map<String, Object>) postsResponse.get("data")).get("posts"));
        } else {
            model.addAttribute("recentPosts", null);
        }

        // 타임리프 레이아웃 고정 메타 데이터 바인딩
        model.addAttribute("pageTitle", "대시보드");
        model.addAttribute("pageDescription", "서비스 운영 지표를 한눈에 확인해.");
        model.addAttribute("activeMenu", "dashboard");

        return "dashboard";
    }
}