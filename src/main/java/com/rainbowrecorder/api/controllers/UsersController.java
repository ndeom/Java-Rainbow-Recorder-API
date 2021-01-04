package com.rainbowrecorder.api.controllers;

import com.rainbowrecorder.api.repositories.UserRepository;
import com.rainbowrecorder.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Object> checkUsernameAvailability(@RequestParam String username) {
        return userService.checkUsername(username);
    }

    @PostMapping
    @RequestMapping("/login")
    public ResponseEntity logIn(@RequestBody Map<String, String> body) {
        return userService.logIn(body.get("username"), body.get("password"));
    }

    @PostMapping
    @RequestMapping(value = "/register")
    public ResponseEntity<Object> register(@RequestBody Map<String, String> body) {
        return userService.register(body.get("username"), body.get("password"));
    }

    @PostMapping
    @RequestMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@RequestBody Map<String, String> body) {
        return userService.refresh(body.get("userID"), body.get("username"));
    }

    @PutMapping
    @RequestMapping("/username")
    public ResponseEntity<Object> changeUsername(@RequestBody Map<String, String> body) {
        return userService.changeUsername(body.get("userID"), body.get("newUsername"));
    }

    @PutMapping
    @RequestMapping("/password")
    public ResponseEntity<Object> changePassword(@RequestBody Map<String, String> body) {
        return userService.changePassword(body.get("userID"), body.get("oldPassword"), body.get("newPassword"));
    }

    @PutMapping
    @RequestMapping("/screenname")
    public ResponseEntity<Object> changeScreenName(@RequestBody Map<String, String> body) {
        return userService.changeScreenName(body.get("userID"), body.get("screenName"));
    }

    @PutMapping
    @RequestMapping("/profilepicture")
    public ResponseEntity<Object> changeProfilePicture(@RequestBody Map<String, String> body) {
        System.out.println("received photo change request");
        return userService.changeProfilePicture(body.get("userID"), body.get("timestamp"), body.get("blob"));
    }

}

