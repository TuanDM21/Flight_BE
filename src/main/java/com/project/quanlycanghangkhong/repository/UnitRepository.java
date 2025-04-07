package com.project.quanlycanghangkhong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.quanlycanghangkhong.model.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByTeam_Id(Integer teamId);

}
