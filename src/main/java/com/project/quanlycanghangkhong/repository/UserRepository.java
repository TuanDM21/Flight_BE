package com.project.quanlycanghangkhong.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.quanlycanghangkhong.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
    
    @Query("SELECT u.id FROM User u WHERE u.team.id = :teamId")
    List<Integer> findUserIdsByTeamId(@Param("teamId") Integer teamId);

    @Query("SELECT u.id FROM User u WHERE u.unit.id = :unitId")
    List<Integer> findUserIdsByUnitId(@Param("unitId") Integer unitId);
}
