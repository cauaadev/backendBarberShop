package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.dto.ClientCreateDTO;
import com.corteBrabo.barbershopApi.dto.ClientDetailDTO;
import com.corteBrabo.barbershopApi.dto.ClientResponseDTO;
import com.corteBrabo.barbershopApi.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> search(@AuthenticationPrincipal User user,
                                                          @RequestParam(required = false) String search) {
        return ResponseEntity.ok(clientService.search(user, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDetailDTO> detail(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(clientService.detail(user, id));
    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(@AuthenticationPrincipal User user, @RequestBody @Valid ClientCreateDTO dto) {
        return ResponseEntity.ok(clientService.create(user, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> update(@AuthenticationPrincipal User user, @PathVariable Long id,
                                                    @RequestBody @Valid ClientCreateDTO dto) {
        return ResponseEntity.ok(clientService.update(user, id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        clientService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
