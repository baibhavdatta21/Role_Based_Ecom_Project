package com.ecommerce.user.controller;

import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping("api/auth/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
    @GetMapping("api/auth/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id){
        UserResponse u= userService.getUser(id);
        if(u!=null){
            return ResponseEntity.status(200).body(u);
        }
        return ResponseEntity.status(200).body(null);
    }
    @PostMapping("api/public/users/login")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest){
        System.out.println("received");
        return userService.addUser(userRequest);
    }
    @PostMapping("api/public/users/signup")
    public ResponseEntity<?> signUp(@RequestBody UserRequest userRequest){
        return userService.signup(userRequest);
    }
    @PostMapping("api/auth/users/signup-admin")
    public ResponseEntity<?> signUpAdmin(@RequestBody UserRequest userRequest){
        return userService.signUpAdmin(userRequest);
    }
    @PutMapping("api/auth/users/{id}")
    public ResponseEntity<?> putUser(@PathVariable String id, @RequestBody UserRequest user, HttpServletRequest request){
        return userService.putUser(id,user,request);
    }
    @DeleteMapping("api/auth/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id,HttpServletRequest request){
        return userService.deleteUser(id,request);
    }
}
