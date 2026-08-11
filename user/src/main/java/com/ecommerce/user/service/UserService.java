package com.ecommerce.user.service;

import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.model.UserPrincipal;
import com.ecommerce.user.model.UserRole;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    JwtUtil jwtUtil;

    public UserResponse mapToUserResponse(User u){
        UserResponse response=new UserResponse();
        response.setId(String.valueOf(u.getId()));
        response.setFirstName(u.getFirstName());
        response.setLastName(u.getLastName());
        response.setEmail(u.getEmail());
        response.setPhone(u.getPhone());
        response.setUserRole(u.getRole().get(0));
        AddressDTO addressDTO=new AddressDTO();
        addressDTO.setCity(u.getAddress().getCity());
        addressDTO.setState(u.getAddress().getState());
        addressDTO.setCountry(u.getAddress().getCountry());
        addressDTO.setZipcode(u.getAddress().getZipcode());
        addressDTO.setStreet(u.getAddress().getStreet());
        response.setAddress(addressDTO);
        return response;
    }
    public User mapToUser(UserRequest userRequest){
        User u=new User();
        u.setFirstName(userRequest.getFirstName());
        u.setLastName(userRequest.getLastName());
        u.setEmail(userRequest.getEmail());
        u.setPhone(userRequest.getPhone());
        u.setPassword(userRequest.getPassword());
        List<UserRole> lst=new ArrayList<>();
        UserRole r=new UserRole(userRequest.getRole());
        lst.add(r);
        u.setRole(lst);
        Address address=new Address();
        address.setCity(userRequest.getAddress().getCity());
        address.setState(userRequest.getAddress().getState());
        address.setCountry(userRequest.getAddress().getCountry());
        address.setZipcode(userRequest.getAddress().getZipcode());
        address.setStreet(userRequest.getAddress().getStreet());
        u.setAddress(address);
        return  u;
    }


    public List<UserResponse> getAllUsers(){
        List<User> u= userRepository.findAll();
        return u.stream().map(m->mapToUserResponse(m)).collect(Collectors.toList());
    }

    public ResponseEntity<?> addUser(UserRequest userRequest){
        System.out.println("Inside Service addUser");
        System.out.println(userRequest);
        try {
            User u=userRepository.findByEmail(userRequest.getEmail()).orElseThrow(()->new BadCredentialsException("ok"));
            UserPrincipal up= new UserPrincipal(u);
            Authentication man = authManager.authenticate(new UsernamePasswordAuthenticationToken(up.getUsername()
                                                                                                ,userRequest.getPassword()
                                                                                                ,up.getAuthorities()));

            if (man.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.OK).body(jwtUtil.generateToken(up));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please enter the correct details");
            }
        }
        catch (BadCredentialsException e) {
            // Handle failed authentication explicitly
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        catch (AuthenticationCredentialsNotFoundException e) {
            // Handle other authentication-related errors
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    public UserResponse getUser(String id) {
        Optional<User> u=userRepository.findById(id);
        if(u.isPresent()) return mapToUserResponse(u.get());
        return null;
    }

    public ResponseEntity<?> putUser(String id, UserRequest putUser, HttpServletRequest request) {
       Optional<User> u=userRepository.findById(id);
       if(u.isPresent()){
           String authHeader = request.getHeader("Authorization");

           if (authHeader == null || !authHeader.startsWith("Bearer ")) {
               return ResponseEntity.status(400).body("Token missing");
           }
           String token = authHeader.substring(7);
//           System.out.println( jwtUtil.extractUserName(token));
//           System.out.println( jwtUtil.extractUserName(u.get().getEmail()));
           if(jwtUtil.hasRole(token,"ADMIN") || jwtUtil.extractUserName(token).equals(u.get().getEmail())){
               u.get().setFirstName(putUser.getFirstName());
               u.get().setLastName(putUser.getLastName());
               u.get().setEmail(putUser.getEmail());
               Address address=new Address(putUser.getAddress().getStreet(),putUser.getAddress().getCity(),
                       putUser.getAddress().getState(),putUser.getAddress().getCountry(),
                       putUser.getAddress().getZipcode());
               u.get().setAddress(address);
               u.get().setPhone(putUser.getPhone());
               return ResponseEntity.status(HttpStatus.OK).body(mapToUserResponse(userRepository.save(u.get())));
           }

       }
       return ResponseEntity.status(400).body("Some issue while saving.");

    }
    public ResponseEntity<?> deleteUser(String id,HttpServletRequest request) {
        Optional<User> u=userRepository.findById(id);

        if(u.isPresent()){
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(400).body("Token missing");
            }
            String token = authHeader.substring(7);
            if(jwtUtil.hasRole(token,"ADMIN") || jwtUtil.extractUserName(token).equals(u.get().getEmail())){
                userRepository.delete(u.get());
                return ResponseEntity.status(HttpStatus.OK).body("Record deleted");
            }

        }
        return ResponseEntity.status(400).body("Some issue while saving.");
    }
    //Sign Up for Sellers and Customers
    public ResponseEntity<?> signup(UserRequest userRequest) {
        userRequest.setRole(userRequest.getRole().toUpperCase());
        String role=userRequest.getRole();
        if(role.equals("ADMIN")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Creation of Admin can't be done");
        }
        if(!role.equals("CUSTOMER") && !role.equals("SELLER")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide the Correct role");
        }
        User u=mapToUser(userRequest);
        userRepository.save(u);
        return  ResponseEntity.status(HttpStatus.OK).body(mapToUserResponse(u));
    }
    //Sign Up for Admins
    public ResponseEntity<?> signUpAdmin(UserRequest userRequest) {

        userRequest.setRole(userRequest.getRole().toUpperCase());
        String role=userRequest.getRole();
        if(!role.equals("ADMIN")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide the correct role.");
        }
        User u=mapToUser(userRequest);
        userRepository.save(u);
        return  ResponseEntity.status(HttpStatus.OK).body(mapToUserResponse(u));
    }
}
