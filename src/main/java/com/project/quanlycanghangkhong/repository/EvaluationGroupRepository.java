package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.EvaluationGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationGroupRepository extends JpaRepository<EvaluationGroup, Integer> {
    // Có thể bổ sung các phương thức truy vấn tùy chỉnh nếu cần
}
