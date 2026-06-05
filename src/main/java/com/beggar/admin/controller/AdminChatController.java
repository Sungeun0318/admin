package com.beggar.admin.controller;

import com.beggar.admin.dto.ChatListItem;
import com.beggar.admin.service.AdminChatService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminChatController {

    private final AdminChatService adminChatService;

    public AdminChatController(AdminChatService adminChatService) {
        this.adminChatService = adminChatService;
    }

    @GetMapping("/admin/chats")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long userNo,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<ChatListItem> chats = adminChatService.getChats(keyword, userNo, page);

        model.addAttribute("pageTitle", "채팅 관리");
        model.addAttribute("pageDescription", "커뮤니티 전체 채팅 메시지를 검색하고 삭제할 수 있어.");
        model.addAttribute("activeMenu", "chats");
        model.addAttribute("chats", chats);
        model.addAttribute("keyword", keyword);
        model.addAttribute("userNo", userNo);
        return "chats/list";
    }

    @PostMapping("/admin/chats/delete")
    public String delete(
            @RequestParam Long chatId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long userNo,
            RedirectAttributes redirectAttributes
    ) {
        adminChatService.deleteChat(chatId);
        redirectAttributes.addFlashAttribute("message", "채팅 메시지를 삭제했어.");
        String userParam = userNo == null ? "" : "&userNo=" + userNo;
        return "redirect:/admin/chats?keyword=" + keyword + userParam;
    }
}
