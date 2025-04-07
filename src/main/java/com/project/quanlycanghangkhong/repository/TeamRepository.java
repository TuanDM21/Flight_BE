package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}
