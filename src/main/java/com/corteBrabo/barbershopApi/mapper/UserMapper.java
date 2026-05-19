package com.corteBrabo.barbershopApi.mapper;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.dto.UserRequestDTO;
import com.corteBrabo.barbershopApi.dto.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponseDTO(User user);
    User toEntity(UserRequestDTO dto);
    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget User user);
}