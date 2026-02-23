package com.yuhe.studybuddyapi.repository;

import com.yuhe.studybuddyapi.entity.Task;
import com.yuhe.studybuddyapi.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // 查某个任务，但必须属于该用户（防止越权）
    Optional<Task> findByIdAndUserId(Long id, Long userId);

    // 列出该用户的所有任务（分页 + 排序）
    Page<Task> findAllByUserId(Long userId, Pageable pageable);

    // 按状态筛选（分页 + 排序）
    Page<Task> findAllByUserIdAndStatus(Long userId, TaskStatus status, Pageable pageable);
}