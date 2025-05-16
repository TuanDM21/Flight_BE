package com.project.quanlycanghangkhong.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import com.project.quanlycanghangkhong.dao.UserDAO;
import com.project.quanlycanghangkhong.dto.DTOConverter;
import com.project.quanlycanghangkhong.dto.UserDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.UserService;
import com.project.quanlycanghangkhong.repository.UserPermissionRepository;
import com.project.quanlycanghangkhong.model.UserPermission;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(DTOConverter::convertUser)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Integer id, User user) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            return userRepository.save(existingUser);
        }).orElse(null);
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> filterUsers(Integer teamId, Integer unitId, String searchText) {
        try {
            List<User> users = userDAO.findUsersByCriteria(teamId, unitId, searchText);
            return users.stream()
                    .map(DTOConverter::convertUser)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching users", e);
        }
    }

    @Override
    public ApiResponseCustom<UserDTO> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO dto = new UserDTO(user);
        // Lấy quyền động từ user_permission
        List<UserPermission> perms = userPermissionRepository.findByUserId(user.getId());
        List<String> permCodes = perms.stream()
            .filter(UserPermission::getValue)
            .map(UserPermission::getPermissionCode)
            .collect(Collectors.toList());
        dto.setPermissions(permCodes);
        // Giữ lại canCreateActivity cho tương thích cũ
        dto.setCanCreateActivity(permCodes.contains("CAN_CREATE_ACTIVITY"));
        return ApiResponseCustom.success(dto);
    }

    @Override
    public List<UserDTO> searchUsersByKeyword(String keyword) {
        List<User> users = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
        return users.stream()
                .map(DTOConverter::convertUser)
                .collect(Collectors.toList());
    }
}
