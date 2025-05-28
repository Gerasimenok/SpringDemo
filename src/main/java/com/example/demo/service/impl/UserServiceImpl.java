package com.example.demo.service.impl;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    public List<User> findAll() {
        log.debug("Attempting to find all users");
        var users = this.userRepository.findAll();
        log.info("Found {} users", users.size());
        return users;
    }

    @Override
    @Transactional
    public User save(User user) {
        log.debug("Attempting to save user {}", user);

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            var role = this.roleService.findByRoleName("ROLE_USER");
            user.addRole(role);
        }

        var savedUser = userRepository.save(user);
        log.info("Saved user {}", savedUser);
        return savedUser;
    }

    @Override
    public User findById(Long id) {
        log.debug("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    public User getCurrentUser() {
        log.debug("Fetching current user");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Текущий пользователь не найден"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user {}", username);
        var user = this.userRepository.findByUsername(username).orElseThrow(() -> {
            var message = "Пользователь с именем: '%s' не найден".formatted(username);
            log.warn(message);
            return new ResourceNotFoundException(message);
        });
        log.info("Loaded user {}", user);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Deleting user with id: {}", id);
        userRepository.deleteById(id);
        log.info("Deleted user with id: {}", id);
    }
}