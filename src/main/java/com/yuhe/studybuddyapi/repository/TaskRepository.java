package com.yuhe.studybuddyapi.repository;

import com.yuhe.studybuddyapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}