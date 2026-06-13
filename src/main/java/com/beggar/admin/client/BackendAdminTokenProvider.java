package com.beggar.admin.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class BackendAdminTokenProvider {

    private final WebClient loginWebClient = WebClient.create();
    private final AtomicReference<String> cachedToken = new AtomicReference<>();

    private final String apiServerUrl;
    private final String username;
    private final String password;

    public BackendAdminTokenProvider(
            @Value("${api.external-server.url}") String apiServerUrl,
            @Value("${backend.admin.username}") String username,
            @Value("${backend.admin.password}") String password
    ) {
        this.apiServerUrl = apiServerUrl;
        this.username = username;
        this.password = password;
    }

    public String getToken() {
        String token = cachedToken.get();
        if (StringUtils.hasText(token)) {
            return token;
        }

        synchronized (this) {
            token = cachedToken.get();
            if (StringUtils.hasText(token)) {
                return token;
            }
            return loginAndCache();
        }
    }

    public synchronized String refresh() {
        cachedToken.set(null);
        return loginAndCache();
    }

    private String loginAndCache() {
        validateRequiredProperties();

        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/auth/login")
                .toUriString();

        try {
            Map<String, Object> response = loginWebClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "username", username,
                            "password", password
                    ))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            String accessToken = extractAccessToken(response);
            cachedToken.set(accessToken);
            return accessToken;
        } catch (WebClientResponseException e) {
            throw new IllegalStateException(
                    "백엔드 관리자 로그인에 실패했어. status=%s, body=%s"
                            .formatted(e.getStatusCode(), e.getResponseBodyAsString()),
                    e
            );
        } catch (RuntimeException e) {
            throw new IllegalStateException("백엔드 관리자 로그인 중 오류가 발생했어.", e);
        }
    }

    private String extractAccessToken(Map<String, Object> response) {
        if (response == null) {
            throw new IllegalStateException("백엔드 관리자 로그인 응답이 비어 있어.");
        }

        Object success = response.get("success");
        if (Boolean.FALSE.equals(success)) {
            throw new IllegalStateException("백엔드 관리자 로그인에 실패했어. response=%s".formatted(response));
        }

        Object data = response.get("data");
        if (!(data instanceof Map<?, ?> dataMap)) {
            throw new IllegalStateException("백엔드 관리자 로그인 응답에 data가 없어. response=%s".formatted(response));
        }

        Object accessToken = dataMap.get("accessToken");
        if (!(accessToken instanceof String token) || !StringUtils.hasText(token)) {
            throw new IllegalStateException("백엔드 관리자 로그인 응답에 accessToken이 없어. response=%s".formatted(response));
        }
        return token;
    }

    private void validateRequiredProperties() {
        if (!StringUtils.hasText(apiServerUrl)) {
            throw new IllegalStateException("api.external-server.url 설정이 필요해.");
        }
        if (!StringUtils.hasText(username)) {
            throw new IllegalStateException("backend.admin.username 설정이 필요해.");
        }
        if (!StringUtils.hasText(password)) {
            throw new IllegalStateException("backend.admin.password 설정이 필요해.");
        }
    }
}
