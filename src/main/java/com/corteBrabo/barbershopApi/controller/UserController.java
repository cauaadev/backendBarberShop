package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.dto.UserRequestDTO;
import com.corteBrabo.barbershopApi.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserRequestDTO user) {
        userService.createUser(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getUser")
    public ResponseEntity<User> getUserById(@RequestParam Long id) {
        return ResponseEntity.ok(userService.getbyId(id));
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok("User " + id + " excluido com sucesso! ");
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequestDTO dto) {
        userService.updateUserById(id, dto);
        return ResponseEntity.ok().build();
    }
}

