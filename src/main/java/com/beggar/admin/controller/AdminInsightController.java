package com.beggar.admin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

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
    public String budgetRisk(Model model) {
        String budgetRiskUri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/ai/predictions/budget-risk")
                .toUriString();

        Map budgetRiskResponse = webClient.get()
                .uri(budgetRiskUri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        Map<String, Object> data =
                budgetRiskResponse == null ? null : (Map<String, Object>) budgetRiskResponse.get("data");

        model.addAttribute("budgetRiskModelVersion", data == null ? null : data.get("modelVersion"));
        model.addAttribute("budgetRiskSummary", data == null ? null : data.get("summary"));
        model.addAttribute("budgetRiskItems", data == null ? null : data.get("items"));
        model.addAttribute("pageTitle", "예산 위험도");
        model.addAttribute("pageDescription", "AI가 예측한 예산 초과 위험 분포와 고위험 방을 확인해.");
        model.addAttribute("activeMenu", "budget-risk");

        return "insights/budget-risk";
    }
}
