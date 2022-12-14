package com.klid.demo_spring_webflux_webclient.rest;

import com.klid.demo_spring_webflux_webclient.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.List;

/**
 * @author Ivan Kaptue
 */
@Component
public class PostRestClient {

    private static final Logger logger = LoggerFactory.getLogger(PostRestClient.class);

    private final WebClient webClient;
    private final String postEndpoint;

    public PostRestClient(
        @Qualifier("postWebClient") WebClient webClient,
        @Value("${app.rest.posts.endpoint}") String postEndpoint
    ) {
        this.webClient = webClient;
        this.postEndpoint = postEndpoint;
    }

    public List<Post> getAllPosts() {
        return this.webClient.get()
            .uri(uriBuilder -> uriBuilder.path(postEndpoint).build())
            .exchangeToMono(this::getAllPostHandler)
            .retryWhen(retryWhen())
            .doOnError(PostApiException.class, this::logError)
            .doOnSuccess(this::logFindAllPostEnd)
            .block();
    }

    private Retry retryWhen() {
        var maxAttempts = 3;
        return Retry
            .max(maxAttempts)
            .doBeforeRetry(retrySignal -> logger.info("Before retry : " + retrySignal))
            .doAfterRetry(retrySignal -> logger.info("After retry : " + retrySignal))
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                new PostApiException("Retry exhausted", retrySignal.failure()));
    }

    private void logFindAllPostEnd(List<Post> posts) {
        logger.info(String.format("Posts found : %s", posts.size()));
    }

    private void logError(PostApiException ex) {
        logger.error("Error logger", ex);
    }

    private Mono<List<Post>> getAllPostHandler(ClientResponse response) {
        if (HttpStatus.OK.equals(response.statusCode())) {
            return response.bodyToMono(Post[].class).map(List::of);
        }

        return buildError(response);
    }

    private <T> Mono<T> buildError(ClientResponse response) {
        return response
            .bodyToMono(String.class)
            .flatMap(body -> Mono.error(new PostApiException("Error when calling posts api", response.statusCode(), body)));
    }
}
