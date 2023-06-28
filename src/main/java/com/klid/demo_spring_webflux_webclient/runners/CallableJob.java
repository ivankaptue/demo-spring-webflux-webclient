package com.klid.demo_spring_webflux_webclient.runners;

import com.klid.demo_spring_webflux_webclient.service.PostService;
import com.klid.demo_spring_webflux_webclient.service.rest.PostApiException;
import com.klid.demo_spring_webflux_webclient.service.rest.RequestNotMadeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.klid.demo_spring_webflux_webclient.runners.ExecutionContext.getContextId;

public class CallableJob implements Callable<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(CallableJob.class);

    private final CountDownLatch latch;
    private final PostService postService;
    private final Integer postId;
    private final long startIndex;
    private final long endIndex;

    public CallableJob(CountDownLatch latch, PostService postService, Integer postId, long startIndex, long endIndex) {
        this.latch = latch;
        this.postService = postService;
        this.postId = postId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public Boolean call() {
        var executionResult = new AtomicBoolean(true);
        logger.info("Batch start [%s]. start Index : %s, end index : %s".formatted(getContextId(), startIndex, endIndex));

        long bound = endIndex + 1;
        for (long index = startIndex; index < bound; index++) {
            logger.info("contextId [%s]. Current index %s".formatted(getContextId(), index + 1));
            try {
                postService.findOnePost(postId);
            } catch (NoSuchElementException ex) {
                logger.error("contextId: [%s]. Empty body returned".formatted(getContextId()));
            } catch (RequestNotMadeException ex) {
                logger.error("contextId [{}], Request not made", getContextId(), ex);
            } catch (PostApiException ex) {
                if (ex.getStatus().is4xxClientError()) {
                    logger.error("contextId [{}], Client error occurred", getContextId(), ex);
                } else {
                    logger.error("contextId [{}], Error occurred", getContextId(), ex);
                    executionResult.set(false);
                    return executionResult.get();
                }
            } finally {
                latch.countDown();
                ExecutionContext.clearContext();
            }
        }

        logger.info("Batch ended [%s]. start Index : %s, end index : %s".formatted(getContextId(), startIndex, endIndex));

        return executionResult.get();
    }
}
