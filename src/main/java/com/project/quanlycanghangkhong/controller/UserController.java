package com.project.quanlycanghangkhong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
        @Operation(summary = "Get all users", description = "Retrieve a list of all users")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all users", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
        })
        public ResponseEntity<ApiResponseCustom<List<UserDTO>>> getAllUsers() {
                List<UserDTO> dtos = userService.getAllUsers();
                return ResponseEntity.ok(ApiResponseCustom.success(dtos));
        }

        // Lấy user theo ID
        @GetMapping("/{id}")
        @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
        })
        public ResponseEntity<ApiResponseCustom<User>> getUserById(@PathVariable Integer id) {
                return userService.getUserById(id)
                                .map(user -> ResponseEntity.ok(ApiResponseCustom.success(user)))
                                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(ApiResponseCustom.notFound("User not found")));
        }

        // Thêm user mới
        @PostMapping
        @Operation(summary = "Create user", description = "Create a new user")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User created successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
        })
        public ResponseEntity<ApiResponseCustom<User>> createUser(@RequestBody User user) {
                User created = userService.createUser(user);
                return ResponseEntity.ok(ApiResponseCustom.success(created));
        }

        // Cập nhật user
        @PutMapping("/{id}")
        @Operation(summary = "Update user", description = "Update an existing user")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
        })
        public ResponseEntity<ApiResponseCustom<User>> updateUser(@PathVariable Integer id, @RequestBody User user) {
                User updatedUser = userService.updateUser(id, user);
                if (updatedUser != null) {
                        return ResponseEntity.ok(ApiResponseCustom.updated(updatedUser));
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(ApiResponseCustom.notFound("User not found"));
                }
        }

        // Xóa user
        @DeleteMapping("/{id}")
        @Operation(summary = "Delete user", description = "Delete a user by ID")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
        })
        public ResponseEntity<ApiResponseCustom<Void>> deleteUser(@PathVariable Integer id) {
                userService.deleteUser(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseCustom.deleted());
        }

        // Endpoint filter user theo team, unit, searchText
        @GetMapping("/filter")
        @Operation(summary = "Filter users", description = "Filter users by team, unit, or search text")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully filtered users", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
        })
        public ResponseEntity<ApiResponseCustom<List<UserDTO>>> filterUsers(
                        @RequestParam(value = "teamId", required = false) Integer teamId,
                        @RequestParam(value = "unitId", required = false) Integer unitId,
                        @RequestParam(value = "searchText", required = false) String searchText) {
                List<UserDTO> dtos = userService.filterUsers(teamId, unitId, searchText);
                return ResponseEntity.ok(ApiResponseCustom.success(dtos));
        }

        // Lấy thông tin user hiện tại dựa vào token
        @GetMapping("/me")
        @Operation(summary = "Get current user", description = "Get the current user based on token")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved current user", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
        })
        public ResponseEntity<ApiResponseCustom<UserDTO>> getCurrentUser() {
                ApiResponseCustom<UserDTO> userResponse = userService.getCurrentUser();
                return ResponseEntity.ok(userResponse);
        }

        // Search user theo keyword (tìm theo tên hoặc email)
        @GetMapping("/search")
        @Operation(summary = "Search users", description = "Search users by keyword (name or email)")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully searched users", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
        })
        public ResponseEntity<ApiResponseCustom<List<UserDTO>>> searchUsersByKeyword(
                        @RequestParam("keyword") String keyword) {
                List<UserDTO> dtos = userService.searchUsersByKeyword(keyword);
                return ResponseEntity.ok(ApiResponseCustom.success(dtos));
        }

        // Lưu expoPushToken vào user khi đăng nhập app
        @PostMapping("/expo-push-token")
        @Operation(summary = "Save Expo Push Token", description = "Save Expo Push Token for the current user")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Expo push token saved successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
        })
        public ResponseEntity<ApiResponseCustom<Void>> saveExpoPushToken(@RequestBody String expoPushToken) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();
                User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                        user.setExpoPushToken(expoPushToken);
                        userRepository.save(user);
                }
                return ResponseEntity.ok(ApiResponseCustom.success(null));
        }

        // Xóa expoPushToken khi user logout và log giá trị token
        @PostMapping("/device-token/remove")
        public ResponseEntity<?> removeDeviceToken(@RequestBody String expoPushToken) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();
                User user = userRepository.findByEmail(email).orElse(null);
                System.out.println("[LOG] User: " + email + ", DB Token: "
                                + (user != null ? user.getExpoPushToken() : "null") + ", Request Token: "
                                + expoPushToken);
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

        @GetMapping("/directors")
        @Operation(summary = "Get users with role DIRECTOR or VICE_DIRECTOR", description = "Retrieve a list of users with role DIRECTOR or VICE_DIRECTOR")
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved directors and vice directors", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
        })
        public ResponseEntity<ApiResponseCustom<List<UserDTO>>> getDirectorsAndViceDirectors() {
            List<UserDTO> dtos = userService.getUsersByRoles(List.of("DIRECTOR", "VICE_DIRECTOR"));
            return ResponseEntity.ok(ApiResponseCustom.success(dtos));
        }

        @GetMapping("/assignable")
        @Operation(summary = "Get assignable users", description = "Lấy danh sách user mà user hiện tại có thể giao việc cho theo phân quyền")
        public ResponseEntity<ApiResponseCustom<List<UserDTO>>> getAssignableUsers() {
            List<UserDTO> dtos = userService.getAssignableUsersForCurrentUser();
            return ResponseEntity.ok(ApiResponseCustom.success(dtos));
        }

}
