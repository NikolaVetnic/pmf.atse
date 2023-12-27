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
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeCardEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.TeacherEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.UserLoginServiceImpl;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.TeacherService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    private MockMvc mockMvc;

    @Mock private UserLoginServiceImpl userLoginService;
    @Mock private TeacherService teacherService;
    @InjectMocks private TeacherController teacherController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController).build();
    }

    @Test
    public void whenGetTeacher_thenReturnTeacher() throws Exception {
        String username = "teacherUsername";
        TeacherEntity teacher = new TeacherEntity();

        when(userLoginService.getLoggedInUsername()).thenReturn(Optional.of(username));
        when(teacherService.getTeacher(username)).thenReturn(teacher);

        mockMvc.perform(get("/api/v1/project/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teacher.getId()));
    }

    @Test
    public void whenGetGradeCards_thenReturnGradeCardsList() throws Exception {
        TeacherEntity teacher = new TeacherEntity(); // Set up teacher entity
        List<GradeCardEntity> gradeCards = new ArrayList<>(); // Set up grade cards list
        when(userLoginService.getLoggedInUsername()).thenReturn(Optional.of("teacherUsername"));
        when(teacherService.getTeacher(anyString())).thenReturn(teacher);
        when(teacherService.getTeacherGradeCards(teacher)).thenReturn(gradeCards);

        mockMvc.perform(get("/api/v1/project/teacher/grades"))
                .andExpect(status().isOk())
                .andExpect(content().json(Objects.requireNonNull(JsonUtil.convertToJson(gradeCards))));
    }

    @Test
    public void whenUpdateTeacher_thenReturnUpdatedTeacher() throws Exception {
        TeacherEntity existingTeacher = new TeacherEntity();
        existingTeacher.setId(1);
        existingTeacher.setFirstName("John");
        existingTeacher.setLastName("Olson");

        TeacherRegisterDto teacherDto = new TeacherRegisterDto();
        teacherDto.setUsername("teacher");
        teacherDto.setPassword("passW0RD!");
        teacherDto.setConfirmPassword("passW0RD!");
        teacherDto.setFirstName("Mike");
        teacherDto.setLastName("Newton");
        teacherDto.setEmail("mike.newton@mail.com");
        teacherDto.setRole(EUserRole.TEACHER);

        TeacherEntity updatedTeacher = new TeacherEntity();
        updatedTeacher.setId(1);
        updatedTeacher.setFirstName("Mike");
        updatedTeacher.setLastName("Newton");

        when(teacherService.updateTeacher(anyInt(), any(TeacherRegisterDto.class), any(BindingResult.class)))
                .thenReturn(updatedTeacher);

        mockMvc.perform(put("/api/v1/project/teacher/update/" + existingTeacher.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(JsonUtil.convertToJson(teacherDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedTeacher.getId()));
    }

    @Test
    public void whenDeleteTeacher_thenRespondWithOk() throws Exception {
        Integer id = 1;
        doNothing().when(teacherService).deleteTeacher(id);

        mockMvc.perform(delete("/api/v1/project/teacher/" + id))
                .andExpect(status().isOk());
    }
}

