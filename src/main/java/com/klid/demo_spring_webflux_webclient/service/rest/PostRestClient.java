package com.klid.demo_spring_webflux_webclient.service.rest;

import com.klid.demo_spring_webflux_webclient.model.Post;
import io.netty.handler.timeout.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
        try {
            return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path(postEndpoint).build())
                .exchangeToFlux(this::getAllPostHandler)
                .retryWhen(retryWhen())
                .collectList()
                .doOnError(RuntimeException.class, this::logError)
                .doOnSuccess(this::logFindAllPostEnd)
                .block();
        } catch (WebClientRequestException ex) {
            var message = "Cannot contact server";
//            logger.error(message, ex);
            throw new RequestNotMadeException(message, ex.getLocalizedMessage(), ex);
        }
    }

    public Optional<Post> getOnePost(int id) {
        try {
            return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path(postEndpoint).path("/{id}").build(id))
                .exchangeToMono(this::getOnePostHandler)
                .retryWhen(retryWhen())
                .doOnError(RuntimeException.class, this::logError)
                .doOnSuccess(post -> logger.info(String.format("Post found : %s", post)))
                .blockOptional();
        } catch (WebClientRequestException ex) {
            var message = "Cannot contact server";
//            logger.error(message, ex);
            throw new RequestNotMadeException(message, ex.getLocalizedMessage(), ex);
        }
    }

    private Retry retryWhen() {
        return Retry
            .max(maxAttempts)
            .filter(retryPredicate())
            .doBeforeRetry(retrySignal -> logger.info("Before retry : " + retrySignal))
            .doAfterRetry(retrySignal -> logger.info("After retry : " + retrySignal))
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new PostApiException("Retry exhausted", retrySignal.failure()));
    }

    private static Predicate<Throwable> retryPredicate() {
        return ex ->
            ex instanceof TimeoutException
                || (ex instanceof PostApiException && ((PostApiException) ex).getStatus() == HttpStatus.SERVICE_UNAVAILABLE);
    }

    private void logFindAllPostEnd(List<Post> posts) {
        logger.info(String.format("Posts found : %s", posts.size()));
    }

    private void logError(RuntimeException ex) {
//        logger.error("Calling posts API terminated with error", ex);
    }

    private Mono<Post> getOnePostHandler(ClientResponse response) {
        var statusCode = (HttpStatus) response.statusCode();
        return switch (statusCode) {
            case OK, CREATED -> response.bodyToMono(Post.class);
            default -> buildMonoError(response);
        };
    }

    private Flux<Post> getAllPostHandler(ClientResponse response) {
        var statusCode = (HttpStatus) response.statusCode();
        return switch (statusCode) {
            case OK, CREATED -> response.bodyToFlux(Post.class);
            default -> buildFluxError(response);
        };
    }

    private <T> Flux<T> buildFluxError(ClientResponse response) {
        return response
            .bodyToFlux(String.class)
            .defaultIfEmpty("empty")
            .flatMap(body ->
                response.createException().flatMap(ex ->
                    Mono.error(new PostApiException("Error when calling posts api", response.statusCode(), body, ex))));
    }

    private <T> Mono<T> buildMonoError(ClientResponse response) {
        return response
            .bodyToMono(String.class)
            .defaultIfEmpty("empty")
            .flatMap(body ->
                response.createException().flatMap(ex ->
                    Mono.error(new PostApiException("Error when calling posts api", response.statusCode(), body, ex))));
    }
}
