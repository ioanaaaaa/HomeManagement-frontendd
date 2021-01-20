package com.fmi.relovut.controllers;

import com.fmi.relovut.dto.tasks.CreateTaskDto;
import com.fmi.relovut.dto.tasks.TaskModelDto;
import com.fmi.relovut.services.TaskService;
import com.sun.mail.iap.Response;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/add-edit")
    public ResponseEntity createOrEditTask(Principal principal, @RequestBody CreateTaskDto taskDto) {
        taskService.createOrEditTask(taskDto, principal);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("")
    public Set<TaskModelDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    /**
     * Get open tasks for current user.
     * returns also the tasks that are assigned to groups to which the user belongs.
     *
     * @param principal
     * @return Set<TaskModelDto>
     */
    @GetMapping("/open/current-user")
    public List<TaskModelDto> getOpenTasksForCurrentUser(Principal principal) {
        return taskService.getOpenTasksForCurrentUser(principal);
    }

    /**
     * Get completed tasks for current user.
     *
     * @param principal
     * @return Set<TaskModelDto>
     */
    @GetMapping("/completed/current-user")
    public List<TaskModelDto> getCompletedTasksForCurrentUser(Principal principal) {
        return taskService.getCompletedTasksForCurrentUser(principal);
    }

    /**
     * Claim task by current user.
     *
     * @param principal
     * @param taskId
     * @return
     */
    @PostMapping("/claim/{id}")
    public ResponseEntity claimTask(Principal principal, @PathVariable("id") Long taskId) {
        taskService.claimTask(principal, taskId);

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Deletes the specified task only if the current user is manager
     * @param taskId
     * @param principal
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteTaskById(@PathVariable("id") Long taskId, Principal principal) throws NotFoundException, IllegalAccessException {
        taskService.deleteTask(taskId, principal);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Submit the task only if current user claimed it
     * @param principal
     */
    @PostMapping("/submit/{id}")
    public ResponseEntity submitTask(Principal principal, @PathVariable("id") Long taskId) throws NotFoundException, IllegalAccessException {
        taskService.submitTask(principal, taskId);

        return new ResponseEntity(HttpStatus.OK);
    }

}
