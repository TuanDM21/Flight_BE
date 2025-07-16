package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    @Cacheable(value = "userTeamIds", key = "#userId")
    @Query("SELECT t.id FROM Team t JOIN t.users u WHERE u.id = :userId")
    List<Integer> findTeamIdsByUserId(@Param("userId") Integer userId);
}
