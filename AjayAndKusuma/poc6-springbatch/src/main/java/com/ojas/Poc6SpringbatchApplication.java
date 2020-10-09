package com.ojas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
//(exclude = DataSourceAutoConfiguration.class)
public class Poc6SpringbatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(Poc6SpringbatchApplication.class, args);
	}

}
