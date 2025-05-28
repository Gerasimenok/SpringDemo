package com.example.demo.service.impl;

import com.example.demo.entity.Role;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role save(Role role) {
        log.debug("Attempting to save role {}", role);

        var newRole = this.roleRepository.save(role);

        log.info("Role saved: {}", newRole);
        return newRole;
    }

    @Override
    public Role findByRoleName(String roleName) {
        log.debug("Attempting to find role by role name {}", roleName);

        var role = this.roleRepository.findByName(roleName).orElseThrow(
                () -> {
                    var message = "Role with name: '%s' was not found".formatted(roleName);
                    log.warn(message);
                    return new ResourceNotFoundException(message);
                }
        );

        log.info("Role found: {}", role);
        return role;
    }
}