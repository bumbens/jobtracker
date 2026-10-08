package com.jobtracker.jobtracker_backend;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

/**
 * Entry point. Excludes the default {@code UserDetailsService} auto-config since
 * auth is fully custom (JWT + {@code AuthService}), and pins the JVM default
 * locale to English so Bean Validation messages don't follow the OS locale.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class JobtrackerBackendApplication {

	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		SpringApplication.run(JobtrackerBackendApplication.class, args);
	}

}
