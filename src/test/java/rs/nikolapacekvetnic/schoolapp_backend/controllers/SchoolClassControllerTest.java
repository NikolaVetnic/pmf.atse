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
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SchoolClassEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SchoolClassRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.StudentRepository;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.SchoolClassService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.JsonUtil;

import java.util.Objects;

import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SchoolClassControllerTest {

    @Mock private SchoolClassService schoolClassService;
    @Mock private StudentRepository studentRepository;

    @InjectMocks private SchoolClassController schoolClassController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(schoolClassController).build();
    }

    @Test
    void whenAddNewSchoolClass_thenReturnSchoolClass() throws Exception {
        SchoolClassRegisterDto schoolClassDTO = new SchoolClassRegisterDto();
        SchoolClassEntity schoolClass = new SchoolClassEntity();

        mockMvc.perform(post("/api/v1/project/classes/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(JsonUtil.convertToJson(schoolClassDTO))))
                .andExpect(status().isOk());
    }

    @Test
    void whenConnectStudentWithClass_thenReturnStudent() throws Exception {
        Integer studentId = 1, schoolClassId = 1;
        StudentEntity student = new StudentEntity();

        mockMvc.perform(put("/api/v1/project/classes/insert/" + studentId + "/into/" + schoolClassId))
                .andExpect(status().isOk());
    }
}

