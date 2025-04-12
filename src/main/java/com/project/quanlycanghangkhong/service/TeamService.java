package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.TeamDTO;
import com.project.quanlycanghangkhong.model.Team;
import java.util.List;

public interface TeamService {
    List<TeamDTO> getAllTeams();

    Team getTeamById(Integer id);

    Team createTeam(Team team);

    void deleteTeam(Integer id);
}
