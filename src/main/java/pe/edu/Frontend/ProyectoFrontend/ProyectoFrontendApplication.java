package pe.edu.Frontend.ProyectoFrontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ProyectoFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoFrontendApplication.class, args);
	}

}
