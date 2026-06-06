package com.beggar.admin.controller;

import com.beggar.admin.dto.GoodPriceStoreForm;
import com.beggar.admin.dto.GoodPriceStoreListItem;
import com.beggar.admin.service.AdminGoodPriceStoreService;
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
public class AdminGoodPriceStoreController {

    private final AdminGoodPriceStoreService storeService;

    public AdminGoodPriceStoreController(AdminGoodPriceStoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/admin/recommendations/stores")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "ALL") String visible,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<GoodPriceStoreListItem> stores = storeService.getStores(keyword, category, visible, page);

        model.addAttribute("pageTitle", "착한가격업소");
        model.addAttribute("pageDescription", "추천에 사용할 착한가격업소를 등록하고 노출 여부를 관리해.");
        model.addAttribute("activeMenu", "stores");
        model.addAttribute("stores", stores);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("visible", visible);
        return "stores/list";
    }

    @GetMapping("/admin/recommendations/stores/new")
    public String createForm(Model model) {
        model.addAttribute("pageTitle", "업소 추가");
        model.addAttribute("pageDescription", "관리자가 추천 후보 업소를 직접 추가해.");
        model.addAttribute("activeMenu", "stores");
        model.addAttribute("form", new GoodPriceStoreForm());
        model.addAttribute("mode", "create");
        model.addAttribute("formAction", "/admin/recommendations/stores");
        return "stores/form";
    }

    @PostMapping("/admin/recommendations/stores")
    public String create(
            @ModelAttribute("form") GoodPriceStoreForm form,
            RedirectAttributes redirectAttributes
    ) {
        storeService.createStore(form);
        redirectAttributes.addFlashAttribute("message", "착한가격업소를 추가했어.");
        return "redirect:/admin/recommendations/stores";
    }

    @GetMapping("/admin/recommendations/stores/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "업소 수정");
        model.addAttribute("pageDescription", "추천 후보 업소 정보를 수정해.");
        model.addAttribute("activeMenu", "stores");
        model.addAttribute("form", storeService.getForm(id));
        model.addAttribute("storeId", id);
        model.addAttribute("mode", "edit");
        model.addAttribute("formAction", "/admin/recommendations/stores/" + id);
        return "stores/form";
    }

    @PostMapping("/admin/recommendations/stores/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("form") GoodPriceStoreForm form,
            RedirectAttributes redirectAttributes
    ) {
        storeService.updateStore(id, form);
        redirectAttributes.addFlashAttribute("message", "착한가격업소 정보를 수정했어.");
        return "redirect:/admin/recommendations/stores";
    }

    @PostMapping("/admin/recommendations/stores/{id}/toggle-visible")
    public String toggleVisible(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        storeService.toggleVisible(id);
        redirectAttributes.addFlashAttribute("message", "노출 여부를 변경했어.");
        return "redirect:/admin/recommendations/stores";
    }

    @PostMapping("/admin/recommendations/stores/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        storeService.deleteStore(id);
        redirectAttributes.addFlashAttribute("message", "착한가격업소를 삭제했어.");
        return "redirect:/admin/recommendations/stores";
    }
}
