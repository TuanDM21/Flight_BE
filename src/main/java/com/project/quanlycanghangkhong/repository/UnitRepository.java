package com.project.quanlycanghangkhong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.cache.annotation.Cacheable;

import com.project.quanlycanghangkhong.model.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {
    List<Unit> findByTeam_Id(Integer teamId);

    @Cacheable(value = "userUnitIds", key = "#userId")
    @Query("SELECT u.id FROM Unit u JOIN u.users usr WHERE usr.id = :userId")
    List<Integer> findUnitIdsByUserId(@Param("userId") Integer userId);
}
