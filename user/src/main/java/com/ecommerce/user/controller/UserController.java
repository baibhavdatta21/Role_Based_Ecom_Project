package com.ecommerce.user.controller;

import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.User;
import com.ecommerce.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger= LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    @GetMapping("api/auth/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        logger.info("Get request for the list of all users received");
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
    @GetMapping("api/auth/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id){
        logger.info("Get request for the user with id:{}, received",id);
        UserResponse u= userService.getUser(id);
        return ResponseEntity.status(200).body(u);
    }
    @PostMapping("api/public/users/login")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest userRequest){
        logger.info("Post request for the Login for user:{}, received",userRequest.getEmail());
        return ResponseEntity.status(200).body(userService.addUser(userRequest));
    }
    @PostMapping("api/public/users/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserRequest userRequest){
        logger.info("Post request for the SignUp for user:{}, who is a non admin, received",userRequest.getEmail());
        UserResponse userResponse=userService.signup(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
    @PostMapping("api/auth/users/signup-admin")
    public ResponseEntity<?> signUpAdmin(@Valid @RequestBody UserRequest userRequest){
        logger.info("Post request for the SignUp for new user admin:{}, received",userRequest.getEmail());
        UserResponse userResponse= userService.signUpAdmin(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
    @PutMapping("api/auth/users/{id}")
    public ResponseEntity<?> putUser(@PathVariable String id,@Valid @RequestBody UserRequest user, HttpServletRequest request){
        logger.info("Put request for the update for user with id:{}, received",id);
        UserResponse userResponse=userService.putUser(id,user,request);
        return ResponseEntity.ok(userResponse);
    }
    @DeleteMapping("api/auth/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id,HttpServletRequest request){
        logger.info("Delete request the user with id:{} received",id);
        userService.deleteUser(id,request);
        return ResponseEntity.noContent().build();
    }
}