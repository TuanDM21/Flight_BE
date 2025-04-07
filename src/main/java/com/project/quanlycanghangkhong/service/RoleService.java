package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.model.Role;
import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();
    Role getRoleById(Long id);
    Role createRole(Role role);
    void deleteRole(Long id);
}
