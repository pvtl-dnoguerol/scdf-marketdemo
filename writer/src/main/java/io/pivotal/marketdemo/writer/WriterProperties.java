package io.pivotal.marketdemo.writer;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("writer")
public class WriterProperties {
    private int maxFailureCount = 1;
    private String awsAccessKey = "";
    private String awsSecretKey = "";
    private String s3InputBucket = "mdc-in";
    private String s3OutputBucket = "mdc-out";
    private String customerId = "unknown";
    private String tickers = "";
    private String dateString = null;

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

    public int getMaxFailureCount() {
        return maxFailureCount;
    }

    public void setMaxFailureCount(int maxFailureCount) {
        this.maxFailureCount = maxFailureCount;
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTickers() {
        return tickers;
    }

    public void setTickers(String tickers) {
        this.tickers = tickers;
    }

    public String getDateString() {
        return dateString != null ? dateString : "2019-08-14";
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }
}
