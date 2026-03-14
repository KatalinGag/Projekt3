package cz.engeto.Projekt3.controller;

import cz.engeto.Projekt3.dto.UserShortDto;
import cz.engeto.Projekt3.model.User;
import cz.engeto.Projekt3.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. Založit nového uživatele: POST api/v1/users
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User newUser = userService.saveUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    // 2. Informace o všech uživatelích: GET api/v1/users (?detail=true)
    @GetMapping
    public ResponseEntity<List<?>> getAllUsers(@RequestParam(defaultValue = "false") boolean detail) {
        if (detail) {
            // Vracíme seznam celých entit (User)
            return ResponseEntity.ok(userService.getAllUsersDetailed());
        } else {
            // Vracíme seznam osekaných dat (UserShortDTO)
            return ResponseEntity.ok(userService.getAllUsersBasic());
        }
    }

    // 3. Informace o jednom uživateli: GET api/v1/users/{id} (?detail=true)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id, @RequestParam(defaultValue = "false") boolean detail) {
        User user = userService.getUserById(id);

        if (detail) {
            // Pošleme celou entitu User (včetně personId a uuid)
            return ResponseEntity.ok(user);
        } else {
            // Pošleme jen osekané DTO
            UserShortDto shortDto = new UserShortDto(user.getId(), user.getName(), user.getSurname());
            return ResponseEntity.ok(shortDto);
        }
    }

    // 4. Upravit informace o uživateli: PUT api/v1/users
    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        User newUser = userService.updateUser(user);
        return ResponseEntity.ok(newUser);
    }

    // 5. Smazat uživatele: DELETE api/v1/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}