package com.project.quanlycanghangkhong.service;

import java.util.List;
import java.util.Optional;

import com.project.quanlycanghangkhong.dto.UserDTO;
import com.project.quanlycanghangkhong.model.User;

public interface UserService {
	 List<UserDTO> getAllUsers();    
	 Optional<User> getUserById(Integer id);
    User createUser(User user);
    User updateUser(Integer id, User user);
    void deleteUser(Integer id);
    Optional<User> login(String email, String password);
    List<UserDTO> filterUsers(Integer teamId, Integer unitId, String searchText);

}
