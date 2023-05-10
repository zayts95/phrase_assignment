package com.phrase.assignment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phrase.assignment.dto.LoginDto;
import com.phrase.assignment.dto.LoginResponseDto;
import com.phrase.assignment.model.AccountConfiguration;
import com.phrase.assignment.repository.AccountConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhraseServiceTest {

    @InjectMocks
    private PhraseService phraseService;

    @Mock
    private AccountConfigRepository accountConfigRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private RestTemplate restTemplate;


    @Mock
    private ObjectMapper objectMapper;

    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_PASSWORD = "testPassword";
    private static final String TEST_TOKEN = "testToken";
    private static final String LOGIN_URL = "api2/v1/auth/login";
    private static final String LIST_PROJECTS_URL = "api2/v1/projects";

    @BeforeEach
    void setUp() {
        when(request.getSession()).thenReturn(session);
    }

    @Test
    void testSetAccount_existingAccount() throws Exception {
        // Prepare test data
        AccountConfiguration accountConfiguration = new AccountConfiguration();
        accountConfiguration.setUserName(TEST_USERNAME);
        accountConfiguration.setPassword(TEST_PASSWORD);
        accountConfiguration.setToken(TEST_TOKEN);
        ModelAndView modelAndView = new ModelAndView();

        when(accountConfigRepository.findById(TEST_USERNAME)).thenReturn(Optional.of(accountConfiguration));

        // Call method under test
        ModelAndView result = phraseService.setAccount(TEST_USERNAME, TEST_PASSWORD, request, modelAndView);

        // Verify results
        verify(session).setAttribute("account", TEST_USERNAME);
        verify(session).setAttribute("token", TEST_TOKEN);
        assertEquals("redirect:projects", result.getViewName());
    }

    // Add more test cases for setAccount method like invalid password, new account, etc.

    @Test
    void testGetProjects_authorized() {
        // Prepare test data
        LoginResponseDto loginResponse = new LoginResponseDto();
        LoginResponseDto.UserDto userDto = loginResponse.createUserDto();
        userDto.setUserName(TEST_USERNAME);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setRole("admin");
        userDto.setId("123");
        userDto.setUid("abc");
        loginResponse.setUser(userDto);
        loginResponse.setToken(TEST_TOKEN);
        loginResponse.setExpires(new Timestamp(System.currentTimeMillis() + 3600 * 1000));
        loginResponse.setLastInvalidateAllSessionsPerformed(new Timestamp(System.currentTimeMillis()));
        AccountConfiguration accountConfiguration = new AccountConfiguration(loginResponse, TEST_PASSWORD);
        ResponseEntity<String> mockResponseEntity = ResponseEntity.ok("Sample response");
        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponseEntity);
        when(session.getAttribute("account")).thenReturn(TEST_USERNAME);
        when(accountConfigRepository.findById(TEST_USERNAME)).thenReturn(Optional.of(accountConfiguration));

        // Call method under test
        ResponseEntity<?> result = phraseService.getProjects(request);

        // Verify results
        assertEquals(HttpStatus.OK, result.getStatusCode());
        // Add more assertions based on the expected response
    }


    @Test
    void testGetProjects_unauthorized() {
        // Prepare test data
        when(session.getAttribute("account")).thenReturn(null);

        // Call method under test
        ResponseEntity<?> result = phraseService.getProjects(request);

        // Verify results
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    // Add more test cases for other methods in PhraseService class
}