package io.pivotal.marketdemo.pushover;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pushover")
public class PushoverProperties {
    private String userKey = "";
    private String apiKey = "";
    private String message = "There was a problem!";

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
}
