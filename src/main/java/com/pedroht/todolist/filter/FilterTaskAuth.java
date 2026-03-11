package com.pedroht.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pedroht.todolist.user.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if (!servletPath.startsWith("/tasks")) {
          filterChain.doFilter(request, response);
          return;
        }

        var authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Basic ")) {
          response.sendError(HttpStatus.UNAUTHORIZED.value());
          return;
        }

        var authEncoded = authorization.substring("Basic".length()).trim();
        byte[] authDecode = Base64.getDecoder().decode(authEncoded);

        var authString = new String(authDecode);
        String[] credentials = authString.split(":");

        if (credentials.length != 2) {
          response.sendError(HttpStatus.UNAUTHORIZED.value());
          return;
        }

        String username = credentials[0];
        String password = credentials[1];

        var user = this.userRepository.findByUsername(username);

        if (user == null) {
          response.sendError(HttpStatus.UNAUTHORIZED.value());
          return;
        }

        var passwordVerifyResult = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

        if (!passwordVerifyResult.verified) {
          response.sendError(HttpStatus.UNAUTHORIZED.value());
          return;
        }

        request.setAttribute("userId", user.getId());
        filterChain.doFilter(request, response);        
  }

}
