package com.beggar.admin.controller;

import com.beggar.admin.dto.AdminAccountForm;
import com.beggar.admin.dto.AdminAccountListItem;
import com.beggar.admin.service.AdminAccountService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    public AdminAccountController(AdminAccountService adminAccountService) {
        this.adminAccountService = adminAccountService;
    }

    @GetMapping("/admin/admins")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<AdminAccountListItem> admins = adminAccountService.getAccounts(keyword, status, page);

        model.addAttribute("pageTitle", "관리자 계정");
        model.addAttribute("pageDescription", "관리자 계정과 권한을 관리해.");
        model.addAttribute("activeMenu", "admins");
        model.addAttribute("admins", admins);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "admins/list";
    }

    @GetMapping("/admin/admins/new")
    public String createForm(Model model) {
        model.addAttribute("pageTitle", "관리자 생성");
        model.addAttribute("pageDescription", "새 관리자 계정을 추가해.");
        model.addAttribute("activeMenu", "admins");
        model.addAttribute("form", new AdminAccountForm());
        model.addAttribute("mode", "create");
        model.addAttribute("formAction", "/admin/admins");
        return "admins/form";
    }

    @PostMapping("/admin/admins")
    public String create(@ModelAttribute("form") AdminAccountForm form, RedirectAttributes redirectAttributes) {
        adminAccountService.createAccount(form);
        redirectAttributes.addFlashAttribute("message", "관리자 계정을 생성했어.");
        return "redirect:/admin/admins";
    }

    @GetMapping("/admin/admins/{adminId}/edit")
    public String editForm(@PathVariable Long adminId, Model model) {
        model.addAttribute("pageTitle", "관리자 수정");
        model.addAttribute("pageDescription", "관리자 권한과 상태를 수정해.");
        model.addAttribute("activeMenu", "admins");
        model.addAttribute("form", adminAccountService.getForm(adminId));
        model.addAttribute("mode", "edit");
        model.addAttribute("adminId", adminId);
        model.addAttribute("formAction", "/admin/admins/" + adminId);
        return "admins/form";
    }

    @PostMapping("/admin/admins/{adminId}")
    public String update(
            @PathVariable Long adminId,
            @ModelAttribute("form") AdminAccountForm form,
            RedirectAttributes redirectAttributes
    ) {
        adminAccountService.updateAccount(adminId, form);
        redirectAttributes.addFlashAttribute("message", "관리자 계정을 수정했어.");
        return "redirect:/admin/admins";
    }

    @PostMapping("/admin/admins/{adminId}/disable")
    public String disable(@PathVariable Long adminId, RedirectAttributes redirectAttributes) {
        adminAccountService.disableAccount(adminId);
        redirectAttributes.addFlashAttribute("message", "관리자 계정을 비활성화했어.");
        return "redirect:/admin/admins";
    }
}
