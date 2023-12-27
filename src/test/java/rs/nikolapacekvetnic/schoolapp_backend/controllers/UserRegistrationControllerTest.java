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
import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.DuplicateEmailException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.AdminEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.AdminRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserRegistrationService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.JsonUtil;

import java.util.Objects;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRegistrationService userRegistrationService;
    @InjectMocks private UserRegistrationController userRegistrationController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userRegistrationController).build();
    }

    @Test
    public void whenAddNewAdmin_thenSaveAdmin() throws Exception {
        AdminRegisterDto adminDto = new AdminRegisterDto();
        AdminEntity adminEntity = new AdminEntity();

        when(userRegistrationService.addNewAdmin(any(AdminRegisterDto.class), any(BindingResult.class)))
                .thenReturn(adminEntity);

        mockMvc.perform(post("/api/v1/project/registration/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(JsonUtil.convertToJson(adminDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminEntity.getId()));
    }

    @Test
    public void whenAddNewTeacherWithDuplicateEmail_thenBadRequest() throws Exception {
        TeacherRegisterDto teacherDto = new TeacherRegisterDto();

        when(userRegistrationService.addNewTeacher(any(TeacherRegisterDto.class), any(BindingResult.class)))
                .thenThrow(new DuplicateEmailException("Email must be unique."));

        mockMvc.perform(post("/api/v1/project/registration/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(JsonUtil.convertToJson(teacherDto))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email must be unique."));
    }
}

