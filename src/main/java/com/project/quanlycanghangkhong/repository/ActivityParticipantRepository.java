package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.ActivityParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipant, Long> {
    List<ActivityParticipant> findByActivityId(Long activityId);
    
    @Modifying
    @Query("DELETE FROM ActivityParticipant ap WHERE ap.activity.id = ?1 AND ap.participantType = ?2 AND ap.participantId = ?3")
    int deleteByActivityIdAndParticipantTypeAndParticipantId(Long activityId, String participantType, Long participantId);
    
    List<ActivityParticipant> findByParticipantTypeAndParticipantId(String participantType, Long participantId);
}
