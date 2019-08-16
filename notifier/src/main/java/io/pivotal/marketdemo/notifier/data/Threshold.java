package io.pivotal.marketdemo.notifier.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Threshold {
    @Id
    private Integer id;
    private Integer count;

    public Threshold() {
        this.id = 0;
    }

    public Threshold(Integer count) {
        this.id = 0;
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
