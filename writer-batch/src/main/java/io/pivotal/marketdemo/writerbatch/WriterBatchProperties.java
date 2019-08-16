package io.pivotal.marketdemo.writerbatch;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("writer-batch")
public class WriterBatchProperties {
    private String awsAccessKey = "";
    private String awsSecretKey = "";
    private String s3InputBucket = "mdc-in";
    private String s3OutputBucket = "mdc-out";
    private String date = null;

    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    public void setAwsAccessKey(String awsAccessKey) {
        this.awsAccessKey = awsAccessKey;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public String getS3InputBucket() {
        return s3InputBucket;
    }

    public void setS3InputBucket(String s3InputBucket) {
        this.s3InputBucket = s3InputBucket;
    }

    public String getS3OutputBucket() {
        return s3OutputBucket;
    }

    public void setS3OutputBucket(String s3OutputBucket) {
        this.s3OutputBucket = s3OutputBucket;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
