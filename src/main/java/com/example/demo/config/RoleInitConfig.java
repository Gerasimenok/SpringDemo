package com.example.demo.config;

import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class RoleInitConfig {

    private final RoleRepository roleRepository;

    @Bean
    @Transactional
    public CommandLineRunner commandLineRunnerBean() {
        return args -> {
            if (!this.roleRepository.existsByName("ROLE_USER")) {
                var user = Role.builder().name("ROLE_USER").build();
                this.roleRepository.save(user);

            }
            if (!this.roleRepository.existsByName("ROLE_ADMIN")) {
                var admin = Role.builder().name("ROLE_ADMIN").build();
                this.roleRepository.save(admin);

            }
        };
    }
}