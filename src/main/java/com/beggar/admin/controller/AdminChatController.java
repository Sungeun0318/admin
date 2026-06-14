package com.beggar.admin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;

@Controller
public class AdminChatController {

    // 💡 채팅 삭제 및 사후 관리를 위해 로컬 서비스(AdminChatService) 유지
    private final WebClient webClient;

    @Value("${api.external-server.url}")
    private String apiServerUrl;

    public AdminChatController(WebClient backendWebClient) {
        this.webClient = backendWebClient;
    }

    /**
     * 3-3-1. 실시간 익명 채팅 내역 모니터링 목록 조회
     */
    @GetMapping("/admin/chats")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long userNo,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        // 💡 외부 API 명세 규격(/admin/chats)에 맞춘 동적 URI 빌드
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/chats")
                .queryParam("keyword", keyword)
                .queryParam("userNo", userNo)
                .queryParam("page", page)
                .toUriString();

        // 외부 API 서버(8080) 호출하여 결과 받아오기
        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // response -> data -> chats 트리 구조 풀어서 뷰 바인딩
        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                model.addAttribute("chats", data.get("chats"));
            } else {
                model.addAttribute("chats", null);
            }
        } else {
            model.addAttribute("chats", null);
        }

        // 타임리프 화면 데이터 및 조건 유지
        model.addAttribute("pageTitle", "채팅 관리");
        model.addAttribute("pageDescription", "커뮤니티 전체 채팅 메시지를 검색하고 삭제할 수 있어.");
        model.addAttribute("activeMenu", "chats");
        model.addAttribute("keyword", keyword);
        model.addAttribute("userNo", userNo);

        return "chats/list";
    }

    /**
     * 3-3-2. 특정 채팅 메시지 삭제 처리 (POST) - 원래 필터 복귀 구조 유지
     */
    @PostMapping("/admin/chats/delete")
    public String delete(
            @RequestParam Long chatId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long userNo,
            RedirectAttributes redirectAttributes
    ) {
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/chats/delete")
                .queryParam("chatId", chatId)
                .toUriString();

        webClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        redirectAttributes.addFlashAttribute("message", "채팅 메시지를 삭제했어.");
        String userParam = userNo == null ? "" : "&userNo=" + userNo;
        return "redirect:/admin/chats?keyword=" + keyword + userParam;
    }
}