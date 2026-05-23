package com.corteBrabo.barbershopApi.config;

import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.database.repository.ServiceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceSeeder implements CommandLineRunner {

    private final ServiceRepository serviceRepository;

    public ServiceSeeder(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public void run(String... args) {
        List<Service> defaults = List.of(
                build("Corte com máquina", 25.0, "Corte feito somente com máquina"),
                build("Corte com tesoura", 35.0, "Corte feito somente com tesoura"),
                build("Corte máquina + tesoura", 40.0, "Corte combinando máquina e tesoura"),
                build("Nevou", 80.0, "Descoloração platinada estilo nevado"),
                build("Luzes", 70.0, "Mechas/luzes no cabelo"),
                build("Barba", 25.0, "Barba feita com navalha e toalha quente"),
                build("Sobrancelha", 15.0, "Design e alinhamento de sobrancelha")
        );

        for (Service s : defaults) {
            boolean exists = serviceRepository.findAll().stream()
                    .anyMatch(existing -> existing.getServiceName().equalsIgnoreCase(s.getServiceName()));
            if (!exists) {
                serviceRepository.save(s);
            }
        }
    }

    private Service build(String name, double price, String description) {
        Service s = new Service();
        s.setServiceName(name);
        s.setPrice(price);
        s.setDescription(description);
        return s;
    }
}
