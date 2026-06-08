package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.dto.ClientCreateDTO;
import com.corteBrabo.barbershopApi.dto.StaffCreateDTO;
import com.corteBrabo.barbershopApi.dto.UserResponseDTO;
import com.corteBrabo.barbershopApi.dto.UserUpdateDTO;
import com.corteBrabo.barbershopApi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getById(user.getId()));
    }

    @PostMapping("/client")
    @PreAuthorize("hasAnyRole('ADM', 'BARBER')")
    public ResponseEntity<UserResponseDTO> createClient(@RequestBody @Valid ClientCreateDTO dto) {
        return ResponseEntity.ok(userService.createClient(dto));
    }

    @PostMapping("/staff")
    @PreAuthorize("hasRole('ADM')")
    public ResponseEntity<UserResponseDTO> createStaff(@RequestBody @Valid StaffCreateDTO dto) {
        return ResponseEntity.ok(userService.createStaff(dto));
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADM', 'BARBER')")
    public ResponseEntity<List<UserResponseDTO>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findByRole(@RequestParam (required = true) UserRole role){
        return ResponseEntity.ok(userService.findByRole(role));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADM', 'BARBER')")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADM') or #id == authentication.principal.id")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateUserById(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADM')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
