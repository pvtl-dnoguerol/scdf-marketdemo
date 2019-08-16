package io.pivotal.marketdemo.writerbatch.model;

import java.util.List;

public class CustomerPricing {
    private String name;
    private List<ClosePrice> prices;

    public CustomerPricing(String name, List<ClosePrice> prices) {
        this.name = name;
        this.prices = prices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClosePrice> getPrices() {
        return prices;
    }

    public void setPrices(List<ClosePrice> prices) {
        this.prices = prices;
    }
}
