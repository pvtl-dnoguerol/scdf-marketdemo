package io.pivotal.marketdemo.writerbatch;

import com.amazonaws.services.s3.AmazonS3;
import io.pivotal.marketdemo.writerbatch.model.ClosePrice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadTasklet implements Tasklet {
    private final Log logger = LogFactory.getLog(DownloadTasklet.class);

    private WriterBatchProperties config;
    private AmazonS3 s3;
    private List<ClosePrice> pricings;

    DownloadTasklet(WriterBatchProperties config, AmazonS3 s3, List<ClosePrice> pricings) {
        this.config = config;
        this.s3 = s3;
        this.pricings = pricings;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        Object o = chunkContext.getStepContext().getJobParameters().get("date");
        if (o == null) {
            o = config.getDate();
        }
        if (o != null) {
            String inFilename = o.toString() + ".csv";
            logger.info("Downloading end-of-day pricing data from: " + inFilename);
            InputStream inputStream = s3.getObject(config.getS3InputBucket(), inFilename).getObjectContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            try {
                pricings.addAll(br.lines().map((line) -> {
                    String[] p = line.split(",");
                    return new ClosePrice(p[0], Double.parseDouble(p[1]));
                }).collect(Collectors.toList()));
            } finally {
                inputStream.close();
            }

            logger.info("Parsed " + pricings.size() + " prices");

            return RepeatStatus.FINISHED;
        } else {
            throw new IllegalArgumentException("No date parameter specified!");
        }
    }

}
