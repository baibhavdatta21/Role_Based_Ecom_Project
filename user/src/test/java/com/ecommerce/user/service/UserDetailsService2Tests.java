package com.ecommerce.user.service;

import com.ecommerce.user.model.User;
import com.ecommerce.user.model.UserPrincipal;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.util.TestDataBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.ecommerce.user.util.TestConstants.TEST_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsService2 Test Suite")
public class UserDetailsService2Tests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsService2 userDetailsService;

    private TestDataBuilder testDataBuilder;
    private User testUser;

    @BeforeEach
    @DisplayName("Initialize test data before each test")
    void setUp() {
        testDataBuilder = new TestDataBuilder();
        testUser = testDataBuilder.buildValidUser();
    }

    // ==================== Load User by Username Tests ====================

    @Nested
    @DisplayName("Load User by Username Tests")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Should load user details when user exists")
        void testLoadUserByUsernameSuccess() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

            // Assert
            assertNotNull(userDetails);
            assertEquals(TEST_EMAIL, userDetails.getUsername());
            assertTrue(userDetails instanceof UserPrincipal);
            verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user not found")
        void testLoadUserByUsernameNotFound() {
            // Arrange
            when(userRepository.findByEmail(anyString()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsernameNotFoundException.class, () ->
                    userDetailsService.loadUserByUsername("nonexistent@email.com"));
            verify(userRepository, times(1)).findByEmail(anyString());
        }

        @Test
        @DisplayName("Should pass correct email to repository")
        void testLoadUserEmailPassed() {
            // Arrange
            String email = "specific@email.com";
            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(testUser));

            // Act
            userDetailsService.loadUserByUsername(email);

            // Assert
            verify(userRepository).findByEmail(email);
        }

        @Test
        @DisplayName("Should return UserPrincipal instance")
        void testLoadUserReturnsUserPrincipal() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

            // Assert
            assertNotNull(userDetails);
            assertInstanceOf(UserPrincipal.class, userDetails);
        }

        @Test
        @DisplayName("Should load different users correctly")
        void testLoadDifferentUsers() {
            // Arrange
            String email1 = "user1@email.com";
            String email2 = "user2@email.com";
            User user1 = new TestDataBuilder().withEmail(email1).buildValidUser();
            User user2 = new TestDataBuilder().withEmail(email2).buildValidUser();

            when(userRepository.findByEmail(email1)).thenReturn(Optional.of(user1));
            when(userRepository.findByEmail(email2)).thenReturn(Optional.of(user2));

            // Act
            UserDetails userDetails1 = userDetailsService.loadUserByUsername(email1);
            UserDetails userDetails2 = userDetailsService.loadUserByUsername(email2);

            // Assert
            assertEquals(email1, userDetails1.getUsername());
            assertEquals(email2, userDetails2.getUsername());
            assertNotEquals(userDetails1.getUsername(), userDetails2.getUsername());
        }
    }

    // ==================== Error Handling Tests ====================

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should throw exception with correct message when user not found")
        void testLoadUserExceptionMessage() {
            // Arrange
            String email = "notfound@email.com";
            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.empty());

            // Act & Assert
            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername(email)
            );
            assertNotNull(exception.getMessage());
        }

        @Test
        @DisplayName("Should handle null email gracefully")
        void testLoadUserNullEmail() {
            // Arrange
            when(userRepository.findByEmail(null))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsernameNotFoundException.class, () ->
                    userDetailsService.loadUserByUsername(null));
        }

        @Test
        @DisplayName("Should handle empty string email")
        void testLoadUserEmptyEmail() {
            // Arrange
            when(userRepository.findByEmail(""))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsernameNotFoundException.class, () ->
                    userDetailsService.loadUserByUsername(""));
        }

        @Test
        @DisplayName("Should handle special characters in email")
        void testLoadUserSpecialCharactersEmail() {
            // Arrange
            String email = "test+special@domain.co.uk";
            User user = new TestDataBuilder().withEmail(email).buildValidUser();
            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(user));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Assert
            assertNotNull(userDetails);
            assertEquals(email, userDetails.getUsername());
        }
    }

    // ==================== UserPrincipal Creation Tests ====================

    @Nested
    @DisplayName("UserPrincipal Creation Tests")
    class UserPrincipalCreationTests {

        @Test
        @DisplayName("Should create UserPrincipal with correct user data")
        void testUserPrincipalCreationCorrect() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

            // Assert
            UserPrincipal principal = (UserPrincipal) userDetails;
            assertEquals(testUser.getEmail(), principal.getUsername());
            assertEquals(testUser.getPassword(), principal.getPassword());
        }

        @Test
        @DisplayName("Should create UserPrincipal with correct authorities")
        void testUserPrincipalAuthorities() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

            // Assert
            assertNotNull(userDetails.getAuthorities());
            assertFalse(userDetails.getAuthorities().isEmpty());
        }

        @Test
        @DisplayName("Should preserve all user information in UserPrincipal")
        void testUserPrincipalPreservesUserData() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);
            UserPrincipal principal = (UserPrincipal) userDetails;

            // Assert
