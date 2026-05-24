package com.ecommerce.user.service;

import com.ecommerce.user.model.User;
import com.ecommerce.user.model.UserPrincipal;
import com.ecommerce.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsService2 implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u=userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("not found"));
        return new UserPrincipal(u);
    }

}
