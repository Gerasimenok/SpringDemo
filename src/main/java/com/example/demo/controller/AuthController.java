package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/signUp")
    public String registerUser(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/signIn")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/signUp")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            this.authService.register(user);
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}