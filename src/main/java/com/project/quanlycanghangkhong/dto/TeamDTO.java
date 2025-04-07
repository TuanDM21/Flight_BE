package com.project.quanlycanghangkhong.dto;

public class TeamDTO {
    private Integer id;
    private String teamName;
    
    public TeamDTO() {}

    public TeamDTO(Integer id, String teamName) {
        this.id = id;
        this.teamName = teamName;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
