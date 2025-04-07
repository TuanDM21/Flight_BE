package com.project.quanlycanghangkhong.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.quanlycanghangkhong.dto.LoginRequestDTO;
import com.project.quanlycanghangkhong.dto.UserDTO;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.service.UserService;


@CrossOrigin(origins = "*") // Hoặc "*"
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    // Lấy danh sách user
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> dtos = userService.getAllUsers();
        return ResponseEntity.ok(dtos);
    }

    // Lấy user theo ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Thêm user mới
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // Cập nhật user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    // Xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Đăng nhập với email và password
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        return userService.login(loginRequest.getEmail(), loginRequest.getPassword())
                .map(user -> {
                    String token = "your_generated_token_here";
                    UserDTO userDTO = new UserDTO(user); // Chỉ gửi dữ liệu cần thiết
                    return ResponseEntity.ok(Map.of("token", token, "user", userDTO));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("message", "Invalid credentials")));
    }

    // Endpoint filter user theo team, unit, searchText
    @GetMapping("/filter")
    public ResponseEntity<List<UserDTO>> filterUsers(
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "unitId", required = false) Integer unitId,
            @RequestParam(value = "searchText", required = false) String searchText) {
        List<UserDTO> dtos = userService.filterUsers(teamId, unitId, searchText);
        return ResponseEntity.ok(dtos);
    }

    
}
