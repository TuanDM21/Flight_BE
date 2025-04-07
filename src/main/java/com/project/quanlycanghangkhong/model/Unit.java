package com.project.quanlycanghangkhong.model;

import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "units")
public class Unit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "unit_name", length = 100, nullable = false)
    private String unitName;
    
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;
    
    // Getters and Setters
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
    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }
    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }
}
