package ru.sushchenko.trelloclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrellocloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrellocloneApplication.class, args);
	}

}
