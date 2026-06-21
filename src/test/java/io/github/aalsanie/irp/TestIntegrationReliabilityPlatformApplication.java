package io.github.aalsanie.irp;

import org.springframework.boot.SpringApplication;

public class TestIntegrationReliabilityPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.from(IntegrationReliabilityPlatformApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
