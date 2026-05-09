package com.MusicPlatForm.user_library_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserLibraryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserLibraryServiceApplication.class, args);
	}

}
