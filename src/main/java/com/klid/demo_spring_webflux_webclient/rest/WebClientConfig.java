package com.klid.demo_spring_webflux_webclient.rest;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Ivan Kaptue
 */
@Configuration
public class WebClientConfig {

    private final long timeoutMillis = 500;

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
            .responseTimeout(Duration.ofMillis(timeoutMillis))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeoutMillis)
            .doOnConnected(connection -> connection
                .addHandlerLast(new ReadTimeoutHandler(timeoutMillis, TimeUnit.MILLISECONDS))
                .addHandlerLast(new WriteTimeoutHandler(timeoutMillis, TimeUnit.MILLISECONDS)));
    }
}
