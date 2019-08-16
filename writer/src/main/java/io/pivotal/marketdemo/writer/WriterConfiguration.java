package io.pivotal.marketdemo.writer;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@EnableTask
@Configuration
@EnableConfigurationProperties({WriterProperties.class})
public class WriterConfiguration {
    @Bean
    public WriterTask writerTask() {
        return new WriterTask();
    }

    public static class WriterTask implements CommandLineRunner {
        private final Log logger = LogFactory.getLog(WriterTask.class);

        @Autowired
        WriterProperties config;

        @Override
        public void run(String... args) throws Exception {
            String inFilename = config.getDateString() + ".csv";

            logger.info("Executing for customer: " + config.getCustomerId());
            logger.info("Using AWS key: " + config.getAwsAccessKey());
            logger.info("Looking for " + inFilename + " in bucket " + config.getS3InputBucket());

            try {
                AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(config.getAwsAccessKey(), config.getAwsSecretKey())
                        )
                ).withRegion(Regions.EU_CENTRAL_1).build();
                writeOutput(
                    s3,
                    config.getDateString(),
                    config.getCustomerId(),
                    config.getS3OutputBucket(),
                    createJSON(
                        parseInput(
                            s3.getObject(config.getS3InputBucket(), inFilename).getObjectContent(),
                            config.getTickers()
                        )
                    )
                );
            } catch (Exception e) {
                incrementFailureCount();
                throw e;
            }

            logger.info("Writer task complete");
        }

        List<ClosePrice> parseInput(InputStream inputStream, String tickers) throws Exception {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            try {
                return br.lines().map((line) -> {
                    String[] p = line.split(",");
                    return new ClosePrice(p[0], Double.parseDouble(p[1]));
                }).filter(closePrice -> tickers != null && tickers.contains(closePrice.getTicker())
                ).collect(Collectors.toList());
            } finally {
                inputStream.close();
            }
        }

        String createJSON(List<ClosePrice> prices) throws Exception {
            logger.info("Generating JSON for " + prices.size() + " ticker(s)");
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(prices);
        }

        void writeOutput(AmazonS3 s3, String date, String customerId, String bucket, String json) throws Exception {
            String key = customerId + "/" + date + ".json";
            logger.info("Writing output for " + customerId + " to file: " + key);
            s3.putObject(bucket, key, json);
        }

        private void incrementFailureCount() {
            logger.info("Failure detected; incrementing failure count");
            // TODO
        }
    }
}
