package com.beggar.admin.controller;

import com.beggar.admin.dto.PostDetail;
import com.beggar.admin.dto.PostListItem;
import com.beggar.admin.service.AdminPostService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminPostController {

    private final AdminPostService adminPostService;

    public AdminPostController(AdminPostService adminPostService) {
        this.adminPostService = adminPostService;
    }

    @GetMapping("/admin/community/posts")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String tag,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<PostListItem> posts = adminPostService.getPosts(keyword, tag, page);

        model.addAttribute("pageTitle", "게시글 관리");
        model.addAttribute("pageDescription", "커뮤니티 게시글을 검색하고 상세 내용을 확인해.");
        model.addAttribute("activeMenu", "posts");
        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);
        model.addAttribute("tag", tag);
        return "community/posts";
    }

    @GetMapping("/admin/community/posts/{postId}")
    public String detail(@PathVariable Long postId, Model model) {
        PostDetail post = adminPostService.getPostDetail(postId);

        model.addAttribute("pageTitle", "게시글 상세");
        model.addAttribute("pageDescription", "게시글 본문과 댓글을 확인하고 관리해.");
        model.addAttribute("activeMenu", "posts");
        model.addAttribute("post", post);
        return "community/post-detail";
    }

    @PostMapping("/admin/community/posts/{postId}/delete")
    public String delete(@PathVariable Long postId, RedirectAttributes redirectAttributes) {
        adminPostService.deletePost(postId);
        redirectAttributes.addFlashAttribute("message", "게시글을 삭제했어.");
        return "redirect:/admin/community/posts";
    }
}
