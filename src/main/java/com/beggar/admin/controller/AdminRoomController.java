package com.beggar.admin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;

@Controller
public class AdminRoomController {

    private final WebClient webClient;

    @Value("${api.external-server.url}")
    private String apiServerUrl;

    public AdminRoomController(WebClient backendWebClient) {
        this.webClient = backendWebClient;
    }

    /**
     * 3-1. 방 목록 조회 (검색/상태필터/페이징)
     */
    @GetMapping("/admin/rooms")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/rooms")
                .queryParam("keyword", keyword)
                .queryParam("status", status)
                .queryParam("page", page)
                .toUriString();

        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                model.addAttribute("rooms", data.get("rooms"));
            } else {
                model.addAttribute("rooms", null);
            }
        } else {
            model.addAttribute("rooms", null);
        }

        model.addAttribute("pageTitle", "방 관리");
        model.addAttribute("pageDescription", "방 정보를 검색하고 상세 운영 데이터를 확인해.");
        model.addAttribute("activeMenu", "rooms");
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "rooms/list";
    }

    /**
     * 3-2. 방 상세 정보 조회
     */
    @GetMapping("/admin/rooms/{roomNo}")
    public String detail(@PathVariable Long roomNo, Model model) {
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/rooms/" + roomNo)
                .toUriString();

        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                model.addAttribute("room", data.get("room"));
            } else {
                model.addAttribute("room", null);
            }
        } else {
            model.addAttribute("room", null);
        }

        model.addAttribute("pageTitle", "방 상세");
        model.addAttribute("pageDescription", "방 기본 정보와 예산/참여/영수증 요약을 확인해.");
        model.addAttribute("activeMenu", "rooms");

        return "rooms/detail";
    }

    /**
     * 3-3. 방 강제 종료 처리 (POST)
     */
    @PostMapping("/admin/rooms/{roomNo}/end")
    public String endRoom(@PathVariable Long roomNo, RedirectAttributes redirectAttributes) {
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/rooms/" + roomNo + "/end")
                .toUriString();

        webClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        redirectAttributes.addFlashAttribute("message", "방을 종료 처리했어.");
        return "redirect:/admin/rooms/%d".formatted(roomNo);
    }

    /**
     * 3-4. 방 삭제 처리 (POST)
     */
    @PostMapping("/admin/rooms/{roomNo}/delete")
    public String deleteRoom(@PathVariable Long roomNo, RedirectAttributes redirectAttributes) {
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/rooms/" + roomNo + "/delete")
                .toUriString();

        webClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        redirectAttributes.addFlashAttribute("message", "방을 삭제 처리했어.");
        return "redirect:/admin/rooms/%d".formatted(roomNo);
    }
}
