package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.repository.ServiceRepository;
import com.corteBrabo.barbershopApi.dto.ServiceRequestDTO;
import com.corteBrabo.barbershopApi.dto.ServiceResponseDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.mapper.ServiceMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final BusinessService businessService;
    private final ServiceMapper serviceMapper;

    public ServiceService(ServiceRepository serviceRepository, BusinessService businessService, ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.businessService = businessService;
        this.serviceMapper = serviceMapper;
    }

    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> findAll(User currentUser) {
        return serviceRepository.findByBusiness_IdOrderByServiceNameAsc(currentUser.getBusinessId()).stream()
                .map(serviceMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ServiceResponseDTO create(User currentUser, ServiceRequestDTO dto) {
        Service service = new Service();
        service.setBusiness(businessService.load(currentUser.getBusinessId()));
        apply(dto, service);
        return serviceMapper.toResponseDTO(serviceRepository.save(service));
    }

    @Transactional
    public ServiceResponseDTO update(User currentUser, Long id, ServiceRequestDTO dto) {
        Service service = load(currentUser, id);
        apply(dto, service);
        return serviceMapper.toResponseDTO(serviceRepository.save(service));
    }

    @Transactional
    public void delete(User currentUser, Long id) {
        Service service = load(currentUser, id);
        try {
            serviceRepository.delete(service);
            serviceRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Esse serviço já foi usado em agendamentos. Desative em vez de excluir.");
        }
    }

    private Service load(User currentUser, Long id) {
        return serviceRepository.findByServiceIdAndBusiness_Id(id, currentUser.getBusinessId())
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
    }

    private static void apply(ServiceRequestDTO dto, Service service) {
        service.setServiceName(dto.name().trim());
        service.setPrice(dto.price());
        service.setDurationMinutes(dto.durationMinutes());
        service.setDescription(dto.description() == null || dto.description().isBlank() ? null : dto.description().trim());
        if (dto.active() != null) service.setActive(dto.active());
    }
}
