package com.jobtracker.jobtracker_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jobtracker.jobtracker_backend.dto.AuthResponse;
import com.jobtracker.jobtracker_backend.dto.LoginRequest;
import com.jobtracker.jobtracker_backend.dto.RegisterRequest;
import com.jobtracker.jobtracker_backend.exception.EmailAlreadyExistsException;
import com.jobtracker.jobtracker_backend.exception.InvalidCredentialsException;
import com.jobtracker.jobtracker_backend.model.User;
import com.jobtracker.jobtracker_backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    static final UUID USER_ID = UUID.randomUUID();
    static final String USER_NAME = "Test User";
    static final String USER_EMAIL = "test@mail.com";
    static final String USER_PASSWORD = "TestPassword123";
    static final String HASHED_PASSWORD = "hashed-password";
    static final String FAKE_JWT_TOKEN = "fake-jwt-token";

    @Test
    void registerHappyPath() {
        RegisterRequest request = new RegisterRequest(USER_EMAIL, USER_PASSWORD, USER_NAME);

        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(USER_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(UUID.randomUUID());
            return savedUser;
        });
        when(jwtService.generateToken(any(UUID.class), eq(USER_EMAIL))).thenReturn(FAKE_JWT_TOKEN);

        AuthResponse response = authService.register(request);

        assertEquals(USER_EMAIL, response.email());
        assertEquals(USER_NAME, response.displayName());
        assertEquals(FAKE_JWT_TOKEN, response.token());
    }

    @Test
    void registerThrowsWhenEmailExists() {
        RegisterRequest request = new RegisterRequest(USER_EMAIL, USER_PASSWORD, USER_NAME);
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginHappyPath() {
        LoginRequest request = new LoginRequest(USER_EMAIL, USER_PASSWORD);

        User user = new User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setPasswordHash(HASHED_PASSWORD);
        user.setDisplayName(USER_NAME);

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(USER_PASSWORD,HASHED_PASSWORD)).thenReturn(true);
        when(jwtService.generateToken(USER_ID, USER_EMAIL)).thenReturn(FAKE_JWT_TOKEN);

        AuthResponse response = authService.login(request);

        assertEquals(USER_EMAIL, response.email());
        assertEquals(USER_NAME, response.displayName());
        assertEquals(FAKE_JWT_TOKEN, response.token());
    }

    @Test 
    void loginThrowsWhenEmailNotFound(){
        LoginRequest request = new LoginRequest(USER_EMAIL, USER_PASSWORD);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test 
    void loginThrowsWhenPasswordIsInvalid(){
        LoginRequest request = new LoginRequest(USER_EMAIL, USER_PASSWORD);

        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setPasswordHash(HASHED_PASSWORD);

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(USER_PASSWORD, HASHED_PASSWORD)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test 
    void loginFailureHaveIdenticalMessage(){
        LoginRequest request = new LoginRequest(USER_EMAIL, USER_PASSWORD);

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());
        InvalidCredentialsException emailNotFoundException = 
            assertThrows(InvalidCredentialsException.class, () -> authService.login(request));

        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setPasswordHash(HASHED_PASSWORD);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(USER_PASSWORD, HASHED_PASSWORD)).thenReturn(false);
        InvalidCredentialsException wrongPasswordException = 
            assertThrows(InvalidCredentialsException.class, () -> authService.login(request));

        assertEquals(emailNotFoundException.getMessage(), wrongPasswordException.getMessage());


    }

}
