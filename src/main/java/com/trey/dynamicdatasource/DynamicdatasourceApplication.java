package com.trey.dynamicdatasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DynamicdatasourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynamicdatasourceApplication.class, args);
	}

}
