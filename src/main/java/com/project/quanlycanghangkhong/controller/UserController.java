package com.project.quanlycanghangkhong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.project.quanlycanghangkhong.dto.UserDTO;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

    // Endpoint filter user theo team, unit, searchText
    @GetMapping("/filter")
    public ResponseEntity<List<UserDTO>> filterUsers(
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "unitId", required = false) Integer unitId,
            @RequestParam(value = "searchText", required = false) String searchText) {
        List<UserDTO> dtos = userService.filterUsers(teamId, unitId, searchText);
        return ResponseEntity.ok(dtos);
    }

    // Lấy thông tin user hiện tại dựa vào token
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    // Search user theo keyword (tìm theo tên hoặc email)
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsersByKeyword(@RequestParam("keyword") String keyword) {
        List<UserDTO> dtos = userService.searchUsersByKeyword(keyword);
        return ResponseEntity.ok(dtos);
    }

    // Lưu expoPushToken vào user khi đăng nhập app
    @PostMapping("/expo-push-token")
    public ResponseEntity<Void> saveExpoPushToken(@RequestBody String expoPushToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setExpoPushToken(expoPushToken);
            userRepository.save(user);
        }
        return ResponseEntity.ok().build();
    }

    // Xóa expoPushToken khi user logout và log giá trị token
    @PostMapping("/device-token/remove")
    public ResponseEntity<?> removeDeviceToken(@RequestBody String expoPushToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        System.out.println("[LOG] User: " + email + ", DB Token: " + (user != null ? user.getExpoPushToken() : "null") + ", Request Token: " + expoPushToken);
        if (user != null && expoPushToken != null && expoPushToken.equals(user.getExpoPushToken())) {
            user.setExpoPushToken(null);
            userRepository.save(user);
            System.out.println("[LOG] Token removed for user: " + email);
            return ResponseEntity.ok().body("Token removed");
        }
        System.out.println("[LOG] Token not removed for user: " + email);
        return ResponseEntity.badRequest().body("Invalid user or token");
    }

    // Xóa sạch thông tin liên quan đến user khi logout (expoPushToken, ...)
    @PostMapping("/logout-cleanup")
    public ResponseEntity<?> logoutCleanup() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            // Xóa expoPushToken
            user.setExpoPushToken(null);
            // Nếu có thêm trường/thông tin nào khác cần xóa khi logout, xử lý tại đây
            userRepository.save(user);
            System.out.println("[LOG] Logout cleanup: Đã xóa expoPushToken cho user: " + email);
            return ResponseEntity.ok().body("Logout cleanup success");
        }
        System.out.println("[LOG] Logout cleanup: Không tìm thấy user: " + email);
        return ResponseEntity.badRequest().body("User not found");
    }
    
}
