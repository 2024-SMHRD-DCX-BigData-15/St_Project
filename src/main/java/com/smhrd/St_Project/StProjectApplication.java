package com.smhrd.St_Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(StProjectApplication.class, args);
	}

}    
