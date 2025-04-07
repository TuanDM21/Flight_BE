package com.project.quanlycanghangkhong.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.quanlycanghangkhong.dto.DTOConverter;
import com.project.quanlycanghangkhong.dto.TeamDTO;
import com.project.quanlycanghangkhong.model.Team;
import com.project.quanlycanghangkhong.repository.TeamRepository;
import com.project.quanlycanghangkhong.service.TeamService;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;

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
    public Team getTeamById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new RuntimeException("Team not found"));
    }

    @Override
    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }
}
