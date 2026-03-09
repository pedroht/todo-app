package com.pedroht.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
