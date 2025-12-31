package com.nestigo.systemdesign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NestigoApplication {

	public static void main(String[] args) {
		SpringApplication.run(NestigoApplication.class, args);
	}

}
