package com.beggar.admin.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class BackendAdminTokenProvider {

    private final WebClient loginWebClient = WebClient.create();
    private final AtomicReference<String> cachedToken = new AtomicReference<>();
    private final AtomicReference<Mono<String>> inFlightLogin = new AtomicReference<>();

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

    // 서블릿 컨트롤러 같은 블로킹 호출부 전용 호환 메서드다.
    // WebClient 필터의 리액터 이벤트 루프에서는 getTokenMono()를 사용해야 한다.
    public String getToken() {
        return getTokenMono().block();
    }

    // 서블릿 컨트롤러 같은 블로킹 호출부 전용 호환 메서드다.
    // WebClient 필터의 리액터 이벤트 루프에서는 refreshMono()를 사용해야 한다.
    public String refresh() {
        return refreshMono().block();
    }

    public Mono<String> getTokenMono() {
        String token = cachedToken.get();
        if (StringUtils.hasText(token)) {
            return Mono.just(token);
        }
        return loginAndCacheMono();
    }

    public Mono<String> refreshMono() {
        cachedToken.set(null);
        return loginAndCacheMono();
    }

    private Mono<String> loginAndCacheMono() {
        validateRequiredProperties();

        String uri = UriComponentsBuilder.fromHttpUrl(apiServerUrl)
                .path("/admin/auth/login")
                .toUriString();

        Mono<String> existingLogin = inFlightLogin.get();
        if (existingLogin != null) {
            return existingLogin;
        }

        AtomicReference<Mono<String>> newLoginRef = new AtomicReference<>();
        Mono<String> newLogin = loginWebClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "username", username,
                        "password", password
                ))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(this::extractAccessToken)
                .doOnNext(cachedToken::set)
                .onErrorMap(WebClientResponseException.class, e -> new IllegalStateException(
                        "백엔드 관리자 로그인에 실패했어. status=%s, body=%s"
                                .formatted(e.getStatusCode(), e.getResponseBodyAsString()),
                        e
                ))
                .onErrorMap(e -> !(e instanceof IllegalStateException),
                        e -> new IllegalStateException("백엔드 관리자 로그인 중 오류가 발생했어.", e))
                .doFinally(signalType -> inFlightLogin.compareAndSet(newLoginRef.get(), null))
                .cache();
        newLoginRef.set(newLogin);

        if (inFlightLogin.compareAndSet(null, newLogin)) {
            return newLogin;
        }

        Mono<String> currentLogin = inFlightLogin.get();
        if (currentLogin != null) {
            return currentLogin;
        }
        return loginAndCacheMono();
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
