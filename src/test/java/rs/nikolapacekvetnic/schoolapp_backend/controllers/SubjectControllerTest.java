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
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SubjectEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SubjectRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.SubjectService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.JsonUtil;

import java.util.Objects;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SubjectControllerTest {

    private MockMvc mockMvc;

    @Mock private SubjectService subjectService;
    @InjectMocks private SubjectController subjectController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(subjectController)
                .build();
    }

    @Test
    public void whenAddNewSubject_thenReturnSubject() throws Exception {
        SubjectRegisterDto subjectDTO = new SubjectRegisterDto();
        subjectDTO.setName("subject");
        subjectDTO.setYearAccredited(2000);
        subjectDTO.setTotalHours(20);

        SubjectEntity subject = new SubjectEntity();

        when(subjectService.addNewSubject(any(SubjectRegisterDto.class), any(BindingResult.class))).thenReturn(subject);

        mockMvc.perform(post("/api/v1/project/subjects/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(JsonUtil.convertToJson(subjectDTO))))
                .andExpect(status().isOk());
    }

    @Test
    public void whenUpdateSubject_thenReturnUpdatedSubject() throws Exception {
        SubjectRegisterDto subjectDTO = new SubjectRegisterDto(); // Setup subject DTO

        SubjectEntity updatedSubject = new SubjectEntity(); // Setup updated subject entity
        updatedSubject.setId(1);

        when(subjectService.updateSubject(eq(updatedSubject.getId()), any(SubjectRegisterDto.class), any(BindingResult.class))).thenReturn(updatedSubject);

        mockMvc.perform(put("/api/v1/project/subjects/update/" + updatedSubject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(JsonUtil.convertToJson(subjectDTO))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedSubject.getId()));
    }

    @Test
    public void whenDeleteSubject_thenRespondWithOk() throws Exception {
        Integer id = 1;
        doNothing().when(subjectService).deleteSubject(id);

        mockMvc.perform(delete("/api/v1/project/subjects/" + id))
                .andExpect(status().isOk());
    }

}
