package com.yuhe.studybuddyapi.service;

import com.yuhe.studybuddyapi.dto.task.TaskRequest;
import com.yuhe.studybuddyapi.dto.task.TaskResponse;
import com.yuhe.studybuddyapi.dto.task.TaskStatusUpdateRequest;
import com.yuhe.studybuddyapi.entity.Task;
import com.yuhe.studybuddyapi.entity.TaskStatus;
import com.yuhe.studybuddyapi.entity.User;
import com.yuhe.studybuddyapi.repository.TaskRepository;
import com.yuhe.studybuddyapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResponse createTask(Long currentUserId, TaskRequest request) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Task task = new Task();
        task.setUser(user);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        // status 可选：不传就用 Task 里默认 TODO
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        task.setDeadline(request.getDeadline());

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long currentUserId, Long taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        return toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long currentUserId, Long taskId, TaskRequest request) {
        Task task = taskRepository.findByIdAndUserId(taskId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        // PUT：通常视为全量更新；如果你想“没传就不改”，可以改成 if != null
        task.setStatus(request.getStatus() != null ? request.getStatus() : task.getStatus());
        task.setDeadline(request.getDeadline());

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    @Transactional
    public TaskResponse updateStatus(Long currentUserId, Long taskId, TaskStatusUpdateRequest request) {
        Task task = taskRepository.findByIdAndUserId(taskId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setStatus(request.getStatus());
        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    @Transactional
    public void deleteTask(Long currentUserId, Long taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> listTasks(Long currentUserId, TaskStatus status, Pageable pageable) {
        Page<Task> page = (status == null)
                ? taskRepository.findAllByUserId(currentUserId, pageable)
                : taskRepository.findAllByUserIdAndStatus(currentUserId, status, pageable);

        return page.map(this::toResponse);
    }

    // ===== DTO mapping =====
    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadline(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
