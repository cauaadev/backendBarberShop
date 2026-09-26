package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.dto.PasswordChangeDTO;
import com.corteBrabo.barbershopApi.dto.ProfileUpdateDTO;
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
@RequestMapping("/team")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findTeam(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.findTeam(user));
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserResponseDTO> create(@AuthenticationPrincipal User user, @RequestBody @Valid StaffCreateDTO dto) {
        return ResponseEntity.ok(userService.createStaff(user, dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserResponseDTO> update(@AuthenticationPrincipal User user, @PathVariable Long id,
                                                  @RequestBody @Valid UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateStaff(user, id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        userService.deleteStaff(user, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateProfile(@AuthenticationPrincipal User user, @RequestBody @Valid ProfileUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateProfile(user, dto));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal User user, @RequestBody @Valid PasswordChangeDTO dto) {
        userService.changePassword(user, dto);
        return ResponseEntity.noContent().build();
    }
}
