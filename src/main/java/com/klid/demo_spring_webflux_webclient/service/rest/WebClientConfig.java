package com.klid.demo_spring_webflux_webclient.service.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * @author Ivan Kaptue
 */
@Configuration
public class WebClientConfig {

    private static final long TIMEOUT_MILLIS = 2000;

    @Bean("postWebClient")
    public WebClient webClient(
        @Value("${app.rest.posts.base-url}") String postBaseUrl
    ) {
        var httpClient = createHttpClient();

        return WebClient.builder()
            .baseUrl(postBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

    private HttpClient createHttpClient() {
        return HttpClient.create()
            .responseTimeout(Duration.ofMillis(TIMEOUT_MILLIS));
//            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) TIMEOUT_MILLIS)
//            .doOnConnected(connection -> connection
//                .addHandlerLast(new ReadTimeoutHandler(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS))
//                .addHandlerLast(new WriteTimeoutHandler(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)));
    }
}
