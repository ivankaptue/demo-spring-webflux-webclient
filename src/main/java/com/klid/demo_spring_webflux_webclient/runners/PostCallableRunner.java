package com.klid.demo_spring_webflux_webclient.runners;

import com.klid.demo_spring_webflux_webclient.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@ConditionalOnProperty(name = "app.performance.service", havingValue = "callable")
@Component
public class PostCallableRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(PostCallableRunner.class);

    @Value("${app.performance.count}")
    private Long performanceCount;
    @Value("${app.performance.postId}")
    private Integer postId;

    private final Integer threadCount;
    private final ExecutorService executorService;

    private final PostService postService;

    public PostCallableRunner(PostService postService, @Value("${app.performance.threads:1}") Integer threadCount) {
        this.postService = postService;
        this.threadCount = threadCount;
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Callable execution");

        var latch = new CountDownLatch(threadCount);
        long countByThread = performanceCount / threadCount;
        List<Future<Boolean>> executions = new ArrayList<>(threadCount);

        logger.info("Count by thread : %s".formatted(countByThread));


        for (int i = 1; i <= threadCount; i++) {
            var startIndex = (countByThread * (i - 1)) + 1;
            var endIndex = startIndex + countByThread - 1;
            var callableJob = new CallableJob(latch, postService, postId, startIndex, endIndex);

            try {
                var future = executorService.submit(callableJob);
                executions.add(future);
            } catch (RejectedExecutionException ex) {
                latch.countDown();
            }
        }

        latch.await();

        checkAllJobStatus(executions);
    }

    private void checkAllJobStatus(List<Future<Boolean>> executions) throws ExecutionException, InterruptedException {
        logger.info("checkAllJobStatus");

        var result = new ArrayList<Boolean>(executions.size());
        for (Future<Boolean> execution : executions) {
            result.add(execution.get());
        }
        if (result.stream().allMatch(item -> item)) {
            logger.info("All jobs terminated without error");
            return;
        }

        logger.info("Jobs terminated with error");
    }

}
