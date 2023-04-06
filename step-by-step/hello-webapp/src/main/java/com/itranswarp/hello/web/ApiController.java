package com.itranswarp.hello.web;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.hello.User;
import com.itranswarp.hello.service.UserService;
import com.itranswarp.summer.annotation.Autowired;
import com.itranswarp.summer.annotation.GetMapping;
import com.itranswarp.summer.annotation.PathVariable;
import com.itranswarp.summer.annotation.RestController;
import com.itranswarp.summer.exception.DataAccessException;

@RestController
public class ApiController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    @GetMapping("/api/user/{email}")
    Map<String, Boolean> userExist(@PathVariable("email") String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        try {
            userService.getUser(email);
            return Map.of("result", Boolean.TRUE);
        } catch (DataAccessException e) {
            return Map.of("result", Boolean.FALSE);
        }
    }

    @GetMapping("/api/users")
    List<User> users() {
        return userService.getUsers();
    }
}
