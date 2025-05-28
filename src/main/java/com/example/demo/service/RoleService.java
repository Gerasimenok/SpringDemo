package com.example.demo.service;

import com.example.demo.entity.Role;

public interface RoleService {

    Role save(Role role);

    Role findByRoleName(String roleName);
}