package hr.algebra.semregprojectbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // Omogućava asinkrone metode u servisima
public class SemRegProjectBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SemRegProjectBackendApplication.class, args);
	}

}

