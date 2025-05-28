package com.example.demo.controller;

import com.example.demo.dto.UpdateUserDto;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String viewProfile(Model model) {
        try {
            User user = getAuthenticatedUser();
            model.addAttribute("user", new UpdateUserDto(user.getId(), user.getUsername(), ""));
            log.info("Профиль загружен для пользователя: {}", user.getUsername());
            return "profile";
        } catch (ResourceNotFoundException e) {
            log.error("Пользователь не найден: {}", e.getMessage());
            model.addAttribute("errorMessage", "Пользователь не найден. Пожалуйста, войдите снова.");
            return "redirect:/signIn";
        }
    }

    @GetMapping("/edit")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String showEditProfileForm(Model model) {
        try {
            User user = getAuthenticatedUser();
            model.addAttribute("user", new UpdateUserDto(user.getId(), user.getUsername(), ""));
            log.info("Форма редактирования профиля открыта для пользователя: {}", user.getUsername());
            return "user-form";
        } catch (ResourceNotFoundException e) {
            log.error("Пользователь не найден: {}", e.getMessage());
            model.addAttribute("errorMessage", "Пользователь не найден. Пожалуйста, войдите снова.");
            return "redirect:/signIn";
        }
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String updateProfile(@Valid @ModelAttribute("user") UpdateUserDto userDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        log.info("Обработка обновления профиля для пользователя с ID: {}", userDto.getId());

        if (result.hasErrors()) {
            log.warn("Ошибки валидации формы: {}", result.getAllErrors());
            return "user-form";
        }

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank() && userDto.getPassword().length() < 6) {
            result.rejectValue("password", "Size.userDto.password", "Пароль должен содержать не менее 6 символов");
            log.warn("Пароль слишком короткий");
            return "user-form";
        }

        try {
            User currentUser = getAuthenticatedUser();
            if (!currentUser.getId().equals(userDto.getId())) {
                log.error("Попытка редактирования чужого профиля: ID {} != {}", userDto.getId(), currentUser.getId());
                model.addAttribute("errorMessage", "Вы можете редактировать только свой профиль");
                return "user-form";
            }

            if (!currentUser.getUsername().equals(userDto.getUsername())) {
                userRepository.findByUsername(userDto.getUsername()).ifPresent(existing -> {
                    if (!existing.getId().equals(userDto.getId())) {
                        throw new IllegalArgumentException("Имя пользователя '" + userDto.getUsername() + "' уже занято");
                    }
                });
                currentUser.setUsername(userDto.getUsername());
            }

            if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
                currentUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            userService.save(currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно обновлен!");
            log.info("Профиль пользователя {} успешно обновлен", userDto.getUsername());
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка валидации: {}", e.getMessage());
            result.rejectValue("username", "error.username", e.getMessage());
            return "user-form";
        } catch (ResourceNotFoundException e) {
            log.error("Пользователь не найден: {}", e.getMessage());
            model.addAttribute("errorMessage", "Пользователь не найден. Пожалуйста, войдите снова.");
            return "redirect:/signIn";
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении профиля: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Произошла ошибка при обновлении профиля. Попробуйте снова.");
            return "user-form";
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            log.error("Пользователь не аутентифицирован");
            throw new ResourceNotFoundException("Пользователь не аутентифицирован");
        }
        return userService.getCurrentUser();
    }
}