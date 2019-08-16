package io.pivotal.marketdemo.writerbatch;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.marketdemo.writerbatch.model.ClosePrice;
import io.pivotal.marketdemo.writerbatch.model.Customer;
import io.pivotal.marketdemo.writerbatch.model.CustomerPricing;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@EnableTask
@EnableBatchProcessing
@Configuration
@EnableConfigurationProperties({WriterBatchProperties.class})
public class WriterBatchConfiguration {
    private final Log logger = LogFactory.getLog(WriterBatchConfiguration.class);

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    WriterBatchProperties config;

    @Autowired
    private AmazonS3 s3;

    @Bean
    @JobScope
    List<ClosePrice> closePrices() {
        return new ArrayList<>();
    }

    @Bean
    public Job jobPricingProcessing(ItemReader<Customer> reader, ItemProcessor<Customer,CustomerPricing> processor, ItemWriter<CustomerPricing> writer, List<ClosePrice> closePrices) {
        Step downloadStep = stepBuilderFactory.get("PricingDownloadStep")
                .tasklet(new DownloadTasklet(config, s3, closePrices))
                .build();

        Step processingStep = stepBuilderFactory.get("PricingProcessingStep")
                .<Customer,CustomerPricing>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();

        return jobBuilderFactory.get("EndOfDayPricingJob")
                .incrementer(new RunIdIncrementer())
                .start(downloadStep)
                .next(processingStep)
                .build();
    }

    @Bean
    public JsonItemReader<Customer> jsonItemReader(Resource customersResource) {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonJsonObjectReader<Customer> jsonObjectReader = new JacksonJsonObjectReader<>(Customer.class);
        jsonObjectReader.setMapper(objectMapper);
        return new JsonItemReaderBuilder<Customer>()
                .jsonObjectReader(jsonObjectReader)
                .resource(customersResource)
                .name("CustomerJsonItemReader")
                .build();
    }

    @Bean
    ItemProcessor<Customer, CustomerPricing> customerProcessor(List<ClosePrice> closePrices) {
        return new CustomerPricingProcessor(closePrices);
    }

    @Bean
    @StepScope
    public ItemWriter<CustomerPricing> customerPricingWriter(@Value("#{jobParameters[date]}") String date) {
        return pricings -> {
            for (CustomerPricing pricing : pricings) {
                writeCustomerPricing(pricing, date);
            }
        };
    }

    @Bean
    public AmazonS3 s3() {
        return AmazonS3ClientBuilder.standard().withCredentials(
            new AWSStaticCredentialsProvider(
                    new BasicAWSCredentials(config.getAwsAccessKey(), config.getAwsSecretKey())
            )
        ).withRegion(Regions.EU_CENTRAL_1).build();
    }

    @Bean
    public Resource customersResource(AmazonS3 s3) {
        return new InputStreamResource(s3.getObject(config.getS3InputBucket(), "customers.json").getObjectContent());
    }

    private void writeCustomerPricing(CustomerPricing pricing, String date) {
        String key = pricing.getName() + "/" + date + ".json";
        logger.info("Writing output for " + pricing.getName() + " to file: " + key);
        ObjectMapper mapper = new ObjectMapper();
        JacksonJsonObjectMarshaller<List<ClosePrice>> marshaller = new JacksonJsonObjectMarshaller<>();
        marshaller.setObjectMapper(mapper);
        s3.putObject(config.getS3OutputBucket(), key, marshaller.marshal(pricing.getPrices()));
    }
}
