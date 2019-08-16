package io.pivotal.marketdemo.writerbatch;

import io.pivotal.marketdemo.writerbatch.model.ClosePrice;
import io.pivotal.marketdemo.writerbatch.model.Customer;
import io.pivotal.marketdemo.writerbatch.model.CustomerPricing;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerPricingProcessor implements ItemProcessor<Customer, CustomerPricing> {
    private List<ClosePrice> closePrices;

    CustomerPricingProcessor(List<ClosePrice> closePrices) {
        this.closePrices = closePrices;
    }

    @Override
    public CustomerPricing process(Customer customer) throws Exception {
        return new CustomerPricing(customer.getName(), closePrices.stream().filter(closePrice -> customer.getTickers() != null && customer.getTickers().contains(closePrice.getTicker())).collect(Collectors.toList()));
    }
}
