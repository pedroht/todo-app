package com.pedroht.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private IUserRepository userRepository;
  
  @PostMapping("")
  public ResponseEntity create(@RequestBody UserModel userModel) {
    var existing = this.userRepository.findByUsername(userModel.getUsername());

    if (existing != null) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("User already exists!");
    }

    this.userRepository.save(userModel);
    return ResponseEntity.status(HttpStatus.CREATED).body(null);
  }  
}
