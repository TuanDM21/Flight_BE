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
    @Query("SELECT a FROM Activity a WHERE DATE(a.startTime) = :date")
    List<Activity> findByDate(LocalDate date);

    @EntityGraph(attributePaths = "participants")
    @Query("SELECT a FROM Activity a WHERE DATE(a.startTime) >= :start AND DATE(a.startTime) <= :end")
    List<Activity> findByDateRange(LocalDate start, LocalDate end);

    @EntityGraph(attributePaths = "participants")
    @Query("SELECT a FROM Activity a WHERE MONTH(a.startTime) = :month AND YEAR(a.startTime) = :year")
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

    List<Activity> findByStartTimeBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT a FROM Activity a WHERE a.startTime BETWEEN :from AND :to")
    List<Activity> findActivitiesStartingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
