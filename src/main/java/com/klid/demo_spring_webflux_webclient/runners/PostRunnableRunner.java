package com.klid.demo_spring_webflux_webclient.runners;

import com.klid.demo_spring_webflux_webclient.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

@ConditionalOnProperty(name = "app.performance.service", havingValue = "runnable", matchIfMissing = true)
@Component
public class PostRunnableRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(PostRunnableRunner.class);

    @Value("${app.performance.count}")
    private Long performanceCount;
    @Value("${app.performance.postId}")
    private Integer postId;

    private final Integer threadCount;
    private final ExecutorService executorService;

    private final PostService postService;

    public PostRunnableRunner(PostService postService, @Value("${app.performance.threads:1}") Integer threadCount) {
        this.postService = postService;
        this.threadCount = threadCount;
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Runnable execution");

        var latch = new CountDownLatch(threadCount);
        long countByThread = performanceCount / threadCount;

        logger.info("Count by thread : %s".formatted(countByThread));

        for (int i = 1; i <= threadCount; i++) {
            var startIndex = (countByThread * (i - 1)) + 1;
            var endIndex = startIndex + countByThread - 1;
            var runnable = new RunnableJob(latch, postService, postId, startIndex, endIndex);

            try {
                executorService.submit(runnable);
            } catch (RejectedExecutionException ex) {
                latch.countDown();
            }
        }

        latch.await();
    }
}
