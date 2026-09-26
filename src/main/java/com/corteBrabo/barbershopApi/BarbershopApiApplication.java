package com.corteBrabo.barbershopApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@SpringBootApplication
public class BarbershopApiApplication {
	public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        Locale.setDefault(new Locale("pt", "BR"));

        SpringApplication app = new SpringApplication(BarbershopApiApplication.class);
        app.setDefaultProperties(Map.of(
                "server.forward-headers-strategy", "native",
                "spring.jpa.open-in-view", "true",
                "management.endpoints.web.exposure.include", "health"
        ));
        app.run(args);
    }

}
