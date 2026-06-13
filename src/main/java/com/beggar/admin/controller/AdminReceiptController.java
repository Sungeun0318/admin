package com.beggar.admin.controller;

import com.beggar.admin.service.AdminReceiptService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class AdminReceiptController {

    // 💡 삭제 로직 등을 안전하게 처리하기 위해 로컬 서비스를 유지합니다.
    private final AdminReceiptService adminReceiptService;
    private final WebClient webClient;

    @Value("${api.external-server.url}")
    private String apiServerUrl;

    public AdminReceiptController(AdminReceiptService adminReceiptService, WebClient backendWebClient) {
        this.adminReceiptService = adminReceiptService;
        this.webClient = backendWebClient;
    }

    /**
     * 4-1. 업로드된 전체 영수증 목록 조회 및 OCR 결과 확인
     */
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
        // 외부 API 서버(8080)의 명세에 맞게 동적 URI 생성 및 파라미터 빌드
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/receipts")
                .queryParam("keyword", keyword)
                .queryParam("roomNo", roomNo)
                .queryParam("roomMemberId", roomMemberId)
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .queryParam("page", page)
                .toUriString();

        // 인증 필터 예외 처리 덕분에 토큰 없이 깔끔하게 호출
        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // response -> data -> receipts 계층 구조를 깨서 모델에 주입
        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                model.addAttribute("receipts", data.get("receipts"));
            } else {
                model.addAttribute("receipts", null);
            }
        } else {
            model.addAttribute("receipts", null);
        }

        // 검색 조건 및 타임리프 고정 데이터 바인딩 유지
        model.addAttribute("pageTitle", "영수증 관리");
        model.addAttribute("pageDescription", "방별 지출 영수증을 검색하고 상세 정보를 확인해.");
        model.addAttribute("activeMenu", "receipts");
        model.addAttribute("keyword", keyword);
        model.addAttribute("roomNo", roomNo);
        model.addAttribute("roomMemberId", roomMemberId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "receipts/list";
    }

    /**
     * 4-2. 영수증 이미지 및 상세 매칭 정보
     */
    @GetMapping("/admin/receipts/{receiptId}")
    public String detail(@PathVariable Long receiptId, Model model) {
        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/receipts/" + receiptId)
                .toUriString();

        Map response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                model.addAttribute("receipt", data.get("receipt"));
            } else {
                model.addAttribute("receipt", null);
            }
        } else {
            model.addAttribute("receipt", null);
        }

        model.addAttribute("pageTitle", "영수증 상세");
        model.addAttribute("pageDescription", "OCR, 착한가격업소 매칭, 지출 금액을 확인해.");
        model.addAttribute("activeMenu", "receipts");

        return "receipts/detail";
    }

    /**
     * 4-3. 영수증 삭제 처리 (POST) - 원본 리다이렉트 포맷팅 유지
     */
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
        // 기존 비즈니스 로직(로컬 서비스 호출) 안전하게 유지
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