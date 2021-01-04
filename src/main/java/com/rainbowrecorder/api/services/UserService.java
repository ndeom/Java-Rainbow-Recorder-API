package com.rainbowrecorder.api.services;

import com.rainbowrecorder.api.config.SecurityConfig;
import com.rainbowrecorder.api.constants.ResponseTypes;
import com.rainbowrecorder.api.models.User;
import com.rainbowrecorder.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AmazonClient amazonClient;

    public ResponseEntity<Object> checkUsername(String username) {
        try {
            var response = checkUsernameAsync(username);
            var users = response.get();
            var responseBody = new HashMap<String, Object>();
            if (users.size() == 0) {
                responseBody.put(ResponseTypes.MESSAGE.label, "Username available.");
            } else {
                responseBody.put(ResponseTypes.ERROR.label, "Username already taken.");
            }
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>();
            errResponse.put(ResponseTypes.ERROR.label, String.format("Error while checking username: %s", Arrays.toString(err.getStackTrace())));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errResponse);

        }
    }

    @Async
    private CompletableFuture<List<User>> checkUsernameAsync(String username) {
        List<User> existingUsers = userRepository.findByUsername(username);
        return CompletableFuture.completedFuture(existingUsers);
    }

    public ResponseEntity logIn(String username, String password) {
        try {
            var response = checkUsernameAsync(username);
            var users = response.get();
            var responseBody = new HashMap<String, Object>();

            // Check that there are non-zero number of users with the submitted username
            if (users.size() == 0) {
                responseBody.put(ResponseTypes.ERROR.label, "User does not exist. Please register for an account to log in.");
                return ResponseEntity.status(HttpStatus.OK).body(responseBody);
            }

            ApplicationContext context = new AnnotationConfigApplicationContext(SecurityConfig.class);
            BCryptPasswordEncoder passwordEncoder = (BCryptPasswordEncoder) context.getBean("passwordEncoder");

            // Check that the provided password matches the hash stored on the db
            if (!passwordEncoder.matches(password, users.get(0).getHash())) {
                System.out.println("Passwords did not match!!!");
                responseBody.put(ResponseTypes.ERROR.label, "Password did not match. Please try again.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            }

            responseBody.put("userInfo", users.get(0));

            // Create token and set header
            var token = jwtService.createToken(users.get(0)).get();
            var headers = jwtService.setAndReturnAuthorizationHeader(token);

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(responseBody);
        } catch(InterruptedException | ExecutionException | IllegalArgumentException err) {
            System.out.printf("Error while logging in : %s", err);
            var errResponse = new HashMap<String, String>() {{
                put(ResponseTypes.ERROR.label, String.format("Error while checking username: %s", Arrays.toString(err.getStackTrace())));
            }};
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errResponse);
        }
    }

    public ResponseEntity register(String username, String password) {
        try {
            System.out.println("registering user");
            var response = checkUsernameAsync(username);
            var users = response.get();
            var responseBody = new HashMap<String, Object>();

            // If there are a nonzero number of users, the username is taken
            if (users.size() != 0) {
                responseBody.put(ResponseTypes.ERROR.label, "Username is already in use. Please try again.");
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(responseBody);
            }

            ApplicationContext context = new AnnotationConfigApplicationContext(SecurityConfig.class);
            BCryptPasswordEncoder passwordEncoder = (BCryptPasswordEncoder) context.getBean("passwordEncoder");

            // Generate the password hash
            var hash = passwordEncoder.encode(password);

            // Send response with user information and token
            var registerResponse = registerAsync(username, hash);
            var user = registerResponse.get();
            responseBody.put(ResponseTypes.MESSAGE.label, "User successfully registered.");
            responseBody.put("userInfo", new HashMap<String, Object>() {{
                put("user_id", user.getUser_id());
                put("username", user.getUsername());
                put("profilePicture", user.getProfile_picture());
                put("screenName", user.getScreen_name());
            }});

            // Create token and set header
            var token = jwtService.createToken(user).get();
            var headers = jwtService.setAndReturnAuthorizationHeader(token);

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(responseBody);
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>() {{
                put(ResponseTypes.ERROR.label, String.format("Error while registering user: %s", Arrays.toString(err.getStackTrace())));
            }};
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errResponse);
        }
    }

    private CompletableFuture<User> registerAsync(String username, String hash) {
        User user = new User();
        user.setUsername(username);
        user.setHash(hash);
        return CompletableFuture.completedFuture(userRepository.saveAndFlush(user));
    }

    public ResponseEntity refresh(String userId, String username) {
        try {
            var response = lookUpByIdAsync(userId);
            var user = response.get();
            var token = jwtService.createToken(user).get();
            var headers = jwtService.setAndReturnAuthorizationHeader(token);
            var userInfo = new HashMap<String, Object>() {{
                put("user_id", user.getUser_id());
                put("username", user.getUsername());
                put("profilePicture", user.getProfile_picture());
                put("screenName", user.getScreen_name());
            }};
            return ResponseEntity.ok().headers(headers).body(
                    new HashMap<String, Object> () {{
                        put(ResponseTypes.MESSAGE.label, "User successfully logged in.");
                        put("userInfo", userInfo);
                    }}
            );
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>() {{
                put(ResponseTypes.ERROR.label, String.format("Error while refreshing token: %s", Arrays.toString(err.getStackTrace())));
            }};
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errResponse);
        }
    }

    private CompletableFuture<User> lookUpByIdAsync(String userId) {
        return CompletableFuture.completedFuture(userRepository.findById(userId).get());
    }

    public ResponseEntity changeUsername(String userId, String username) {
        try {
            var response = updateUsernameAndReturnUserAsync(userId, username);
            var user = response.get();
            return ResponseEntity.ok().body(
                    new HashMap<String, Object>() {{
                        put(ResponseTypes.MESSAGE.label, "Username successfully updated.");
                        put("userInfo", user);
                    }}
            );
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>() {{
                put(ResponseTypes.ERROR.label, String.format("Error while changing username: %s", Arrays.toString(err.getStackTrace())));
            }};
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errResponse);
        }
    }

    private CompletableFuture<User> updateUsernameAndReturnUserAsync(String userId, String username) {
        userRepository.updateUsername(userId, username);
        return CompletableFuture.completedFuture(userRepository.findById(userId).get());
    }

    public ResponseEntity changePassword(String userId, String oldPassword, String newPassword) {
        try {
            var response = lookUpByIdAsync(userId);
            var user = response.get();

            ApplicationContext context = new AnnotationConfigApplicationContext(SecurityConfig.class);
            BCryptPasswordEncoder passwordEncoder = (BCryptPasswordEncoder) context.getBean("passwordEncoder");

            if (!passwordEncoder.matches(oldPassword, user.getHash())) {
                return ResponseEntity.ok().body(new HashMap<String, String>() {{
                    put(ResponseTypes.ERROR.label, "Password did not match existing record.");
                }});
            }

            var newHash = passwordEncoder.encode(newPassword);
            var updateResponse = updatePasswordAsync(userId, newHash);
            updateResponse.get();
            return ResponseEntity.ok().body(
                    new HashMap<String, Object>() {{ put(ResponseTypes.MESSAGE.label, "Username successfully updated."); }}
            );
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>() {{
                put(ResponseTypes.ERROR.label, String.format("Error while changing password: %s", Arrays.toString(err.getStackTrace())));
            }};
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errResponse);
        }
    }

    private CompletableFuture<Void> updatePasswordAsync(String userId, String newHash) {
        userRepository.updatePassword(userId, newHash);
        return CompletableFuture.completedFuture(null);
    }

    public ResponseEntity changeScreenName(String userId, String screenName) {
        try {
            var response = updateScreenNameAsync(userId, screenName);
            response.get();
            return ResponseEntity.ok().body(
                    new HashMap<String, Object>() {{
                        put(ResponseTypes.MESSAGE.label, "Screen name successfully updated.");
                    }}
            );
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>() {{
                put(ResponseTypes.ERROR.label, String.format("Error while changing screen name: %s", Arrays.toString(err.getStackTrace())));
            }};
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errResponse);
        }
    }

    private CompletableFuture<Void> updateScreenNameAsync(String userId, String screenName) {
        userRepository.updateScreenName(userId, screenName);
        return CompletableFuture.completedFuture(null);
    }

    public ResponseEntity changeProfilePicture(String userId, String timestamp, String image) {
        try {

            // Check to see if user already has profile picture
            var currentProfilePic = userRepository.getExistingProfilePicture(userId);

            // If there is already a profile picture, delete the object from S3 storage
            currentProfilePic.ifPresent(file -> amazonClient.deleteFileFromS3Bucket(file));

            var imageUrl = amazonClient.uploadImage(userId, timestamp, image);

            if (imageUrl.isEmpty()) {
                System.out.println("Image url was empty");
                throw new Exception("Error while uploading image.");
            }

            var response = updateProfilePictureAsync(userId, imageUrl.get());
            response.get();
            return ResponseEntity.ok().body(
                    new HashMap<String, String>() {{
                        put(ResponseTypes.MESSAGE.label, "User profile picture successfully changed.");
                    }}
            );
        } catch(Exception err) {
            System.out.println("Catch block exception");
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new HashMap<String, String>() {{
                        put(ResponseTypes.ERROR.label, String.format("Error while changing profile picture: %s", Arrays.toString(err.getStackTrace())));
                    }}
            );
        }
    }

    private CompletableFuture<Void> updateProfilePictureAsync(String userId, String imageUrl) {
        userRepository.updateProfilePicture(userId, imageUrl);
        return CompletableFuture.completedFuture(null);
    }

}
