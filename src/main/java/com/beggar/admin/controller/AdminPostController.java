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
public class AdminPostController {

    // 💡 게시글 삭제 로직을 처리하기 위해 로컬 서비스를 유지합니다.
    private final WebClient webClient;

    @Value("${api.external-server.url}")
    private String apiServerUrl;

    public AdminPostController( WebClient backendWebClient) {
        this.webClient = backendWebClient;
    }

    /**
     * 3-1-1. 커뮤니티 자유게시판 전체 글 관리 목록 조회
     */
    @GetMapping("/admin/community/posts")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String tag,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        // 💡 외부 API 명세 규격(/admin/posts)에 맞춰 URI를 동적 생성합니다.
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/community/posts")
                .queryParam("keyword", keyword)
                .queryParam("tag", tag)
                .queryParam("page", page)
                .toUriString();

        // 8080 서버 통과 후 Map 형태로 응답 래핑 해제
        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // response -> data -> posts 트리 구조 데이터 바인딩
        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                model.addAttribute("posts", data.get("posts"));
            } else {
                model.addAttribute("posts", null);
            }
        } else {
            model.addAttribute("posts", null);
        }

        // 타임리프 화면 렌더링용 고정 속성값 유지
        model.addAttribute("pageTitle", "게시글 관리");
        model.addAttribute("pageDescription", "커뮤니티 게시글을 검색하고 상세 내용을 확인합니다.");
        model.addAttribute("activeMenu", "posts");
        model.addAttribute("keyword", keyword);
        model.addAttribute("tag", tag);

        return "community/posts";
    }

    /**
     * 3-1-2. 게시글 본문 및 댓글 상세 확인
     */
    @GetMapping("/admin/community/posts/{postId}")
    public String detail(@PathVariable Long postId, Model model) {
        // 💡 외부 API 명세 규격(/admin/posts/{postId})에 맞춰 URI 빌드
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/community/posts/" + postId)
                .toUriString();

        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                model.addAttribute("post", data.get("post"));
            } else {
                model.addAttribute("post", null);
            }
        } else {
            model.addAttribute("post", null);
        }

        model.addAttribute("pageTitle", "게시글 상세");
        model.addAttribute("pageDescription", "게시글 본문과 댓글을 확인하고 관리합니다.");
        model.addAttribute("activeMenu", "posts");

        return "community/post-detail";
    }

    /**
     * 3-1-3. 부적절한 게시글 삭제 처리 (POST)
     */
    @PostMapping("/admin/community/posts/{postId}/delete")
    public String delete(@PathVariable Long postId, RedirectAttributes redirectAttributes) {
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/community/posts/" + postId + "/delete")
                .toUriString();

        webClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        redirectAttributes.addFlashAttribute("message", "게시글 삭제 완료 ✅");
        return "redirect:/admin/community/posts";
    }
}