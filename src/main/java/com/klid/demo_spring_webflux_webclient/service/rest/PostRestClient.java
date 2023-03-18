package com.klid.demo_spring_webflux_webclient.service.rest;

import com.klid.demo_spring_webflux_webclient.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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

    @Value("${app.rest.posts.endpoint}")
    private String postEndpoint;
    @Value("${app.rest.posts.retry.max-attempts}")
    private long maxAttempts;

    public PostRestClient(@Qualifier("postWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Post> getAllPosts() {
        return this.webClient.get()
            .uri(uriBuilder -> uriBuilder.path(postEndpoint).build())
            .exchangeToFlux(this::getAllPostHandler)
            .retryWhen(retryWhen())
            .collectList()
            .doOnError(PostApiException.class, this::logError)
            .doOnSuccess(this::logFindAllPostEnd)
            .block();
    }

    private Retry retryWhen() {
        return Retry
            .max(maxAttempts)
            .filter(ex -> ex instanceof PostApiException && ((PostApiException) ex).getStatus() == HttpStatus.SERVICE_UNAVAILABLE)
            .doBeforeRetry(retrySignal -> logger.info("Before retry : " + retrySignal))
            .doAfterRetry(retrySignal -> logger.info("After retry : " + retrySignal))
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                new PostApiException("Retry exhausted", retrySignal.failure()));
    }

    private void logFindAllPostEnd(List<Post> posts) {
        logger.info(String.format("Posts found : %s", posts.size()));
    }

    private void logError(PostApiException ex) {
        logger.error("Calling posts API terminated with error", ex);
    }

    private Flux<Post> getAllPostHandler(ClientResponse response) {
        if (HttpStatus.OK.equals(response.statusCode())) {
//            return response.bodyToMono(Post[].class).map(List::of);
            return response.bodyToFlux(Post.class);
        }

        return buildError(response);
    }

    private <T> Flux<T> buildError(ClientResponse response) {
        return response
            .bodyToFlux(String.class)
            .flatMap(body ->
                response.createException().flatMap(ex ->
                    Mono.error(new PostApiException("Error when calling posts api", response.statusCode(), body, ex))));
    }
}
