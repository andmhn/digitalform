package com.github.andmhn.digitalform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DigitalFormApplication {
	public static void main(String[] args) {
		SpringApplication.run(DigitalFormApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				String allowedOrigins = System.getenv("ALLOWED_ORIGINS") +  ",http://localhost:4200,";
				registry.addMapping("/**")
						.allowedOrigins(allowedOrigins.split(","))
						.allowedMethods("GET", "POST", "PATCH", "DELETE");
			}
		};
	}
}
