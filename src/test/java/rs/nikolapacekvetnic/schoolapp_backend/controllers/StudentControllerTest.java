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
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeCardEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.StudentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.StudentService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.JsonUtil;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock private StudentService studentService;
    @Mock private UserLoginService userLoginService;
    @InjectMocks private StudentController studentController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .build();
    }

    @Test
    public void whenGetStudent_thenReturnStudent() throws Exception {
        String username = "student1";
        StudentEntity student = new StudentEntity(); // Mock the student entity
        student.setUsername(username);

        when(userLoginService.getLoggedInUsername()).thenReturn(Optional.of(username));
        when(studentService.getStudent(username)).thenReturn(student);

        mockMvc.perform(get("/api/v1/project/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(student.getUsername()));
    }

    @Test
    public void whenGetGradeCards_thenReturnGradeCards() throws Exception {
        String username = "student1";
        Set<GradeCardEntity> gradeCards = new HashSet<>(); // Mock the grade card set

        when(userLoginService.getLoggedInUsername()).thenReturn(Optional.of(username));
        when(studentService.getGradeCards(username)).thenReturn(gradeCards);

        mockMvc.perform(get("/api/v1/project/student/grades"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetGradeCardsForSubject_thenReturnGradeCards() throws Exception {
        String username = "student1";
        Integer subjectId = 1;
        List<GradeCardEntity> gradeCards = new ArrayList<>(); // Mock the grade card list

        when(userLoginService.getLoggedInUsername()).thenReturn(Optional.of(username));
        when(studentService.getGradeCardsForSubject(username, subjectId)).thenReturn(gradeCards);

        mockMvc.perform(get("/api/v1/project/student/grades/" + subjectId))
                .andExpect(status().isOk());
    }

    @Test
    public void whenUpdateStudent_thenReturnUpdatedStudent() throws Exception {
        StudentRegisterDto studentDto = new StudentRegisterDto();

        StudentEntity updatedStudent = new StudentEntity();
        updatedStudent.setId(1);

        when(studentService.updateStudent(eq(updatedStudent.getId()), any(StudentRegisterDto.class), any(BindingResult.class))).thenReturn(updatedStudent);

        mockMvc.perform(put("/api/v1/project/student/update/" + updatedStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(JsonUtil.convertToJson(studentDto))))
                .andExpect(status().isOk());
    }

    @Test
    public void whenDeleteStudent_thenRespondOk() throws Exception {
        Integer id = 1;

        doNothing().when(studentService).deleteStudent(id);

        mockMvc.perform(delete("/api/v1/project/student/" + id))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(id);
    }
}
