package com.ecommerce.user.service;

import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.model.UserRole;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.JwtUtil;
import com.ecommerce.user.service.UserService;
import com.ecommerce.user.util.TestDataBuilder;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import static com.ecommerce.user.util.TestConstants.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.user.util.TestConstants.JWT_TOKEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequest userRequest;
    private TestDataBuilder testDataBuilder;
    private User testUser;
    private UserRequest testUserRequest;
    private Address testAddress;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(userService, "authManager", authManager);
        ReflectionTestUtils.setField(userService, "jwtUtil", jwtUtil);

        Address address = new Address(
                "street",
                "city",
                "state",
                "country",
                "700001"
        );

        UserRole role = new UserRole("CUSTOMER");

        user = new User();
        user.setId(1);
        user.setFirstName("Leo");
        user.setLastName("Messi");
        user.setEmail("goat@gmail.com");
        user.setPassword("wc2022");
        user.setPhone("9999999999");
        user.setAddress(address);
        user.setRole(List.of(role));

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet("street");
        addressDTO.setCity("city");
        addressDTO.setState("state");
        addressDTO.setCountry("country");
        addressDTO.setZipcode("700001");

        userRequest = new UserRequest();
        userRequest.setFirstName("Leo");
        userRequest.setLastName("Messi");
        userRequest.setEmail("goat@gmail.com");
        userRequest.setPassword("wc2022");
        userRequest.setPhone("9999999999");
        userRequest.setRole("CUSTOMER");
        userRequest.setAddress(addressDTO);
    }

    @Test
    @Order(1)
    void signupSuccess() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserResponse response = userService.signup(userRequest);
        assertEquals(userRequest.getEmail(),response.getEmail());
        verify(userRepository, times(1))
                .save(any(User.class));
    }
    @Test
    @Order(2)
    void signupFailureInvalidRole() {
        userRequest.setRole("ADMIN");
        assertThrows(AccessDeniedException.class, ()->userService.signup(userRequest));
    }
    @Test
    @Order(3)
    void signupAdminSuccess(){
        userRequest.setRole("ADMIN");
        UserResponse response = userService.signUpAdmin(userRequest);
        assertEquals(response.getEmail(), response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    @Order(4)
    void addUser_ShouldReturnJwt_WhenAuthenticated() {

        Authentication authentication = mock(Authentication.class);

        when(userRepository.findByEmail("goat@gmail.com")).thenReturn(Optional.of(user));
        when(authManager.authenticate(any())) .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(jwtUtil.generateToken(any()))
                .thenReturn("jwt-token");

        String response =
                userService.addUser(userRequest);;

        assertEquals("jwt-token", response);
    }

    @Test
    @Order(5)
    void getAllUsersSuccess(){
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserResponse> response=userService.getAllUsers();
        assertEquals(1,response.size());
        assertEquals("goat@gmail.com", response.get(0).getEmail());
    }
    @Test
    @Order(6)
    void getUserSuccess(){
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        UserResponse response = userService.getUser("1");
        assertEquals("Leo", response.getFirstName());
        verify(userRepository, times(1)).findById("1");
    }
    @Test
    @Order(7)
    void putUserSuccess(){
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(request.getHeader("Authorization")).thenReturn("Bearer jwt-token");
        when(jwtUtil.hasRole("jwt-token","ADMIN")).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        UserResponse userResponse=userService.putUser("1",userRequest,request);
        assertEquals(userRequest.getEmail(),userResponse.getEmail() );
    }
    @Test
    @Order(8)
    void deleteUserSuccess(){
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.hasRole("token","ADMIN")).thenReturn(true);
        userService.deleteUser("1", request);

        verify(userRepository, times(1)).delete(user);
    }
    @Test
    @Order(9)
    void deleteUser_ShouldFail_WhenUnauthorized() {

        when(userRepository.findById("1"))
                .thenReturn(Optional.of(user));

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer token");

        when(jwtUtil.hasRole("token", "ADMIN"))
                .thenReturn(false);

        when(jwtUtil.extractUserName("token")).thenReturn("another@gmail.com");

       assertThrows(EntityNotFoundException.class,()->userService.deleteUser("1",request));
    }
    @BeforeEach
    @DisplayName("Initialize test data and mocks before each test")
    void setUp() {
        testDataBuilder = new TestDataBuilder();
        testUser = testDataBuilder.buildValidUser();
        testUserRequest = testDataBuilder.buildValidUserRequest();
        testAddress = testDataBuilder.buildValidAddress();
    }
//
//    // ==================== Signup Tests ====================
//
//    @Nested
//    @DisplayName("Signup Method Tests")
//    class SignupTests {
//
//        @Test
//        @DisplayName("Should signup customer successfully with valid request")
//        void testSignupCustomerSuccess() {
//            // Arrange
//            testUserRequest.setRole(CUSTOMER_ROLE);
//            when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//            // Act
//            ResponseEntity<?> response = userService.signup(testUserRequest);
//
//            // Assert
//            assertNotNull(response);
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertTrue(response.getBody() instanceof UserResponse);
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should signup seller successfully with valid request")
//        void testSignupSellerSuccess() {
//            // Arrange
//            testUserRequest.setRole(SELLER_ROLE);
//            when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//            // Act
//            ResponseEntity<?> response = userService.signup(testUserRequest);
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should fail signup when attempting to create admin account")
//        void testSignupFailureAdminRole() {
//            // Arrange
//            testUserRequest.setRole(ADMIN_ROLE);
//
//            // Act
//            ResponseEntity<?> response = userService.signup(testUserRequest);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//            assertTrue(response.getBody().toString()
//                    .contains("Creation of Admin can't be done"));
//            verify(userRepository, never()).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should fail signup with invalid role")
//        void testSignupFailureInvalidRole() {
//            // Arrange
//            testUserRequest.setRole("INVALID_ROLE");
//
//            // Act
//            ResponseEntity<?> response = userService.signup(testUserRequest);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//            assertTrue(response.getBody().toString()
//                    .contains("Provide the Correct role"));
//            verify(userRepository, never()).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should convert role to uppercase before validation")
//        void testSignupRoleConversion() {
//            // Arrange
//            testUserRequest.setRole("customer");
//            when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//            // Act
//            ResponseEntity<?> response = userService.signup(testUserRequest);
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @ParameterizedTest
//        @ValueSource(strings = {"customer", "CUSTOMER", "Customer"})
//        @DisplayName("Should handle various case formats for role validation")
//        void testSignupRoleCaseInsensitive(String role) {
//            // Arrange
//            testUserRequest.setRole(role);
//            when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//            // Act
//            ResponseEntity<?> response = userService.signup(testUserRequest);
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//        }
//    }
//
//    // ==================== Admin Signup Tests ====================
//
//    @Nested
//    @DisplayName("Admin Signup Method Tests")
//    class AdminSignupTests {
//
//        @Test
//        @DisplayName("Should signup admin successfully with valid admin role")
//        void testSignupAdminSuccess() {
//            // Arrange
//            testUserRequest.setRole(ADMIN_ROLE);
//            when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//            // Act
//            ResponseEntity<?> response = userService.signUpAdmin(testUserRequest);
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertTrue(response.getBody() instanceof UserResponse);
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should fail admin signup with non-admin role")
//        void testSignupAdminFailureNonAdminRole() {
//            // Arrange
//            testUserRequest.setRole(CUSTOMER_ROLE);
//
//            // Act
//            ResponseEntity<?> response = userService.signUpAdmin(testUserRequest);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//            assertTrue(response.getBody().toString()
//                    .contains("Please provide the correct role"));
//            verify(userRepository, never()).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should fail admin signup with seller role")
//        void testSignupAdminFailureSellerRole() {
//            // Arrange
//            testUserRequest.setRole(SELLER_ROLE);
//
//            // Act
//            ResponseEntity<?> response = userService.signUpAdmin(testUserRequest);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//            verify(userRepository, never()).save(any(User.class));
//        }
//    }
//
//    // ==================== Authentication Tests ====================
//
//    @Nested
//    @DisplayName("User Authentication Tests (addUser)")
//    class AuthenticationTests {
//
//        @Test
//        @DisplayName("Should authenticate user and generate JWT token successfully")
//        void testAddUserAuthenticationSuccess() {
//            // Arrange
//            Authentication authentication = mock(Authentication.class);
//            String expectedToken = JWT_TOKEN;
//
//            when(userRepository.findByEmail(TEST_EMAIL))
//                    .thenReturn(Optional.of(testUser));
//            when(authManager.authenticate(any()))
//                    .thenReturn(authentication);
//            when(authentication.isAuthenticated()).thenReturn(true);
//            when(jwtUtil.generateToken(any())).thenReturn(expectedToken);
//
//            // Act
//            ResponseEntity<?> response = userService.addUser(testUserRequest);
//
//            // Assert
//            assertNotNull(response);
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertEquals(expectedToken, response.getBody());
//            verify(authManager, times(1)).authenticate(any());
//            verify(jwtUtil, times(1)).generateToken(any());
//        }
//
//        @Test
//        @DisplayName("Should fail authentication when user not found")
//        void testAddUserAuthenticationFailureUserNotFound() {
//            // Arrange
//            when(userRepository.findByEmail(anyString()))
//                    .thenReturn(Optional.empty());
//
//            // Act
//            ResponseEntity<?> response = userService.addUser(testUserRequest);
//
//            // Assert
//            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//            assertTrue(response.getBody().toString()
//                    .contains("Invalid username or password"));
//            verify(jwtUtil, never()).generateToken(any());
//        }
//
//        @Test
//        @DisplayName("Should fail authentication when credentials are invalid")
//        void testAddUserAuthenticationFailureInvalidCredentials() {
//            // Arrange
//            when(userRepository.findByEmail(TEST_EMAIL))
//                    .thenReturn(Optional.of(testUser));
//            when(authManager.authenticate(any()))
//                    .thenThrow(new BadCredentialsException("Invalid credentials"));
//
//            // Act
//            ResponseEntity<?> response = userService.addUser(testUserRequest);
//
//            // Assert
//            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//            assertTrue(response.getBody().toString()
//                    .contains("Invalid username or password"));
//        }
//
//        @Test
//        @DisplayName("Should fail authentication when authentication not confirmed")
//        void testAddUserAuthenticationFailureNotAuthenticated() {
//            // Arrange
//            Authentication authentication = mock(Authentication.class);
//            when(userRepository.findByEmail(TEST_EMAIL))
//                    .thenReturn(Optional.of(testUser));
//            when(authManager.authenticate(any())).thenReturn(authentication);
//            when(authentication.isAuthenticated()).thenReturn(false);
//
//            // Act
//            ResponseEntity<?> response = userService.addUser(testUserRequest);
//
//            // Assert
//            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        }
//    }
//
//    // ==================== Get User Tests ====================
//
//    @Nested
//    @DisplayName("Get User Tests")
//    class GetUserTests {
//
//        @Test
//        @DisplayName("Should retrieve user by id successfully")
//        void testGetUserSuccess() {
//            // Arrange
//            String userId = "1";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//
//            // Act
//            UserResponse response = userService.getUser(userId);
//
//            // Assert
//            assertNotNull(response);
//            assertEquals(TEST_FIRST_NAME, response.getFirstName());
//            assertEquals(TEST_EMAIL, response.getEmail());
//            verify(userRepository, times(1)).findById(userId);
//        }
//
//        @Test
//        @DisplayName("Should return null when user not found by id")
//        void testGetUserNotFound() {
//            // Arrange
//            String userId = "999";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.empty());
//
//            // Act
//            UserResponse response = userService.getUser(userId);
//
//            // Assert
//            assertNull(response);
//            verify(userRepository, times(1)).findById(userId);
//        }
//
//        @Test
//        @DisplayName("Should map user entity to response correctly")
//        void testGetUserMappingCorrect() {
//            // Arrange
//            when(userRepository.findById("1"))
//                    .thenReturn(Optional.of(testUser));
//
//            // Act
//            UserResponse response = userService.getUser("1");
//
//            // Assert
//            assertNotNull(response);
//            assertEquals(testUser.getFirstName(), response.getFirstName());
//            assertEquals(testUser.getLastName(), response.getLastName());
//            assertEquals(testUser.getEmail(), response.getEmail());
//            assertEquals(testUser.getPhone(), response.getPhone());
//            assertNotNull(response.getAddress());
//        }
//    }
//
//    // ==================== Get All Users Tests ====================
//
//    @Nested
//    @DisplayName("Get All Users Tests")
//    class GetAllUsersTests {
//
//        @Test
//        @DisplayName("Should retrieve all users successfully")
//        void testGetAllUsersSuccess() {
//            // Arrange
//            List<User> users = List.of(testUser, testDataBuilder.buildValidUser());
//            when(userRepository.findAll()).thenReturn(users);
//
//            // Act
//            List<UserResponse> responses = userService.getAllUsers();
//
//            // Assert
//            assertNotNull(responses);
//            assertEquals(2, responses.size());
//            verify(userRepository, times(1)).findAll();
//        }
//
//        @Test
//        @DisplayName("Should return empty list when no users exist")
//        void testGetAllUsersEmptyList() {
//            // Arrange
//            when(userRepository.findAll()).thenReturn(Collections.emptyList());
//
//            // Act
//            List<UserResponse> responses = userService.getAllUsers();
//
//            // Assert
//            assertNotNull(responses);
//            assertTrue(responses.isEmpty());
//            assertEquals(0, responses.size());
//        }
//
//        @Test
//        @DisplayName("Should map all users correctly")
//        void testGetAllUsersMappingCorrect() {
//            // Arrange
//            User user2 = testDataBuilder.buildValidUser();
//            user2.setEmail("different@email.com");
//            List<User> users = List.of(testUser, user2);
//            when(userRepository.findAll()).thenReturn(users);
//
//            // Act
//            List<UserResponse> responses = userService.getAllUsers();
//
//            // Assert
//            assertEquals(2, responses.size());
//            assertEquals(testUser.getEmail(), responses.get(0).getEmail());
//            assertEquals(user2.getEmail(), responses.get(1).getEmail());
//        }
//    }
//
//    // ==================== Update User Tests ====================
//
//    @Nested
//    @DisplayName("Update User (PUT) Tests")
//    class UpdateUserTests {
//
//        @Test
//        @DisplayName("Should update user successfully with ADMIN role")
//        void testPutUserSuccessAsAdmin() {
//            // Arrange
//            String userId = "1";
//            String token = "valid-token";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//            when(request.getHeader("Authorization"))
//                    .thenReturn("Bearer " + token);
//            when(jwtUtil.hasRole(token, ADMIN_ROLE)).thenReturn(true);
//            when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//            // Act
//            ResponseEntity<?> response = userService.putUser(userId, testUserRequest, request);
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertTrue(response.getBody() instanceof UserResponse);
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should update user successfully as the same user")
//        void testPutUserSuccessAsOwner() {
//            // Arrange
//            String userId = "1";
//            String token = "valid-token";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//            when(request.getHeader("Authorization"))
//                    .thenReturn("Bearer " + token);
//            when(jwtUtil.hasRole(token, ADMIN_ROLE)).thenReturn(false);
//            when(jwtUtil.extractUserName(token)).thenReturn(TEST_EMAIL);
//            when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//            // Act
//            ResponseEntity<?> response = userService.putUser(userId, testUserRequest, request);
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should fail update when user not found")
//        void testPutUserFailureUserNotFound() {
//            // Arrange
//            String userId = "999";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.empty());
//
//            // Act
//            ResponseEntity<?> response = userService.putUser(userId, testUserRequest, request);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//            verify(userRepository, never()).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should fail update when authorization header is missing")
//        void testPutUserFailureNoAuthorizationHeader() {
//            // Arrange
//            String userId = "1";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//            when(request.getHeader("Authorization")).thenReturn(null);
//
//            // Act
//            ResponseEntity<?> response = userService.putUser(userId, testUserRequest, request);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//            verify(userRepository, never()).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should fail update when authorization header format is invalid")
//        void testPutUserFailureInvalidAuthorizationFormat() {
//            // Arrange
//            String userId = "1";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//            when(request.getHeader("Authorization")).thenReturn("InvalidFormat token");
//
//            // Act
//            ResponseEntity<?> response = userService.putUser(userId, testUserRequest, request);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        }
//
//        @Test
//        @DisplayName("Should fail update when user lacks authorization")
//        void testPutUserFailureUnauthorized() {
//            // Arrange
//            String userId = "1";
//            String token = "valid-token";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//            when(request.getHeader("Authorization"))
//                    .thenReturn("Bearer " + token);
//            when(jwtUtil.hasRole(token, ADMIN_ROLE)).thenReturn(false);
//            when(jwtUtil.extractUserName(token)).thenReturn("different@email.com");
//
//            // Act
//            ResponseEntity<?> response = userService.putUser(userId, testUserRequest, request);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//            verify(userRepository, never()).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should update user fields correctly")
//        void testPutUserFieldsUpdated() {
//            // Arrange
//            String userId = "1";
//            String token = "valid-token";
//            UserRequest updateRequest = new UserRequest();
//            updateRequest.setFirstName("UpdatedName");
//            updateRequest.setLastName("UpdatedLast");
//            updateRequest.setEmail("updated@email.com");
//            updateRequest.setPhone("9876543210");
//            updateRequest.setAddress(testDataBuilder.buildValidAddressDTO());
//
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//            when(request.getHeader("Authorization"))
//                    .thenReturn("Bearer " + token);
//            when(jwtUtil.hasRole(token, ADMIN_ROLE)).thenReturn(true);
//            when(userRepository.save(any(User.class)))
//                    .thenAnswer(invocation -> invocation.getArgument(0));
//
//            // Act
//            ResponseEntity<?> response = userService.putUser(userId, updateRequest, request);
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
//            verify(userRepository).save(userCaptor.capture());
//            User savedUser = userCaptor.getValue();
//            assertEquals("UpdatedName", savedUser.getFirstName());
//            assertEquals("UpdatedLast", savedUser.getLastName());
//        }
//    }
//
//    // ==================== Delete User Tests ====================
//
//    @Nested
//    @DisplayName("Delete User Tests")
//    class DeleteUserTests {
//
//        @Test
//        @DisplayName("Should delete user successfully with ADMIN role")
//        void testDeleteUserSuccessAsAdmin() {
//            // Arrange
//            String userId = "1";
//            String token = "valid-token";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//            when(request.getHeader("Authorization"))
//                    .thenReturn("Bearer " + token);
//            when(jwtUtil.hasRole(token, ADMIN_ROLE)).thenReturn(true);
//
//            // Act
//            ResponseEntity<?> response = userService.deleteUser(userId, request);
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            verify(userRepository, times(1)).delete(testUser);
//        }
//
//        @Test
//        @DisplayName("Should delete user successfully as the same user")
//        void testDeleteUserSuccessAsOwner() {
//            // Arrange
//            String userId = "1";
//            String token = "valid-token";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//            when(request.getHeader("Authorization"))
//                    .thenReturn("Bearer " + token);
//            when(jwtUtil.hasRole(token, ADMIN_ROLE)).thenReturn(false);
//            when(jwtUtil.extractUserName(token)).thenReturn(TEST_EMAIL);
//
//            // Act
//            ResponseEntity<?> response = userService.deleteUser(userId, request);
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            verify(userRepository, times(1)).delete(testUser);
//        }
//
//        @Test
//        @DisplayName("Should fail delete when user not found")
//        void testDeleteUserFailureUserNotFound() {
//            // Arrange
//            String userId = "999";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.empty());
//
//            // Act
//            ResponseEntity<?> response = userService.deleteUser(userId, request);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//            verify(userRepository, never()).delete(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should fail delete when authorization header is missing")
//        void testDeleteUserFailureNoAuthorizationHeader() {
//            // Arrange
//            String userId = "1";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//            when(request.getHeader("Authorization")).thenReturn(null);
//
//            // Act
//            ResponseEntity<?> response = userService.deleteUser(userId, request);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//            verify(userRepository, never()).delete(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Should fail delete when authorization header format is invalid")
//        void testDeleteUserFailureInvalidAuthorizationFormat() {
//            // Arrange
//            String userId = "1";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//            when(request.getHeader("Authorization")).thenReturn("InvalidFormat");
//
//            // Act
//            ResponseEntity<?> response = userService.deleteUser(userId, request);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        }
//
//        @Test
//        @DisplayName("Should fail delete when user lacks authorization")
//        void testDeleteUserFailureUnauthorized() {
//            // Arrange
//            String userId = "1";
//            String token = "valid-token";
//            when(userRepository.findById(userId))
//                    .thenReturn(Optional.of(testUser));
//            when(request.getHeader("Authorization"))
//                    .thenReturn("Bearer " + token);
//            when(jwtUtil.hasRole(token, ADMIN_ROLE)).thenReturn(false);
//            when(jwtUtil.extractUserName(token))
//                    .thenReturn("different@email.com");
//
//            // Act
//            ResponseEntity<?> response = userService.deleteUser(userId, request);
//
//            // Assert
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//            verify(userRepository, never()).delete(any(User.class));
//        }
//    }
//
//    // ==================== User Mapping Tests ====================
//
//    @Nested
//    @DisplayName("User Mapping Tests")
//    class UserMappingTests {
//
//        @Test
//        @DisplayName("Should map User entity to UserResponse correctly")
//        void testMapToUserResponseSuccess() {
//            // Act
//            UserResponse response = userService.mapToUserResponse(testUser);
//
//            // Assert
//            assertNotNull(response);
//            assertEquals(String.valueOf(testUser.getId()), response.getId());
//            assertEquals(testUser.getFirstName(), response.getFirstName());
//            assertEquals(testUser.getLastName(), response.getLastName());
//            assertEquals(testUser.getEmail(), response.getEmail());
//            assertEquals(testUser.getPhone(), response.getPhone());
//            assertNotNull(response.getAddress());
//        }
//
//        @Test
//        @DisplayName("Should map UserRequest to User entity correctly")
//        void testMapToUserSuccess() {
//            // Act
//            User user = userService.mapToUser(testUserRequest);
//
//            // Assert
//            assertNotNull(user);
//            assertEquals(testUserRequest.getFirstName(), user.getFirstName());
//            assertEquals(testUserRequest.getLastName(), user.getLastName());
//            assertEquals(testUserRequest.getEmail(), user.getEmail());
//            assertEquals(testUserRequest.getPassword(), user.getPassword());
//            assertEquals(testUserRequest.getPhone(), user.getPhone());
//            assertNotNull(user.getRole());
//            assertEquals(1, user.getRole().size());
//        }
//
//        @Test
//        @DisplayName("Should preserve address information during mapping")
//        void testMapPreservesAddressInformation() {
//            // Act
//            User user = userService.mapToUser(testUserRequest);
//
//            // Assert
//            assertNotNull(user.getAddress());
//            assertEquals(testUserRequest.getAddress().getCity(),
//                    user.getAddress().getCity());
//            assertEquals(testUserRequest.getAddress().getState(),
//                    user.getAddress().getState());
//            assertEquals(testUserRequest.getAddress().getZipcode(),
//                    user.getAddress().getZipcode());
//        }
//    }
//
}
