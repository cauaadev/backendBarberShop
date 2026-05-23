package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.database.repository.ServiceRepository;
import com.corteBrabo.barbershopApi.dto.ServiceRequestDTO;
import com.corteBrabo.barbershopApi.dto.ServiceResponseDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.mapper.ServiceMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    public ServiceService(ServiceRepository serviceRepository, ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
    }

    public List<ServiceResponseDTO> findAll() {
        return serviceRepository.findAll().stream()
                .map(serviceMapper::toResponseDTO)
                .toList();
    }

    public ServiceResponseDTO getById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado: " + id));
        return serviceMapper.toResponseDTO(service);
    }

    public ServiceResponseDTO create(ServiceRequestDTO dto) {
        Service service = serviceMapper.toEntity(dto);
        return serviceMapper.toResponseDTO(serviceRepository.save(service));
    }

    public ServiceResponseDTO update(Long id, ServiceRequestDTO dto) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado: " + id));
        serviceMapper.updateEntityFromDto(dto, service);
        return serviceMapper.toResponseDTO(serviceRepository.save(service));
    }

    public void delete(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new NotFoundException("Serviço não encontrado: " + id);
        }
        serviceRepository.deleteById(id);
    }
}
