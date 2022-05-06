package com.example.account_service.controller;

import com.example.account_service.model.Event;
import com.example.account_service.model.RolePut;
import com.example.account_service.model.User;
import com.example.account_service.model.UserAccess;
import com.example.account_service.service.EventService;
import com.example.account_service.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ServiceController {
    private final UserService userService;
    private final EventService eventService;

    public ServiceController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    /*
    * Changes user roles.
    * Accepts data in the JSON format:
    * {
    *    "user": "<String value, not empty>",
    *    "role": "<User role>",
    *    "operation": "<[GRANT, REMOVE]>"
    * }
    * Return a response in the JSON format:
    * {
    *    "id": "<Long value, not empty>",
    *    "name": "<String value, not empty>",
    *    "lastname": "<String value, not empty>",
    *    "email": "<String value, not empty>",
    *    "roles": "[<User roles>]"
    * }
    */
    @PutMapping("/admin/user/role")
    public User changeUserRole(@Valid @RequestBody RolePut rolePut) {
        return userService.role(rolePut);
    }

    /*
    * Deletes a user.
    * Return a response in the JSON format:
    * {
    *    "user": "<user email>",
    *    "status": "Deleted successfully!"
    * }
    */
    @DeleteMapping(value = {"/admin/user","/admin/user/{email}"})
    public Map<String, String> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return Map.of("user", email, "status", "Deleted successfully!");
    }

    /*
    * Displays information about all users.
    * Return a response in the JSON format:
    * [
    *     {
    *         "id": "<user1 id>",
    *         "name": "<user1 name>",
    *         "lastname": "<user1 last name>",
    *         "email": "<user1 email>",
    *         "roles": "<[user1 roles]>"
    *     },
    *      ...
    *     {
    *         "id": "<userN id>",
    *         "name": "<userN name>",
    *         "lastname": "<userN last name>",
    *         "email": "<userN email>",
    *         "roles": "<[userN roles]>"
    *     }
    * ]
    */
    @GetMapping("/admin/user")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    /*
    * Endpoint that locks/unlocks users.
    * It accepts the following JSON body:
    * {
    *    "user": "<String value, not empty>",
    *    "operation": "<[LOCK, UNLOCK]>"
    * }
    * Where operation determines whether the user will be locked or unlocked.
    * If successful, respond with the HTTP OK status (200) and the following body:
    * {
    *     "status": "User <username> <[locked, unlocked]>!"
    * }
    */
    @PutMapping("/admin/user/access")
    public Map<String, String> lockUnlockUser(@Valid @RequestBody UserAccess userAccess, @AuthenticationPrincipal UserDetails userDetails) {
        return userService.setAccess(userAccess, userDetails.getUsername());
    }

    @GetMapping("/security/events")
    public List<Event> getEvents() {
        return eventService.getEvents();
    }
}
