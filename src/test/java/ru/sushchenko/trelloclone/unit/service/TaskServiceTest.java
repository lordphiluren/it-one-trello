package ru.sushchenko.trelloclone.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.sushchenko.trelloclone.dto.task.TaskFilterRequest;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.dto.user.UserResponse;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.repo.TaskRepo;
import ru.sushchenko.trelloclone.service.TagService;
import ru.sushchenko.trelloclone.service.UserService;
import ru.sushchenko.trelloclone.service.impl.TagServiceImpl;
import ru.sushchenko.trelloclone.service.impl.TaskServiceImpl;
import ru.sushchenko.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.sushchenko.trelloclone.utils.exception.ResourceNotFoundException;
import ru.sushchenko.trelloclone.utils.mapper.TaskMapper;

import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {
    @Mock
    private TaskRepo taskRepo;

    @Mock
    private TaskMapper taskMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private TaskServiceImpl taskService;
    @Mock
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGetAllTasks() {
        Task task1 = new Task();
        Task task2 = new Task();

        TaskResponse taskResponse1 = new TaskResponse();
        TaskResponse taskResponse2 = new TaskResponse();

        when(taskRepo.findAll()).thenReturn(List.of(task1, task2));
        when(taskMapper.toDto(task1)).thenReturn(taskResponse1);
        when(taskMapper.toDto(task2)).thenReturn(taskResponse2);

        List<TaskResponse> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        verify(taskRepo, times(1)).findAll();
        verify(taskMapper, times(1)).toDto(task1);
        verify(taskMapper, times(1)).toDto(task2);
    }

    @Test
    void shouldGetTaskDtoById() {
        UUID taskId = UUID.randomUUID();
        Task task = new Task();

        TaskResponse taskResponse = new TaskResponse();

        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponse);

        TaskResponse response = taskService.getTaskById(taskId);

        assertNotNull(response);
        verify(taskRepo, times(1)).findById(taskId);
        verify(taskMapper, times(1)).toDto(task);
    }

    @Test
    void shouldValidatePermissions() {
        Task task = new Task();
        User currentUser = new User();

        task.setCreator(currentUser);

        assertDoesNotThrow(() -> taskService.validatePermissions(task, currentUser));
    }

    @Test
    void shouldNotValidatePermissionsAndThrowException() {
        Task task = new Task();
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());
        User anotherUser1 = new User();
        anotherUser1.setId(UUID.randomUUID());
        User anotherUser2 = new User();
        anotherUser2.setId(UUID.randomUUID());

        task.setCreator(anotherUser1);
        task.setExecutors(Set.of(anotherUser2));

        assertThrows(NotEnoughPermissionsException.class, () -> taskService.validatePermissions(task, currentUser));
    }

    @Test
    void shouldValidateOwnership() {
        Task task = new Task();
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        task.setCreator(currentUser);

        assertDoesNotThrow(() -> taskService.validateOwnership(task, currentUser));
    }

    @Test
    void shouldNotValidateOwnershipAndThrowException() {
        Task task = new Task();
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        task.setCreator(anotherUser);

        assertThrows(NotEnoughPermissionsException.class, () -> taskService.validateOwnership(task, currentUser));
    }

    @Test
    void shouldAddTask() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTags(Set.of("tag"));
        User creator = new User();
        Task task = new Task();
        User executor = new User();
        Set<User> executors = new HashSet<>();
        executors.add(executor);

        when(taskMapper.toEntity(taskRequest)).thenReturn(task);
        when(taskRepo.save(task)).thenReturn(task);
        //when(userService.getUsersByIdIn(anySet())).thenReturn(executors);
        when(taskMapper.toDto(task)).thenReturn(new TaskResponse());

        TaskResponse response = taskService.addTask(taskRequest, creator);

        assertNotNull(response);
        verify(taskRepo, times(1)).save(task);
        verify(taskMapper, times(1)).toDto(task);
    }

    @Test
    void shouldUpdateTaskById() {
        UUID taskId = UUID.randomUUID();
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTags(Set.of("tag"));
        taskRequest.setName("new name");

        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());
        Task task = new Task();
        task.setCreator(currentUser);

        Task modifiedTask = new Task();
        modifiedTask.setCreator(currentUser);
        modifiedTask.setName(taskRequest.getName());

        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepo.save(any(Task.class))).thenReturn(modifiedTask);
        when(taskMapper.toDto(modifiedTask)).thenReturn(TaskResponse.builder().name(modifiedTask.getName()).build());

        TaskResponse response = taskService.updateTaskById(taskId, taskRequest, currentUser);

        assertEquals(taskRequest.getName(), response.getName());
        assertNotNull(response);
        verify(taskRepo, times(1)).save(any(Task.class));
        verify(taskMapper, times(1)).toDto(modifiedTask);
    }

    @Test
    void shouldAddExecutorToTaskById() {
        UUID taskId = UUID.randomUUID();
        UUID executorId = UUID.randomUUID();

        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        User executor = new User();
        executor.setId(executorId);

        Task task = new Task();
        task.setCreator(currentUser);
        task.setExecutors(new HashSet<>());

        Task modifiedTask = new Task();
        modifiedTask.setCreator(currentUser);
        modifiedTask.setExecutors(Set.of(executor));

        TaskResponse responseWithExecutor = TaskResponse.builder()
                .executors(Set.of(UserResponse.builder().id(executor.getId()).build()))
                .build();

        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        //when(userService.getUserById(executorId)).thenReturn(executor);
        when(taskRepo.save(task)).thenReturn(modifiedTask);
        when(taskMapper.toDto(modifiedTask)).thenReturn(responseWithExecutor);

        TaskResponse response = taskService.addExecutorToTaskById(taskId, executorId, currentUser);

        assertEquals(1, response.getExecutors().size());
        assertTrue(response.getExecutors().stream().anyMatch(userResponse -> userResponse.getId().equals(executorId)));
        verify(taskRepo, times(1)).save(task);
    }

    @Test
    void shouldRemoveExecutorFromTaskById() {
        UUID taskId = UUID.randomUUID();
        UUID executorId = UUID.randomUUID();

        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        User executor = new User();
        executor.setId(executorId);

        Task task = new Task();
        task.setCreator(currentUser);
        task.setExecutors(Set.of(executor));

        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepo.save(task)).thenReturn(task);

        taskService.removeExecutorFromTaskById(taskId, executorId, currentUser);

        assertEquals(0, task.getExecutors().size());
        verify(taskRepo, times(1)).save(task);
    }

    @Test
    void shouldDeleteTaskById() {
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);
        task.setCreator(currentUser);

        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        taskService.deleteTaskById(taskId, currentUser);

        verify(taskRepo, times(1)).findById(taskId);
        verify(taskRepo, times(1)).deleteById(taskId);
    }
}
