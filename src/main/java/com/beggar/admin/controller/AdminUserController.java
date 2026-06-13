package com.beggar.admin.controller;

import com.beggar.admin.dto.UserDetail;
import com.beggar.admin.service.AdminUserService;
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
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final WebClient webClient;

    // 1. application.properties에서 설정한 URL 주입받기
    @Value("${api.external-server.url}")
    private String apiServerUrl;

    public AdminUserController(AdminUserService adminUserService, WebClient backendWebClient) {
        this.adminUserService = adminUserService;
        this.webClient = backendWebClient;
    }

    /**
     * 2-1. 회원 목록 조회 (검색/페이징) - 💡 원하셨던 코드 100% 적용
     */
    @GetMapping("/admin/users")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        // 2. 주입받은 apiServerUrl 변수를 활용하여 동적 URI 생성
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/users") // 도메인 뒤의 고정 경로 지정
                .queryParam("keyword", keyword)
                .queryParam("page", page)
                .toUriString();

        // 3. WebClient 호출
        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // 4. response -> data -> users 계층 데이터 구조 추출
        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                Object usersData = data.get("users");
                model.addAttribute("users", usersData);
            } else {
                model.addAttribute("users", null);
            }
        } else {
            model.addAttribute("users", null);
        }

        model.addAttribute("pageTitle", "회원 관리");
        model.addAttribute("pageDescription", "회원 정보를 검색하고 상세 활동을 확인해.");
        model.addAttribute("activeMenu", "users");
        model.addAttribute("keyword", keyword);

        return "users/list";
    }

    /**
     * 2-2. 특정 회원 상세 정보 및 활동 요약
     */
    @GetMapping("/admin/users/{userNo}")
    public String detail(@PathVariable Long userNo, Model model) {
        // 목록 조회 데이터 정합성을 맞추기 위해 상세 조회도 외부 서버(8080)에서 데이터를 긁어옵니다.
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/users/" + userNo)
                .toUriString();

        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                Object userData = data.get("user");
                model.addAttribute("user", userData);
            } else {
                model.addAttribute("user", null);
            }
        } else {
            model.addAttribute("user", null);
        }

        model.addAttribute("pageTitle", "회원 상세");
        model.addAttribute("pageDescription", "회원 기본 정보와 활동 요약을 확인해.");
        model.addAttribute("activeMenu", "users");

        return "users/detail";
    }

    // 💡 추후 여기에 adminUserService.deleteUser(userNo) 같은 로직을 사용하는
    // @PostMapping("/admin/users/{userNo}/delete") 메서드를 추가하시면 됩니다!
}