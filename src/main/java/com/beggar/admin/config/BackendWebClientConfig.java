package com.beggar.admin.config;

import com.beggar.admin.client.BackendAdminTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class BackendWebClientConfig {

    @Bean
    public WebClient backendWebClient(BackendAdminTokenProvider tokenProvider) {
        return WebClient.builder()
                .filter(adminAuthorizationFilter(tokenProvider))
                .build();
    }

    private ExchangeFilterFunction adminAuthorizationFilter(BackendAdminTokenProvider tokenProvider) {
        return (request, next) -> tokenProvider.getTokenMono()
                .flatMap(token -> next.exchange(withBearerToken(request, token))
                    .flatMap(response -> {
                        if (response.statusCode().value() != 401) {
                            return Mono.just(response);
                        }

                        return response.releaseBody()
                                .then(tokenProvider.refreshMono())
                                .flatMap(newToken -> next.exchange(withBearerToken(request, newToken)));
                    }));
    }

    private ClientRequest withBearerToken(ClientRequest request, String token) {
        return ClientRequest.from(request)
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .build();
    }
}
