package com.pedroht.todolist.user;

public class UserModel {
  private String name;
  private String username;
  private String password;

  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getUsername() {
    return this.username;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getPassword() {
    return this.password;
  }
}
