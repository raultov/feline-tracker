package com.ayoza.feline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class FelineTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FelineTrackerApplication.class, args);
	}
}
