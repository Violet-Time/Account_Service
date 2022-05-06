package com.example.account_service.controller;

import com.example.account_service.model.User;
import com.example.account_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /*
    * Allows the user to register on the service.
    * Accepts data in the JSON format:
    * {
    *    "name": "<String value, not empty>",
    *    "lastname": "<String value, not empty>",
    *    "email": "<String value, not empty>",
    *    "password": "<String value, not empty>"
    * }
    * Return a response in the JSON format:
    * {
    *    "id": "<Long value, not empty>",
    *    "name": "<String value>",
    *    "lastname": "<String value>",
    *    "email": "<String value>"
    *    "roles": "<[User roles]>"
    * }
    */
    @PostMapping("/signup")
    public User postSignup(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    /*
    * Changes a user password.
    * Accepts data in the JSON format:
    * {
    *    "new_password": "<String value, not empty>"
    * }
    * Return a response in the JSON format:
    * {
    *    "email": "<String value, not empty>",
    *    "status": "The password has been updated successfully"
    * }
    */
    @PostMapping("/changepass")
    public Map<String, String> postChangePass(@RequestBody Map<String, Object> mapBody, @AuthenticationPrincipal User user) {
        if (!mapBody.containsKey("new_password")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        userService.changePass(user, (String) mapBody.get("new_password"));
        return Map.of("email", user.getEmail(), "status", "The password has been updated successfully");
    }
}
