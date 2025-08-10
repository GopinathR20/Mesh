package com.mesh.controller;

import com.mesh.model.User;
import com.mesh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @PostMapping("/signup")   // Separate endpoint for students
    public String studentSignup(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already registered";
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("student");
        } else {
            String role = user.getRole().toLowerCase();
            if (!role.equals("student") && !role.equals("admin") && !role.equals("faculty")) {
                return "Invalid role. Allowed roles: student, admin, faculty";
            }
            user.setRole(role);
        }

        user.setRegisteredAt(LocalDateTime.now());
        userRepository.save(user);

        return "User registered successfully as " + user.getRole();
    }
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (existingUser == null) {
            return "Email not found";
        }

        if (existingUser.getPassword().equals(user.getPassword())) {
            return "Login successful as "+existingUser.getRole();
        } else {
            return "Incorrect password";
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);
    }
}
