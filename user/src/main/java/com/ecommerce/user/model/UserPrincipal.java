package com.ecommerce.user.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    User user;
    private Set<UserRole> authorities = new HashSet<>();
    public UserPrincipal(User user) {
        this.user=user;
        List<UserRole> hold=user.getRole();
        if(hold!=null) {
            for(UserRole h:hold) {
                authorities.add(h);
            }
        }

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return user.getEmail();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true; // Assume account is not expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Assume account is not locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Assume credentials are not expired
    }

    @Override
    public boolean isEnabled() {
        return true; // Assume user is enabled
    }


}
