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

    // Query với JOIN FETCH để load userPermissions
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userPermissions")
    List<User> findAllWithPermissions();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userPermissions WHERE u.id = :id")
    Optional<User> findByIdWithPermissions(@Param("id") Integer id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userPermissions WHERE u.email = :email")
    Optional<User> findByEmailWithPermissions(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userPermissions WHERE u.name LIKE %:name% OR u.email LIKE %:email%")
    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseWithPermissions(@Param("name") String name, @Param("email") String email);
    
    @Query("SELECT u.id FROM User u WHERE u.team.id = :teamId")
    List<Integer> findUserIdsByTeamId(@Param("teamId") Integer teamId);

    @Query("SELECT u.id FROM User u WHERE u.unit.id = :unitId")
    List<Integer> findUserIdsByUnitId(@Param("unitId") Integer unitId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userPermissions WHERE u.role.roleName IN :roleNames")
    List<User> findByRoleNamesWithPermissions(@Param("roleNames") List<String> roleNames);

    @Query("SELECT u FROM User u WHERE u.role.roleName IN :roleNames")
    List<User> findByRoleNames(@Param("roleNames") List<String> roleNames);

    @Query("SELECT u FROM User u WHERE u.team.id = :teamId AND u.role.roleName = 'TEAM_LEAD'")
    Optional<User> findTeamLeadByTeamId(@Param("teamId") Integer teamId);

    @Query("SELECT u FROM User u WHERE u.unit.id = :unitId AND u.role.roleName = 'UNIT_LEAD'")
    Optional<User> findUnitLeadByUnitId(@Param("unitId") Integer unitId);
    
    // ✅ BATCH LOADING: Eliminate N+1 queries for team/unit leads
    @Query("SELECT u FROM User u WHERE u.team.id IN :teamIds AND u.role.roleName = 'TEAM_LEAD'")
    List<User> findTeamLeadsByTeamIds(@Param("teamIds") List<Integer> teamIds);

    @Query("SELECT u FROM User u WHERE u.unit.id IN :unitIds AND u.role.roleName = 'UNIT_LEAD'")
    List<User> findUnitLeadsByUnitIds(@Param("unitIds") List<Integer> unitIds);
    
    // ✅ REPLACEMENT FOR UserDAO: Find users by criteria
    @Query("SELECT u FROM User u WHERE " +
           "(:teamId IS NULL OR u.team.id = :teamId) AND " +
           "(:unitId IS NULL OR u.unit.id = :unitId) AND " +
           "(:searchText IS NULL OR :searchText = '' OR LOWER(u.name) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<User> findUsersByCriteria(@Param("teamId") Integer teamId, 
                                   @Param("unitId") Integer unitId, 
                                   @Param("searchText") String searchText);
}
