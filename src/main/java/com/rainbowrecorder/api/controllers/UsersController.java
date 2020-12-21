package com.rainbowrecorder.api.controllers;

import com.rainbowrecorder.api.repositories.UserRepository;
import com.rainbowrecorder.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    private final UserService userService;

    public UsersController (UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Object> checkUsernameAvailability(@RequestBody String username) {
        return userService.checkUsername(username);
    }

    @PostMapping
    @RequestMapping("/login")
    public ResponseEntity<Object> logIn(@RequestBody String username, @RequestBody String password) {

    }

    @PostMapping
    @RequestMapping("/register")
    public ResponseEntity<Object> register(@RequestBody String username, @RequestBody String password) {

    }

    @PostMapping
    @RequestMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@RequestBody String userId, @RequestBody String username) {

    }

    @PutMapping
    @RequestMapping("/username")
    public ResponseEntity<Object> changeUsername(@RequestBody String userId, @RequestBody String username) {

    }

    @PutMapping
    @RequestMapping("/password")
    public ResponseEntity<Object> changeUsername(@RequestBody String userId, @RequestBody String oldPassword, @RequestBody String newPassword) {

    }

    @PutMapping
    @RequestMapping("/screenname")
    public ResponseEntity<Object> changeUsername(@RequestBody String userId, @RequestBody String screenName) {

    }

    @PutMapping
    @RequestMapping("/profilepicture")
    public ResponseEntity<Object> changeUsername(@RequestBody String userId, @RequestBody String timestamp, @RequestBody byte[] blob) {

    }

}
