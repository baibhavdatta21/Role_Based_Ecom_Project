package com.ecommerce.user.util;

public class TestConstants {

    // ==================== User Data Constants ====================
    public static final String TEST_EMAIL = "goat@gmail.com";
    public static final String TEST_FIRST_NAME = "Leo";
    public static final String TEST_LAST_NAME = "Messi";
    public static final String TEST_PASSWORD = "wc2022";
    public static final String TEST_PHONE = "9999999999";
    public static final Integer TEST_USER_ID = 1;

    // ==================== Address Data Constants ====================
    public static final String TEST_STREET = "street";
    public static final String TEST_CITY = "city";
    public static final String TEST_STATE = "state";
    public static final String TEST_COUNTRY = "country";
    public static final String TEST_ZIPCODE = "700001";

    // ==================== Role Constants ====================
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String CUSTOMER_ROLE = "CUSTOMER";
    public static final String SELLER_ROLE = "SELLER";

    // ==================== JWT Token Constants ====================
//    public static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W3siYXV0aG9yaXR5IjoiQ1VTVE9NRVIifV0sInN1YiI6Imdvf@gmail.com","iat":1234567890,"exp":1234654290}";
    public static final String JWT_TOKEN =
            "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W3siYXV0aG9yaXR5IjoiQ1VTVE9NRVIifV0sInN1YiI6ImdvZkBnbWFpbC5jb20iLCJpYXQiOjEyMzQ1Njc4OTAsImV4cCI6MTIzNDY1NDI5MH0";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final String AUTH_HEADER = "Authorization";

    // ==================== HTTP Status Constants ====================
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_NOT_FOUND = 404;

    // ==================== Endpoint Constants ====================
    public static final String ENDPOINT_LOGIN = "api/public/users/login";
    public static final String ENDPOINT_SIGNUP = "api/public/users/signup";
    public static final String ENDPOINT_SIGNUP_ADMIN = "api/auth/users/signup-admin";
    public static final String ENDPOINT_GET_USERS = "api/auth/users";
    public static final String ENDPOINT_GET_USER_BY_ID = "api/auth/users/{id}";
    public static final String ENDPOINT_UPDATE_USER = "api/auth/users/{id}";
    public static final String ENDPOINT_DELETE_USER = "api/auth/users/{id}";

    // ==================== Error Messages Constants ====================
    public static final String ERROR_ADMIN_CREATION = "Creation of Admin can't be done";
    public static final String ERROR_INVALID_ROLE = "Provide the Correct role";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid username or password";
    public static final String ERROR_UNAUTHORIZED = "Unauthorized";
    public static final String ERROR_USER_NOT_FOUND = "User not found";
    public static final String ERROR_AUTHENTICATION_FAILED = "Authentication failed";
    public static final String ERROR_TOKEN_MISSING = "Token missing";

    // ==================== Success Messages Constants ====================
    public static final String MESSAGE_RECORD_DELETED = "Record deleted";
    public static final String MESSAGE_USER_CREATED = "User created successfully";

    private TestConstants() {
    throw new AssertionError("TestConstants class should not be instantiated");
    }
}

