//package com.project.quanlycanghangkhong.controller;
//
//import java.util.Map;
//import java.util.Optional;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.project.quanlycanghangkhong.dto.LoginRequestDTO;
//import com.project.quanlycanghangkhong.model.User;
//import com.project.quanlycanghangkhong.repository.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class AuthController {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtTo jwtTokenProvider;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
//        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
//
//        if (user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
//            String token = jwtTokenProvider.generateToken(user.get().getEmail());
//            return ResponseEntity.ok(Map.of("token", token));
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//    }
//}
//
