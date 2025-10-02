package com.store.management.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StoreManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreManagementApplication.class, args);
	}

}
