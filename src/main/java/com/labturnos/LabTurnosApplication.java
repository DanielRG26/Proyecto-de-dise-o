package com.labturnos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LabTurnosApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabTurnosApplication.class, args);
	}

}
