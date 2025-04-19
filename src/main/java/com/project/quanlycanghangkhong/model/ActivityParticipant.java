package com.project.quanlycanghangkhong.model;

import jakarta.persistence.*;

@Entity
@Table(name = "activity_participants")
public class ActivityParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(nullable = false)
    private String participantType; // USER, TEAM, UNIT

    @Column(nullable = false)
    private Long participantId;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Activity getActivity() {
        return activity;
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    public String getParticipantType() {
        return participantType;
    }
    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }
    public Long getParticipantId() {
        return participantId;
    }
    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
}
