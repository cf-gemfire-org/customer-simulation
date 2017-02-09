package io.pivotal;


import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CustomerSimulationApplication {

	public static void main(String[] args) {
//		SpringApplication.run(CustomerSimulationApplication.class, args);
		SimulationRunner.run();
	}

}
