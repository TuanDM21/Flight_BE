package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.request.LoginRequest;
import com.project.quanlycanghangkhong.dto.request.RegisterRequest;
import com.project.quanlycanghangkhong.dto.response.ApiResponse;
import com.project.quanlycanghangkhong.dto.response.LoginResponse;
import com.project.quanlycanghangkhong.dto.response.RegisterResponse;
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
import com.project.quanlycanghangkhong.dto.TeamDTO;
import com.project.quanlycanghangkhong.dto.UnitDTO;
import com.project.quanlycanghangkhong.dto.DTOConverter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
	public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getEmail(),
						loginRequest.getPassword()));

		String token = jwtTokenProvider.generateToken(authentication);

		LoginResponse loginResponse = LoginResponse.builder()
				.accessToken(token)
				.tokenType("Bearer")
				.expiresIn(3600L)
				.build();

		return ApiResponse.success(loginResponse);
	}

	@Override
	@Transactional
	public ApiResponse<RegisterResponse> register(RegisterRequest registerRequest) {
		// Kiểm tra email đã tồn tại chưa
		if (userRepository.existsByEmail(registerRequest.getEmail())) {
			return ApiResponse.error(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
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

		// Create and return RegisterResponse with user details
		RegisterResponse response = new RegisterResponse();
		response.setId(user.getId());
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		response.setRole(new RoleDTO(user.getRole()));
		response.setTeam(DTOConverter.convertTeam(user.getTeam()));
		response.setUnit(DTOConverter.convertUnit(user.getUnit()));
		return ApiResponse.success(response);
	}
}
