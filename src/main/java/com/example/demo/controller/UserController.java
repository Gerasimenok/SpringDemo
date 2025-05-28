package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String findAll(Model model) {
        var users = this.userService.findAll();
        model.addAttribute("listUsers", users);
        return "users";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user-form";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "user-form";
        }
        userService.save(user);
        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }

    @GetMapping("/db")
    @ResponseBody
    public List<User> findAllDb() {
        return userService.findAll();
    }
}