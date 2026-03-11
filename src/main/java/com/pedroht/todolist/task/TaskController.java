package com.pedroht.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedroht.todolist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;




@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  ITaskRepository taskRepository;

  @PostMapping("") 
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {

    var userId = request.getAttribute("userId");
    taskModel.setUserId((UUID) userId);

    var currentDate = LocalDateTime.now();

    if (taskModel.getStartAt().isBefore((currentDate))) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Start date must be greater than today date!");
    }

    if (currentDate.isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("End date must be a date greater than today!");
    }

    if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Start date must be greater than end date!");
    }

    this.taskRepository.save(taskModel);

    return ResponseEntity.status(HttpStatus.CREATED).body(null);
  }

  @GetMapping("") 
  public ResponseEntity list(HttpServletRequest request) {
    var userId = request.getAttribute("userId");
    var tasks = this.taskRepository.findByUserId((UUID) userId);

    return ResponseEntity.status(HttpStatus.OK).body(tasks);
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@PathVariable UUID id, @RequestBody TaskModel taskModel, HttpServletRequest request) {
      var userId = request.getAttribute("userId");

      if (id == null) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("You need to inform the id for this task!");
      }

      // Check if task exists
      var task = this.taskRepository.findById(id).orElse(null);

      if (task == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found!");
      }

      if (!task.getUserId().equals(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to execute this action!");
      }

      Utils.copyNonNullProperties(taskModel, task);

      this.taskRepository.save(task);
      
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}
