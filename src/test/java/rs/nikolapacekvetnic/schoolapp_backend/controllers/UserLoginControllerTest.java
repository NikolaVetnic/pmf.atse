package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.UserLoginDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserLoginControllerTest {

    private MockMvc mockMvc;

    @Mock private UserLoginService userLoginService;
    @InjectMocks private UserLoginController userLoginController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userLoginController).build();
    }

    @Test
    public void whenLoginWithValidCredentials_thenReturnsToken() throws Exception {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername("user");
        userLoginDto.setToken("token");

        when(userLoginService.getUserLoginDto("user", "password"))
                .thenReturn(Optional.of(userLoginDto));

        mockMvc.perform(post("/api/v1/project/login")
                        .param("username", "user")
                        .param("password", "password")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    public void whenLoginWithInvalidCredentials_thenReturnsUnauthorized() throws Exception {
        when(userLoginService.getUserLoginDto("user", "wrong-password"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/project/login")
                        .param("username", "user")
                        .param("password", "wrong-password")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Username and password do not match"));
    }
}
