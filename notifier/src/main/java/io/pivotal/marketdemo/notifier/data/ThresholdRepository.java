package io.pivotal.marketdemo.notifier.data;

import org.springframework.data.repository.CrudRepository;

public interface ThresholdRepository extends CrudRepository<Threshold,Integer> {
}
