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
public class AdminCommentController {

    // 💡 댓글 삭제 및 사후 처리를 위해 로컬 서비스(AdminCommentService) 유지
    private final WebClient webClient;

    @Value("${api.external-server.url}")
    private String apiServerUrl;

    public AdminCommentController(WebClient backendWebClient) {
        this.webClient = backendWebClient;
    }

    /**
     * 3-2-1. 전체 댓글 목록 조회 (검색/특정게시글 필터링/페이징)
     */
    @GetMapping("/admin/community/comments")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long postId,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        // 💡 외부 API 서버(8080) 명세인 "/admin/comments" 경로로 URI 빌드
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/community/comments")
                .queryParam("keyword", keyword)
                .queryParam("postId", postId)
                .queryParam("page", page)
                .toUriString();

        // WebClient로 데이터 바인딩 받기
        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // response -> data -> comments 트리 구조 풀어서 뷰에 매핑
        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                model.addAttribute("comments", data.get("comments"));
            } else {
                model.addAttribute("comments", null);
            }
        } else {
            model.addAttribute("comments", null);
        }

        // 타임리프 전용 렌더링 속성 및 파라미터 백업 유지
        model.addAttribute("pageTitle", "댓글 관리");
        model.addAttribute("pageDescription", "커뮤니티 댓글을 검색하고 삭제할 수 있습니다.");
        model.addAttribute("activeMenu", "comments");
        model.addAttribute("keyword", keyword);
        model.addAttribute("postId", postId);

        return "community/comments";
    }

    /**
     * 3-2-2. 부적절한 댓글 삭제 처리 (POST) - 원래 필터 복귀 구조 유지
     */
    @PostMapping("/admin/community/comments/delete")
    public String delete(
            @RequestParam Long commentId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long postId,
            RedirectAttributes redirectAttributes
    ) {
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/community/comments/delete")
                .queryParam("commentId", commentId)
                .toUriString();

        webClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        redirectAttributes.addFlashAttribute("message", "댓글 삭제 완료 ✅");
        String postParam = postId == null ? "" : "&postId=" + postId;
        return "redirect:/admin/community/comments?keyword=" + keyword + postParam;
    }
}