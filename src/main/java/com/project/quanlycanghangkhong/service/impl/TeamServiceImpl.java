package com.project.quanlycanghangkhong.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

import com.project.quanlycanghangkhong.dto.DTOConverter;
import com.project.quanlycanghangkhong.dto.TeamDTO;
import com.project.quanlycanghangkhong.model.Team;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.TeamRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.TeamService;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    
    @Autowired
    private UserRepository userRepository;

    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public List<TeamDTO> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(DTOConverter::convertTeam) // Chuyển sang TeamDTO
                .collect(Collectors.toList());
    }

    @Override
    public Team getTeamById(Integer id) {
        return teamRepository.findById(id).orElseThrow(() -> new RuntimeException("Team not found"));
    }

    @Override
    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public void deleteTeam(Integer id) {
        teamRepository.deleteById(id);
    }

    @Override
    public List<TeamDTO> getAssignableTeamsForCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        
        if (currentUser == null || currentUser.getRole() == null) {
            return List.of();
        }
        
        String role = currentUser.getRole().getRoleName();
        List<Team> result;
        
        switch (role) {
            case "DIRECTOR":
            case "VICE_DIRECTOR":
                // Có thể giao việc cho tất cả teams
                result = teamRepository.findAll();
                break;
                
            case "TEAM_LEAD":
            case "TEAM_VICE_LEAD":
                // Chỉ có thể giao việc cho team của mình
                if (currentUser.getTeam() == null) return List.of();
                result = List.of(currentUser.getTeam());
                break;
                
            case "UNIT_LEAD":
            case "UNIT_VICE_LEAD":
                // UNIT_LEAD không nên giao việc ở cấp độ team, chỉ nên giao ở cấp độ unit và user
                // Vì logic trong UserServiceImpl chỉ cho phép giao cho users trong cùng unit
                result = List.of();
                break;
                
            case "MEMBER":
            case "OFFICE":
                // Có thể giao việc cho team của mình (nhất quán với Unit logic)
                if (currentUser.getTeam() == null) return List.of();
                result = List.of(currentUser.getTeam());
                break;
                
            default:
                result = List.of();
        }
        
        return result.stream()
                .map(DTOConverter::convertTeam)
                .collect(Collectors.toList());
    }
}
