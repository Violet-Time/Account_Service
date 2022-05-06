package com.example.account_service.service;

import com.example.account_service.model.*;
import com.example.account_service.repos.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventService eventService;
    private final Set<String> breachedPasswords;

    {
        breachedPasswords = Set.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
    }

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EventService eventService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventService = eventService;
    }

    public User findByEmail(String email) {

        Optional<User> optional = userRepository.findByEmailIgnoreCase(email);

        if (optional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        return optional.get();
    }

    public User addUser(User user) {

        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }

        checkPass(user.getPassword());
        List<Role> roles = new ArrayList<>();

        if (user.getRoles() == null && userRepository.count() == 0) {
            roles.add(Role.ROLE_ADMINISTRATOR);
            user.setRoles(roles);
        }

        if (user.getRoles() == null && userRepository.count() > 0) {
            roles.add(Role.ROLE_USER);
            user.setRoles(roles);
        }

        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        eventService.addEvent(ActionEvent.CREATE_USER, user.getEmail(), "/api/auth/signup");

        return userRepository.save(user);
    }

    public User changePass(User user, String newPassword) {

        checkPass(newPassword);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        eventService.addEvent(ActionEvent.CHANGE_PASSWORD, user.getEmail(), "/api/auth/changepass");

        return userRepository.save(user);
    }

    public void checkPass(String password) {

        if (password.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password length must be 12 chars minimum!");
        }
        if (breachedPasswords.contains(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String email) {

        User user = findByEmail(email);

        if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }

        eventService.addEvent(ActionEvent.DELETE_USER, email, "/api/admin/user");

        userRepository.deleteById(user.getId());
    }

    public User role(RolePut rolePut) {

        User user = findByEmail(rolePut.getUser());

        Role role;

        try {
            role = Role.fromValue(rolePut.getRole());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }

        if (rolePut.getOperation().equals("GRANT")) {
            grandRole(role, user);
        } else if (rolePut.getOperation().equals("REMOVE")) {
            removeRole(role, user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Operation not found!");
        }

        user.getRoles().sort(Comparator.reverseOrder());

        userRepository.save(user);

        return user;
    }

    public void grandRole(Role role, User user) {

        if (user.getRoles().contains(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user have the role!");
        }

        if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR) ||
                (!user.getRoles().contains(Role.ROLE_ADMINISTRATOR) && role == Role.ROLE_ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        }

        eventService.addEvent(ActionEvent.GRANT_ROLE, "Grant role " + role.getValue() + " to " + user.getEmail(), "/api/admin/user/role");

        user.getRoles().add(role);
    }

    public void removeRole(Role role, User user) {

        if (!user.getRoles().contains(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        }

        if (role == Role.ROLE_ADMINISTRATOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }

        if (user.getRoles().size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        }

        eventService.addEvent(ActionEvent.REMOVE_ROLE, "Remove role " + role.getValue() + " from " + user.getEmail(), "/api/admin/user/role");

        user.getRoles().remove(role);
    }

    public Map<String, String> setAccess(UserAccess userAccess, String name) {

        User user = findByEmail(userAccess.getUser());

        if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        }

        if (userAccess.getOperation().equals("LOCK")) {
            lockUser(user, name);
            return Map.of("status", "User " + user.getEmail() + " locked!");
        } else if (userAccess.getOperation().equals("UNLOCK")) {
            unlockUser(user, name);
            return Map.of("status", "User " + user.getEmail() + " unlocked!");
        }

        return null;
    }

    public void lockUser(User user, String name) {
        eventService.addEvent(ActionEvent.LOCK_USER, name, "Lock user " + user.getEmail(), "/api/admin/user/access");
        user.setLock(true);
        userRepository.save(user);
    }

    public void unlockUser(User user, String name) {
        eventService.addEvent(ActionEvent.UNLOCK_USER, name,"Unlock user " + user.getEmail(), "/api/admin/user/access");
        user.setLock(false);
        userRepository.save(user);
    }
}
