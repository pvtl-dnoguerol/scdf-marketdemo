package io.pivotal.marketdemo.notifier;

import io.pivotal.marketdemo.notifier.data.Threshold;
import io.pivotal.marketdemo.notifier.data.ThresholdRepository;
import net.pushover.client.PushoverClient;
import net.pushover.client.PushoverMessage;
import net.pushover.client.PushoverRestClient;
import net.pushover.client.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@EnableTask
@Configuration
@EnableConfigurationProperties({NotifierProperties.class})
public class NotifierConfiguration {
    @Bean
    public NotifierTask notifierTask() {
        return new NotifierTask();
    }

    public static class NotifierTask implements CommandLineRunner {
        private final Log logger = LogFactory.getLog(NotifierTask.class);

        @Autowired
        NotifierProperties config;

        @Autowired
        ThresholdRepository repository;

        @Override
        public void run(String... args) throws Exception {
            logger.info("Notifier task starting with respository: " + repository);

            // check current failure count
            Optional<Threshold> t = repository.findById(0);

            if (t.isPresent()) {
                int currentCount = t.get().getCount();
                logger.info("Current count is: " + currentCount);
                currentCount++;
                // if threshold is exceeded, send notification and reset count
                if (currentCount >= config.getThreshold()) {
                    logger.info("Threshold exceeded; sending push message with user ID " + config.getUserKey());
                    // send push message
                    PushoverClient client = new PushoverRestClient();
                    Status status = client.pushMessage(PushoverMessage.builderWithApiToken(config.getApiKey()).setUserId(config.getUserKey()).setMessage(config.getMessage()).build());
                    logger.info("Push request returned with status: " + status.getStatus());
                    repository.save(new Threshold(0));
                } else {
                    repository.save(new Threshold(currentCount));
                }
            } else {
                logger.info("No current count; setting to 1");
                repository.save(new Threshold(1));
            }

            logger.info("Notifier task finished");
        }
    }
}
