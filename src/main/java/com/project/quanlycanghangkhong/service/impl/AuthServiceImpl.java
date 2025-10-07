package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.request.LoginRequest;
import com.project.quanlycanghangkhong.request.RegisterRequest;
import com.project.quanlycanghangkhong.request.FirstLoginPasswordChangeRequest;
import com.project.quanlycanghangkhong.dto.LoginDTO;
import com.project.quanlycanghangkhong.dto.RegisterDTO;
import com.project.quanlycanghangkhong.dto.FirstLoginPasswordChangeDTO;
import com.project.quanlycanghangkhong.model.Role;
import com.project.quanlycanghangkhong.model.Team;
import com.project.quanlycanghangkhong.model.Unit;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.RoleRepository;
import com.project.quanlycanghangkhong.repository.TeamRepository;
import com.project.quanlycanghangkhong.repository.UnitRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.security.JwtTokenProvider;
import com.project.quanlycanghangkhong.service.AuthService;
import com.project.quanlycanghangkhong.dto.RoleDTO;
import com.project.quanlycanghangkhong.dto.DTOConverter;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final TeamRepository teamRepository;
	private final UnitRepository unitRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public ApiResponseCustom<LoginDTO> login(LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginRequest.getEmail(),
							loginRequest.getPassword()));

			// Lấy thông tin user để kiểm tra first login
			User user = userRepository.findByEmail(loginRequest.getEmail())
					.orElseThrow(() -> new RuntimeException("User not found"));

			String token = jwtTokenProvider.generateToken(authentication);

			LoginDTO.LoginDTOBuilder loginResponseBuilder = LoginDTO.builder()
					.accessToken(token)
					.tokenType("Bearer")
					.expiresIn(3600L);

			// Kiểm tra nếu đây là lần đăng nhập đầu tiên
			if (user.getIsFirstLogin() != null && user.getIsFirstLogin()) {
				loginResponseBuilder
						.requiresPasswordChange(true)
						.message("Bạn cần đổi mật khẩu lần đầu đăng nhập");
			} else {
				loginResponseBuilder
						.requiresPasswordChange(false)
						.message("Đăng nhập thành công");
			}

			return ApiResponseCustom.success(loginResponseBuilder.build());
		} catch (BadCredentialsException e) {
			return ApiResponseCustom.error(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		} catch (DisabledException e) {
			return ApiResponseCustom.error(HttpStatus.FORBIDDEN, "User account is disabled");
		} catch (Exception e) {
			return ApiResponseCustom.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
		}
	}

	@Override
	@Transactional
	public ApiResponseCustom<RegisterDTO> register(RegisterRequest registerRequest) {
		// Kiểm tra email đã tồn tại chưa
		if (userRepository.existsByEmail(registerRequest.getEmail())) {
			return ApiResponseCustom.error(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
		}

		// Tạo user mới
		User user = new User();
		user.setName(registerRequest.getName());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

		// Set role
		if (registerRequest.getRoleId() != null) {
			Role role = roleRepository.findById(registerRequest.getRoleId())
					.orElseThrow(() -> new RuntimeException("Role không tồn tại"));
			user.setRole(role);
		}

		// Set team
		if (registerRequest.getTeamId() != null) {
			Team team = teamRepository.findById(registerRequest.getTeamId())
					.orElseThrow(() -> new RuntimeException("Team không tồn tại"));
			user.setTeam(team);
		}

		// Set unit
		if (registerRequest.getUnitId() != null) {
			Unit unit = unitRepository.findById(registerRequest.getUnitId())
					.orElseThrow(() -> new RuntimeException("Unit không tồn tại"));
			user.setUnit(unit);
		}

		// Lưu user
		userRepository.save(user);

		// Create and return RegisterDTO with user details
		RegisterDTO response = new RegisterDTO();
		response.setId(user.getId());
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		response.setRole(new RoleDTO(user.getRole()));
		response.setTeam(DTOConverter.convertTeam(user.getTeam()));
		response.setUnit(DTOConverter.convertUnit(user.getUnit()));
		return ApiResponseCustom.success(response);
	}

	@Override
	@Transactional
	public ApiResponseCustom<FirstLoginPasswordChangeDTO> changePasswordFirstLogin(FirstLoginPasswordChangeRequest request) {
		try {
			// Lấy thông tin user hiện tại từ SecurityContext
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String userEmail = authentication.getName();
			
			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new RuntimeException("User not found"));

			// Kiểm tra user có phải first login không
			if (user.getIsFirstLogin() == null || !user.getIsFirstLogin()) {
				return ApiResponseCustom.error(HttpStatus.BAD_REQUEST, "Tài khoản này không cần đổi mật khẩu lần đầu");
			}

			// Kiểm tra mật khẩu hiện tại
			if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
				return ApiResponseCustom.error(HttpStatus.BAD_REQUEST, "Mật khẩu hiện tại không đúng");
			}

			// Kiểm tra mật khẩu mới và confirm password
			if (!request.getNewPassword().equals(request.getConfirmPassword())) {
				return ApiResponseCustom.error(HttpStatus.BAD_REQUEST, "Mật khẩu mới và xác nhận mật khẩu không khớp");
			}

			// Kiểm tra mật khẩu mới không trùng với mật khẩu cũ
			if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
				return ApiResponseCustom.error(HttpStatus.BAD_REQUEST, "Mật khẩu mới phải khác mật khẩu hiện tại");
			}

			// Cập nhật mật khẩu và đặt isFirstLogin = false
			user.setPassword(passwordEncoder.encode(request.getNewPassword()));
			user.setIsFirstLogin(false);
			userRepository.save(user);

			// Tạo token mới sau khi đổi password
			Authentication newAuth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userEmail, request.getNewPassword()));
			String newToken = jwtTokenProvider.generateToken(newAuth);

			FirstLoginPasswordChangeDTO response = FirstLoginPasswordChangeDTO.builder()
					.message("Đổi mật khẩu thành công")
					.success(true)
					.newToken(newToken)
					.tokenType("Bearer")
					.expiresIn(3600L)
					.build();

			return ApiResponseCustom.success(response);

		} catch (Exception e) {
			return ApiResponseCustom.error(HttpStatus.INTERNAL_SERVER_ERROR, 
					"Có lỗi xảy ra khi đổi mật khẩu: " + e.getMessage());
		}
	}
}
