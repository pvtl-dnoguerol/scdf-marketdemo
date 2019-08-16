package io.pivotal.marketdemo.writer;

public class ClosePrice {
    private String ticker;
    private double close;

    public ClosePrice(String ticker, Double close) {
        this.ticker = ticker;
        this.close = close;
    }

    public String getTicker() {
        return ticker;
    }

    public double getClose() {
        return close;
    }
}
