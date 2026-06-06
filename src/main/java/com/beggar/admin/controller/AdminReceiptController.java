package com.beggar.admin.controller;

import com.beggar.admin.dto.ReceiptDetail;
import com.beggar.admin.dto.ReceiptListItem;
import com.beggar.admin.service.AdminReceiptService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class AdminReceiptController {

    private final AdminReceiptService adminReceiptService;

    public AdminReceiptController(AdminReceiptService adminReceiptService) {
        this.adminReceiptService = adminReceiptService;
    }

    @GetMapping("/admin/receipts")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long roomNo,
            @RequestParam(required = false) Long roomMemberId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<ReceiptListItem> receipts = adminReceiptService.getReceipts(
                keyword,
                roomNo,
                roomMemberId,
                fromDate,
                toDate,
                page
        );

        model.addAttribute("pageTitle", "영수증 관리");
        model.addAttribute("pageDescription", "방별 지출 영수증을 검색하고 상세 정보를 확인해.");
        model.addAttribute("activeMenu", "receipts");
        model.addAttribute("receipts", receipts);
        model.addAttribute("keyword", keyword);
        model.addAttribute("roomNo", roomNo);
        model.addAttribute("roomMemberId", roomMemberId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        return "receipts/list";
    }

    @GetMapping("/admin/receipts/{receiptId}")
    public String detail(@PathVariable Long receiptId, Model model) {
        ReceiptDetail receipt = adminReceiptService.getReceiptDetail(receiptId);

        model.addAttribute("pageTitle", "영수증 상세");
        model.addAttribute("pageDescription", "OCR, 착한가격업소 매칭, 지출 금액을 확인해.");
        model.addAttribute("activeMenu", "receipts");
        model.addAttribute("receipt", receipt);
        return "receipts/detail";
    }

    @PostMapping("/admin/receipts/delete")
    public String delete(
            @RequestParam Long receiptId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long roomNo,
            @RequestParam(required = false) Long roomMemberId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            RedirectAttributes redirectAttributes
    ) {
        adminReceiptService.deleteReceipt(receiptId);
        redirectAttributes.addFlashAttribute("message", "영수증을 삭제했어.");
        return "redirect:/admin/receipts?keyword=%s%s%s%s%s".formatted(
                keyword,
                roomNo == null ? "" : "&roomNo=" + roomNo,
                roomMemberId == null ? "" : "&roomMemberId=" + roomMemberId,
                fromDate == null ? "" : "&fromDate=" + fromDate,
                toDate == null ? "" : "&toDate=" + toDate
        );
    }
}
