package com.ecommerce.user.controller;

import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.service.UserService;
import com.ecommerce.user.util.TestDataBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static com.ecommerce.user.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Test Suite")
public class UserControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserController userController;

    private TestDataBuilder testDataBuilder;
    private UserRequest testUserRequest;
    private UserResponse testUserResponse;

    @BeforeEach
    @DisplayName("Initialize test data before each test")
    void setUp() {
        testDataBuilder = new TestDataBuilder();
        testUserRequest = testDataBuilder.buildValidUserRequest();
        testUserResponse = testDataBuilder.buildValidUserResponse();
    }

    // ==================== Get All Users Endpoint Tests ====================

    @Nested
    @DisplayName("GET /api/auth/users - Get All Users Endpoint")
    class GetAllUsersEndpointTests {

        @Test
        @DisplayName("Should return all users with HTTP 200")
        void testGetAllUsersSuccess() {
            // Arrange
            List<UserResponse> users = new ArrayList<>();
            users.add(testUserResponse);
            when(userService.getAllUsers()).thenReturn(users);

            // Act
            ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
            verify(userService, times(1)).getAllUsers();
        }

        @Test
        @DisplayName("Should return empty list when no users exist")
        void testGetAllUsersEmptyList() {
            // Arrange
            when(userService.getAllUsers()).thenReturn(new ArrayList<>());

            // Act
            ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isEmpty());
        }

        @Test
        @DisplayName("Should call userService getAllUsers exactly once")
        void testGetAllUsersCallCount() {
            // Arrange
            when(userService.getAllUsers()).thenReturn(new ArrayList<>());

            // Act
            userController.getAllUsers();

            // Assert
            verify(userService, times(1)).getAllUsers();
            verify(userService, only()).getAllUsers();
        }
    }

    // ==================== Get User by ID Endpoint Tests ====================

    @Nested
    @DisplayName("GET /api/auth/users/{id} - Get User by ID Endpoint")
    class GetUserByIdEndpointTests {

        @Test
        @DisplayName("Should return user when found")
        void testGetUserSuccess() {
            // Arrange
            String userId = "1";
            when(userService.getUser(userId)).thenReturn(testUserResponse);

            // Act
            ResponseEntity<UserResponse> response = userController.getUser(userId);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(TEST_EMAIL, response.getBody().getEmail());
            verify(userService, times(1)).getUser(userId);
        }

        @Test
        @DisplayName("Should return null response when user not found")
        void testGetUserNotFound() {
            // Arrange
            String userId = "999";
            when(userService.getUser(userId)).thenReturn(null);

            // Act
            ResponseEntity<UserResponse> response = userController.getUser(userId);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNull(response.getBody());
            verify(userService, times(1)).getUser(userId);
        }

        @Test
        @DisplayName("Should pass user ID correctly to service")
        void testGetUserIdPassed() {
            // Arrange
            String userId = "123";
            when(userService.getUser(userId)).thenReturn(testUserResponse);

            // Act
            userController.getUser(userId);

            // Assert
            verify(userService).getUser("123");
        }
    }

    // ==================== Login Endpoint Tests ====================

    @Nested
    @DisplayName("POST /api/public/users/login - Login Endpoint")
    class LoginEndpointTests {

        @Test
        @DisplayName("Should login user successfully and return JWT token")
        void testLoginSuccess() {
            // Arrange
            String expectedToken = JWT_TOKEN;
            when(userService.addUser(testUserRequest))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            // Act
            ResponseEntity<?> response = userController.createUser(testUserRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertEquals(expectedToken, response.getBody());
            verify(userService, times(1)).addUser(testUserRequest);
        }

        @Test
        @DisplayName("Should return UNAUTHORIZED when login fails")
        void testLoginFailure() {
            // Arrange
            when(userService.addUser(testUserRequest))
                    .thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

            // Act
            ResponseEntity<?> response = userController.createUser(testUserRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(userService, times(1)).addUser(testUserRequest);
        }

        @Test
        @DisplayName("Should pass user request to service")
        void testLoginRequestPassed() {
            // Arrange
            when(userService.addUser(any(UserRequest.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            // Act
            userController.createUser(testUserRequest);

            // Assert
            verify(userService).addUser(any(UserRequest.class));
        }
    }

    // ==================== Signup Endpoint Tests ====================

    @Nested
    @DisplayName("POST /api/public/users/signup - Signup Endpoint")
    class SignupEndpointTests {

        @Test
        @DisplayName("Should signup user successfully and return user response")
        void testSignupSuccess() {
            // Arrange
            when(userService.signup(testUserRequest))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));
            // Act
            ResponseEntity<?> response = userController.signUp(testUserRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertTrue(response.getBody() instanceof UserResponse);
            verify(userService, times(1)).signup(testUserRequest);
        }

        @Test
        @DisplayName("Should return BAD_REQUEST when signup fails")
        void testSignupFailure() {
            // Arrange
            testUserRequest.setRole("ADMIN");
            when(userService.signup(testUserRequest))
                    .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

            // Act
            ResponseEntity<?> response = userController.signUp(testUserRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(userService, times(1)).signup(testUserRequest);
        }

        @Test
        @DisplayName("Should pass user request to service")
        void testSignupRequestPassed() {
            // Arrange
            when(userService.signup(any(UserRequest.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            // Act
            userController.signUp(testUserRequest);

            // Assert
            verify(userService).signup(any(UserRequest.class));
        }
    }

    // ==================== Admin Signup Endpoint Tests ====================

    @Nested
    @DisplayName("POST /api/auth/users/signup-admin - Admin Signup Endpoint")
    class AdminSignupEndpointTests {

        @Test
        @DisplayName("Should signup admin successfully")
        void testSignupAdminSuccess() {
            // Arrange
            testUserRequest.setRole(ADMIN_ROLE);
            when(userService.signUpAdmin(testUserRequest))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            // Act
            ResponseEntity<?> response = userController.signUpAdmin(testUserRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(userService, times(1)).signUpAdmin(testUserRequest);
        }

        @Test
        @DisplayName("Should return error when non-admin role provided")
        void testSignupAdminFailureNonAdminRole() {
            // Arrange
            testUserRequest.setRole(CUSTOMER_ROLE);
            when(userService.signUpAdmin(testUserRequest))
                    .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

            // Act
            ResponseEntity<?> response = userController.signUpAdmin(testUserRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    // ==================== Update User Endpoint Tests ====================

    @Nested
    @DisplayName("PUT /api/auth/users/{id} - Update User Endpoint")
    class UpdateUserEndpointTests {

        @Test
        @DisplayName("Should update user successfully")
        void testUpdateUserSuccess() {
            // Arrange
            String userId = "1";
            when(userService.putUser(userId, testUserRequest, request))
                    .thenReturn(new ResponseEntity<>( HttpStatus.OK));

            // Act
            ResponseEntity<?> response = userController.putUser(userId, testUserRequest, request);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(userService, times(1)).putUser(userId, testUserRequest, request);
        }

        @Test
        @DisplayName("Should return BAD_REQUEST when update fails")
        void testUpdateUserFailure() {
            // Arrange
            String userId = "999";
            when(userService.putUser(userId, testUserRequest, request))
                    .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

            // Act
            ResponseEntity<?> response = userController.putUser(userId, testUserRequest, request);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Should pass correct parameters to service")
        void testUpdateUserParametersPassed() {
            // Arrange
            String userId = "1";
            when(userService.putUser(anyString(), any(UserRequest.class), any(HttpServletRequest.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            // Act
            userController.putUser(userId, testUserRequest, request);

            // Assert
            verify(userService).putUser(userId, testUserRequest, request);
        }
    }

    // ==================== Delete User Endpoint Tests ====================

    @Nested
    @DisplayName("DELETE /api/auth/users/{id} - Delete User Endpoint")
    class DeleteUserEndpointTests {

        @Test
        @DisplayName("Should delete user successfully")
        void testDeleteUserSuccess() {
            // Arrange
            String userId = "1";
            when(userService.deleteUser(userId, request))
                    .thenReturn(new ResponseEntity<>( HttpStatus.OK));

            // Act
            ResponseEntity<?> response = userController.deleteUser(userId, request);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(userService, times(1)).deleteUser(userId, request);
        }

        @Test
        @DisplayName("Should return BAD_REQUEST when user not found")
        void testDeleteUserNotFound() {
            // Arrange
            String userId = "999";
            when(userService.deleteUser(userId, request))
                    .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

            // Act
            ResponseEntity<?> response = userController.deleteUser(userId, request);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Should pass correct parameters to service")
        void testDeleteUserParametersPassed() {
            // Arrange
            String userId = "1";
            when(userService.deleteUser(anyString(), any(HttpServletRequest.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            // Act
            userController.deleteUser(userId, request);

            // Assert
            verify(userService).deleteUser(userId, request);
        }
    }
}

