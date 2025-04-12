package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
