package com.github.andmhn.digitalform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

@SpringBootApplication
public class DigitalFormApplication {
	private static String allowedOrigin = "http://localhost:4200";

	public static void main(String[] args) {
		if(args.length > 0 && Objects.equals(args[0], "--all-cors")){
			allowedOrigin = "*";
		}
		SpringApplication.run(DigitalFormApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins(allowedOrigin).allowedMethods("GET", "POST", "PATCH", "DELETE");
			}
		};
	}
}
