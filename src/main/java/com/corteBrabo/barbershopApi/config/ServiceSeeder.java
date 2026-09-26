package com.corteBrabo.barbershopApi.config;

import com.corteBrabo.barbershopApi.database.model.Business;
import com.corteBrabo.barbershopApi.database.model.BusinessHours;
import com.corteBrabo.barbershopApi.database.model.BusinessSegment;
import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.database.repository.BusinessHoursRepository;
import com.corteBrabo.barbershopApi.database.repository.ServiceRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Component
public class ServiceSeeder {

    private record Template(String name, String price, int minutes, String description) {
    }

    private static final Map<BusinessSegment, List<Template>> TEMPLATES = Map.of(
            BusinessSegment.BARBEARIA, List.of(
                    new Template("Corte", "40.00", 30, "Corte masculino com acabamento"),
                    new Template("Barba", "30.00", 30, "Barba com toalha quente"),
                    new Template("Corte + Barba", "65.00", 60, "Combo completo"),
                    new Template("Sobrancelha", "15.00", 15, "Alinhamento de sobrancelha")),
            BusinessSegment.SALAO, List.of(
                    new Template("Corte feminino", "80.00", 60, "Corte com lavagem e finalização"),
                    new Template("Escova", "60.00", 45, "Escova modelada"),
                    new Template("Coloração", "150.00", 120, "Coloração completa"),
                    new Template("Hidratação", "70.00", 45, "Tratamento de hidratação")),
            BusinessSegment.ESTETICA, List.of(
                    new Template("Limpeza de pele", "150.00", 60, "Limpeza de pele profunda"),
                    new Template("Drenagem linfática", "120.00", 60, "Sessão de drenagem"),
                    new Template("Massagem relaxante", "130.00", 60, "Massagem corporal")),
            BusinessSegment.MANICURE, List.of(
                    new Template("Mão", "35.00", 45, "Cutilagem e esmaltação"),
                    new Template("Pé", "40.00", 45, "Cutilagem e esmaltação"),
                    new Template("Mão + Pé", "70.00", 90, "Combo mão e pé"),
                    new Template("Alongamento em gel", "150.00", 120, "Alongamento de unhas")),
            BusinessSegment.SOBRANCELHA, List.of(
                    new Template("Design de sobrancelha", "40.00", 30, "Design com pinça"),
                    new Template("Design com henna", "55.00", 45, "Design com aplicação de henna"),
                    new Template("Lash lifting", "120.00", 60, "Curvatura dos cílios")),
            BusinessSegment.TATUAGEM, List.of(
                    new Template("Orçamento", "0.00", 30, "Conversa para criar sua arte"),
                    new Template("Sessão pequena", "250.00", 120, "Tatuagem de até 10 cm"),
                    new Template("Sessão grande", "600.00", 240, "Sessão de até 4 horas")),
            BusinessSegment.PET, List.of(
                    new Template("Banho", "60.00", 60, "Banho com secagem"),
                    new Template("Banho e tosa", "90.00", 90, "Banho com tosa higiênica"),
                    new Template("Tosa completa", "110.00", 120, "Tosa na máquina ou tesoura")),
            BusinessSegment.FITNESS, List.of(
                    new Template("Aula experimental", "0.00", 60, "Primeira aula gratuita"),
                    new Template("Aula individual", "120.00", 60, "Aula personalizada"),
                    new Template("Avaliação física", "80.00", 45, "Avaliação completa"))
    );

    private static final List<Template> DEFAULT = List.of(
            new Template("Atendimento", "100.00", 60, "Atendimento padrão"),
            new Template("Retorno", "50.00", 30, "Sessão de retorno"));

    private final ServiceRepository serviceRepository;
    private final BusinessHoursRepository hoursRepository;

    public ServiceSeeder(ServiceRepository serviceRepository, BusinessHoursRepository hoursRepository) {
        this.serviceRepository = serviceRepository;
        this.hoursRepository = hoursRepository;
    }

    public void seed(Business business) {
        TEMPLATES.getOrDefault(business.getSegment(), DEFAULT).forEach(t -> {
            Service s = new Service();
            s.setBusiness(business);
            s.setServiceName(t.name());
            s.setPrice(new BigDecimal(t.price()));
            s.setDurationMinutes(t.minutes());
            s.setDescription(t.description());
            serviceRepository.save(s);
        });

        for (int day = 1; day <= 7; day++) {
            BusinessHours h = new BusinessHours();
            h.setBusiness(business);
            h.setDayOfWeek(day);
            h.setOpenTime(LocalTime.of(9, 0));
            h.setCloseTime(day == 6 ? LocalTime.of(17, 0) : LocalTime.of(19, 0));
            h.setClosed(day == 7);
            hoursRepository.save(h);
        }
    }
}
