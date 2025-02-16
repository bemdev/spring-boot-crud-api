package com.mediaforge.crud;

import com.mediaforge.crud.config.GenericControllerConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "com.mediaforge.crud.*" })
@ComponentScan({
		"com.mediaforge.crud.config",
		"com.mediaforge.crud.controllers",
		"com.mediaforge.crud.services",
		"com.mediaforge.crud.repositories",
		"com.mediaforge.crud.entities",
		"com.mediaforge.crud.dto"
})
@EntityScan(basePackages = "com.mediaforge.crud.entities")
@EnableJpaRepositories(basePackages = "com.mediaforge.crud.repositories")
public class CrudApplication<T> {

	private final GenericControllerConfig<T, ?> registration;

	public CrudApplication(GenericControllerConfig<T, ?> registration) {
		this.registration = registration;
	}

	public static void main(String[] args) {
		SpringApplication.run(CrudApplication.class, args);
	}

	@Bean
	public CommandLineRunner CommandLineRunnerBean() {
		registration.registerController("users", new String[] { "deleteAll", "patch" });
		registration.registerController("posts");
		return (args) -> {
			System.out.println("Controllers generated. App Started.");
		};
	}
}
