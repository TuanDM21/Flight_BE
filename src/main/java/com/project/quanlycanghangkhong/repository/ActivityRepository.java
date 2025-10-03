package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @EntityGraph(attributePaths = "participants")
    @Query("SELECT a FROM Activity a WHERE DATE(a.startDate) = :date")
    List<Activity> findByDate(LocalDate date);

    @EntityGraph(attributePaths = "participants")
    @Query("SELECT a FROM Activity a WHERE DATE(a.startDate) >= :start AND DATE(a.startDate) <= :end")
    List<Activity> findByDateRange(LocalDate start, LocalDate end);

    @EntityGraph(attributePaths = "participants")
    @Query("SELECT a FROM Activity a WHERE MONTH(a.startDate) = :month AND YEAR(a.startDate) = :year")
    List<Activity> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT DISTINCT a FROM Activity a JOIN a.participants p " +
           "WHERE (p.participantType = 'USER' AND p.participantId = :userId) " +
           "OR (p.participantType = 'TEAM' AND p.participantId IN :teamIds) " +
           "OR (p.participantType = 'UNIT' AND p.participantId IN :unitIds)")
    List<Activity> findActivitiesForUser(
        @Param("userId") Integer userId,
        @Param("teamIds") List<Integer> teamIds,
        @Param("unitIds") List<Integer> unitIds
    );

    List<Activity> findByStartDateBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT a FROM Activity a WHERE a.startDate BETWEEN :from AND :to")
    List<Activity> findActivitiesStartingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT a FROM Activity a WHERE a.startDate >= :startDate AND a.endDate <= :endDate")
    List<Activity> findByDateRange(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    // Optimized query for pinned activities with JOIN FETCH
    @EntityGraph(attributePaths = "participants")
    @Query("SELECT DISTINCT a FROM Activity a WHERE a.pinned = true ORDER BY a.startDate DESC")
    List<Activity> findPinnedActivitiesOptimized();

    // Basic pinned activities query (fallback)
    @Query("SELECT a FROM Activity a WHERE a.pinned = true ORDER BY a.startDate DESC")
    List<Activity> findByPinnedTrue();

    // Optimized query for user activities with JOIN FETCH to avoid N+1 problem
    @EntityGraph(attributePaths = "participants")
    @Query("SELECT DISTINCT a FROM Activity a JOIN a.participants p " +
           "WHERE (p.participantType = 'USER' AND p.participantId = :userId) " +
           "OR (p.participantType = 'TEAM' AND p.participantId IN :teamIds) " +
           "OR (p.participantType = 'UNIT' AND p.participantId IN :unitIds) " +
           "ORDER BY a.startDate DESC")
    List<Activity> findActivitiesForUserOptimized(
        @Param("userId") Integer userId,
        @Param("teamIds") List<Integer> teamIds,
        @Param("unitIds") List<Integer> unitIds
    );
}
