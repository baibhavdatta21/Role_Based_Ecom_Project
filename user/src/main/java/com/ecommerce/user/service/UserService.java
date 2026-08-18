package com.ecommerce.user.service;

import com.ecommerce.user.controller.UserController;
import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.model.UserPrincipal;
import com.ecommerce.user.model.UserRole;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    private static final Logger logger= LoggerFactory.getLogger(UserService.class);
    public UserResponse mapToUserResponse(User u){
        logger.debug("Mapping User to UserResponse for userId: {}", u.getId());
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
        logger.debug("Mapping UserRequest to User for userId: {}", userRequest.getId());
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
        logger.info("mapping from UserRequest to User");
        return  u;
    }


    public List<UserResponse> getAllUsers(){
        List<User> u= userRepository.findAll();
        logger.info("Returning all the users fetched");
        return u.stream().map(m->mapToUserResponse(m)).collect(Collectors.toList());
    }

    public String addUser(UserRequest userRequest){
        logger.debug("Inside the AddUser Method");
        logger.info("Initiating the Authetication for the user:{}",userRequest.getEmail());
        try {
            User u=userRepository.findByEmail(userRequest.getEmail()).orElseThrow(()->{
                logger.warn("Login attempted for non-existent email: {}", userRequest.getEmail());
                throw new BadCredentialsException("Invalid username or password");
            });
            UserPrincipal up= new UserPrincipal(u);
            Authentication man = authManager.authenticate(new UsernamePasswordAuthenticationToken(up.getUsername()
                                                                                                ,userRequest.getPassword()
                                                                                                ,up.getAuthorities()));

            if (man.isAuthenticated()) {
                logger.info("Authentication Successfully generating the token");
                return jwtUtil.generateToken(up);
            } else {
                logger.warn("Autentication Failed:Invalid username or password ");
                 throw new BadCredentialsException("Invalid username or password");
            }
        }
        catch (BadCredentialsException e) {
            logger.warn("Autentication Failed:Invalid username or password ");
            throw new BadCredentialsException("Invalid username or password");
        }
        catch (AuthenticationCredentialsNotFoundException e) {
            logger.warn("Autentication Failed:Credentials not found");
            throw new AuthenticationCredentialsNotFoundException("Authentication failed");
        }
    }

    public UserResponse getUser(String id) {
        logger.info("Fetching the User with id:{}",id);
        Optional<User> u=userRepository.findById(id);
        if(u.isPresent()) return mapToUserResponse(u.get());
        logger.warn("No Entity Present with id:{}",id);
        throw new EntityNotFoundException("No such user Present");
    }

    public UserResponse putUser(String id, UserRequest putUser, HttpServletRequest request) {
        logger.info("Initiating the Update for the user id:{}",id);
       Optional<User> u=userRepository.findById(id);
       if(u.isPresent()){
           String authHeader = request.getHeader("Authorization");

           if (authHeader == null || !authHeader.startsWith("Bearer ")) {
               logger.warn("Autentication Failed:Credentials not found");
               throw new AuthenticationCredentialsNotFoundException("Authentication failed");
           }
           String token = authHeader.substring(7);
           logger.trace("Token generated successfully");
               if(jwtUtil.hasRole(token,"ADMIN") || jwtUtil.extractUserName(token).equals(u.get().getEmail())){
               u.get().setFirstName(putUser.getFirstName());
               u.get().setLastName(putUser.getLastName());
               u.get().setEmail(putUser.getEmail());
               Address address=new Address(putUser.getAddress().getStreet(),putUser.getAddress().getCity(),
                       putUser.getAddress().getState(),putUser.getAddress().getCountry(),
                       putUser.getAddress().getZipcode());
               u.get().setAddress(address);
               u.get().setPhone(putUser.getPhone());
               logger.info("User details update, returning the UserResponse");
               return mapToUserResponse(userRepository.save(u.get()));
           }

       }
        logger.warn("Access denied for userId: {}, requester does not have permission", id);
        throw new AccessDeniedException("You do not have permission to update this user");
    }
    public void deleteUser(String id,HttpServletRequest request) {
        logger.info("Initiating the Delete for the user id:{}",id);
        Optional<User> u=userRepository.findById(id);

        if(u.isPresent()){
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Autentication Failed:Credentials not found");
                throw new AuthenticationCredentialsNotFoundException("Authentication failed");
            }
            String token = authHeader.substring(7);
            if(jwtUtil.hasRole(token,"ADMIN") || jwtUtil.extractUserName(token).equals(u.get().getEmail())){
                userRepository.delete(u.get());
                logger.info("User has been deleted successfully");
                return;
            }

        }
        logger.warn("No Entity Present with id:{}",id);
        throw new EntityNotFoundException(id);
    }
    public UserResponse signup(UserRequest userRequest) {
        logger.info("Initiating the new user SignUp");
        userRequest.setRole(userRequest.getRole().toUpperCase());
        String role=userRequest.getRole();
        if(role.equals("ADMIN")){
            throw new AccessDeniedException("Only Admins can create another admin");
        }
        if(!role.equals("CUSTOMER") && !role.equals("SELLER")){
            throw new BadRequestException( "Provide the Correct role");
        }
        User u=mapToUser(userRequest);
        userRepository.save(u);
        logger.info("User SignUp complete returning the User Response");
        return mapToUserResponse(u);
    }
    //Sign Up for Admins
    public UserResponse signUpAdmin(UserRequest userRequest) {
        logger.info("Initiating the Signup for Admin users");
        userRequest.setRole(userRequest.getRole().toUpperCase());
        String role=userRequest.getRole();
        if(!role.equals("ADMIN")){
            logger.warn("New Admin sign Up cannot be performed as only admins can create new Admins");
            throw new AccessDeniedException("Only Admins can create another admin");
        }
        User u=mapToUser(userRequest);
        userRepository.save(u);
        logger.info("New Admin SignUp successfull, returning the user response");
        return  mapToUserResponse(u);
    }
}
