package com.rainbowrecorder.api.services;

import com.rainbowrecorder.api.constants.ResponseTypes;
import com.rainbowrecorder.api.models.User;
import com.rainbowrecorder.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Object> checkUsername(String username) {
        try {
            var response = checkUsernameAsync(username);
            var users = response.get();
            var responseBody = new HashMap<String, Object>();
            if (users.size() == 0) {
                responseBody.put(ResponseTypes.MESSAGE.label, "Username available.");
            } else {
                responseBody.put(ResponseTypes.MESSAGE.label, "Username already taken.");
            }
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>();
            errResponse.put(ResponseTypes.ERROR.label, String.format("Error while checking username: %s", Arrays.toString(err.getStackTrace())));
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Async
    private CompletableFuture<List<User>> checkUsernameAsync(String username) {
        List<User> existingUsers = userRepository.findByUsername(username);
        return CompletableFuture.completedFuture(existingUsers);
    }

    public ResponseEntity<Object> logIn(String username, String password) {
        try {
            var response = checkUsernameAsync(username);
            var users = response.get();
            var responseBody = new HashMap<String, Object>();
            if (users.size() == 0) {
                responseBody.put(ResponseTypes.ERROR.label, "User does not exist. Please register fro an account to log in.");
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }

            if (!BCrypt.checkpw(password, users.get(0).getHash())) {
                responseBody.put(ResponseTypes.ERROR.label, "Password did not match. Please try again.");
                return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
            }



            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>();
            errResponse.put(ResponseTypes.ERROR.label, String.format("Error while checking username: %s", Arrays.toString(err.getStackTrace())));
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
        }
    }


}
