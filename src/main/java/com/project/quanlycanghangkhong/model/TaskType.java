package com.project.quanlycanghangkhong.model;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * TaskType entity - Phân loại nhiệm vụ
 * Quản lý các loại nhiệm vụ khác nhau trong hệ thống sân bay
 */
@Entity
@Table(name = "task_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    // Quan hệ một-nhiều với Task
    @OneToMany(mappedBy = "taskType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
}
