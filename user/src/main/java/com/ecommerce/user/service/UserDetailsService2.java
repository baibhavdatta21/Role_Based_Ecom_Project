package com.ecommerce.user.service;

import com.ecommerce.user.model.User;
import com.ecommerce.user.model.UserPrincipal;
import com.ecommerce.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsService2 implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    private static final Logger logger= LoggerFactory.getLogger(UserService.class);
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u=userRepository.findByEmail(username).orElseThrow(()-> {
            logger.warn("UserDetailsService: user not found for username: {}", username);
           throw  new UsernameNotFoundException("not found");
        });
        return new UserPrincipal(u);
    }

}
