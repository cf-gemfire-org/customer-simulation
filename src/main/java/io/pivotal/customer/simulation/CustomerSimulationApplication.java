package io.pivotal.customer.simulation;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;


@SpringBootApplication
@EnableGemfireRepositories
public class CustomerSimulationApplication {


	public static void main(String[] args) {
		SpringApplication.run(CustomerSimulationApplication.class);
	}

}
