package com.klid.demo_spring_webflux_webclient.runners;

import com.klid.demo_spring_webflux_webclient.service.PostService;
import com.klid.demo_spring_webflux_webclient.service.rest.PostApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.stream.LongStream;

import static com.klid.demo_spring_webflux_webclient.runners.ExecutionContext.getContextId;

public class RunnableJob implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RunnableJob.class);

    private final CountDownLatch latch;
    private final PostService postService;
    private final Integer postId;
    private final long startIndex;
    private final long endIndex;

    public RunnableJob(CountDownLatch latch, PostService postService, Integer postId, long startIndex, long endIndex) {
        this.latch = latch;
        this.postService = postService;
        this.postId = postId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void run() {
        logger.info("Batch start [%s]. start Index : %s, end index : %s".formatted(getContextId(), startIndex, endIndex));

        LongStream.range(startIndex, endIndex + 1)
            .forEach((index) -> {
                logger.info("contextId [%s]. Current index %s".formatted(getContextId(), index + 1));
                try {
                    postService.findOnePost(postId);
                } catch (NoSuchElementException ex) {
                    logger.error("contextId: [%s]. Empty body returned".formatted(getContextId()));
                } catch (PostApiException ex) {
                    if (ex.getStatus().is4xxClientError()) {
                        logger.error("contextId [{}], Client error occurred", getContextId(), ex);
                    } else {
                        logger.error("contextId [{}], Error occurred", getContextId(), ex);
                        throw ex;
                    }
                } finally {
                    latch.countDown();
                    ExecutionContext.clearContext();
                }
            });

        logger.info("Batch ended [%s]. start Index : %s, end index : %s".formatted(getContextId(), startIndex, endIndex));
    }
}
