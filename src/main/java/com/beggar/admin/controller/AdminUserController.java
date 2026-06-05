package com.beggar.admin.controller;

import com.beggar.admin.dto.UserDetail;
import com.beggar.admin.entity.User;
import com.beggar.admin.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/admin/users")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<User> users = adminUserService.getUsers(keyword, page);

        model.addAttribute("pageTitle", "회원 관리");
        model.addAttribute("pageDescription", "회원 정보를 검색하고 상세 활동을 확인해.");
        model.addAttribute("activeMenu", "users");
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "users/list";
    }

    @GetMapping("/admin/users/{userNo}")
    public String detail(@PathVariable Long userNo, Model model) {
        UserDetail user = adminUserService.getUserDetail(userNo);

        model.addAttribute("pageTitle", "회원 상세");
        model.addAttribute("pageDescription", "회원 기본 정보와 활동 요약을 확인해.");
        model.addAttribute("activeMenu", "users");
        model.addAttribute("user", user);
        return "users/detail";
    }
}
