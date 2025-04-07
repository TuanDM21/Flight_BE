package com.project.quanlycanghangkhong.dto;

public class UnitDTO {
    private Integer id;
    private String unitName;
    private Integer teamId;  // chỉ lưu id của team

    public UnitDTO() {}

    public UnitDTO(Integer id, String unitName, Integer teamId) {
        this.id = id;
        this.unitName = unitName;
        this.teamId = teamId;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getUnitName() {
        return unitName;
    }
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    public Integer getTeamId() {
        return teamId;
    }
    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
}
