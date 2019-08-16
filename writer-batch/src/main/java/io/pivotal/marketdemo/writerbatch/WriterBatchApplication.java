package io.pivotal.marketdemo.writerbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;

@SpringBootApplication
public class WriterBatchApplication {
	public static void main(String[] args) {
		SpringApplication.run(WriterBatchApplication.class, args);
	}
}
