package io.pivotal.marketdemo.pushover;

import net.pushover.client.PushoverClient;
import net.pushover.client.PushoverMessage;
import net.pushover.client.PushoverRestClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableTask
@Configuration
@EnableConfigurationProperties({PushoverProperties.class})
public class PushoverConfiguration {
    @Bean
    public PushoverTask pushoverTask() {
        return new PushoverTask();
    }

    public static class PushoverTask implements CommandLineRunner {
        private final Log logger = LogFactory.getLog(PushoverTask.class);

        @Autowired
        PushoverProperties config;

        @Override
        public void run(String... args) throws Exception {
            logger.info("Pushover task starting");

            // send push message
            PushoverClient client = new PushoverRestClient();
            client.pushMessage(PushoverMessage.builderWithApiToken(config.getApiKey()).setUserId(config.getUserKey()).setMessage(config.getMessage()).build());

            logger.info("Pushover task finished");
        }
    }
}
