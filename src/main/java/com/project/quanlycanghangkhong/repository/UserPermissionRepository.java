package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Integer> {
    List<UserPermission> findByUserId(Integer userId);
    UserPermission findByUserIdAndPermissionCode(Integer userId, String permissionCode);
}
