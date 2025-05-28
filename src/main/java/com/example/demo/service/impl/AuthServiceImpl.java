package com.example.demo.service.impl;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final RoleService roleService;

    @Override
    public User register(User user) {
        try {
            String rawPassword = user.getPassword();
            user.setPassword(passwordEncoder.encode(rawPassword));
            Role userRole = roleService.findByRoleName("ROLE_USER");
            user.addRole(userRole);
            var newUser = userService.save(user);
            var authenticationToken = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    rawPassword
            );
            var authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return newUser;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user: " + e.getMessage());
        }
    }
}