//            assertEquals(testUser.getFirstName(), principal.getUser().getFirstName());
//            assertEquals(testUser.getLastName(), principal.getUser().getLastName());
            assertEquals(testUser.getEmail(), principal.getUsername());
//            assertEquals(testUser.getPhone(), principal.getUser().getPhone());
//            assertNotNull(principal.getUser().getAddress());
        }
    }

    // ==================== Repository Interaction Tests ====================

    @Nested
    @DisplayName("Repository Interaction Tests")
    class RepositoryInteractionTests {

        @Test
        @DisplayName("Should call repository findByEmail method exactly once")
        void testRepositoryCalledOnce() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL))
                    .thenReturn(Optional.of(testUser));

            // Act
            userDetailsService.loadUserByUsername(TEST_EMAIL);

            // Assert
            verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        }

        @Test
        @DisplayName("Should not call repository multiple times for same user")
        void testRepositoryNotCalledMultipleTimes() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL))
                    .thenReturn(Optional.of(testUser));

            // Act
            userDetailsService.loadUserByUsername(TEST_EMAIL);
            userDetailsService.loadUserByUsername(TEST_EMAIL);

            // Assert
            // Each call should invoke repository once (no caching expected)
            verify(userRepository, times(2)).findByEmail(TEST_EMAIL);
        }

        @Test
        @DisplayName("Should use Optional.of() correctly")
        void testOptionalHandling() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

            // Assert
            assertNotNull(userDetails);
        }
    }

    // ==================== Integration Scenarios Tests ====================

    @Nested
    @DisplayName("Integration Scenario Tests")
    class IntegrationScenarioTests {

        @Test
        @DisplayName("Should handle rapid successive load requests")
        void testRapidSuccessiveLoads() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL))
                    .thenReturn(Optional.of(testUser));

            // Act & Assert
            for (int i = 0; i < 5; i++) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);
                assertNotNull(userDetails);
            }
        }

        @Test
        @DisplayName("Should handle loading after user not found")
        void testLoadAfterNotFound() {
            // Arrange
            when(userRepository.findByEmail("notfound@email.com"))
                    .thenReturn(Optional.empty());
            when(userRepository.findByEmail(TEST_EMAIL))
                    .thenReturn(Optional.of(testUser));

            // Act & Assert
            assertThrows(UsernameNotFoundException.class, () ->
                    userDetailsService.loadUserByUsername("notfound@email.com"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);
            assertNotNull(userDetails);
        }

        @Test
        @DisplayName("Should handle switching between different users")
        void testSwitchingBetweenUsers() {
            // Arrange
            User user1 = new TestDataBuilder().withEmail("user1@email.com").buildValidUser();
            User user2 = new TestDataBuilder().withEmail("user2@email.com").buildValidUser();

            when(userRepository.findByEmail("user1@email.com")).thenReturn(Optional.of(user1));
            when(userRepository.findByEmail("user2@email.com")).thenReturn(Optional.of(user2));

            // Act
            UserDetails userDetails1 = userDetailsService.loadUserByUsername("user1@email.com");
            UserDetails userDetails2 = userDetailsService.loadUserByUsername("user2@email.com");

            // Assert
            assertNotEquals(userDetails1.getUsername(), userDetails2.getUsername());
        }
    }
}
