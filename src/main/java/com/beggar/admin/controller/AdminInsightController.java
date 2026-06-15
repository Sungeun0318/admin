package com.beggar.admin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
public class AdminInsightController {

    private final WebClient webClient;

    @Value("${api.external-server.url}")
    private String apiServerUrl;

    public AdminInsightController(WebClient backendWebClient) {
        this.webClient = backendWebClient;
    }

    @GetMapping("/admin/insights")
    public String spendingInsights(Model model) {
        String spendingUri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/ai/insights/spending-summary")
                .toUriString();

        Map spendingResponse = webClient.get()
                .uri(spendingUri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        Map<String, Object> data = spendingResponse == null ? null : (Map<String, Object>) spendingResponse.get("data");

        model.addAttribute("summary", data == null ? null : data.get("summary"));
        model.addAttribute("topRegions", data == null ? null : data.get("topRegions"));
        model.addAttribute("tagClicks", data == null ? null : data.get("tagClicks"));
        model.addAttribute("highBudgetUsageRooms", data == null ? null : data.get("highBudgetUsageRooms"));

        model.addAttribute("pageTitle", "소비 인사이트");
        model.addAttribute("pageDescription", "서비스 전체 소비 흐름과 추천 반응을 확인해.");
        model.addAttribute("activeMenu", "insights");

        return "insights/summary";
    }

    @GetMapping("/admin/budget-risk")
    public String budgetRisk(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 50));
        String budgetRiskUri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/ai/predictions/budget-risk")
                .queryParam("page", safePage)
                .queryParam("size", safeSize)
                .toUriString();

        Map budgetRiskResponse = webClient.get()
                .uri(budgetRiskUri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        Map<String, Object> data =
                budgetRiskResponse == null ? null : (Map<String, Object>) budgetRiskResponse.get("data");
        int currentPage = toInt(data == null ? null : data.get("page"), safePage);
        int currentSize = toInt(data == null ? null : data.get("size"), safeSize);
        int totalItems = toInt(data == null ? null : data.get("totalItems"), 0);
        int totalPages = toInt(data == null ? null : data.get("totalPages"), 0);
        List<Integer> pageNumbers = pageNumbers(currentPage, totalPages);

        model.addAttribute("budgetRiskModelVersion", data == null ? null : data.get("modelVersion"));
        model.addAttribute("budgetRiskSummary", data == null ? null : data.get("summary"));
        model.addAttribute("budgetRiskItems", data == null ? null : data.get("items"));
        model.addAttribute("riskPage", currentPage);
        model.addAttribute("riskSize", currentSize);
        model.addAttribute("riskTotalItems", totalItems);
        model.addAttribute("riskTotalPages", totalPages);
        model.addAttribute("riskPageNumbers", pageNumbers);
        model.addAttribute("riskHasPrevious", currentPage > 0);
        model.addAttribute("riskHasNext", totalPages > 0 && currentPage < totalPages - 1);
        model.addAttribute("riskHasJumpPrevious", currentPage >= 5);
        model.addAttribute("riskHasJumpNext", totalPages > 0 && currentPage + 5 < totalPages);
        model.addAttribute("riskJumpPreviousPage", Math.max(currentPage - 5, 0));
        model.addAttribute("riskJumpNextPage", Math.min(currentPage + 5, Math.max(totalPages - 1, 0)));
        model.addAttribute("pageTitle", "예산 위험도");
        model.addAttribute("pageDescription", "AI가 예측한 예산 초과 위험 분포와 고위험 방을 확인해.");
        model.addAttribute("activeMenu", "budget-risk");

        return "insights/budget-risk";
    }

    private int toInt(Object value, int defaultValue) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return defaultValue;
    }

    private List<Integer> pageNumbers(int currentPage, int totalPages) {
        if (totalPages <= 0) {
            return List.of();
        }
        int start = Math.max(currentPage - 2, 0);
        int end = Math.min(start + 5, totalPages);
        start = Math.max(end - 5, 0);
        return IntStream.range(start, end)
                .boxed()
                .toList();
    }
}
