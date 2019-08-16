package io.pivotal.marketdemo.notifier;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("notifier")
public class NotifierProperties {
    private String userKey = "";
    private String apiKey = "";
    private String message = "There was a problem!";
    private int threshold = 2;

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
