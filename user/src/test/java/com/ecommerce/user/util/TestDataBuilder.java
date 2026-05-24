package com.ecommerce.user.util;

import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.model.UserPrincipal;
import com.ecommerce.user.model.UserRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;


import static com.ecommerce.user.util.TestConstants.*;
public class TestDataBuilder {

    private String email = TEST_EMAIL;
    private String firstName = TEST_FIRST_NAME;
    private String lastName = TEST_LAST_NAME;
    private String password = TEST_PASSWORD;
    private String phone = TEST_PHONE;
    private Integer userId = TEST_USER_ID;
    private String street = TEST_STREET;
    private String city = TEST_CITY;
    private String state = TEST_STATE;
    private String country = TEST_COUNTRY;
    private String zipcode = TEST_ZIPCODE;
    private String role = CUSTOMER_ROLE;

    // ==================== Builder Methods ====================

    public TestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public TestDataBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public TestDataBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public TestDataBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public TestDataBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public TestDataBuilder withUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public TestDataBuilder withRole(String role) {
        this.role = role;
        return this;
    }

    public TestDataBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public TestDataBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public TestDataBuilder withState(String state) {
        this.state = state;
        return this;
    }

    public TestDataBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public TestDataBuilder withZipcode(String zipcode) {
        this.zipcode = zipcode;
        return this;
    }

    // ==================== Build Methods ====================

    /**
     * Builds a valid User entity with default test values
     */
    public User buildValidUser() {
        User user = new User();
        user.setId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setRole(List.of(new UserRole(role)));
        user.setAddress(buildValidAddress());
        return user;
    }

    /**
     * Builds a valid UserRequest DTO with default test values
     */
    public UserRequest buildValidUserRequest() {
        UserRequest request = new UserRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);
        request.setPhone(phone);
        request.setRole(role);
        request.setAddress(buildValidAddressDTO());
        return request;
    }

    /**
     * Builds a valid UserResponse DTO with default test values
     */
    public UserResponse buildValidUserResponse() {
        UserResponse response = new UserResponse();
        response.setId(String.valueOf(userId));
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setEmail(email);
        response.setPhone(phone);
        response.setUserRole(new UserRole(role));
        response.setAddress(buildValidAddressDTO());
        return response;
    }

    /**
     * Builds a valid Address entity with default test values
     */
    public Address buildValidAddress() {
        return new Address(street, city, state, country, zipcode);
    }

    /**
     * Builds a valid AddressDTO with default test values
     */
    public AddressDTO buildValidAddressDTO() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet(street);
        addressDTO.setCity(city);
        addressDTO.setState(state);
        addressDTO.setCountry(country);
        addressDTO.setZipcode(zipcode);
        return addressDTO;
    }

    /**
     * Builds a valid UserPrincipal with default test values
     */
    public UserPrincipal buildValidUserPrincipal() {
        User user = buildValidUser();
        return new UserPrincipal(user);
    }

    /**
     * Builds a UserPrincipal with specific role
     */
    public UserPrincipal buildUserPrincipalWithRole(String role) {
        User user = buildValidUser();
        user.setRole(List.of(new UserRole(role)));
        return new UserPrincipal(user);
    }

    /**
     * Builds an admin user
     */
    public User buildAdminUser() {
        return new TestDataBuilder()
                .withRole(ADMIN_ROLE)
                .buildValidUser();
    }

    /**
     * Builds a customer user
     */
    public User buildCustomerUser() {
        return new TestDataBuilder()
                .withRole(CUSTOMER_ROLE)
                .buildValidUser();
    }

    /**
     * Builds a seller user
     */
    public User buildSellerUser() {
        return new TestDataBuilder()
                .withRole(SELLER_ROLE)
                .buildValidUser();
    }

    /**
     * Builds a user with no address
     */
    public User buildUserWithoutAddress() {
        User user = buildValidUser();
        user.setAddress(null);
        return user;
    }

    /**
     * Builds a user with null email
     */
    public User buildUserWithoutEmail() {
        User user = buildValidUser();
        user.setEmail(null);
        return user;
    }

    /**
     * Builds a minimal user with only required fields
     */
    public User buildMinimalUser() {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    /**
     * Builds a UserRequest with admin role
     */
    public UserRequest buildAdminUserRequest() {
        return new TestDataBuilder()
                .withRole(ADMIN_ROLE)
                .buildValidUserRequest();
    }

    /**
     * Builds a UserRequest with invalid role
     */
    public UserRequest buildUserRequestWithInvalidRole() {
        return new TestDataBuilder()
                .withRole("INVALID_ROLE")
                .buildValidUserRequest();
    }

    /**
     * Resets builder to default state
     */
    public void reset() {
        this.email = TEST_EMAIL;
        this.firstName = TEST_FIRST_NAME;
        this.lastName = TEST_LAST_NAME;
        this.password = TEST_PASSWORD;
        this.phone = TEST_PHONE;
        this.userId = TEST_USER_ID;
        this.street = TEST_STREET;
        this.city = TEST_CITY;
        this.state = TEST_STATE;
        this.country = TEST_COUNTRY;
        this.zipcode = TEST_ZIPCODE;
        this.role = CUSTOMER_ROLE;
    }
}
